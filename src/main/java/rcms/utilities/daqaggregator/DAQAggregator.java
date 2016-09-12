package rcms.utilities.daqaggregator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;

import rcms.common.db.DBConnectorException;
import rcms.utilities.daqaggregator.data.DAQ;
import rcms.utilities.daqaggregator.datasource.FileFlashlistRetriever;
import rcms.utilities.daqaggregator.datasource.Flashlist;
import rcms.utilities.daqaggregator.datasource.FlashlistRetriever;
import rcms.utilities.daqaggregator.datasource.HardwareConnector;
import rcms.utilities.daqaggregator.datasource.LASFlashlistRetriever;
import rcms.utilities.daqaggregator.datasource.MonitorManager;
import rcms.utilities.daqaggregator.datasource.SessionRetriever;
import rcms.utilities.daqaggregator.persistence.PersistenceFormat;
import rcms.utilities.daqaggregator.persistence.PersistorManager;
import rcms.utilities.hwcfg.HardwareConfigurationException;
import rcms.utilities.hwcfg.InvalidNodeTypeException;
import rcms.utilities.hwcfg.PathNotFoundException;

public class DAQAggregator {

	private static final Logger logger = Logger.getLogger(DAQAggregator.class);

	public static void main(String[] args) {
		try {
			String propertiesFile = "DAQAggregator.properties";
			if (args.length > 0)
				propertiesFile = args[0];
			logger.info("DAQAggregator started with properties file '" + propertiesFile + "'");

			Application.initialize(propertiesFile);

			/*
			 * Run mode from properties file
			 */
			RunMode runMode = RunMode.decode(Application.get().getProp().getProperty(Application.RUN_MODE));
			logger.info("Run mode:" + runMode);

			Pair<MonitorManager, PersistorManager> initializedManagers;

			initializedManagers = initialize(runMode);

			MonitorManager monitorManager = initializedManagers.getLeft();
			PersistorManager persistenceManager = initializedManagers.getRight();

			/*
			 * Persist mode from properties file
			 */
			PersistMode persistMode = PersistMode
					.decode(Application.get().getProp().getProperty(Application.PERSISTENCE_MODE));
			logger.info("Persist mode:" + persistMode);

			switch (runMode) {
			case FILE:
				try {
					int problems = 0;
					while (true) {
						boolean result = monitorAndPersist(monitorManager, persistenceManager, persistMode);
						if (!result) {
							problems++;
							logger.info("Unsuccessful iteration, already for " + problems + "time(s)");
						}
					}
				} catch (DAQException e) {
					if (e.getCode() == DAQExceptionCode.NoMoreFlashlistSourceFiles)
						logger.info("All flashlist files processed");
					else
						throw e;
				} catch (HardwareConfigurationException | PathNotFoundException | InvalidNodeTypeException e) {
					e.printStackTrace();
				}
				break;
			case RT:
				while (true) {
					try {
						monitorAndPersist(monitorManager, persistenceManager, persistMode);
					} catch (DAQException e) {
						logger.error(e.getMessage());
						logger.info("Going to sleep for 10 seconds before trying again...");
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} catch (Exception e) {
						logger.fatal("Fatal problem in RT loop, unknown problem, going to sleep for 2 minutes");
						e.printStackTrace();

						try {
							Thread.sleep(120000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}

		} catch (DBConnectorException | HardwareConfigurationException | IOException e1) {
			e1.printStackTrace();
		}
	}

	private static boolean monitorAndPersist(MonitorManager monitorManager, PersistorManager persistorManager,
			PersistMode persistMode)
			throws HardwareConfigurationException, PathNotFoundException, InvalidNodeTypeException {
		long start = System.currentTimeMillis();
		Triple<DAQ, Collection<Flashlist>, Boolean> a = monitorManager.getSystemSnapshot();
		if (a == null)
			return false;
		switch (persistMode) {
		case SNAPSHOT:
			persistorManager.persistSnapshot(a.getLeft());
			break;
		case FLASHLIST:
			persistorManager.persistFlashlists(a.getMiddle());
			break;
		case ALL:
			persistorManager.persistSnapshot(a.getLeft());
			persistorManager.persistFlashlists(a.getMiddle());
			break;
		}
		long end = System.currentTimeMillis();
		int resultTime = (int) (end - start);
		logger.info("Monitor & persist in " + resultTime + "ms");
		return true;

	}

	public static Pair<MonitorManager, PersistorManager> initialize(RunMode runMode)
			throws DBConnectorException, HardwareConfigurationException, IOException {

		/*
		 * Setup database
		 */
		HardwareConnector hardwareConnector = new HardwareConnector();
		String url = Application.get().getProp().getProperty(Application.PROPERTYNAME_HWCFGDB_DBURL);
		String host = Application.get().getProp().getProperty(Application.PROPERTYNAME_HWCFGDB_HOST);
		String port = Application.get().getProp().getProperty(Application.PROPERTYNAME_HWCFGDB_PORT);
		String sid = Application.get().getProp().getProperty(Application.PROPERTYNAME_HWCFGDB_SID);
		String user = Application.get().getProp().getProperty(Application.PROPERTYNAME_HWCFGDB_LOGIN);
		String passwd = Application.get().getProp().getProperty(Application.PROPERTYNAME_HWCFGDB_PWD);
		hardwareConnector.initialize(url, host, port, sid, user, passwd);

		/*
		 * Setup proxy
		 */
		ProxyManager.get().startProxy();

		/*
		 * Get the urls
		 */
		String[] lasURLs = Application.get().getProp().getProperty(Application.PROPERTYNAME_MONITOR_URLS).split(" +");
		List<String> urlList = Arrays.asList(lasURLs);
		String mainUrl = Application.get().getProp().getProperty(Application.PROPERTYNAME_SESSION_LASURL_GE).toString();

		/*
		 * Get persistence dirs
		 */
		String snapshotPersistenceDir = Application.get().getProp().getProperty(Application.PERSISTENCE_SNAPSHOT_DIR);
		String flashlistPersistenceDir = Application.get().getProp().getProperty(Application.PERSISTENCE_FLASHLIST_DIR);

		/*
		 * Format of snapshot from properties file
		 */
		PersistenceFormat flashlistFormat = PersistenceFormat
				.decode(Application.get().getProp().getProperty(Application.PERSISTENCE_FLASHLIST_FORMAT));
		PersistenceFormat snapshotFormat = PersistenceFormat
				.decode(Application.get().getProp().getProperty(Application.PERSISTENCE_SNAPSHOT_FORMAT));

		PersistorManager persistorManager = new PersistorManager(snapshotPersistenceDir, flashlistPersistenceDir,
				snapshotFormat, flashlistFormat);

		FlashlistRetriever flashlistRetriever = null;
		switch (runMode) {
		case RT:
			flashlistRetriever = new LASFlashlistRetriever(mainUrl, urlList);

			break;
		case FILE:
			FileFlashlistRetriever fileFlashlistRetriever = new FileFlashlistRetriever(flashlistPersistenceDir,
					flashlistFormat);
			flashlistRetriever = fileFlashlistRetriever;
			fileFlashlistRetriever.prepare();
			break;
		}

		String filter1 = Application.get().getProp().getProperty(Application.PROPERTYNAME_SESSION_L0FILTER1);
		String filter2 = Application.get().getProp().getProperty(Application.PROPERTYNAME_SESSION_L0FILTER2);

		SessionRetriever sessionRetriever = new SessionRetriever(filter1, filter2);
		MonitorManager monitorManager = new MonitorManager(flashlistRetriever, sessionRetriever, hardwareConnector);

		logger.info("DAQAggregator is initialized");

		return Pair.of(monitorManager, persistorManager);

	}

}
