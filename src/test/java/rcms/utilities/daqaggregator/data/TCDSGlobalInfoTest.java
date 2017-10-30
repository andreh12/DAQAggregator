package rcms.utilities.daqaggregator.data;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import rcms.utilities.daqaggregator.persistence.PersistenceFormat;
import rcms.utilities.daqaggregator.persistence.StructureSerializer;

/**
 *
 * @author holzner
 */
public class TCDSGlobalInfoTest {
	
	
	/**
	 * method to load and deserialize a snapshot given a file name
	 *
	 * similar to FlowchartCaseTestBase.getSnapshot() but with
	 * a different base directory.
	 */
	public static DAQ getSnapshot(String fname) throws URISyntaxException {

		StructureSerializer serializer = new StructureSerializer();
    
		URL url = TCDSGlobalInfo.class.getResource(fname);

		File file = new File(url.toURI());

		return serializer.deserialize(file.getAbsolutePath());

	}
	
	/** tests reading the format before schema change 2017-10 */ 
	@Test
	public void testReadOldSerializedFormat() throws URISyntaxException, JsonMappingException, IOException {

		// check whether deserialization from old format works
		DAQ daq = getSnapshot("1503256889057.json.gz");
		
		TCDSGlobalInfo tcdsInfo = daq.getTcdsGlobalInfo();
		
		System.out.println(tcdsInfo.getTrg_rate_total() + tcdsInfo.getSup_trg_rate_total());
	}
	
	/** serializes the DAQ object to a JSON string */
	private String serializeToString(DAQ daq) throws JsonMappingException, IOException {
		// serialize in new format
		OutputStream out = new ByteArrayOutputStream();

		StructureSerializer serializer = new StructureSerializer();
		serializer.serialize(daq, out, PersistenceFormat.JSON);
		
		return out.toString();
	}
	
	/** tests whether serialization to the new format works.
	 * 
	 *  For convenience we assume that the previous test worked
	 *  and that we can read non-trivial data to be serialized
	 *  in new format from a file in old format. 
	 */
	@Test
	public void testNewSerialization() throws URISyntaxException, JsonMappingException, IOException {

		// read (old format) snapshot
		DAQ daq = getSnapshot("1503256889057.json.gz");

		// generate json in new format
		String serialized = serializeToString(daq);
		
		InputStream is = new ByteArrayInputStream(
						serialized.getBytes(StandardCharsets.UTF_8.name()));

		// parse the generated output as Json
		// and ensure we have a triggerRates group
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonDoc = mapper.readValue(is, JsonNode.class);
		
		JsonNode tcdsInfo = jsonDoc.get("tcdsGlobalInfo");
		assertNotNull(tcdsInfo);
		
		JsonNode triggerRates = tcdsInfo.get("triggerRates");
		assertNotNull(triggerRates);
		
		// check that all fields are in the new object and not in the old one
		for (String fieldName : new String[] {
			"sup_trg_rate_beamactive_tt_values",
			"sup_trg_rate_tt_values",
			"trg_rate_beamactive_tt_values",
			"trg_rate_tt_values",
			"sup_trg_rate_beamactive_total",
			"sup_trg_rate_total",
			"trg_rate_beamactive_total",
			"trg_rate_total",
			"sectionNumber_rates"
		}) {

			// check field does not exist as direct member of TCDSGlobalInfo
			//
			// TODO: for the moment this does not hold, needs to be fixed
			//
			// JsonNode oldField = tcdsInfo.get(fieldName);
			// assertNull(oldField);
			
			// check that field exists as member of new TCDSTriggerRates
			JsonNode newField = triggerRates.get(fieldName);
			assertNotNull(newField);
			
		} // loop over fields
	}
	
	/** tests whether we can read back the new format */
	@Test 
	public void testReadNewFormat() throws URISyntaxException, IOException {
		// read (old format) snapshot
		DAQ daq = getSnapshot("1503256889057.json.gz");

		// write new format json into a temporary file
		File ftemp = File.createTempFile("test", ".json");
		ftemp.deleteOnExit();
		
		FileOutputStream fout = new FileOutputStream(ftemp);
		
		StructureSerializer serializer = new StructureSerializer();
		serializer.serialize(daq, fout, PersistenceFormat.JSON);

		// make sure data is written to the file
		fout.close();
		
		// read the new format json back again
		DAQ daq2 = serializer.deserialize(ftemp.getAbsolutePath());
		
		// TODO: for the moment this causes a stack overflow
		// (as does .toString() but also on the original daq object.
		// assertEquals(daq, daq2);
	}
	
}
