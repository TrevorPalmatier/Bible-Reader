package bibleReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import bibleReader.model.Bible;
import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * A utility class that has useful methods to read/write Bibles and Verses.
 * 
 * @author cusack
 * @author Trevor Palmatier. Modified March 5, 2020.
 */
public class BibleIO {

	/**
	 * Read in a file and create a Bible object from it and return it.
	 * 
	 * @param bibleFile
	 * @return
	 */
	// This method is complete, but it won't work until the methods it uses are
	// implemented.
	public static VerseList readBible(File bibleFile) { // Get the extension of
														// the file
		String name = bibleFile.getName();
		String extension = name.substring(name.lastIndexOf('.') + 1, name.length());

		// Call the read method based on the file type.
		if ("atv".equals(extension.toLowerCase())) {
			return readATV(bibleFile);
		} else if ("xmv".equals(extension.toLowerCase())) {
			return readXMV(bibleFile);
		} else {
			return null;
		}
	}

	/**
	 * Read in a Bible that is saved in the "ATV" format. The format is described
	 * below.
	 * 
	 * The first line of the file will always be the Bible version in the form of,
	 * "Abbreviation: Full title". Every line afterward corresponds to exactly one
	 * verse of the Bible in the form of, "book Abbreviation@Chapter:Verse@Verse
	 * text".
	 * 
	 * @param bibleFile The file containing a Bible with .atv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if there
	 *         was an error reading the file.
	 */
	private static VerseList readATV(File bibleFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(bibleFile));

			// sets the defaults for the title and description.
			String abrv = "unknown";
			String desc = "";
			String currentLine;

			// sets the title and description if there is data in the file for it.
			String[] bibleTitleAndDesc = reader.readLine().split(": ");
			if (bibleTitleAndDesc.length == 2) {
				abrv = bibleTitleAndDesc[0];
				desc = bibleTitleAndDesc[1];
			} else if (!bibleTitleAndDesc[0].isEmpty()) {
				abrv = bibleTitleAndDesc[0];
			}
			// creates the list of verses in the bible and reads in the verses from the
			// file.
			VerseList readVerses = new VerseList(abrv, desc);

