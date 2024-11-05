package PL;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import BLL.IEditorBO;
import DTO.Documents;
import DTO.Pages;

public class EditorPO extends JFrame {

	private static final long serialVersionUID = 1L;
	private IEditorBO businessObj;
	private DefaultTableModel tableModel;
	private JPanel mainPanel, editPanel, transliterationPanel;
	private JTable fileTable;
	private JTextArea contentTextArea, transliteratedTextArea;
	private JButton nextButton, previousButton;
	private JLabel pageCountLabel;
	private Documents doc;
	private List<Pages> pages;
	private int currentPage = 1;
	private int totalPageCount = 0;

	public EditorPO(IEditorBO businessObj) {
		this.businessObj = businessObj;

		setTitle("Real Text Editor");
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new CardLayout());

		// Initialize and set up panels
		mainPanel = new JPanel(new BorderLayout());
		setupMainMenuPanel();
		editPanel = new JPanel(new BorderLayout());
		setupEditPanel();
		transliterationPanel = new JPanel(new BorderLayout());
		setupTransliterationPanel();

		// Adding all panels to the frame
		add(mainPanel, "MainMenu");
		add(editPanel, "EditDocument");
		add(transliterationPanel, "TransliterationView");

		setVisible(true);
	}

	private void setupMainMenuPanel() {
		tableModel = new DefaultTableModel(new Object[] { "File ID", "File Name", "Last Modified", "Date Created" },
				0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		fileTable = new JTable(tableModel);
		fileTable.getColumnModel().getColumn(0).setMinWidth(0);
		fileTable.getColumnModel().getColumn(0).setMaxWidth(0);
		fileTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane scroller = new JScrollPane(fileTable);

		JButton importFileButton = new JButton("Upload Files");
		JButton createFileButton = new JButton("Create New File");
		JButton deleteFileButton = new JButton("Delete File(s)");
		JButton viewFilesButton = new JButton("View Files");

		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
		buttonPanel.add(importFileButton);
		buttonPanel.add(createFileButton);
		buttonPanel.add(deleteFileButton);
		buttonPanel.add(viewFilesButton);

		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(scroller, BorderLayout.CENTER);

		fileTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					int selectedRow = fileTable.getSelectedRow();
					if (selectedRow != -1) {
						int fileId = (int) tableModel.getValueAt(selectedRow, 0);
						openEditPanel(fileId);
					}
				}
			}
		});

		importFileButton.addActionListener(this::importFiles);
		createFileButton.addActionListener(this::createFile);
		deleteFileButton.addActionListener(this::deleteSelectedFiles);
		viewFilesButton.addActionListener(e -> refreshFileList());
	}

	private void setupEditPanel() {
		contentTextArea = new JTextArea(10, 40);
		contentTextArea.setLineWrap(true);
		contentTextArea.setWrapStyleWord(true);
		contentTextArea.setEditable(true);

		JScrollPane contentScroller = new JScrollPane(contentTextArea);
		contentScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JButton saveFileButton = new JButton("Save File");
		JButton backButton = new JButton("Back to Menu");
		JButton transliterateButton = new JButton("Transliterate Content");

		nextButton = new JButton("Next Page");
		previousButton = new JButton("Previous Page");
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);

		pageCountLabel = new JLabel("Page 0 of 0");

		JPanel editButtonPanel = new JPanel(new FlowLayout());
		editButtonPanel.add(previousButton);
		editButtonPanel.add(pageCountLabel);
		editButtonPanel.add(nextButton);
		editButtonPanel.add(saveFileButton);
		editButtonPanel.add(backButton);
		editButtonPanel.add(transliterateButton);

		editPanel.add(contentScroller, BorderLayout.CENTER);
		editPanel.add(editButtonPanel, BorderLayout.SOUTH);

		nextButton.addActionListener(e -> nextPage());
		previousButton.addActionListener(e -> previousPage());
		saveFileButton.addActionListener(e -> saveFile());
		backButton.addActionListener(e -> {
			CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
			cardLayout.show(getContentPane(), "MainMenu");
			refreshFileList();
		});
		transliterateButton.addActionListener(e -> transliterateContent());
	}

	private void setupTransliterationPanel() {
		transliteratedTextArea = new JTextArea();
		transliteratedTextArea.setLineWrap(true);
		transliteratedTextArea.setWrapStyleWord(true);
		transliteratedTextArea.setEditable(false); // Set as non-editable

		JScrollPane transliterationScroller = new JScrollPane(transliteratedTextArea);
		transliterationScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JButton backToEditButton = new JButton("Back to Edit");

		backToEditButton.addActionListener(e -> {
			CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
			cardLayout.show(getContentPane(), "EditDocument");
		});

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(backToEditButton);

		transliterationPanel.add(transliterationScroller, BorderLayout.CENTER);
		transliterationPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void openEditPanel(int fileId) {
		currentPage = 1;
		doc = businessObj.getFile(fileId);
		pages = doc.getPages();
		totalPageCount = pages.size();

		loadPage(currentPage);

		CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
		cardLayout.show(getContentPane(), "EditDocument");
	}

	private void importFiles(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();
			if (selectedFiles.length > 0) {
				for (File selectedFile : selectedFiles) {
					String fileName = selectedFile.getName();
					boolean isImport = businessObj.importTextFiles(selectedFile, fileName);
					JOptionPane.showMessageDialog(null,
							isImport ? fileName + " uploaded successfully!" : fileName + " failed to upload!");
				}
				refreshFileList();
			}
		}
	}

	private void createFile(ActionEvent e) {
		String fileName = JOptionPane.showInputDialog("Enter file name:");
		String fileContent = JOptionPane.showInputDialog("Enter file content:");
		if (fileName != null) {
			boolean created = businessObj.createFile(fileName, fileContent);
			JOptionPane.showMessageDialog(null, created ? "File created successfully!" : "File creation failed!");
			refreshFileList();
		}
	}

	private void deleteSelectedFiles(ActionEvent e) {
		if (confirmAction("Do you want to delete the selected file?")) {
			int selectedRow = fileTable.getSelectedRow();
			if (selectedRow != -1) {
				int fileId = (int) tableModel.getValueAt(selectedRow, 0);
				boolean deleted = businessObj.deleteFile(fileId);
				JOptionPane.showMessageDialog(null,
						deleted ? "File deleted successfully!" : "Failed to delete the selected file.");
				refreshFileList();
			} else {
				JOptionPane.showMessageDialog(null, "Please select a file to delete.");
			}
		}
	}

	private void saveFile() {
		int selectedRow = fileTable.getSelectedRow();
		if (selectedRow != -1) {
			int fileId = (int) tableModel.getValueAt(selectedRow, 0);
			String fileName = (String) tableModel.getValueAt(selectedRow, 1);
			String content = contentTextArea.getText();

			if (content == null || content.trim().isEmpty()) {
				content = "";
			}

			boolean updated = businessObj.updateFile(fileId, fileName, currentPage, content);
			JOptionPane.showMessageDialog(null,
					updated ? "File updated successfully!" : "File update failed. Duplicate file may exist.");
			refreshFilePage(fileId, currentPage);
		} else {
			JOptionPane.showMessageDialog(null, "Please select a file to save.");
		}
	}

	private void nextPage() {
		if (currentPage < totalPageCount) {
			currentPage++;
			loadPage(currentPage);
		}
	}

	private void previousPage() {
		if (currentPage > 1) {
			currentPage--;
			loadPage(currentPage);
		}
	}

	private void loadPage(int page) {
		String pageContent = "";
		for (int i = 0; i < pages.size(); i++) {
			if (page == pages.get(i).getPageNumber()) {
				pageContent = pages.get(i).getPageContent();
			}
		}
		contentTextArea.setText(pageContent);

		pageCountLabel.setText("Page " + (page) + " of " + totalPageCount);

		nextButton.setEnabled(page < totalPageCount);
		previousButton.setEnabled(page > 1);
	}

	private boolean confirmAction(String message) {
		int option = JOptionPane.showConfirmDialog(null, message, "Confirm Action", JOptionPane.YES_NO_OPTION);
		return option == JOptionPane.YES_OPTION;
	}

	private void transliterateContent() {
		String content = contentTextArea.getText();
		int pageId = pages.get(currentPage - 1).getPageId();
		if (content != null && !content.trim().isEmpty()) {
			String transliteratedContent = businessObj.transliterate(pageId, content);
			transliteratedTextArea.setText(transliteratedContent);

			CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
			cardLayout.show(getContentPane(), "TransliterationView");
		} else {
			JOptionPane.showMessageDialog(null, "Content is empty. Please enter text to transliterate.");
		}
	}

	private void refreshFilePage(int fileId, int currPage) {
		openEditPanel(fileId);
		loadPage(currPage);
	}

	private void refreshFileList() {
		List<Documents> docs = businessObj.getAllFiles();
		tableModel.setRowCount(0);

		for (Documents doc : docs) {
			Object[] rowData = { doc.getId(), doc.getName(), doc.getLastModified(), doc.getDateCreated() };
			tableModel.addRow(rowData);
		}
	}
}