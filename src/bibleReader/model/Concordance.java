package bibleReader.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Concordance is a class which implements a concordance for a Bible. In other
 * words, it allows the easy lookup of all references which contain a given
 * word.
 * 
 * @author Chuck Cusack, March 2013 (Provided the interface)
 * @author Trevor Palmatier, April 2020 (Provided the implementation details)
 */
public class Concordance {
	// Add fields here.  (I actually only needed one field.)
	private HashMap<String, ArrayList<Reference>> concordance;

	/**
	 * Construct a concordance for the given Bible.
	 */
	public Concordance(Bible bible) {
		concordance = new HashMap<String, ArrayList<Reference>>();
		VerseList verses = bible.getAllVerses();
		for (Verse verse : verses) {
			Reference currentReference = verse.getReference();
			for (String word : extractWords(verse.getText())) {
				ArrayList<Reference> currentList = concordance.get(word);
				if (currentList != null) {
					if (!currentReference.equals(currentList.get(currentList.size() - 1))) {
						currentList.add(currentReference);
					}
				} else {
					currentList = new ArrayList<Reference>();
					currentList.add(currentReference);
					concordance.put(word, currentList);
				}
			}
		}

	}

	/**
	 * Return the list of references to verses that contain the word 'word' (ignoring case) in the version of the Bible
	 * that this concordance was created with.
	 * 
	 * @param word a single word (no spaces, etc.)
	 * @return the list of References of verses from this version that contain the word, or an empty list if no verses
	 *         contain the word.
	 */
	public ArrayList<Reference> getReferencesContaining(String word) {
		ArrayList<Reference> results = concordance.get(word.toLowerCase());
		if (results != null)
			return new ArrayList<Reference>(results);
		return new ArrayList<Reference>();
	}

	/**
	 * Given an array of Strings, where each element of the array is expected to be a single word (with no spaces, etc.,
	 * but ignoring case), return a ArrayList<Reference> containing all of the verses that contain <i>all of the words</i>.
	 * 
	 * @param words A list of words.
	 * @return An ArrayList<Reference> containing references to all of the verses that contain all of the given words, or an
	 *         empty list if
	 */
	public ArrayList<Reference> getReferencesContainingAll(ArrayList<String> words) {
		HashSet<String> wordsClean = new HashSet<String>();
		for (String word : words) {
			wordsClean.add(word.toLowerCase());
		}
		Iterator<String> itWords = wordsClean.iterator();
		if (wordsClean.size() == 1) {
			ArrayList<Reference> list = concordance.get(itWords.next());
			if (list != null) {
				return list;
			}
			return new ArrayList<Reference>();
		}
		ArrayList<ArrayList<Reference>> refLists = new ArrayList<ArrayList<Reference>>();
		while (itWords.hasNext()) {
			ArrayList<Reference> temp = concordance.get(itWords.next());
			if (temp == null) {
				return new ArrayList<Reference>();
			}
			refLists.add(temp);
		}
		if (refLists.size() > 0) {
			refLists.sort(new Comparator<ArrayList<Reference>>() {
				@Override
				public int compare(ArrayList<Reference> o1, ArrayList<Reference> o2) {
					return o1.size() - o2.size();
				}
			});
			ArrayList<Reference> results = new ArrayList<Reference>(refLists.get(0));
			for (int i = 1; i < refLists.size(); i++) {
				HashSet<Reference> compare = new HashSet<Reference>(refLists.get(i));
				results.retainAll(compare);
			}

			return results;
		}

		return new ArrayList<Reference>();
	}



	public static ArrayList<String> extractWords(String text) {
		text = text.toLowerCase();
		// Removes a few HTML tags (relevant to ESV) and 's at end of words.
		// Replaces them with space so words around them don’t get squished
		// together. Notice the two types of apostrophe—each is used in a
		// different version.
		text = text.replaceAll("(<sup>[,\\w]*?</sup>|'s|’s|&#\\w*;)", " ");

		// Remove commas. This should help us match numbers better.
		text = text.replaceAll(",", "");

		String[] words = text.split("\\W+");

		ArrayList<String> toRet = new ArrayList<String>(Arrays.asList(words));

		toRet.remove("");

		return toRet;

	}
}
