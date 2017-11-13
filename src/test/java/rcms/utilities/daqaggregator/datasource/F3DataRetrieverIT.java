package rcms.utilities.daqaggregator.datasource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import rcms.utilities.daqaggregator.Application;
import rcms.utilities.daqaggregator.DAQException;
import rcms.utilities.daqaggregator.DAQExceptionCode;
import rcms.utilities.daqaggregator.Settings;
import rcms.utilities.daqaggregator.data.BUSummary;
import rcms.utilities.daqaggregator.data.DAQ;
import rcms.utilities.daqaggregator.data.HltInfo;
import rcms.utilities.daqaggregator.datasource.F3DataRetriever.CpuLoadType;
import rcms.utilities.daqaggregator.TestWithExternalSystem;

/**
 * Integration test for F3 retrieval: contacts the actual F3 server
 * and checks that the values can be retrieved
 */
@Category(TestWithExternalSystem.class)
public class F3DataRetrieverIT {
	private static final Logger logger = Logger.getLogger(F3DataRetrieverIT.class);
	
	private static F3DataRetriever f3dataRetriever;
	private static DAQ daq;
	private static Integer runNumber;

	/** retrieve the information only once and check it in the individual test cases */
	@BeforeClass
	public static void makeRetriever() throws IOException {

		Application.initialize("DAQAggregator.properties");
		
		String hltUrl = Application.get().getProp(Settings.F3_HLT_URL);
		String diskUrl = Application.get().getProp(Settings.F3_DISK_URL);
		String crashesUrl = Application.get().getProp(Settings.F3_CRASHES_URL);
		String cpuLoadUrl = Application.get().getProp(Settings.F3_CPU_LOAD_URL);
		String cpuLoadType = Application.get().getProp(Settings.F3_CPU_LOAD_TYPE);

		f3dataRetriever = new F3DataRetriever(new Connector(false), hltUrl, diskUrl,crashesUrl,
		  cpuLoadUrl, CpuLoadType.getByKey(cpuLoadType));

		daq = new DAQ();
		
		// TODO: use MappingManager.map() instead
		daq.setBuSummary(new BUSummary());
		daq.setHltInfo(new HltInfo());
		
		// find a recent/current run number first
		// (the web applications for the hlt info will not return anything
		// if the run url parameter is not specified)
		runNumber = getLatestRun();
		logger.info("found latest run number " + runNumber);

		// needed for F3DataRetriever.dispatch()
		daq.setRunNumber(runNumber);

		f3dataRetriever.dispatch(daq);
	}
	
	/** checks that the F3DataRetriever could retrieve non-null values */
	@Test
	public void diskInfoDirectTest() throws IOException {

		// retrieve data from the F3mon serverse
		F3DataRetriever.DiskInfo di = f3dataRetriever.getDiskInfo();
		
		logger.info("disk output occupancy fraction is "  + di.getOutputOccupancyFraction());
		logger.info("disk total output is "               + di.getOutputTotal());
		logger.info("ramdisk occupancy fraction is "      + di.getRamdiskOccupancyFraction());
		logger.info("ramdisk total is "                   + di.getRamdiskTotal());
		
		assertNotNull(di.getOutputOccupancyFraction());
		assertNotNull(di.getOutputTotal());
		assertNotNull(di.getRamdiskOccupancyFraction());
		assertNotNull(di.getRamdiskTotal());

	}
	
	/** checks that some values were dispatched to the DAQ object. Note that
	    it looks like not all quantities in F3DataRetriever.DiskInfo are
			put into the DAQ object */
	@Test
	public void diskInfoDispatchTest()  {

		BUSummary buSummary = daq.getBuSummary();
		assertNotNull(buSummary);
		
		assertNotNull(buSummary.getOutputDiskTotal());
		assertNotNull(buSummary.getOutputDiskUsage());
		
		// F3DataRetriever.DiskInfo.ramdiskOccupancyFraction and
		// F3DataRetriever.DiskInfo.ramdiskTotal are not mapped into the DAQ
		// object
		
	}

	/** fetches the latest run number from F3mon */
	private static Integer getLatestRun() throws IOException {
		
		// get the base URL
		String lastRunUrl = Application.get().getProp(Settings.F3_LAST_RUN_URL);
		
		if (lastRunUrl == null) {
			throw new DAQException(DAQExceptionCode.MissingProperty, "property " + Settings.F3_LAST_RUN_URL.getKey() + " is not set");
		}
		
		Connector connector = new Connector(false);
		
		Pair<Integer, List<String>> a = connector.retrieveLines(lastRunUrl + "?setup=cdaq");
		
		List<String> result = a.getRight();

		long count = result.size();
		if (count == 1) {
			JsonNode resultJson = new ObjectMapper().readValue(result.get(0), JsonNode.class);

			try {

				return resultJson.get("number").asInt();

			} catch (NoSuchElementException e) {
				logger.warn("Cannot retrieve latest run number (no such element) from response: " + result.get(0));
				return null;
			} catch (NullPointerException e) {
				logger.warn("Cannot retrieve latest run number from response: " + result.get(0));
				return null;
			}
		} else {
			logger.warn("Expected 1 node as a response but was " + count);
			return null;
		}
	}

	
	/**
	 * Test for HLT rates and bandwidths.
	 *
	 * Note that dispatching the HLT rates will only dispatch the physics stream
	 * rate/bandwidth to the daq object which does not not exist during
	 * interfills. We therefore do not implement test of the dispatching mechanism
	 * for the HLT rate and bandwidth quantities.
	 */
	@Test
	public void hltInfoDirectTest() throws IOException {

		F3DataRetriever.HLToutputInfo hltInfo = f3dataRetriever.getHLToutputInfo(runNumber);

		// "PhysicsMuons" is there both for LHC fills and interfill runs
		// but we have seen cases of zero rates during interfill
		// use "HLTRates" instead
		String streamName = "HLTRates";

		Double eventRate = hltInfo.getEventRate(streamName);
		logger.info("HLT event rate for stream " + streamName + " is " + eventRate);
		assertNotNull(eventRate);

		Double bandwidth = hltInfo.getBandwidth(streamName);
		logger.info("HLT bandwidth for stream " + streamName + " is " + bandwidth);
		assertNotNull(bandwidth);
	}

	/**
	 * Test for number of HLT crashes
	 */
	@Test
	public void crashesDirectTest() throws IOException {

		Integer crashes = f3dataRetriever.getCrashes();

		logger.info("HLT number of crashes is " + crashes);
		assertNotNull(crashes);
	}
	
	/**
	 * test of dispatching of number of HLT crashes
	 */
	@Test
	public void crashesDispatchTest() {
		
		Integer crashes = daq.getHltInfo().getCrashes();
		assertNotNull(crashes);

	}
	
	@Test
	public void cpuLoadDirectTest() {

		Float cpuLoad = f3dataRetriever.getCpuLoad();
		
		logger.info("HLT CPU load is " + cpuLoad);
		assertNotNull(cpuLoad);

	}

	/** checks that a cpu load was dispatched to the DAQ object */
	@Test
	public void cpuLoadDispatchTest() {
		
		Float cpuLoad = daq.getHltInfo().getCpuLoad();
		assertNotNull(cpuLoad);
		
	}
}
