package bibleReader.tests;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import bibleReader.BibleIO;
import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.VerseList;

/**
 * Tests for the single-word exact searching.
 * 
 * @author Chuck Cusack, March, 2013
 */
public class Stage12_1ConcordanceCreationTest {
	@Rule
	public Timeout globalTimeout = new Timeout(1000);

	private static Bible	bible;

	@BeforeClass
	public static void readFilesAndCreateConcordance() {
			File file = new File("esv.atv");
			VerseList verses = BibleIO.readBible(file);
			bible = BibleFactory.createBible(verses);
	}

	@Before
	public void setUp() throws Exception {}

	@Test
	public void testConcordanceCreationTime() {
			BibleFactory.createConcordance(bible);
	}
}