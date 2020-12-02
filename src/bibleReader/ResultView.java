package bibleReader;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import bibleReader.model.BibleReaderModel;
import bibleReader.model.NavigableResults;
import bibleReader.model.Reference;
import bibleReader.model.ResultType;

/**
 * The display panel for the Bible Reader.
 * 
 * @author cusack
 * @author Trevor Palmatier. Implemented 2/19/2020.
 */
public class ResultView extends JPanel {

	private NavigableResults navResults;
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	private JPanel statsPanel;
	private JPanel prevNextPanel;
	private JLabel pageCount;
	private JLabel statsLabel;
	private JButton next;
	private JButton previous;
	private BibleReaderModel model;

	/**
	 * Construct a new ResultView and set its model to myModel. It needs to model to
	 * look things up.
	 * 
	 * @param myModel The model this view will access to get information.
	 */
	public ResultView(BibleReaderModel myModel) {
		model = myModel;
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		next = new JButton("Next");
		next.setName("NextButton");
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePage(navResults.nextResults(), navResults.getType());
				previous.setEnabled(true);
				if (!navResults.hasNextResults()) {
					next.setEnabled(false);
				}

			}
		});

		previous = new JButton("Previous");
		previous.setName("PreviousButton");
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePage(navResults.previousResults(), navResults.getType());
				next.setEnabled(true);
				if (!navResults.hasPreviousResults()) {
					previous.setEnabled(false);
				}

			}
		});

		pageCount = new JLabel("");

		statsLabel = new JLabel();
		statsPanel = new JPanel();
		statsPanel.add(statsLabel);
		gridbag.setConstraints(statsPanel, c);

		editorPane = new JEditorPane("text/html", "");
		editorPane.setName("OutputEditorPane");
		editorPane.setEditable(false);
		scrollPane = new JScrollPane(editorPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		prevNextPanel = new JPanel(new FlowLayout());
		prevNextPanel.add(pageCount);
		prevNextPanel.add(previous);
		prevNextPanel.add(next);
		gridbag.setConstraints(prevNextPanel, c);

		c.anchor = GridBagConstraints.CENTER;
		c.weighty = 1.0;
		c.insets = new Insets(5, 5, 5, 5);

		gridbag.setConstraints(scrollPane, c);
		add(statsPanel);
		add(scrollPane);
		add(prevNextPanel);

		next.setEnabled(false);
		previous.setEnabled(false);
	}

	/**
	 * Finds all verses that contain the user input, tells the user how many verses
	 * that contain the input. Only moves to displaying the verses if there are
	 * verses to display.
	 * 
	 * @param input The input that the user wants to search for.
	 */
	public void doWordSearch(String input) {
		navResults = new NavigableResults(model.getReferencesContainingAllWordsAndPhrases(input), input,
				ResultType.SEARCH);

		statsLabel.setText(" There are " + navResults.size() + " verses containing the word(s): " + input);
		if (navResults.size() > 0) {
			pageCount.setText("Page " + navResults.getPageNumber() + " of " + navResults.getNumberPages());
			buttonsEnable();
			displaySearchResults(navResults.currentResults());
		} else {
			noResults();
		}
	}

	public void doPassageSearch(String input) {
		navResults = new NavigableResults(model.getReferencesForPassage(input), input, ResultType.PASSAGE);

		if (navResults.size() > 0) {
			statsLabel.setText(" There are " + navResults.size() + " verses in the passage " + input);
			pageCount.setText("Page " + navResults.getPageNumber() + " of " + navResults.getNumberPages());
			buttonsEnable();
			displayPassageResults(navResults.currentResults());
		} else {
			statsLabel.setText("Invalid Passage");
			noResults();
		}
	}

	/**
	 * Creates and shows the display for the given list of verses.
	 * 
	 * @param references The list of references to be displayed.
	 */
	private void displaySearchResults(ArrayList<Reference> references) {
		// Gets the all the text for the different versions.
		String[] versions = model.getVersions();
		// A place to build the html document that the JEditorPane will display.
		StringBuilder result = new StringBuilder();
		result.append("<table><tr><th valign='top' width='120'>Verse</th>");
		// Create a heading for each bible version in the model.
		for (String version : versions) {
			if (version != null) {
				result.append("<th>");
				result.append(version);
				result.append("</th>");
			}
		}
		// Display the verse under the appropriate heading.
		for (Reference reference : references) {

			result.append("</tr><tr><td>");
			result.append(reference.toString());
			result.append("</td>");

			for (String version : versions) {
				String text = model.getText(version, reference);
				if (!text.equals("")) {
					text = boldSearch(navResults.getQueryWords(), text);
					result.append("<td>");
					result.append(text);
					result.append("</td>");
				} else {
					result.append("<td>");
					result.append("</td>");
				}
			}
		}
		result.append("</tr></table>");
		editorPane.setText(result.toString());
		editorPane.setCaretPosition(0);
	}


	private void displayPassageResults(ArrayList<Reference> references) {
		// Gets the all the text for the different versions.
		String[] versions = model.getVersions();
		// A place to build the html document that the JEditorPane will display.
		StringBuilder result = new StringBuilder();
		String range = passageRange(references);
		result.append("<table><tr><th align='center' valign='top' colspan='3'>");
		result.append(range);

		result.append("<tr>");
		for (String version : versions) {
			result.append("<td align='center'>");
			result.append(version);
			result.append("</td>");
		}
		result.append("</tr>");

		// Display the verse under the appropriate heading.
		result.append("<tr>");
		for (String version : versions) {
			result.append("<td valign='top'>");
			for (Reference reference : references) {
				String text = model.getText(version, reference);
				if (!text.equals("")) {
					int verseNum = reference.getVerse();
					if (verseNum != 1) {
						result.append("<sup style='color:#00ccff;'>");
						result.append(verseNum);
						result.append("</sup>");
					} else {
						if (!reference.equals(references.get(0)))
							result.append("</p><p></p>");
						result.append("<p><b><sup style='font-size:130%; color:#00ccff;'>");
						result.append(reference.getChapter());
						result.append("</sup></b>");
					}
					result.append(text);
				}
			}
			result.append("</td>");
		}


		result.append("</tr></table>");
		editorPane.setText(result.toString());
		editorPane.setCaretPosition(0);
	}

	/**
	 * Displays "No Results"
	 */
	public void noResults() {
		editorPane.setText("<h>No Results</h>");
		next.setEnabled(false);
		previous.setEnabled(false);
		pageCount.setText("");
	}

	/**
	 * Refreshes the results with the previous search results.
	 */
	public void refreshResults() {
		if (navResults != null)
			updatePage(navResults.currentResults(), navResults.getType());
	}

	private void buttonsEnable() {
		if (navResults.hasNextResults()) {
			next.setEnabled(true);
		} else {
			next.setEnabled(false);
		}
		previous.setEnabled(false);
	}

	private void updatePage(ArrayList<Reference> references, ResultType type) {
		switch (type) {
		case SEARCH:
			displaySearchResults(references);
			break;
		case PASSAGE:
			displayPassageResults(references);
			break;
		case NONE:
			noResults();
			break;
		}
		pageCount.setText("Page " + navResults.getPageNumber() + " of " + navResults.getNumberPages());
	}

	private String boldSearch(ArrayList<String> words, String verse) {
		String output = verse;
		for (String word : words) {
			output = output.replaceAll("(?i)(?<!\\w)" + word + "(?!\\w)", "<b>$0</b>");
		}
		return output;
	}

	private String passageRange(ArrayList<Reference> references) {
		String result = "";
		Reference start = references.get(0);
		Reference end = references.get(references.size() - 1);
		if (start.getChapter() != end.getChapter()) {
			result = start.toString() + "-" + end.getChapter() + ":" + end.getVerse();
		} else {
			result = start.toString() + "-" + end.getVerse();
		}
		return result;
	}

}
