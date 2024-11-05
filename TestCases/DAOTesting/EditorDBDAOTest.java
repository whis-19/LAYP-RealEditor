package DAOTesting;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import DAL.EditorDBDAO;
import DTO.Documents;
import Utils.ExcelReader;

public class EditorDBDAOTest {
    private EditorDBDAO editorDBDAO;
    private ExcelReader excelReader;

    @BeforeEach
    public void setUp() throws IOException {
        editorDBDAO = new EditorDBDAO();
        excelReader = new ExcelReader("testData/TestCases.xlsx");
        
    }
//    @AfterEach
//    void tearDown() {
//        List<Documents> documents = editorDBDAO.getFilesFromDB();
//        for (Documents document : documents) {
//            editorDBDAO.deleteFileInDB(document.getId());
//        }
//    }

    // Test cases for getFilesFromDB
    @Test
    @DisplayName("Should return non-empty list when there are files")
    public void testGetFiles_ShouldReturnNonEmpty() {
        assertFalse(editorDBDAO.getFilesFromDB().isEmpty());
    }


    @Test
    @DisplayName("Should create file with special characters in filename")
    public void testCreateFile_ShouldCreateSpecialChars() {
        assertTrue(editorDBDAO.createFileInDB("File@123", "This is a test content."));
    }

    @Test
    @DisplayName("Should create file with empty content")
    public void testCreateFile_ShouldCreateEmptyContent() {
        assertTrue(editorDBDAO.createFileInDB("TestFile", ""));
    }

    @Test
    @DisplayName("Should not create file with null content")
    public void testCreateFile_ShouldNotCreateNullContent() {
        assertFalse(editorDBDAO.createFileInDB("TestFile", null),
                "Creating file with null content should return false.");
    }

    @Test
    @DisplayName("Should update file with valid input")
    public void testUpdateFile_ShouldUpdateValid() {
        assertTrue(editorDBDAO.updateFileInDB(1, "UpdatedFile", 1, "Updated content."));
    }

    @Test
    @DisplayName("Should not update with non-existent file ID")
    public void testUpdateFile_ShouldNotUpdateNonExistent() {
        assertFalse(editorDBDAO.updateFileInDB(-1, "UpdatedFile", 1, "Updated content."));
    }

    @Test
    @DisplayName("Should update even with empty filename")
    public void testUpdateFile_ShouldUpdateEmptyFileName() {
        assertTrue(editorDBDAO.updateFileInDB(1, "", 1, "Updated content."));
    }

    @Test
    @DisplayName("Should not update with zero page number")
    public void testUpdateFile_ShouldNotUpdatePageZero() {
        assertFalse(editorDBDAO.updateFileInDB(1, "UpdatedFile", 0, "Updated content."));
    }

    @Test
    @DisplayName("Should update even with empty content")
    public void testUpdateFile_ShouldUpdateEmptyContent() {
        assertTrue(editorDBDAO.updateFileInDB(1, "UpdatedFile", 1, ""));
    }

    @Test
    @DisplayName("Should update with null content")
    public void testUpdateFile_ShouldUpdateNullContent() {
        assertTrue(editorDBDAO.updateFileInDB(1, "UpdatedFile", 1, null),
                "Updating file with null content should return false.");
    }

    @Test
    @DisplayName("Should delete file with valid ID")
    public void testDeleteFile_ShouldDeleteValidID() {
        assertTrue(editorDBDAO.deleteFileInDB(1));
    }


    @Test
    @DisplayName("Should return null for non-existent page ID")
    public void testTransliterate_ShouldReturnNullForNonExistentID() {
        assertNull(editorDBDAO.transliterateInDB(-1, "مرحبا"));
    }

    @Test
    @DisplayName("Should return null for empty Arabic text")
    public void testTransliterate_ShouldReturnNullForEmptyText() {
        assertEquals(null, editorDBDAO.transliterateInDB(1, ""),
                "Transliterating empty Arabic text should return null.");
    }

    @Test
    @DisplayName("Should transliterate very long Arabic text")
    public void testTransliterate_ShouldTransliterateLongText() {
        String longText = generateRepeatedString("مرحبا", 1024);
        assertNotNull(editorDBDAO.transliterate(longText));
    }

    @Test
    @DisplayName("Should create file with very long filename")
    public void testCreateFile_ShouldCreateLongFileName() {
        String longFileName = generateRepeatedString("A", 255);
        assertTrue(editorDBDAO.createFileInDB(longFileName, "This is a test content."));
    }

