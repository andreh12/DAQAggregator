package rcms.utilities.daqaggregator.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rcms.utilities.daqaggregator.data.BU;
import rcms.utilities.daqaggregator.data.BUSummary;
import rcms.utilities.daqaggregator.data.DAQ;
import rcms.utilities.daqaggregator.data.FED;
import rcms.utilities.daqaggregator.data.FEDBuilder;
import rcms.utilities.daqaggregator.data.FEDBuilderSummary;
import rcms.utilities.daqaggregator.data.FMM;
import rcms.utilities.daqaggregator.data.FMMApplication;
import rcms.utilities.daqaggregator.data.FRL;
import rcms.utilities.daqaggregator.data.FRLPc;
import rcms.utilities.daqaggregator.data.RU;
import rcms.utilities.daqaggregator.data.SubFEDBuilder;
import rcms.utilities.daqaggregator.data.SubSystem;
import rcms.utilities.daqaggregator.data.TCDSGlobalInfo;
import rcms.utilities.daqaggregator.data.TTCPartition;
import rcms.utilities.daqaggregator.data.mixin.BUMixIn;
import rcms.utilities.daqaggregator.data.mixin.BUSummaryMixIn;
import rcms.utilities.daqaggregator.data.mixin.DAQMixIn;
import rcms.utilities.daqaggregator.data.mixin.FEDBuilderMixIn;
import rcms.utilities.daqaggregator.data.mixin.FEDBuilderSummaryMixIn;
import rcms.utilities.daqaggregator.data.mixin.FEDMixIn;
import rcms.utilities.daqaggregator.data.mixin.FMMApplicationMixIn;
import rcms.utilities.daqaggregator.data.mixin.FMMMixIn;
import rcms.utilities.daqaggregator.data.mixin.FRLMixIn;
import rcms.utilities.daqaggregator.data.mixin.FRLPcMixIn;
import rcms.utilities.daqaggregator.data.mixin.RUMixIn;
import rcms.utilities.daqaggregator.data.mixin.SubFEDBuilderMixIn;
import rcms.utilities.daqaggregator.data.mixin.SubSystemMixIn;
import rcms.utilities.daqaggregator.data.mixin.TCDSGlobalInfoMixIn;
import rcms.utilities.daqaggregator.data.mixin.TTCPartitionMixIn;
import rcms.utilities.daqaggregator.datasource.Flashlist;
import rcms.utilities.daqaggregator.datasource.FlashlistMixin;

