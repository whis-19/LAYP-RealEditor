package BLL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import DAL.IFacadeDAO;
import DTO.Documents;

public class EditorBO implements IEditorBO {

	private IFacadeDAO db;

	public EditorBO(IFacadeDAO db) {
		this.db = db;
	}

	@Override
	public boolean createFile(String nameOfFile) {
		try {
			return db.createFileInDB(nameOfFile);
		} catch (Exception e) {
			e.printStackTrace(); // Log exception for troubleshooting
			return false;
		}
	}

	@Override
	public boolean updateFile(int id, String nameOfFile, String content) {
		try {
			return db.updateFileInDB(id, nameOfFile, content);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteFile(int id) {
		try {
			return db.deleteFileInDB(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean importTextFiles(File file, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		String fileExtension = getFileExtension(fileName);

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				fileContent.append(line).append("\n");
			}

			// Avoid importing duplicate content
			List<Documents> docs = db.getFilesFromDB();
			for (Documents doc : docs) {
				if (doc.getContent() != null && doc.getContent().equals(fileContent.toString())) {
					return false;
				}
			}

			// Save content in pages if it's a text file
			if (fileExtension.equalsIgnoreCase("txt") || fileExtension.equalsIgnoreCase("md5")) {
				return db.importFileInDB(fileName, fileContent.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public String getFile(int id) {
		return db.getLatestContentByFileId(id); // Retrieve the latest content version
	}

	@Override
	public String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf('.');
		return (lastIndexOfDot == -1) ? "" : fileName.substring(lastIndexOfDot + 1);
	}

	@Override
	public List<Documents> getAllFiles() {
		return db.getFilesFromDB();
	}

	@Override
	public String transliterate(String arabicText) {
		return db.transliterate(arabicText);
	}

}