			currentLine = reader.readLine();
			while (currentLine != null) {
				// splits the book from the chapter and verse from the text.
				String[] parts = currentLine.split("@");
				// verifies that the data was read in as expected.
				if (parts.length <= 1) {
					reader.close();
					return null;
				}
				// verifies the book abbreviation is valid.
				BookOfBible book = BookOfBible.getBookOfBible(parts[0]);
				if (book == null) {
					reader.close();
					return null;
				}
				// splits chapter and verse and verifies that it was successful.
				String[] chapAndVerse = parts[1].split(":");
				if (chapAndVerse.length <= 1) {
					reader.close();
					return null;
				}
				int chap = Integer.parseInt(chapAndVerse[0]);
				int verse = Integer.parseInt(chapAndVerse[1]);

				readVerses.add(new Verse(book, chap, verse, parts[2].trim()));
				currentLine = reader.readLine();
			}
			reader.close();
			return readVerses;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} catch (NumberFormatException e2) {
			e2.printStackTrace();
			return null;
		}

	}

	/**
	 * Read in the Bible that is stored in the XMV format.
	 * 
	 * The format of XMV consists of every line starting with a tag. The tags are
	 * held within <> and contains the key words Version, Book, Chapter, Verse, or
	 * Title. The first line will always contain the Version tag. Version is
	 * followed by a space, a abbreviation for the version, a colon, then the full
	 * title of the version, all within the tag (Ex. <Version Abvr: Full Title>).
	 * Book is followed by a space and the book name, a comma, then any other
	 * information about the book, all contained in the tag (Ex. <Book Title, other
	 * info>). Chapter is followed by a space and then the chapter number, all
	 * within the tag (Ex. <Chapter 1>). Verse is followed by a space and then the
	 * verse number, all within the tag. The verse text follows the verse tag
	 * without a space separating it (Ex. <Verse 1>Text of verse). Title is contain
	 * in a tag by itself followed by the title of Chapter denoted in the line
	 * before it. 
	 * (Ex. 
	 * <Chapter 1> 
	 * <Title>Title of Chapter 1.)
	 * 
	 * 
	 * @param bibleFile The file containing a Bible with .xmv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if there
	 *         was an error reading the file.
	 */
	private static VerseList readXMV(File bibleFile) {
		try (BufferedReader reader = new BufferedReader(new FileReader(bibleFile))) {

			String abrv = "unknown";
			String description = "";
			String currentLine;
			BookOfBible currentBook = null;
			int currentChapter = 0;

			String[] versionAndDescription = reader.readLine().split(": ", 2);
			if (versionAndDescription.length == 2) {
				String[] beginingAndAbrv = versionAndDescription[0].split(" ");
				if (beginingAndAbrv.length == 2) {
					abrv = beginingAndAbrv[1];
				}
				description = versionAndDescription[1];
			}

			VerseList readVerses = new VerseList(abrv, description);

			currentLine = reader.readLine();
			while (currentLine != null) {
				if (currentLine.startsWith("<Book")) {
					String[] bookParts = currentLine.split(",", 2);
					if (bookParts.length > 1) {
						String book = bookParts[0].substring(6);
						currentBook = BookOfBible.getBookOfBible(book);
						if (currentBook == null)
							return null;
					} else {
						return null;
					}
				} else if (currentLine.startsWith("<Chapter")) {
					String[] chapterParts = currentLine.split(">");
					currentChapter = Integer.parseInt(chapterParts[0].substring(9));
				} else if (currentLine.startsWith("<Verse")) {
					String[] verseParts = currentLine.split(">", 2);
					if (verseParts.length > 1 && currentBook != null && currentChapter != 0) {
						int currentVerse = Integer.parseInt(verseParts[0].substring(7));
						readVerses.add(new Verse(currentBook, currentChapter, currentVerse, verseParts[1].trim()));
					} else {
						return null;
					}

				}
				currentLine = reader.readLine();
			}

			return readVerses;

		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} catch (NumberFormatException e2) {
			e2.printStackTrace();
			return null;
		}

	}

	/**
	 * Write out the Bible in the ATV format.
	 * 
	 * @param file  The file that the Bible should be written to.
	 * @param bible The Bible that will be written to the file.
	 */
	public static void writeBibleATV(File file, Bible bible) {
		String description = bible.getVersion() + ": " + bible.getTitle();
		VerseList verses = bible.getAllVerses();
		writeVersesATV(file, description, verses);
	}

	/**
	 * Write out the given verses in the ATV format, using the description as the
	 * first line of the file.
	 * 
	 * @param file        The file that the Bible should be written to.
	 * @param description The contents that will be placed on the first line of the
	 *                    file, formatted appropriately.
	 * @param verses      The verses that will be written to the file.
	 */
	public static void writeVersesATV(File file, String description, VerseList verses) {
		try (PrintWriter pWriter = new PrintWriter(new FileWriter(file))) {
			pWriter.print(description);
			pWriter.print("\n");

			for (Verse verse : verses) {
				Reference ref = verse.getReference();
				pWriter.print(ref.getBook());
				pWriter.print("@");
				pWriter.print(ref.getChapter());
				pWriter.print(":");
				pWriter.print(ref.getVerse());
				pWriter.print("@");
				pWriter.print(verse.getText());
				pWriter.print("\n");
			}

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Write the string out to the given file. It is presumed that the string is an
	 * HTML rendering of some verses, but really it can be anything.
	 * 
	 * @param file
	 * @param text
	 */
	public static void writeText(File file, String text) {
		try (PrintWriter pWriter = new PrintWriter(new FileWriter(file))) {

			pWriter.write(text);

		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}
