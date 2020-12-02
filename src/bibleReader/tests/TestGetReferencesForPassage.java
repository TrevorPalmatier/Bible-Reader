package bibleReader.tests;

import static org.junit.Assert.assertArrayEquals;
// If you organize imports, the following import might be removed and you will
// not be able to find certain methods. If you can't find something, copy the
// commented import statement below, paste a copy, and remove the comments.
// Keep this commented one in case you organize imports multiple times.
//
// import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import bibleReader.BibleIO;
import bibleReader.model.ArrayListBible;
import bibleReader.model.Bible;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * Tests for the getReferencesForPassage method of the BibleReaderModel class.
 * These tests assume BibleIO is working an can read in the kjv.atv file.
 * 
 * @author Collin Reenders, February, 2020
 * @author Gabe Boonstra, February, 2020
 * @author Trevor Palmatier, February, 2020
 */
public class TestGetReferencesForPassage {
	@Rule
	public Timeout globalTimeout = new Timeout(1000);

	private static VerseList versesFromFile;
	private BibleReaderModel model;

	@BeforeClass
	public static void readFile() {
		// Our tests will be based on the KJV version for now.
		File file = new File("kjv.atv");
		// We read the file here so it isn't done before every test.
		versesFromFile = BibleIO.readBible(file);
	}

	@Before
	public void setUp() throws Exception {
		// Make a shallow copy of the verses.
		ArrayList<Verse> copyOfList = versesFromFile.copyVerses();
		// Now make a copy of the VerseList
		VerseList copyOfVerseList = new VerseList(versesFromFile.getVersion(), versesFromFile.getDescription(),
				copyOfList);

		Bible testBible = new ArrayListBible(copyOfVerseList);
		model = new BibleReaderModel();
		model.addBible(testBible);
	}

	public VerseList getVerseForReference(String reference) {
		ArrayList<Reference> list = model.getReferencesForPassage(reference);
		VerseList results = model.getVerses("KJV", list);
		return results;
	}

	@Test
	public void testSingleVerse() {
		VerseList actualVerses = getVerseForReference("John 3 : 16");
		// Since we are comparing the verse directly rather than through
		// assertArrayEquals, we must check that there is the expected amount of verses
		// as well.
		assertEquals(1, actualVerses.size());
		assertEquals(versesFromFile.get(26136), actualVerses.get(0));

		actualVerses = getVerseForReference("Gen 1:1");
		assertEquals(1, actualVerses.size());
		assertEquals(versesFromFile.get(0), actualVerses.get(0));

		actualVerses = getVerseForReference("Revelation 22:21");
		assertEquals(1, actualVerses.size());
		assertEquals(versesFromFile.get(31101), actualVerses.get(0));

	}

	@Test
	public void testVersesFromSingleChapter() {
		VerseList actual = getVerseForReference("Ecclesiastes 3 : 1 - 8");
		List<Verse> expected = versesFromFile.subList(17360, 17368);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Joshua 24:28-33");
		expected = versesFromFile.subList(6504, 6510);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Psalm 23:1-6");
		expected = versesFromFile.subList(14236, 14242);
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

	@Test
	public void testWholeChapters() {
		VerseList actual = getVerseForReference("Song of Solomon 3");
		List<Verse> expected = versesFromFile.subList(17572, 17583);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Revelation 22");
		expected = versesFromFile.subList(31081, 31102);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("1 Tim 2-4");
		expected = versesFromFile.subList(29717, 29764);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("1 John 2 - 3");
		expected = versesFromFile.subList(30551, 30604);
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

	@Test
	public void testVersesFromMultipleChapters() {
		VerseList actual = getVerseForReference("Isa 52:13 - 53:12");
		List<Verse> expected = versesFromFile.subList(18709, 18724);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Mal 2:6-4:6");
		expected = versesFromFile.subList(23109, 23145);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("1 Tim 2 : 1-4 : 3");
		expected = versesFromFile.subList(29717, 29751);
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

	@Test
	public void testWholeBook() {
		VerseList actual = getVerseForReference("1 Kings");
		List<Verse> expected = versesFromFile.subList(8718, 9534);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Philemon");
		expected = versesFromFile.subList(29939, 29964);
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

	@Test
	public void testOddSyntax() {
		VerseList actual = getVerseForReference("Ephesians 5-6:9");
		List<Verse> expected = versesFromFile.subList(29305, 29347);
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Hebrews 11 - 12 : 2");
		expected = versesFromFile.subList(30173, 30215);
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

	@Test
	public void testInvaidBookChapterVerse() {
		VerseList actual = getVerseForReference("Jude 2");
		// All of these inputs should return empty lists.
		VerseList expected = new VerseList("", "");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Herman 2");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("John 3:163");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Mal 13:6-24:7");
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

	@Test
	public void testInvalidSyntax() {
		VerseList actual = getVerseForReference("1Tim 3-2");
		// All of these inputs should return empty lists.
		VerseList expected = new VerseList("", "");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Deut :2-3");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Josh 6:4- :6");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Ruth : - :");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("2 Sam : 4-7 :");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Matthew 1: -");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("Ephesians 5:2,4");
		assertArrayEquals(expected.toArray(), actual.toArray());

		actual = getVerseForReference("John 3;16");
		assertArrayEquals(expected.toArray(), actual.toArray());

	}

}
