package bibleReader.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A list of verses. This is just a subclass of ArrayList with a few useful
 * fields tacked on. It is mostly for convenience. This is done as a subclass
 * rather than a wrapper class so that all of the ArrayList methods are
 * available.
 * 
 * @author cusack, January 21, 2013.
 */
public class VerseList implements Iterable<Verse> {
	ArrayList<Verse> list;
	private String version;
	private String description;

	/**
	 * Create a list of verses from the given version.
	 * 
	 * @param version
	 *            The version of the Bible the verses will be from.
	 * @param description
	 *            A description of the verses. Depending on context, this might
	 *            be the title of the Bible (e.g. "ASV"), the term(s) common to
	 *            all of the Verses (e.g. "God loved"), or the passage the
	 *            verses are from (e.g. "Ecclesiastes 3:1-8").
	 */
	public VerseList(String version, String description) {
		super();
		this.version = version;
		this.description = description;
		this.list = new ArrayList<Verse>();
	}

	/**
	 * Create a list of verses from the given version.
	 * 
	 * @param version
	 *            The version of the Bible the verses will be from.
	 * @param description
	 *            description A description of the verses. Depending on context,
	 *            this might be the title of the Bible (e.g. "ASV"), the term(s)
	 *            common to all of the Verses (e.g. "God loved"), or the passage
	 *            the verses are from (e.g. "Ecclesiastes 3:1-8").
	 * @param verses
	 *            the list of verses to place in the list.
	 */
	public VerseList(String version, String description, Collection<? extends Verse> verses) {
		list = new ArrayList<Verse>(verses);
		this.version = version;
		this.description = description;
	}

	/**
	 * Create a new VerseList that is identical to vl.
	 * 
	 * @param vl
	 */
	public VerseList(VerseList vl) {
		list = vl.copyVerses();
		this.version = vl.version;
		this.description = vl.description;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	// public ArrayList<Verse> getVerses() {
	// return list;
	// }

	public ArrayList<Verse> copyVerses() {
		return new ArrayList<Verse>(list);
	}

	// Delegate methods.

	public void add(int index, Verse element) {
		list.add(index, element);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public List<Verse> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public Verse get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean add(Verse e) {
		return list.add(e);
	}

	public int size() {
		return list.size();
	}

	/**
	 * Returns the index of the Verse with the given Reference, or -1 if there
	 * is no Verse with the given Reference. This method assumes that the
	 * VerseList is sorted by the Reference of the Verse objects and that no
	 * References are repeated.
	 * 
	 * @param ref
	 *            The reference being searched for.
	 * @return the index of the Verse with the given Reference if it is
	 *         contained in this VerseList and -1 otherwise.
	 */
	public int indexOfVerseWithReference(Reference ref) {
		return indexOfBinarySearch(ref, 0, list.size() - 1);
	}

	private int indexOfBinarySearch(Reference ref, int first, int last) {
		if (ref == null || last < first) {
			return -1;
		}
		int mid = (last + first) / 2;
		int comp = ref.compareTo(list.get(mid).getReference());
		if (comp == 0) {
			return mid;
		} else if (comp < 0) {
			return indexOfBinarySearch(ref, first, mid - 1);
		} else {
			return indexOfBinarySearch(ref, mid + 1, last);
		}
	}

	@Override
	public Iterator<Verse> iterator() {
		return list.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VerseList) {
			VerseList vl = (VerseList) o;
			if (description != null) {
				if (!description.equals(vl.description)) {
					return false;
				}
			} else {
				if (vl.description != null) {
					return false;
				}
			}

			if (version != null) {
				if (!version.equals(vl.version)) {
					return false;
				}
			} else {
				if (vl.version != null) {
					return false;
				}
			}

			if (list != null) {
				if (vl.list != null) {
					return list.equals(vl.list);
				} else {
					return false;
				}
			} else {
				if (vl.list != null) {
					return false;
				}
			}
		}
		return false;
	}

}
