package bibleReader.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import bibleReader.BibleIO;
import bibleReader.model.ArrayListBible;
import bibleReader.model.Bible;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.BookOfBible;
import bibleReader.model.MultiBibleModel;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * Tests for the methods of Bible that are related to passage lookup methods.
 * Many of the tests perform the same lookup twice--once using a method that
 * takes in details about the passage (e.g. Reference, Book, Chapter, etc.), and
 * once using the method that takes a String. This should help with debugging
 * since if the former passes but the latter fails, then it narrows down where
 * the problem is.
 * 
 * @author Chuck Cusack, February 12, 2013. Modified February 2015.
 */
public class Stage07_3ModelPassageTest {
	@Rule
	public Timeout globalTimeout = new Timeout(100);
	
	protected MultiBibleModel model;
	protected static VerseList verseList;
	protected static String version = "kjv.atv";
	protected static ArrayList<Reference> allRefs;

	@BeforeClass
	public static void readFile() {
		File file = new File(version);
		verseList = BibleIO.readBible(file);
		allRefs = new ArrayList<Reference>();
		for (Verse v : verseList) {
			allRefs.add(v.getReference());
		}
	}

	@Before
	public void setUp() throws Exception {
		model = new BibleReaderModel();
		// Make a copy of the VerseList
		VerseList copyOfVerseList = new VerseList(verseList);
		Bible testBible = new ArrayListBible(copyOfVerseList);
		model.addBible(testBible);
	}

	@Test(timeout=50)
	public void testSingleVerse_String() {
		ArrayList<Reference> results = model.getReferencesForPassage("John 3:16");
		assertEquals(1, results.size());
		assertEquals(allRefs.get(26136), results.get(0));

		results = model.getReferencesForPassage("Genesis 1: 1");
		assertEquals(1, results.size());
		assertEquals(allRefs.get(0), results.get(0));

		results = model.getReferencesForPassage("Rev 22 : 21");
		assertEquals(1, results.size());
		assertEquals(allRefs.get(allRefs.size() - 1), results.get(0));
	}

	@Test(timeout=50)
	public void testSingleVerse() {
		ArrayList<Reference> results = model.getVerseReferences(BookOfBible.John, 3, 16);
		assertEquals(1, results.size());
		assertEquals(allRefs.get(26136), results.get(0));

		results = model.getVerseReferences(BookOfBible.Genesis, 1, 1);
		assertEquals(1, results.size());
		assertEquals(allRefs.get(0), results.get(0));

		results = model.getVerseReferences(BookOfBible.Revelation, 22, 21);
		assertEquals(1, results.size());
		assertEquals(allRefs.get(allRefs.size() - 1), results.get(0));

	}

	@Test(timeout=50)
	public void testGetBook_String() {
		ArrayList<Reference> results = model.getReferencesForPassage("Philemon");
		testPassageResults(new Reference(BookOfBible.Philemon, 1, 1), new Reference(BookOfBible.Philemon, 1, 25),
				results);

		results = model.getReferencesForPassage("1 Kings");
		testPassageResults(new Reference(BookOfBible.Kings1, 1, 1), new Reference(BookOfBible.Kings1, 22, 53), results);

		results = model.getReferencesForPassage("Revelation");
		testPassageResults(new Reference(BookOfBible.Revelation, 1, 1), new Reference(BookOfBible.Revelation, 22, 21),
				results);
	}

	@Test(timeout=50)
	public void testGetBook() {
		ArrayList<Reference> results = model.getBookReferences(BookOfBible.Philemon);
		testPassageResults(new Reference(BookOfBible.Philemon, 1, 1), new Reference(BookOfBible.Philemon, 1, 25),
				results);

		results = model.getBookReferences(BookOfBible.Kings1);
		testPassageResults(new Reference(BookOfBible.Kings1, 1, 1), new Reference(BookOfBible.Kings1, 22, 53), results);

		// Revelation could cause problems, depending on implementation.
		results = model.getBookReferences(BookOfBible.Revelation);
		testPassageResults(new Reference(BookOfBible.Revelation, 1, 1), new Reference(BookOfBible.Revelation, 22, 21),
				results);
	}

	@Test(timeout=50)
	public void testGetChapter_String() {
		ArrayList<Reference> results = model.getReferencesForPassage("Song of Solomon 3");
		testPassageResults(new Reference(BookOfBible.SongOfSolomon, 3, 1),
				new Reference(BookOfBible.SongOfSolomon, 3, 11), results);

		results = model.getReferencesForPassage("Revelation 22");
		testPassageResults(new Reference(BookOfBible.Revelation, 22, 1), new Reference(BookOfBible.Revelation, 22, 21),
				results);
	}

	@Test(timeout=50)
	public void testGetChapter() {
		ArrayList<Reference> results = model.getChapterReferences(BookOfBible.SongOfSolomon, 3);
		testPassageResults(new Reference(BookOfBible.SongOfSolomon, 3, 1),
				new Reference(BookOfBible.SongOfSolomon, 3, 11), results);

		// Revelation 22 might mess up since it is at the end of the Bible.
		results = model.getChapterReferences(BookOfBible.Revelation, 22);
		testPassageResults(new Reference(BookOfBible.Revelation, 22, 1), new Reference(BookOfBible.Revelation, 22, 21),
				results);
	}

