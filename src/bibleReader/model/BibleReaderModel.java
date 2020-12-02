package bibleReader.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model of the Bible Reader. It stores the Bibles and has methods for
 * searching for verses based on words or references.
 * 
 * @author cusack
 * @author Implemented by Trevor Palmatier, March, 2, 2020
 */
public class BibleReaderModel implements MultiBibleModel {

	private ArrayList<Bible> bibles;
	private ArrayList<String> versions;
	private ArrayList<Concordance> concordances;
	private boolean versionsIsSorted;
	public static final String number = "\\s*(\\d+)\\s*";
	public static Pattern quote = Pattern.compile("\"([^\"]*\\w+[^\"]+)\"");
	public static Pattern bookPattern = Pattern.compile("\\s*((?:1|2|3|I|II|III)\\s*\\w+|(?:\\s*[a-zA-Z]+)+)\\s*(.*)");
	public static Pattern cvcvPattern = Pattern.compile(number + ":" + number + "-" + number + ":" + number);
	public static Pattern ccPattern = Pattern.compile(number + "-" + number);
	public static Pattern cvPattern = Pattern.compile(number + ":" + number);
	public static Pattern cvvPattern = Pattern.compile(number + ":" + number + "-" + number);
	public static Pattern cPattern = Pattern.compile(number);
	public static Pattern ccvPattern = Pattern.compile(number + "-" + number + ":" + number);

	/**
	 * Default constructor. Instantiates the key fields.
	 */
	public BibleReaderModel() {
		bibles = new ArrayList<Bible>();
		versions = new ArrayList<String>();
		concordances = new ArrayList<Concordance>();
		versionsIsSorted = true;
	}

	// TODO come back and check how we are handling version and version sorting.
	@Override
	public String[] getVersions() {
		if (!versionsIsSorted) {
			Collections.sort(versions);
			versionsIsSorted = true;
		}
		return versions.toArray(new String[versions.size()]);
	}

	@Override
	public int getNumberOfVersions() {
		return bibles.size();
	}

	@Override
	public void addBible(Bible bible) {
		if (bible != null) {
			versionsIsSorted = false;
			bibles.add(bible);
			versions.add(bible.getVersion());
			concordances.add(new Concordance(bible));
		}
	}

	@Override
	public Bible getBible(String version) {
		for (Bible bible : bibles) {
			if (bible.getVersion().equals(version))
				return bible;
		}
		return null;
	}

	@Override
	public ArrayList<Reference> getReferencesContaining(String words) {
		TreeSet<Reference> refsContaining = new TreeSet<Reference>();
		for(int i = 0; i< bibles.size(); i++) {
			refsContaining.addAll(bibles.get(i).getReferencesContaining(words));
		}
		return new ArrayList<Reference>(refsContaining);
	}

	@Override
	public VerseList getVerses(String version, ArrayList<Reference> references) {
		Bible bible = getBible(version);
		if (bible != null) {
			return bible.getVerses(references);
		}
		return null;
	}

	@Override
	public String getText(String version, Reference reference) {
		Bible bible = getBible(version);
		if (bible != null) {
			String text = bible.getVerseText(reference);
			if (text != null) {
				return text;
			}
		}
		return "";
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(String reference) {
		String theRest = null;
		String book = null;
		int chapter1, chapter2, verse1, verse2;

		Matcher m = bookPattern.matcher(reference);

		if (m.matches()) {

			book = m.group(1);
			theRest = m.group(2);
			BookOfBible bookOfBible = BookOfBible.getBookOfBible(book);

			if (theRest.length() == 0) {
				return getBookReferences(bookOfBible);
			} else if ((m = cvcvPattern.matcher(theRest)).matches()) {
				chapter1 = Integer.parseInt(m.group(1));
				verse1 = Integer.parseInt(m.group(2));
				chapter2 = Integer.parseInt(m.group(3));
				verse2 = Integer.parseInt(m.group(4));
				return getPassageReferences(bookOfBible, chapter1, verse1, chapter2, verse2);

			} else if ((m = ccPattern.matcher(theRest)).matches()) {
				chapter1 = Integer.parseInt(m.group(1));
				chapter2 = Integer.parseInt(m.group(2));
				return getChapterReferences(bookOfBible, chapter1, chapter2);

			} else if ((m = cvPattern.matcher(theRest)).matches()) {
				chapter1 = Integer.parseInt(m.group(1));
				verse1 = Integer.parseInt(m.group(2));
				return getVerseReferences(bookOfBible, chapter1, verse1);
			} else if ((m = cvvPattern.matcher(theRest)).matches()) {
				chapter1 = Integer.parseInt(m.group(1));
				verse1 = Integer.parseInt(m.group(2));
				verse2 = Integer.parseInt(m.group(3));
				return getPassageReferences(bookOfBible, chapter1, verse1, verse2);
			} else if ((m = cPattern.matcher(theRest)).matches()) {
				chapter1 = Integer.parseInt(m.group(1));
				return getChapterReferences(bookOfBible, chapter1);
			} else if ((m = ccvPattern.matcher(theRest)).matches()) {
				chapter1 = Integer.parseInt(m.group(1));
				chapter2 = Integer.parseInt(m.group(2));
				verse2 = Integer.parseInt(m.group(3));
				return getPassageReferences(bookOfBible, chapter1, 1, chapter2, verse2);
			}
		}
		return new ArrayList<Reference>();
	}

	@Override
	public ArrayList<Reference> getVerseReferences(BookOfBible book, int chapter, int verse) {
		ArrayList<Reference> reference = new ArrayList<Reference>();
		Reference ref = new Reference(book, chapter, verse);
		for (int i = 0; i < bibles.size(); i++) {
			if (bibles.get(i).getVerse(ref) != null) {
				reference.add(ref);
				return reference;
			}
		}
		return reference;
	}

	@Override
	public ArrayList<Reference> getPassageReferences(Reference startVerse, Reference endVerse) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (int i=0; i<bibles.size(); i++) {
			references.addAll(bibles.get(i).getReferencesInclusive(startVerse, endVerse));
		}
		return new ArrayList<Reference>(references);
	}

