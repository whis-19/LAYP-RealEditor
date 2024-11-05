package BOTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import BLL.EditorBO;
import DAL.DummyEditorDBDAO;
import DTO.Documents;
import Utils.ExcelReader;

@DisplayName("EditorBO Test Suite")
class EditorBOTest {
    private EditorBO editorBO;
    private DummyEditorDBDAO dummyDAO;
    private ExcelReader excelReader;

    @BeforeEach
    @DisplayName("Setup before each test")
    void setUp() throws IOException {
        dummyDAO = new DummyEditorDBDAO();
        editorBO = new EditorBO(dummyDAO);
        excelReader = new ExcelReader("testData/TestCases.xlsx");
        
    }


    @Test
    @DisplayName("Create File - Normal Input")
    void testCreateFileWithNormalInput() {
    	
        assertTrue(editorBO.createFile("TestFile", "This is a test content."));
    }

    @Test
    @DisplayName("Create File - Empty Content")
    void testCreateFileWithEmptyContent() {
        assertTrue(editorBO.createFile("EmptyContentFile", ""));
    }

    @Test
    @DisplayName("Create File - Special Characters in File Name")
    void testCreateFileWithSpecialCharactersInName() {
        assertTrue(editorBO.createFile("Special@File#", "This is a test content."));
    }

    @Test
    @DisplayName("Create File - Large Content")
    void testCreateFileWithLargeContent() {
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) largeContent.append("large_content ");
        assertTrue(editorBO.createFile("LargeContentFile", largeContent.toString()));
    }

    @Test
    @DisplayName("Update File - Normal Input")
    void testUpdateFileWithNormalInput() {
        editorBO.createFile("InitialFile", "Initial content.");
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        assertTrue(editorBO.updateFile(doc.getId(), "UpdatedFile", 1, "Updated content."));
    }

    @Test
    @DisplayName("Update File - Nonexistent Page Number")
    void testUpdateFileWithNonexistentPageNumber() {
        editorBO.createFile("InitialFile", "Initial content.");
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        assertFalse(editorBO.updateFile(doc.getId(), "UpdatedFile", 999, "Updated content."));
    }

    @Test
    @DisplayName("Update File - Empty Content")
    void testUpdateFileWithEmptyContent() {
        editorBO.createFile("InitialFile", "Initial content.");
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        assertTrue(editorBO.updateFile(doc.getId(), "UpdatedFile", 1, ""));
    }

    @Test
    @DisplayName("Update File - Nonexistent File ID")
    void testUpdateFileWithNonExistentFileId() {
        assertFalse(editorBO.updateFile(9999, "NonExistentFile", 1, "Updated content."));
    }

    @Test
    @DisplayName("Delete File - Existing File")
    void testDeleteExistingFile() {
        editorBO.createFile("FileToDelete", "Content to delete.");
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        assertTrue(editorBO.deleteFile(doc.getId()));
    }

    @Test
    @DisplayName("Delete File - Nonexistent File")
    void testDeleteNonExistentFile() {
        assertFalse(editorBO.deleteFile(9999));
    }

    @Test
    @DisplayName("Import Text File - Supported Extension")
    void testImportTextFile() throws Exception {
        File tempFile = File.createTempFile("importFile", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sample content for import.");
        }
        assertTrue(editorBO.importTextFiles(tempFile, "importFile.txt"));
    }

    @Test
    @DisplayName("Import Text File - Unsupported Extension")
    void testImportUnsupportedFileExtension() throws Exception {
        File unsupportedFile = File.createTempFile("importFile", ".pdf");
        unsupportedFile.deleteOnExit();
        assertFalse(editorBO.importTextFiles(unsupportedFile, "importFile.pdf"));
    }


    @Test
    @DisplayName("Get File - Existing File")
    void testGetExistingFile() {
        String fileName = excelReader.getCellDataString("BOTesting", 0, 1);
        String content = excelReader.getCellDataString("BOTesting", 0, 2);
        editorBO.createFile(fileName, content);
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        assertNotNull(editorBO.getFile(doc.getId()));
    }
    @Test
    @DisplayName("Get File - Nonexistent File")
    void testGetNonExistentFile() {
        String fileName = excelReader.getCellDataString("BOTesting", 1, 1);
        // No creation of a file, directly check for non-existent
        assertNull(editorBO.getFile(9999));
    }

    @Test
    @DisplayName("Get File Extension - With Extension")
    void testGetFileExtensionWithExtension() {
        String fileName = excelReader.getCellDataString("BOTesting", 2, 1);
        assertEquals("txt", editorBO.getFileExtension(fileName));
    }
    @Test
    @DisplayName("Get File Extension - No Extension")
    void testGetFileExtensionNoExtension() {
        String fileName = excelReader.getCellDataString("BOTesting", 3, 1);
        assertEquals("", editorBO.getFileExtension(fileName));
    }

    @Test
    @DisplayName("Get File Extension - MD5 Extension")
    void testGetFileExtensionWithMd5Extension() {
        assertEquals("md5", editorBO.getFileExtension("check.md5"));
    }


    @Test
    @DisplayName("Get All Files - No Files Exist")
    void testGetAllFilesWhenNoFilesExist() {
        assertTrue(editorBO.getAllFiles().isEmpty());
    }

    @Test
    @DisplayName("Get All Files - Multiple Files")
    void testGetAllFilesWithMultipleFiles() {
        editorBO.createFile("File1", "Content1");
        editorBO.createFile("File2", "Content2");
        List<Documents> files = editorBO.getAllFiles();
        assertEquals(2, files.size());
    }

    @Test
    @DisplayName("Transliterate - Normal Arabic Text")
    void testTransliterateWithNormalArabicText() {
        String arabicText = "مرحبا";
        editorBO.createFile("TransliterateFile", arabicText);
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        int pageId = doc.getPages().get(0).getPageId();
        assertNotNull(editorBO.transliterate(pageId, arabicText));
    }

    @Test
    @DisplayName("Transliterate - Empty Text")
    void testTransliterateWithEmptyText() {
        String arabicText = "مرحبا";
        editorBO.createFile("TransliterateFile", arabicText);
        Documents doc = dummyDAO.getFilesFromDB().get(0);
        int pageId = doc.getPages().get(0).getPageId();
        assertEquals("", editorBO.transliterate(pageId, ""));
    }

    @Test
    @DisplayName("Transliterate - Nonexistent Page ID")
    void testTransliterateWithNonExistentPageId() {
        String arabicText = "مرحبا";
        assertNull(editorBO.transliterate(9999, arabicText));
    }
}
