package DAL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import DTO.Documents;
import DTO.Pages;
import DTO.TransliteratedPage;

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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean createFileInDB(String nameOfFile, String content) {
		String hash = null;
		List<Pages> pages = null;

		try {
			hash = HashCalculator.calculateHash(content);
			pages = PaginationDAO.paginate(content);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		String query = null;
		Connection conn = null;
		PreparedStatement fileStmt = null;
	    PreparedStatement insertStmt = null;
		
		try {
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(false);
			query = "INSERT INTO files (fileName, fileHash) VALUES (?, ?)";
			fileStmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

			fileStmt.setString(1, nameOfFile);
			fileStmt.setString(2, hash);
			fileStmt.executeUpdate();
			ResultSet fileRS = fileStmt.getGeneratedKeys();
			fileRS.next();
			int fileID = fileRS.getInt(1);

			for (Pages page : pages) {
				query = "INSERT INTO pages (fileId, pageNumber, pageContent) VALUES (?, ?, ?)";
				fileStmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

				fileStmt.setInt(1, fileID);
				fileStmt.setInt(2, page.getPageNumber());
				fileStmt.setString(3, page.getPageContent());
				fileStmt.executeUpdate();
				
	            ResultSet pageRS = fileStmt.getGeneratedKeys();
	            pageRS.next();
	            int pageId = pageRS.getInt(1); 
				
	            String transliteratedText = transliterate(page.getPageContent()); 
	            String insertQuery = "INSERT INTO transliteratedpages (pageId, transliteratedText) VALUES (?, ?)";
	            insertStmt = conn.prepareStatement(insertQuery);
	            insertStmt.setInt(1, pageId);
	            insertStmt.setString(2, transliteratedText);
	            insertStmt.executeUpdate();
				


			}
			conn.commit();
			return true;

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}


	//Helper Fucntion
	public String transliterate(String arabicText) {
	    Transliteration tsl = new Transliteration();
	    return tsl.transliterate(arabicText); 
	}

	@Override
	public boolean updateFileInDB(int id, String fileName, int pageNumber, String content) {
		Connection conn = null;
		PreparedStatement fileStmt = null;
		String query = null;

		try {
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(false);
			query = "UPDATE FILES SET fileName = ? , lastModified = CURRENT_TIMESTAMP() WHERE fileId = ?";
			fileStmt = conn.prepareStatement(query);

			fileStmt.setString(1, fileName);
			fileStmt.setInt(2, id);
			fileStmt.executeUpdate();

			query = "UPDATE pages SET pageContent = ? WHERE fileId = ? AND pageNumber = ?";
			fileStmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

			fileStmt.setString(1, content);
			fileStmt.setInt(2, id);
			fileStmt.setInt(3, pageNumber);
			fileStmt.executeUpdate();
			
			
			
			conn.commit();
			return true;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteFileInDB(int id) {
		String query = "DELETE FROM FILES WHERE fileId = ?";

		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement fileStmt = conn.prepareStatement(query)) {

			fileStmt.setInt(1, id);
			fileStmt.executeUpdate();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Documents> getFilesFromDB() {
		List<Documents> documents = new ArrayList<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		String query = null;
		ResultSet rs;

		try {
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(false);
			query = "SELECT fileId, fileName, filehash, dateCreated, lastModified FROM files";
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("fileId");
				String name = rs.getString("fileName");
				String hash = rs.getString("fileHash");
				String lastModified = rs.getString("lastModified");
				String dateCreated = rs.getString("dateCreated");

				String query1 = "SELECT pageId, fileId, pageNumber, pageContent FROM pages where fileId = ?";
				PreparedStatement stmt1 = conn.prepareStatement(query1);
				stmt1.setInt(1, id);
				ResultSet rs1 = stmt1.executeQuery();
				List<Pages> pages = new ArrayList<Pages>();

				while (rs1.next()) {
					pages.add(new Pages(rs1.getInt("pageId"), rs1.getInt("fileId"), rs1.getInt("pageNumber"),
							rs1.getString("pageContent")));
				}

				documents.add(new Documents(id, name, hash, lastModified, dateCreated, pages));
			}
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return documents;
	}


	@Override
	public String transliterateInDB(int pageId, String arabicText) {
	    String content = "";

	    content = transliterate(arabicText);

	    TransliteratedPage tp = new TransliteratedPage(0, pageId, content);
	    Connection conn = null;
	    PreparedStatement deleteStmt = null;
	    PreparedStatement insertStmt = null;
	    String deleteQuery, insertQuery;

	    try {
	        conn = DriverManager.getConnection(url, username, password);
	        conn.setAutoCommit(false);


	        deleteQuery = "DELETE FROM transliteratedpages WHERE pageId = ?";
	        deleteStmt = conn.prepareStatement(deleteQuery);
	        deleteStmt.setInt(1, pageId);
	        deleteStmt.executeUpdate();

	        insertQuery = "INSERT INTO transliteratedpages (pageId, transliteratedText) VALUES (?, ?)";
	        insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
	        insertStmt.setInt(1, pageId);
	        insertStmt.setString(2, content);
	        insertStmt.executeUpdate();

	        conn.commit();
	        return content;

	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            if (conn != null) conn.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        return null;
	    } finally {
	        try {
	            if (deleteStmt != null) deleteStmt.close();
	            if (insertStmt != null) insertStmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException closeEx) {
	            closeEx.printStackTrace();
	        }
	    }
	}



}