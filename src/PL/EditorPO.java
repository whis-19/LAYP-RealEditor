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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import BLL.IEditorBO;
import DTO.Documents;

public class EditorPO extends JFrame {

    private static final long serialVersionUID = 1L;
    private IEditorBO businessObj;
    private DefaultTableModel tableModel;
    private JPanel mainPanel, editPanel;
    private JTable fileTable;
    private JTextArea contentTextArea;

    public EditorPO(IEditorBO businessObj) {
        this.businessObj = businessObj;

        setTitle("Real Text Editor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new CardLayout());

        // Main Menu Panel
        mainPanel = new JPanel(new BorderLayout());
        setupMainMenuPanel();

        // Edit Document Panel
        editPanel = new JPanel(new BorderLayout());
        setupEditPanel();

        // Adding both panels to the frame
        add(mainPanel, "MainMenu");
        add(editPanel, "EditDocument");

        setVisible(true);
    }

    private void setupMainMenuPanel() {
        tableModel = new DefaultTableModel(new Object[] { "File ID", "File Name", "Last Modified", "Date Created" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No cells are editable
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

        // Add Mouse Listener for single-click to select and double-click to open files
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 1) { // Single-click to select
                    int selectedRow = fileTable.getSelectedRow();
                    fileTable.setRowSelectionInterval(selectedRow, selectedRow);
                } else if (event.getClickCount() == 2) { // Double-click to open
                    int selectedRow = fileTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int fileId = (int) tableModel.getValueAt(selectedRow, 0);
                        openEditPanel(fileId);
                    }
                }
            }
        });

        // Action Listeners for buttons
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

        JPanel editButtonPanel = new JPanel(new FlowLayout());
        editButtonPanel.add(saveFileButton);
        editButtonPanel.add(backButton);
        editButtonPanel.add(transliterateButton);

        editPanel.add(contentScroller, BorderLayout.CENTER);
        editPanel.add(editButtonPanel, BorderLayout.SOUTH);

        // Save File Action
        saveFileButton.addActionListener(e -> saveFile());

        // Back to Menu Action
        backButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
            cardLayout.show(getContentPane(), "MainMenu");
            refreshFileList();
        });

        // Transliterate Content Action
        transliterateButton.addActionListener(e -> transliterateContent());
    }

    private void openEditPanel(int fileId) {
        String fileContent = businessObj.getFile(fileId);
        contentTextArea.setText(fileContent != null ? fileContent : "");
        contentTextArea.setEditable(true); // Ensure the text area is editable when opened
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
        if (fileName != null) {
            boolean created = businessObj.createFile(fileName);
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
                JOptionPane.showMessageDialog(null, "Cannot save empty content. Please enter some text.");
                return;
            }

            boolean updated = businessObj.updateFile(fileId, fileName, content);
            JOptionPane.showMessageDialog(null,
                    updated ? "File updated successfully!" : "File update failed. Duplicate file may exist.");
            refreshFileList();
        } else {
            JOptionPane.showMessageDialog(null, "Please select a file to save.");
        }
    }

    private void transliterateContent() {
        String content = contentTextArea.getText();
        if (content != null && !content.trim().isEmpty()) {
            String transliteratedContent = businessObj.transliterate(content);
            contentTextArea.setText(transliteratedContent);
            JOptionPane.showMessageDialog(null, "Content transliterated successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Content is empty. Please enter text to transliterate.");
        }
    }

    private boolean confirmAction(String message) {
        int response = JOptionPane.showConfirmDialog(this, message, "Confirm Action",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return response == JOptionPane.YES_OPTION;
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