package bibleReader.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface)
 * @author Trevor Palmatier (provided the implementation)
 */
public class TreeMapBible implements Bible {

	// The Fields
	private String						version;
	private String title;
	private TreeMap<Reference, String>	theVerses;

	// Or replace the above with:
	// private TreeMap<Reference, Verse> theVerses;
	// Add more fields as necessary.

	/**
	 * Create a new Bible with the given verses.
	 * 
	 * @param version the version of the Bible (e.g. ESV, KJV, ASV, NIV).
	 * @param verses All of the verses of this version of the Bible.
	 */
	public TreeMapBible(VerseList verses) {
		theVerses = new TreeMap<Reference, String>();
		version = verses.getVersion();
		title = verses.getDescription();

		for (Verse verse : verses) {
			theVerses.put(verse.getReference(), verse.getText());
		}

		theVerses.put(new Reference(BookOfBible.Dummy, 1, 1), "");
	}

	@Override
	public int getNumberOfVerses() {
		return theVerses.size() - 1;
	}

	@Override
	public VerseList getAllVerses() {
		VerseList allVerses = new VerseList(version, title);
		Verse dummy = new Verse(new Reference(BookOfBible.Dummy, 1, 1), "");

		Set<Map.Entry<Reference, String>> mySet = theVerses.entrySet();

		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			if (!aVerse.equals(dummy))
				allVerses.add(aVerse);
		}
		return allVerses;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isValid(Reference ref) {
		if (ref != null) {
			if (ref.getBookOfBible() != BookOfBible.Dummy) {
				return theVerses.containsKey(ref);
			}
		}
		return false;
	}


	@Override
	public String getVerseText(Reference r) {
		if (r != null) {
			if (r.getBookOfBible() != BookOfBible.Dummy)
				return theVerses.get(r);
		}
		return null;
	}

	@Override
	public Verse getVerse(Reference r) {
		if (r != null) {
			if (r.getBookOfBible() != BookOfBible.Dummy) {
				String verseText = theVerses.get(r);
				if (verseText != null) {
					return new Verse(r, verseText);
				}
			}
		}
		return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		Reference ref = new Reference(book, chapter, verse);
		return getVerse(ref);
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		VerseList versesContaining = new VerseList(version, title);
		Set<Map.Entry<Reference, String>> mySet = theVerses.entrySet();

		if (phrase == null)
			return versesContaining;
		if (phrase.equals(""))
			return versesContaining;
		String phraseLower = phrase.toLowerCase();
		for (Map.Entry<Reference, String> element : mySet) {
			String verseText = element.getValue();
			if (verseText.toLowerCase().contains(phraseLower)) {
				versesContaining.add(new Verse(element.getKey(), verseText));
			}
		}
		return versesContaining;
	}

	@Override
	public ArrayList<Reference> getReferencesContaining(String phrase) {
		ArrayList<Reference> refsContaining = new ArrayList<Reference>();
		Set<Map.Entry<Reference, String>> mySet = theVerses.entrySet();

		if (phrase == null)
			return refsContaining;
		if (phrase.equals(""))
			return refsContaining;
		String phraseLower = phrase.toLowerCase();
		for (Map.Entry<Reference, String> element : mySet) {
			String verseText = element.getValue();
			if (verseText.toLowerCase().contains(phraseLower)) {
				refsContaining.add(element.getKey());
			}
		}
		return refsContaining;
	}

