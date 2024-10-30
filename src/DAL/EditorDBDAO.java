package DAL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import DTO.Documents;

public class EditorDBDAO implements IEditorDBDAO {
	private String url = "";
	private String username = "";
	private String password = "";

	public EditorDBDAO() {
		try {
			FileInputStream propertiesInput = new FileInputStream("config.properties");
			Properties properties = new Properties();
			properties.load(propertiesInput);
			url = properties.getProperty("db.url");
			username = properties.getProperty("db.username");
			password = properties.getProperty("db.password");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}


    @Override
    public boolean createFileInDB(String nameOfFile) {
        String insertFileSQL = "INSERT INTO FILES (fileName, fileHash) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement fileStmt = conn.prepareStatement(insertFileSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            String placeholderHash = ""; // Placeholder for empty files
            fileStmt.setString(1, nameOfFile);
            fileStmt.setString(2, placeholderHash);

            int rowsAffected = fileStmt.executeUpdate();
            return rowsAffected > 0; // Return true if the file was successfully created
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return false;
        }
    }

    @Override
    public boolean updateFileInDB(int id, String nameOfFile, String content) {
        String updateFileSQL = "UPDATE FILES SET fileName = ? WHERE fileId = ?";
        String selectVersionSQL = "SELECT COUNT(*) AS versionCount FROM CONTENT WHERE fileId = ?";
        String insertContentSQL = "INSERT INTO CONTENT (fileId, content, version) VALUES (?, ?, ?)"; // Insert new version of content

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement fileStmt = conn.prepareStatement(updateFileSQL);
             PreparedStatement selectVersionStmt = conn.prepareStatement(selectVersionSQL);
             PreparedStatement insertContentStmt = conn.prepareStatement(insertContentSQL)) {

            // Update the file's name
            fileStmt.setString(1, nameOfFile);
            fileStmt.setInt(2, id);
            boolean fileUpdated = fileStmt.executeUpdate() > 0;

            if (!content.isEmpty()) {
                // Check how many versions exist for this file
                selectVersionStmt.setInt(1, id);
                ResultSet rs = selectVersionStmt.executeQuery();
                int newVersion = 1; // Default to version 1

                if (rs.next()) {
                    newVersion = rs.getInt("versionCount") + 1; // Increment the current count
                }

                // Insert the new version of the content
                insertContentStmt.setInt(1, id);
                insertContentStmt.setString(2, content);
                insertContentStmt.setInt(3, newVersion);

                // Calculate hash only for the first version update
                if (newVersion == 1) {
                    String hash = HashCalculator.calculateHash(content);
                    // Update the file's hash only for the first version
                    String updateHashSQL = "UPDATE FILES SET fileHash = ? WHERE fileId = ?";
                    try (PreparedStatement updateHashStmt = conn.prepareStatement(updateHashSQL)) {
                        updateHashStmt.setString(1, hash);
                        updateHashStmt.setInt(2, id);
                        updateHashStmt.executeUpdate(); // Update the hash
                    }
                }

                // Execute content insertion
                boolean contentInserted = insertContentStmt.executeUpdate() > 0;

                return fileUpdated && contentInserted; // Return true if both file and content were updated
            } else {
                return fileUpdated; // Return true if only the file was updated
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public boolean deleteFileInDB(int id) {
        String deleteContentSQL = "DELETE FROM CONTENT WHERE fileId = ?";
        String deleteFileSQL = "DELETE FROM FILES WHERE fileId = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement contentStmt = conn.prepareStatement(deleteContentSQL);
             PreparedStatement fileStmt = conn.prepareStatement(deleteFileSQL)) {

            // First delete from CONTENT
            contentStmt.setInt(1, id);
            contentStmt.executeUpdate();

            // Then delete from FILES
            fileStmt.setInt(1, id);
            return fileStmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return false;
        }
    }

    @Override
    public List<Documents> getFilesFromDB() {
        List<Documents> documents = new ArrayList<>();
        String selectSQL = "SELECT f.fileId, f.fileName, f.fileHash, f.dateCreated, c.content, c.lastModified " +
                           "FROM FILES f " +
                           "JOIN CONTENT c ON f.fileId = c.fileId " +
                           "WHERE c.lastModified = (SELECT MAX(lastModified) FROM CONTENT WHERE fileId = f.fileId)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("fileId");
                String name = rs.getString("fileName");
                String hash = rs.getString("fileHash");
                String content = rs.getString("content");
                Timestamp lastModified = rs.getTimestamp("lastModified");
                Timestamp dateCreated = rs.getTimestamp("dateCreated");

                documents.add(new Documents(id, name, hash, content, lastModified, dateCreated));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return documents;
    }


    // New method to get content by fileId
    public String getContentByFileId(int fileId) {
        String content = null;
        String selectSQL = "SELECT content FROM CONTENT WHERE fileId = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {

            stmt.setInt(1, fileId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                content = rs.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return content;
    }

    @Override
    public boolean importFileInDB(String nameOfFile, String content) {
        String insertFileSQL = "INSERT INTO FILES (fileName, fileHash) VALUES (?, ?)";
        String insertContentSQL = "INSERT INTO CONTENT (fileId, content) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement fileStmt = conn.prepareStatement(insertFileSQL, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement contentStmt = conn.prepareStatement(insertContentSQL)) {

            fileStmt.setString(1, nameOfFile);

            String hash;
            if (content.isEmpty()) {
                hash = ""; // Placeholder for empty content
            } else {
                hash = HashCalculator.calculateHash(content); // Calculate hash for non-empty content
            }
            fileStmt.setString(2, hash); // Set the calculated or placeholder hash

            // Execute file insert
            int rowsAffected = fileStmt.executeUpdate();
            if (rowsAffected > 0) {
                // Get generated fileId
                ResultSet generatedKeys = fileStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedFileId = generatedKeys.getInt(1);

                    // Insert content
                    contentStmt.setInt(1, generatedFileId);
                    contentStmt.setString(2, content);
                    return contentStmt.executeUpdate() > 0;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return false;
        }
    }
    
    @Override
    public String getLatestContentByFileId(int fileId) {
        String content = null;
        String selectSQL = "SELECT content FROM content WHERE fileId = ? ORDER BY version DESC LIMIT 1"; // Get the latest version

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setInt(1, fileId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                content = rs.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return content;
    }


	@Override
	public String transliterate(String arabicText) {
		Transliteration tsl = new Transliteration();
		return tsl.transliterate(arabicText);
	}

}