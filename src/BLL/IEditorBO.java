package BLL;

import java.io.File;
import java.util.List;

import DTO.Documents;

public interface IEditorBO {
	boolean createFile(String nameOfFile);

	boolean updateFile(int id, String nameOfFile, String content);

	boolean deleteFile(int id);

	boolean importTextFiles(File file, String fileName);

	String getFile(int id);

	List<Documents> getAllFiles();

	String getFileExtension(String fileName);

	String transliterate(String arabicText);

}