	@Override
	public ArrayList<Reference> getBookReferences(BookOfBible book) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (int i=0; i<bibles.size(); i++) {
			references.addAll(bibles.get(i).getReferencesForBook(book));
		}
		return new ArrayList<Reference>(references);
	}

	@Override
	public ArrayList<Reference> getChapterReferences(BookOfBible book, int chapter) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (int i=0; i<bibles.size(); i++) {
			references.addAll(bibles.get(i).getReferencesForChapter(book, chapter));
		}
		ArrayList<Reference> result = new ArrayList<Reference>(references);
		return result;
	}

	@Override
	public ArrayList<Reference> getChapterReferences(BookOfBible book, int chapter1, int chapter2) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (int i = 0; i < bibles.size(); i++) {
			references.addAll(bibles.get(i).getReferencesForChapters(book, chapter1, chapter2));
		}
		return new ArrayList<Reference>(references);
	}

	@Override
	public ArrayList<Reference> getPassageReferences(BookOfBible book, int chapter, int verse1, int verse2) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (int i = 0; i < bibles.size(); i++) {
			references.addAll(bibles.get(i).getReferencesForPassage(book, chapter, verse1, verse2));
		}
		return new ArrayList<Reference>(references);
	}

	@Override
	public ArrayList<Reference> getPassageReferences(BookOfBible book, int chapter1, int verse1, int chapter2,
			int verse2) {
		TreeSet<Reference> references = new TreeSet<Reference>();
		for (int i = 0; i < bibles.size(); i++) {
			references.addAll(bibles.get(i).getReferencesForPassage(book, chapter1, verse1, chapter2, verse2));
		}
		return new ArrayList<Reference>(references);
	}

	// ------------------------------------------------------------------
	// These are the better searching methods.
	//
	@Override
	public ArrayList<Reference> getReferencesContainingWord(String word) {
		if (word != null) {
			if (!word.equals("")) {
				String wordClean = word.trim();
				TreeSet<Reference> references = new TreeSet<Reference>();
				for (Concordance concordance : concordances) {
					references.addAll(concordance.getReferencesContaining(wordClean));
				}
				return new ArrayList<Reference>(references);
			}
		}
		return new ArrayList<Reference>();
	}

	@Override
	public ArrayList<Reference> getReferencesContainingAllWords(String words) {
		if (words != null) {
			ArrayList<String> wordsList = cleanInput(words);
			TreeSet<Reference> references = new TreeSet<Reference>();
			for (Concordance concordance : concordances) {
				references.addAll(concordance.getReferencesContainingAll(wordsList));
			}
			return new ArrayList<Reference>(references);
		}
		return new ArrayList<Reference>();
	}

	@Override
	public ArrayList<Reference> getReferencesContainingAllWordsAndPhrases(String words) {
		if (words != null) {
			TreeSet<Reference> results = new TreeSet<Reference>();
			ArrayList<Reference> references = getReferencesContainingAllWords(words);

			if (references.size() > 0) {
				Matcher quoteMatcher = quote.matcher(words);
				ArrayList<String> quotes = new ArrayList<String>();
				while (quoteMatcher.find()) {
					quotes.add(cleanPhrase(quoteMatcher.group(1)));
				}

				if (quotes.size() != 0) {
					for (Reference reference : references) {
						for (Bible bible : bibles) {
							int phraseContained = 0;
							for (String phrase : quotes) {
								Pattern phrasePattern = Pattern.compile("\\b" + phrase.toLowerCase() + "\\b");
								Verse verse = bible.getVerse(reference);
								if(verse!=null) {
									Matcher phraseMatcher = phrasePattern.matcher(verse.getText().toLowerCase());
									if (phraseMatcher.find()) {
										phraseContained++;
									}
								}
							}
							if (phraseContained == quotes.size()) {
								results.add(reference);
								break;
							}
						}
					}
					return new ArrayList<Reference>(results);
				}
				return references;
			}
		}
		return new ArrayList<Reference>();
	}

	private ArrayList<String> cleanInput(String words) {
		if (words != null) {
			HashSet<String> wordsList = new HashSet<String>();
			words = words.replaceAll("\"", " ");
			words = words.replaceAll("(\\s{2,})", " ");
			String[] splitWords = words.trim().toLowerCase().split(" ");
			for (String word : splitWords) {
				word = word.replaceAll("('s|’s|'|,|;|\\?|!|:|\\.|\\(|\\))", "");
				wordsList.add(word);
			}
			return new ArrayList<String>(wordsList);
		}
		return new ArrayList<String>();
	}

	private String cleanPhrase(String phrase) {
		phrase = phrase.replaceAll("\\s{2,}", " ");
		phrase = phrase.trim();
		return phrase;
	}
}