	@Test(timeout=50)
	public void testGetChapters_String() {
		ArrayList<Reference> results = model.getReferencesForPassage("1 John 2-3");
		testPassageResults(new Reference(BookOfBible.John1, 2, 1), new Reference(BookOfBible.John1, 3, 24), results);

		results = model.getReferencesForPassage("1 Timothy 2-4");
		testPassageResults(new Reference(BookOfBible.Timothy1, 2, 1), new Reference(BookOfBible.Timothy1, 4, 16),
				results);
	}

	@Test(timeout=50)
	public void testGetChapters() {
		ArrayList<Reference> results = model.getChapterReferences(BookOfBible.John1, 2, 3);
		testPassageResults(new Reference(BookOfBible.John1, 2, 1), new Reference(BookOfBible.John1, 3, 24), results);

		results = model.getChapterReferences(BookOfBible.Timothy1, 2, 4);
		testPassageResults(new Reference(BookOfBible.Timothy1, 2, 1), new Reference(BookOfBible.Timothy1, 4, 16),
				results);
	}

	// Why is this one timing out on the autograder at 50?!
	// CAC, 1/3/19
	@Test(timeout=75)
	public void testGetPassage_Ref_Ref_String() {
		ArrayList<Reference> results = model.getReferencesForPassage("2 Kings 3:4-11:2");
		Reference startRef = new Reference(BookOfBible.Kings2, 3, 4);
		Reference endRef = new Reference(BookOfBible.Kings2, 11, 2);
		testPassageResults(startRef, endRef, results);

		results = model.getReferencesForPassage("Isa 52 :  13  -53:12 ");
		startRef = new Reference(BookOfBible.Isaiah, 52, 13);
		endRef = new Reference(BookOfBible.Isaiah, 53, 12);
		testPassageResults(startRef, endRef, results);

		results = model.getReferencesForPassage("Josh 24:28-33");
		startRef = new Reference(BookOfBible.Joshua, 24, 28);
		endRef = new Reference(BookOfBible.Joshua, 24, 33);
		testPassageResults(startRef, endRef, results);
	}

	@Test(timeout=50)
	public void testGetPassage_Ref_Ref() {
		Reference startRef = new Reference(BookOfBible.Kings2, 3, 4);
		Reference endRef = new Reference(BookOfBible.Kings2, 11, 2);
		ArrayList<Reference> results = model.getPassageReferences(startRef, endRef);
		testPassageResults(startRef, endRef, results);

		startRef = new Reference(BookOfBible.Isaiah, 52, 13);
		endRef = new Reference(BookOfBible.Isaiah, 53, 12);
		results = model.getPassageReferences(startRef, endRef);
		testPassageResults(startRef, endRef, results);

		startRef = new Reference(BookOfBible.Joshua, 24, 28);
		endRef = new Reference(BookOfBible.Joshua, 24, 33);
		results = model.getPassageReferences(startRef, endRef);
		testPassageResults(startRef, endRef, results);
	}

	@Test(timeout=50)
	public void testGetPassage_CVCV_String() {
		ArrayList<Reference> results = model.getReferencesForPassage("2 Kings 3:4-11:2");
		Reference startRef = new Reference(BookOfBible.Kings2, 3, 4);
		Reference endRef = new Reference(BookOfBible.Kings2, 11, 2);
		testPassageResults(startRef, endRef, results);

		results = model.getReferencesForPassage("Isa 52 :  13  -53:12 ");
		startRef = new Reference(BookOfBible.Isaiah, 52, 13);
		endRef = new Reference(BookOfBible.Isaiah, 53, 12);
		testPassageResults(startRef, endRef, results);
	}

	@Test(timeout=50)
	public void testGetPassage_CVCV() {
		ArrayList<Reference> results = model.getPassageReferences(BookOfBible.Kings2, 3, 4, 11, 2);
		Reference startRef = new Reference(BookOfBible.Kings2, 3, 4);
		Reference endRef = new Reference(BookOfBible.Kings2, 11, 2);
		testPassageResults(startRef, endRef, results);

		results = model.getPassageReferences(BookOfBible.Isaiah, 52, 13, 53, 12);
		startRef = new Reference(BookOfBible.Isaiah, 52, 13);
		endRef = new Reference(BookOfBible.Isaiah, 53, 12);
		testPassageResults(startRef, endRef, results);
	}

	@Test(timeout=50)
	public void testGetPassage_CVV_String() {
		ArrayList<Reference> results = model.getReferencesForPassage(" Eccl 3:1-8");
		Reference startRef = new Reference(BookOfBible.Ecclesiastes, 3, 1);
		Reference endRef = new Reference(BookOfBible.Ecclesiastes, 3, 8);
		testPassageResults(startRef, endRef, results);

		results = model.getReferencesForPassage("Josh 24:28-33");
		startRef = new Reference(BookOfBible.Joshua, 24, 28);
		endRef = new Reference(BookOfBible.Joshua, 24, 33);
		testPassageResults(startRef, endRef, results);
	}

