package bibleReader.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface). Modified February 9, 2015.
 * @author Trevor Palmatier (provided the implementation). Implemented February
 *         13, 2020.
 */
public class ArrayListBible implements Bible {

	private VerseList verses;

	/**
	 * Create a new Bible with the given verses.
	 * 
	 * @param verses All of the verses of this version of the Bible.
	 */
	public ArrayListBible(VerseList verses) {
		this.verses = new VerseList(verses.getVersion(), verses.getDescription(), verses.copyVerses());
		this.verses.add(new Verse(BookOfBible.Dummy, 1, 1, ""));
	}

	@Override
	public int getNumberOfVerses() {
		return verses.size() - 1;
	}

	@Override
	public String getVersion() {
		return verses.getVersion();
	}

	@Override
	public String getTitle() {
		return verses.getDescription();
	}

	@Override
	public boolean isValid(Reference ref) {
		for (Verse compare : verses) {
			Reference compRef = compare.getReference();
			if (compRef.equals(ref) && !compRef.getBook().equals("Dummy"))
				return true;
		}
		return false;
	}

	@Override
	public String getVerseText(Reference r) {
		Verse verse = getVerse(r);
		if (verse != null)
			return verse.getText();
		return null;
	}

	@Override
	public Verse getVerse(Reference r) {
		for (Verse compare : verses) {
			if (compare.getReference().equals(r))
				return compare;
		}
		return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		return getVerse(new Reference(book, chapter, verse));
	}

	@Override
	public VerseList getAllVerses() {
		List<Verse> cleanList = verses.subList(0, verses.size() - 1);
		return new VerseList(verses.getVersion(), verses.getDescription(), cleanList);
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		VerseList versesContaining = new VerseList(verses.getVersion(), phrase);
		if (phrase == null)
			return versesContaining;
		if (phrase.equals(""))
			return versesContaining;
		String phraseLower = phrase.toLowerCase();
		for (Verse contains : verses) {
			if (contains.getText().toLowerCase().indexOf(phraseLower) != -1) {
				versesContaining.add(contains);
			}
		}
		return versesContaining;
	}

	@Override
	public ArrayList<Reference> getReferencesContaining(String phrase) {
		ArrayList<Reference> refsContaining = new ArrayList<Reference>();
		if (phrase == null)
			return refsContaining;
		if (phrase.equals(""))
			return refsContaining;
		String phraseLower = phrase.toLowerCase();
		for (Verse contains : verses) {
			if (contains.getText().toLowerCase().indexOf(phraseLower) != -1) {
				refsContaining.add(contains.getReference());
			}
		}
		return refsContaining;
	}

	@Override
	public VerseList getVerses(ArrayList<Reference> references) {
		VerseList versesContainingRef = new VerseList(verses.getVersion(), "Arbitrary list of Verses");
		for (Reference ref : references) {
			versesContainingRef.add(getVerse(ref));
		}
		return versesContainingRef;
	}

	// TODO Look the cleanliness of the next two. Do something else than the
	// previous current.
	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		if (book != null) {
			int searchIndex = verses.indexOfVerseWithReference(new Reference(book, chapter, 1));
			if (searchIndex != -1) {
				searchIndex++;
				Reference current;
				while (searchIndex < verses.size()) {
					current = verses.get(searchIndex).getReference();
					if (!current.getBook().equals(book.toString()) || current.getChapter() != chapter) {
						return verses.get(searchIndex - 1).getReference().getVerse();
					}
					searchIndex++;
				}
			}
		}
		return -1;
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		if (book != null) {
			int searchIndex = verses.indexOfVerseWithReference(new Reference(book, 1, 1));
			if (searchIndex != -1) {
				searchIndex++;
				while (searchIndex < verses.size()) {
					if (!verses.get(searchIndex).getReference().getBook().equals(book.toString())) {
						return verses.get(searchIndex - 1).getReference().getChapter();
					}
					searchIndex++;
				}
			}
		}

		return -1;
	}

	@Override
	public ArrayList<Reference> getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		if (firstVerse.compareTo(lastVerse) <= 0 || !lastVerse.equals(new Reference(BookOfBible.Dummy, 1, 1))) {
			int start = verses.indexOfVerseWithReference(firstVerse);
			int end = verses.indexOfVerseWithReference(lastVerse);
			if (start != -1 && end != -1) {
				for (int i = start; i <= end; i++) {
					references.add(verses.get(i).getReference());
				}
			}
		}
		return references;
	}

	@Override
	public ArrayList<Reference> getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		if (firstVerse.compareTo(lastVerse) <= 0
				|| lastVerse.compareTo(verses.get(verses.size() - 1).getReference()) > 0) {
			int start = verses.indexOfVerseWithReference(firstVerse);
			int end = verses.indexOfVerseWithReference(lastVerse);
			if (start != -1) {
				int index = start;
				while (index < verses.size() && end < 0) {
					if (lastVerse.compareTo(verses.get(index).getReference()) < 0) {
						end = index;
					}
					index++;
				}
			}
			if (start != -1 && end != -1) {
				for (int i = start; i < end; i++) {
					references.add(verses.get(i).getReference());
				}
			}
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
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2,
			int verse2) {
		return getReferencesInclusive(new Reference(book, chapter1, verse1), new Reference(book, chapter2, verse2));
	}

	// TODO Again, look to try and make things cleaner.
	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		VerseList outVerses = new VerseList(this.getVersion(), firstVerse.toString() + " - " + lastVerse.toString());
		if (firstVerse.compareTo(lastVerse) <= 0
				|| lastVerse.compareTo(verses.get(verses.size() - 1).getReference()) > 0) {
			int start = verses.indexOfVerseWithReference(firstVerse);
			int end = verses.indexOfVerseWithReference(lastVerse);
			if (start != -1 && end != -1) {
				for (int i = start; i <= end; i++) {
					outVerses.add(verses.get(i));
				}
			}
		}
		return outVerses;
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		VerseList outVerses = new VerseList(this.getVersion(), firstVerse.toString() + " - " + lastVerse.toString());
		if (firstVerse.compareTo(lastVerse) <= 0
				|| lastVerse.compareTo(verses.get(verses.size() - 1).getReference()) > 0) {
			int start = verses.indexOfVerseWithReference(firstVerse);
			int end = verses.indexOfVerseWithReference(lastVerse);
			if (start != -1) {
				int index = start;
				while (index < verses.size() && end < 0) {
					if (lastVerse.compareTo(verses.get(index).getReference()) < 0) {
						end = index;
					}
					index++;
				}
			}
			if (start != -1 && end != -1) {
				for (int i = start; i < end; i++) {
					outVerses.add(verses.get(i));
				}
			}
		}
		return outVerses;
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