/**
 * Persists DAQ structure in multiple formats format
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class StructureSerializer {

	private static final Logger logger = Logger.getLogger(StructureSerializer.class);

	/**
	 * Serialize DAQ snapshot object with given format
	 * 
	 * @param daqSnapshot
	 *            DAQ snapshot object to be serialized
	 * @param outputStream
	 *            output stream to which daqSnapshot will be serialized
	 * @param format
	 *            format in which daqSnapshot will be serialized
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public void serialize(DAQ daqSnapshot, OutputStream outputStream, PersistenceFormat format)
			throws JsonGenerationException, JsonMappingException, IOException {

		ObjectMapper mapper = format.getMapper();
		boolean prettyPrint = format.isPrettyPrint();

		OutputStream finalOutputStream = outputStream;

		switch (format) {

		case JSON:
			addMixins(mapper);
			break;
		case ZIPPED:
			GZIPOutputStream gzipOS = new GZIPOutputStream(outputStream);
			finalOutputStream = gzipOS;
			addMixins(mapper);
			break;
		case SMILE:
			addMixins(mapper);
			break;
		case SMILE_ZIPPED:
			GZIPOutputStream smileGzipOs = new GZIPOutputStream(outputStream);
			finalOutputStream = smileGzipOs;
			addMixins(mapper);
			break;
		case JSONREFPREFIXED:
			addRefMixins(mapper);
			break;
		case JSONUGLY:
			addMixins(mapper);
			break;
		case JSONREFPREFIXEDUGLY:
			addRefMixins(mapper);
			break;
		default:
			logger.warn("Format of snapshot not available");
		}

		if (prettyPrint)
			mapper.writerWithDefaultPrettyPrinter().writeValue(finalOutputStream, daqSnapshot);
		else
			mapper.writeValue(finalOutputStream, daqSnapshot);

	}

	public Flashlist deserializeFlashlist(File file, PersistenceFormat format) {
		ObjectMapper mapper = format.getMapper();

		mapper.addMixIn(Flashlist.class, FlashlistMixin.class);

		Flashlist flashlist = null;

		ObjectInputStream in = null;
		FileInputStream fileIn = null;
		try {
			logger.debug("Deserialize file: " + file.getAbsolutePath());
			flashlist = mapper.readValue(file, Flashlist.class);
			return flashlist;
		} catch (IOException i) {
			logger.error("File incompatible: " + file.getAbsolutePath(), i);
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e1) {
				}
			if (fileIn != null)
				try {
					fileIn.close();
				} catch (IOException e1) {
				}
		}
	}

	public DAQ deserialize(String filepath) {
		PersistenceFormat format = PersistenceFormat.decodeFromFilename(filepath);
		return deserialize(filepath, format);
	}

	public DAQ deserialize(String filepath, PersistenceFormat format) {

		ObjectMapper mapper = format.getMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			InputStream finalInputStream = new FileInputStream(filepath);

			switch (format) {

			case JSON:
				addMixins(mapper);
				break;
			case ZIPPED:
				GZIPInputStream gzis = new GZIPInputStream(finalInputStream);
				finalInputStream = gzis;
				addMixins(mapper);
				break;
			case SMILE:
				addMixins(mapper);
				break;
			case SMILE_ZIPPED:
				GZIPInputStream gzisSmile = new GZIPInputStream(finalInputStream);
				finalInputStream = gzisSmile;
				addMixins(mapper);
				break;
			case JSONREFPREFIXED:
				addRefMixins(mapper);
				break;
			case JSONUGLY:
				addMixins(mapper);
				break;
			case JSONREFPREFIXEDUGLY:
				addRefMixins(mapper);
				break;
			default:
				logger.warn("Format of snapshot not available");
			}

			DAQ daq = null;

			try {
				daq = mapper.readValue(finalInputStream, DAQ.class);
				return daq;
			} catch (IOException i) {
				logger.error("File incompatible: " + filepath, i);
				return null;
			} finally {
				if (finalInputStream != null)
					try {
						finalInputStream.close();
					} catch (IOException e1) {
					}
			}

		} catch (IOException e) {
			logger.error("Problem accessing file: " + filepath, e);
			return null;
		}

	}

	/**
	 * Add mixin objects to object mapper
	 * 
	 * @param objectMapper
	 */
	private void addRefMixins(ObjectMapper objectMapper) {
		objectMapper.addMixIn(BU.class, rcms.utilities.daqaggregator.data.mixin.ref.BUMixIn.class);
		objectMapper.addMixIn(BUSummary.class, rcms.utilities.daqaggregator.data.mixin.ref.BUSummaryMixIn.class);
		objectMapper.addMixIn(DAQ.class, rcms.utilities.daqaggregator.data.mixin.ref.DAQMixIn.class);
		objectMapper.addMixIn(FED.class, rcms.utilities.daqaggregator.data.mixin.ref.FEDMixIn.class);
		objectMapper.addMixIn(FEDBuilder.class, rcms.utilities.daqaggregator.data.mixin.ref.FEDBuilderMixIn.class);
		objectMapper.addMixIn(FEDBuilderSummary.class,
				rcms.utilities.daqaggregator.data.mixin.ref.FEDBuilderSummaryMixIn.class);
		objectMapper.addMixIn(FMM.class, rcms.utilities.daqaggregator.data.mixin.ref.FMMMixIn.class);
		objectMapper.addMixIn(FMMApplication.class,
				rcms.utilities.daqaggregator.data.mixin.ref.FMMApplicationMixIn.class);
		objectMapper.addMixIn(FRL.class, rcms.utilities.daqaggregator.data.mixin.ref.FRLMixIn.class);
		objectMapper.addMixIn(FRLPc.class, rcms.utilities.daqaggregator.data.mixin.ref.FRLPcMixIn.class);
		objectMapper.addMixIn(RU.class, rcms.utilities.daqaggregator.data.mixin.ref.RUMixIn.class);
		objectMapper.addMixIn(SubFEDBuilder.class,
				rcms.utilities.daqaggregator.data.mixin.ref.SubFEDBuilderMixIn.class);
		objectMapper.addMixIn(SubSystem.class, rcms.utilities.daqaggregator.data.mixin.ref.SubSystemMixIn.class);
		objectMapper.addMixIn(TTCPartition.class, rcms.utilities.daqaggregator.data.mixin.ref.TTCPartitionMixIn.class);
	}

	/**
	 * Add mixin objects to object mapper
	 * 
	 * @param objectMapper
	 */
	private void addMixins(ObjectMapper objectMapper) {
		objectMapper.addMixIn(BU.class, BUMixIn.class);
		objectMapper.addMixIn(BUSummary.class, BUSummaryMixIn.class);
		objectMapper.addMixIn(DAQ.class, DAQMixIn.class);
		objectMapper.addMixIn(FED.class, FEDMixIn.class);
		objectMapper.addMixIn(FEDBuilder.class, FEDBuilderMixIn.class);
		objectMapper.addMixIn(FEDBuilderSummary.class, FEDBuilderSummaryMixIn.class);
		objectMapper.addMixIn(FMM.class, FMMMixIn.class);
		objectMapper.addMixIn(FMMApplication.class, FMMApplicationMixIn.class);
		objectMapper.addMixIn(FRL.class, FRLMixIn.class);
		objectMapper.addMixIn(FRLPc.class, FRLPcMixIn.class);
		objectMapper.addMixIn(RU.class, RUMixIn.class);
		objectMapper.addMixIn(SubFEDBuilder.class, SubFEDBuilderMixIn.class);
		objectMapper.addMixIn(SubSystem.class, SubSystemMixIn.class);
		objectMapper.addMixIn(TTCPartition.class, TTCPartitionMixIn.class);
		objectMapper.addMixIn(TCDSGlobalInfo.class, TCDSGlobalInfoMixIn.class);
	}

}