	@Test(timeout=50)
	public void testGetPassage_CVV() {
		Reference startRef = new Reference(BookOfBible.Ecclesiastes, 3, 1);
		Reference endRef = new Reference(BookOfBible.Ecclesiastes, 3, 8);

		ArrayList<Reference> results = model.getPassageReferences(BookOfBible.Ecclesiastes, 3, 1, 8);
		testPassageResults(startRef, endRef, results);

		startRef = new Reference(BookOfBible.Joshua, 24, 28);
		endRef = new Reference(BookOfBible.Joshua, 24, 33);
		results = model.getPassageReferences(BookOfBible.Joshua, 24, 28, 33);
		testPassageResults(startRef, endRef, results);
	}

	@Test(timeout=50)
	public void testGetPassage_CCV_String() {
		ArrayList<Reference> results = model.getReferencesForPassage(" Ephesians 5-6:9");
		testPassageResults(new Reference(BookOfBible.Ephesians, 5, 1), new Reference(BookOfBible.Ephesians, 6, 9),
				results);

		results = model.getReferencesForPassage("Hebrews 11-12:2");
		testPassageResults(new Reference(BookOfBible.Hebrews, 11, 1), new Reference(BookOfBible.Hebrews, 12, 2),
				results);
	}

	@Test(timeout=50)
	public void testGetPassage_CCV() {
		ArrayList<Reference> results = model.getPassageReferences(BookOfBible.Ephesians, 5, 1, 6, 9);
		testPassageResults(new Reference(BookOfBible.Ephesians, 5, 1), new Reference(BookOfBible.Ephesians, 6, 9),
				results);

		results = model.getPassageReferences(BookOfBible.Hebrews, 11, 1, 12, 2);
		testPassageResults(new Reference(BookOfBible.Hebrews, 11, 1), new Reference(BookOfBible.Hebrews, 12, 2),
				results);
	}

	@Test(timeout=50)
	public void testInvalidPassages_String() {
		String[] invalidPassages = { "Jude 2", "John 3:163", "Herman", "Herman 3", "Herman 3-5", "Herman 2:3-7",
				"Herman 3:4-12:45", "Herman 3-4:7", "1 Hess 3-4:7", "1 Timothy 3-2", "2 Peter 3:7-3",
				"2 Kings 13:4-11:2", "Deut :2-3", "Josh 6:4- :6", "Ruth : - :", "2 Sam : 4-7 :", "Ephesians 5:2,4",
				"John 3;16", "Herman 13-4:7", "1 Hess 34, 35", "Isaiah 53:12-52:13" };
		for (String s : invalidPassages) {
			ArrayList<Reference> results = model.getReferencesForPassage(s);
			assertEquals(s + " should have returned 0 results but didn't", 0, results.size());
		}
	}

	@Test(timeout=50)
	public void testInvalidPassages() {

		// Now let's try invalid ones that call the other get methods
		ArrayList<Reference> results = model.getVerseReferences(BookOfBible.Revelation, 23, 2);
		assertEquals(0, results.size());
		results = model.getBookReferences(null);
		assertEquals(0, results.size());
		results = model.getChapterReferences(BookOfBible.John3, -4);
		assertEquals(0, results.size());
		results = model.getPassageReferences(new Reference(BookOfBible.Revelation, 1, 1),
				new Reference(BookOfBible.Genesis, 1, 1));
		assertEquals(0, results.size());
		results = model.getPassageReferences(new Reference(BookOfBible.Ephesians, 3, 2),
				new Reference(BookOfBible.Ephesians, 2, 2));
		assertEquals(0, results.size());
		results = model.getPassageReferences(new Reference(BookOfBible.Ephesians, 13, 2),
				new Reference(BookOfBible.Ephesians, 2, 2));
		assertEquals(0, results.size());
		results = model.getChapterReferences(BookOfBible.Samuel2, 12, 10);
		assertEquals(0, results.size());
		results = model.getPassageReferences(BookOfBible.Galatians, 2, 10, 4);
		assertEquals(0, results.size());
		results = model.getPassageReferences(BookOfBible.Romans, 4, 3, 3, 6);
		assertEquals(0, results.size());
	}

	/**
	 * The same as the previous method except that this one checks
	 * ArrayList<Reference>s.
	 * 
	 * @param firstVerse
	 * @param lastVerse
	 * @param actualResults
	 */
	public void testPassageResults(Reference firstVerse, Reference lastVerse, ArrayList<Reference> actualResults) {
		int i = 0;
		while (!allRefs.get(i).equals(firstVerse)) {
			i++;
		}
		int firstIndex = i;
		while (!allRefs.get(i).equals(lastVerse)) {
			i++;
		}
		ArrayList<Reference> expected = new ArrayList<Reference>();
		int lastIndex = i + 1; // It does not include the last index, so add
								// one.
		for (int j = firstIndex; j < lastIndex; j++) {
			expected.add(allRefs.get(j));
		}
		assertArrayEquals(expected.toArray(), actualResults.toArray());
	}
}