    @Test
    @DisplayName("Should delete file after creation")
    public void testDeleteFile_ShouldDeleteAfterCreation() {
        editorDBDAO.createFileInDB("TestFileToDelete", "Content for deletion");
        List<Documents> documents = editorDBDAO.getFilesFromDB();
        assertFalse(documents.isEmpty());
        Documents document = documents.get(0);
        assertTrue(editorDBDAO.deleteFileInDB(document.getId()));

        // Verify that it is deleted
        assertFalse(editorDBDAO.getFilesFromDB().stream().anyMatch(doc -> doc.getId() == document.getId()));
    }

    @Test
    @DisplayName("Should retrieve files after creation")
    public void testGetFiles_ShouldRetrieveAfterCreation() {
        editorDBDAO.createFileInDB("FileForGetTest", "Content to retrieve");
        List<Documents> documents = editorDBDAO.getFilesFromDB();
        assertEquals(1, documents.size());
        assertEquals("FileForGetTest", documents.get(0).getName());
    }

    @Test
    @DisplayName("Should change file content in database")
    public void testUpdateFile_ShouldChangeContent() {
        editorDBDAO.createFileInDB("FileToUpdate", "Original content");
        List<Documents> documents = editorDBDAO.getFilesFromDB();
        int fileId = documents.get(0).getId();
        editorDBDAO.updateFileInDB(fileId, "FileToUpdate", 1, "Updated content");

        Documents updatedDocument = editorDBDAO.getFilesFromDB().stream()
                .filter(doc -> doc.getId() == fileId).findFirst().orElse(null);
        assertNotNull(updatedDocument);
        assertEquals("Updated content", updatedDocument.getPages().get(0).getPageContent());
    }


    @Test
    @DisplayName("Should not update with non-existent file ID")
    public void testUpdateFile_ShouldNotUpdateNonExistentID() {
        assertFalse(editorDBDAO.updateFileInDB(9999, "NonExistentFile", 1, "Some content."));
    }

    @Test
    @DisplayName("Should not delete with non-existent ID")
    public void testDeleteFile_ShouldNotDeleteNonExistentID() {
        assertFalse(editorDBDAO.deleteFileInDB(-1));
    }

    @Test
    @DisplayName("Should create file with very long content")
    public void testCreateFile_ShouldCreateLongContent() {
        String longContent = generateRepeatedString("A", 1024 * 1024);
        assertTrue(editorDBDAO.createFileInDB("TestFile", longContent));
    }
    @Test
    @DisplayName("Should create file with valid input")
    public void testCreateFile_ShouldCreateValid() {
        String fileName = excelReader.getCellDataString("TestCases", 1, 1);
        String content = excelReader.getCellDataString("TestCases", 1, 2);
        boolean expectedResult = Boolean.parseBoolean(excelReader.getCellDataString("TestCases", 1, 3));

        boolean isCreated = editorDBDAO.createFileInDB(fileName, content);
        assertEquals(expectedResult, isCreated, "File creation did not match expected result");
    }

    @Test
    @DisplayName("Should create file with empty filename")
    public void testCreateFile_ShouldCreateEmptyFileName() {
        String fileName = excelReader.getCellDataString("TestCases", 2, 1);
        String content = excelReader.getCellDataString("TestCases", 2, 2);
        boolean expectedResult = Boolean.parseBoolean(excelReader.getCellDataString("TestCases", 2, 3));

        boolean isCreated = editorDBDAO.createFileInDB(fileName, content);
        assertEquals(expectedResult, isCreated, "File creation with empty filename did not match expected result");
    }

    // Continue modifying other test cases similarly...

    @Test
    @DisplayName("Should transliterate file content correctly")
    public void testTransliterate_ShouldTransliterateFileContent() {
        String fileName = excelReader.getCellDataString("TestCases", 11, 1);
        String content = excelReader.getCellDataString("TestCases", 11, 2);
        String expectedTransliteration = excelReader.getCellDataString("TestCases", 11, 3);

        editorDBDAO.createFileInDB(fileName, content);
        List<Documents> documents = editorDBDAO.getFilesFromDB();
        int pageId = documents.get(0).getPages().get(0).getPageId();

        String transliteratedText = editorDBDAO.transliterateInDB(pageId, content);
        assertEquals(expectedTransliteration, transliteratedText, "Transliteration did not match expected result");
    }
    //test case helper function
    private String generateRepeatedString(String s, int count) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < count) {
            sb.append(s);
        }
        return sb.substring(0, count);
    }
}