	@Override
	public VerseList getVerses(ArrayList<Reference> references) {
		VerseList versesContainingRef = new VerseList(version, "Arbitrary list of Verses");
		for (Reference ref : references) {
			versesContainingRef.add(getVerse(ref));
		}
		return versesContainingRef;
	}

	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		if (!isValid(new Reference(book, chapter, 1))) {
			return -1;
		}
		Reference key = new Reference(book, chapter + 1, 1);
		if (theVerses.get(key) != null) {
			Reference last = theVerses.lowerKey(key);
			return last.getVerse();
		}
		key = new Reference(BookOfBible.nextBook(book), 1, 1);
		if (theVerses.get(key) != null) {
			Reference last = theVerses.lowerKey(key);
			return last.getVerse();
		}
		return -1;
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		if (!isValid(new Reference(book, 1, 1))) {
			return -1;
		}
		Reference key = new Reference(BookOfBible.nextBook(book), 1, 1);
		if (theVerses.get(key) != null) {
			Reference last = theVerses.lowerKey(key);
			return last.getChapter();
		}
		return -1;
	}

	@Override
	public ArrayList<Reference> getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> references = new ArrayList<Reference>();

		if (firstVerse.compareTo(lastVerse) > 0) {
			return references;
		}

		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, true, lastVerse, true);

		Set<Map.Entry<Reference, String>> mySet = s.entrySet();

		for (Map.Entry<Reference, String> element : mySet) {
			Reference aReference = element.getKey();
			references.add(aReference);
		}
		return references;
	}

	@Override
	public ArrayList<Reference> getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> references = new ArrayList<Reference>();

		if (firstVerse.compareTo(lastVerse) > 0) {
			return references;
		}

		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);

		Set<Map.Entry<Reference, String>> mySet = s.entrySet();

		for (Map.Entry<Reference, String> element : mySet) {
			Reference aReference = element.getKey();
			references.add(aReference);
		}
		return references;
	}

	@Override
	public ArrayList<Reference> getReferencesForBook(BookOfBible book) {
		if (book != null) {
			return getReferencesExclusive(new Reference(book, 1, 1), new Reference(BookOfBible.nextBook(book), 1, 1));
		}
		return new ArrayList<Reference>();
	}

	@Override
	public ArrayList<Reference> getReferencesForChapter(BookOfBible book, int chapter) {
		if (book != null) {
			return getReferencesExclusive(new Reference(book, chapter, 1), new Reference(book, chapter + 1, 1));
		}
		return new ArrayList<Reference>();
	}

	@Override
	public ArrayList<Reference> getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		if (chapter1 <= chapter2 && book != null) {
			return getReferencesExclusive(new Reference(book, chapter1, 1), new Reference(book, chapter2 + 1, 1));
		}
		return new ArrayList<Reference>();
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		return getReferencesInclusive(new Reference(book, chapter, verse1), new Reference(book, chapter, verse2));
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		return getReferencesInclusive(new Reference(book, chapter1, verse1), new Reference(book, chapter2, verse2));
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		VerseList someVerses = new VerseList(getVersion(), firstVerse + "-" + lastVerse);

		if (firstVerse.compareTo(lastVerse) > 0) {
			return someVerses;
		}

		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, true, lastVerse, true);

		Set<Map.Entry<Reference, String>> mySet = s.entrySet();

		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			someVerses.add(aVerse);
		}
		return someVerses;
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		// Implementation of this method provided by Chuck Cusack.
		// This is provided so you have an example to help you get started
		// with the other methods.

		// We will store the resulting verses here. We copy the version from
		// this Bible and set the description to be the passage that was searched for.
		VerseList someVerses = new VerseList(getVersion(), firstVerse + "-" + lastVerse);

		// Make sure the references are in the correct order. If not, return an empty list.
		if (firstVerse.compareTo(lastVerse) > 0) {
			return someVerses;
		}
		// Return the portion of the TreeMap that contains the verses between
		// the first and the last, not including the last.
		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);

		// Get the entries from the map so we can iterate through them.
		Set<Map.Entry<Reference, String>> mySet = s.entrySet();

		// Iterate through the set and put the verses in the VerseList.
		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			someVerses.add(aVerse);
		}
		return someVerses;
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		if (book != null) {
			BookOfBible next = BookOfBible.nextBook(book);
			return getVersesExclusive(new Reference(book, 1, 1), new Reference(next, 1, 1));
		}
		return new VerseList(this.getVersion(), "");
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		return getVersesExclusive(new Reference(book, chapter, 1), new Reference(book, chapter + 1, 1));
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		return getVersesExclusive(new Reference(book, chapter1, 1), new Reference(book, chapter2 + 1, 1));
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		return getVersesInclusive(new Reference(book, chapter, verse1), new Reference(book, chapter, verse2));
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		return getVersesInclusive(new Reference(book, chapter1, verse1), new Reference(book, chapter2, verse2));
	}

}
