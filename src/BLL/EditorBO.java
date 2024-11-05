
package BLL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import DAL.DummyEditorDBDAO;
import DAL.IFacadeDAO;
import DTO.Documents;

public class EditorBO implements IEditorBO {

	private DummyEditorDBDAO db;

	public EditorBO(DummyEditorDBDAO db) {
		this.db = db;
	}

	@Override
	public boolean createFile(String nameOfFile, String content) {
		try {
			return db.createFileInDB(nameOfFile, content);
		} catch (Exception e) {
			e.printStackTrace(); 
			return false; 
		}
	}

	@Override
	public boolean updateFile(int id, String fileName, int pageNumber, String content) {
		try {
			return db.updateFileInDB(id, fileName, pageNumber, content);
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
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			
			while ((line = reader.readLine()) != null) {
				fileContent.append(line).append("\n");
			}
			reader.close();

			if (fileExtension.equalsIgnoreCase("txt") || fileExtension.equalsIgnoreCase("md5")) {
				return db.createFileInDB(fileName, fileContent.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Documents getFile(int id) {
		List<Documents> docs = getAllFiles();
		for(int i = 0; i < docs.size();i++)
		{
			if(id == docs.get(i).getId()) {
				return docs.get(i);
			}
		}
		return null;
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
	public String transliterate(int pageId, String arabicText) {
		return db.transliterateInDB(pageId, arabicText);
	}

}
