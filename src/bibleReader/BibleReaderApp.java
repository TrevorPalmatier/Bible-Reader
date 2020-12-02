package bibleReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;

import bibleReader.model.ArrayListBible;
import bibleReader.model.Bible;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.VerseList;

/**
 * The main class for the Bible Reader Application.
 * 
 * @author cusack
 * @author Trevor Palmatier. Implemented 2/19/2020.
 */
public class BibleReaderApp extends JFrame {
	public static final int width = 800;
	public static final int height = 630;

	public static void main(String[] args) {
		new BibleReaderApp();
	}

	// Fields
	private BibleReaderModel model;
	private ResultView resultView;
	private JButton wordSearchButton;
	private JButton passageSearchButton;
	private JTextField searchInput;
	private JLabel searchLabel;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenuItem quitMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem aboutMenuItem;
	private JFileChooser fileChooser;

	/**
	 * Default constructor. Prepares the GUI to be set up and then displays the set
	 * up GUI.
	 *
	 */
	public BibleReaderApp() {
		model = new BibleReaderModel();
		File kjvFile = new File("kjv.atv");
		VerseList verses = BibleIO.readBible(kjvFile);

		Bible kjv = new ArrayListBible(verses);

		model.addBible(kjv);

		
		File esvFile = new File("esv.atv");
		VerseList esvVerse = BibleIO.readBible(esvFile);
		Bible esv = new ArrayListBible(esvVerse);
		model.addBible(esv);

		File asvFile = new File("asv.xmv");
		VerseList asvVerse = BibleIO.readBible(asvFile);
		Bible asv = new ArrayListBible(asvVerse);
		model.addBible(asv);

		setupLookAndFeel();

		resultView = new ResultView(model);

		Dimension minSize = new Dimension(width, height);
		setupGUI();
		pack();
		setSize(width, height);
		setMinimumSize(minSize);

		// So the application exits when you click the "x".
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Set up the main GUI.
	 */
	private void setupGUI() {
		setTitle("Bible Reader");

		menuBar = new JMenuBar();
		menuBar.setBorderPainted(true);

		fileMenu = new JMenu("File");

		helpMenu = new JMenu("Help");

		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		FileNameExtensionFilter filter = new FileNameExtensionFilter("ATV & XMV Bibles", "atv", "xmv");
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(getContentPane());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					VerseList bible = BibleIO.readBible(fileChooser.getSelectedFile());
					if (bible == null) {
						JOptionPane.showMessageDialog(null, "An error occured while adding the bible",
								"Failed to Add Bible", JOptionPane.ERROR_MESSAGE);
					} else {
						model.addBible(new ArrayListBible(bible));
						resultView.refreshResults();
					}
				} else if (returnVal == JFileChooser.ERROR_OPTION) {
					JOptionPane.showMessageDialog(null, "An Error Occured", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		String bio = "Written by: Trevor Palmatier\nAn app that allows you to search the bible for passages or for phrases.";

		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, bio, "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		fileMenu.add(openMenuItem);
		fileMenu.add(quitMenuItem);
		helpMenu.add(aboutMenuItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		this.setJMenuBar(menuBar);

		// The items that belong to the search GUI panel.
		wordSearchButton = new JButton("Search Keyword");
		wordSearchButton.setName("SearchButton");
		searchInput = new JTextField(30);
		searchInput.setName("InputTextField");
		searchLabel = new JLabel("Enter a Keyword or Passage:");

		passageSearchButton = new JButton("Search Passage");
		passageSearchButton.setName("PassageButton");
		passageSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resultView.doPassageSearch(searchInput.getText());
			}
		});

		ActionListener parseWordSearch = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resultView.doWordSearch(searchInput.getText());
			}
		};
		wordSearchButton.addActionListener(parseWordSearch);
		// searchInput.addActionListener(parseWordSearch);

		// Placing everything where it should go on the main content pane.
		Container contentsMain = this.getContentPane();

		JPanel searchPanel = new JPanel(new FlowLayout());

		contentsMain.add(searchPanel, BorderLayout.NORTH);
		contentsMain.add(resultView, BorderLayout.CENTER);

		searchPanel.add(searchLabel);
		searchPanel.add(searchInput);
		searchPanel.add(wordSearchButton);
		searchPanel.add(passageSearchButton);

	}

	private void setupLookAndFeel() {
		UIManager.put("control", new Color(240, 240, 255));
		UIManager.put("nimbusLightBackground", new Color(255, 250, 255));
		UIManager.put("nimbusFocus", new Color(163, 163, 194));
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// It will use the default look and feel.
		}
	}
	
}
