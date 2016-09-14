package rcms.utilities.daqaggregator.datasource;

import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import rcms.utilities.daqaggregator.persistence.PersistenceFormat;

public class FileFlashlistRetrieverTest {

	private static final String TEST_FLASHLISTS_DIR = "src/test/resources/test-flashlists/json/";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void noFlashlistTest() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage(is(FileFlashlistRetriever.EXCEPTION_NO_FLASHLISTS_AVAILABLE));
		FileFlashlistRetriever retriever = new FileFlashlistRetriever(TEST_FLASHLISTS_DIR, PersistenceFormat.JSON);
		retriever.retrieveAllFlashlists();
	}

	@Test
	public void noMoreFlashlistsTest() throws IOException {
		FileFlashlistRetriever retriever = new FileFlashlistRetriever(TEST_FLASHLISTS_DIR, PersistenceFormat.JSON);
		retriever.prepare();
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());

		thrown.expect(RuntimeException.class);
		thrown.expectMessage(is(FileFlashlistRetriever.EXCEPTION_NO_FLASHLISTS_AVAILABLE));
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
	}

	@Test
	public void retrievalTimeTest() throws IOException {
		FileFlashlistRetriever retriever = new FileFlashlistRetriever(TEST_FLASHLISTS_DIR, PersistenceFormat.JSON);
		retriever.prepare();
		Map<FlashlistType, Flashlist> result = retriever.retrieveAllFlashlists();
		Assert.assertEquals(21, result.size());
		Date d1 = result.get(FlashlistType.BU).getRetrievalDate();
		Assert.assertEquals(1472743072594L, d1.getTime());

		result = retriever.retrieveAllFlashlists();
		Assert.assertEquals(21, result.size());
		Date d2 = result.get(FlashlistType.BU).getRetrievalDate();
		Assert.assertEquals(1472743080834L, d2.getTime());

		result = retriever.retrieveAllFlashlists();
		Assert.assertEquals(21, result.size());
		Date d3 = result.get(FlashlistType.BU).getRetrievalDate();
		Assert.assertEquals(1472743088565L, d3.getTime());

		result = retriever.retrieveAllFlashlists();
		Assert.assertEquals(21, result.size());
		Date d4 = result.get(FlashlistType.BU).getRetrievalDate();
		Assert.assertEquals(1472743097506L, d4.getTime());

		result = retriever.retrieveAllFlashlists();
		Assert.assertEquals(21, result.size());
		Date d5 = result.get(FlashlistType.BU).getRetrievalDate();
		Assert.assertEquals(1472743106576L, d5.getTime());

	}

	@Test
	public void singleFlashlistRetrieval() throws IOException {
		FileFlashlistRetriever retriever = new FileFlashlistRetriever(TEST_FLASHLISTS_DIR, PersistenceFormat.JSON);
		retriever.prepare();
		Assert.assertEquals(FlashlistType.LEVEL_ZERO_FM_STATIC,
				retriever.retrieveFlashlist(FlashlistType.LEVEL_ZERO_FM_STATIC).getFlashlistType());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(FlashlistType.LEVEL_ZERO_FM_STATIC,
				retriever.retrieveFlashlist(FlashlistType.LEVEL_ZERO_FM_STATIC).getFlashlistType());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(FlashlistType.LEVEL_ZERO_FM_STATIC,
				retriever.retrieveFlashlist(FlashlistType.LEVEL_ZERO_FM_STATIC).getFlashlistType());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(FlashlistType.LEVEL_ZERO_FM_STATIC,
				retriever.retrieveFlashlist(FlashlistType.LEVEL_ZERO_FM_STATIC).getFlashlistType());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());
		Assert.assertEquals(FlashlistType.LEVEL_ZERO_FM_STATIC,
				retriever.retrieveFlashlist(FlashlistType.LEVEL_ZERO_FM_STATIC).getFlashlistType());
		Assert.assertEquals(21, retriever.retrieveAllFlashlists().size());

	}
}