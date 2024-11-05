
package BLL;

import java.io.File;
import java.util.List;

import DTO.Documents;

public interface IEditorBO {
	boolean createFile(String nameOfFile, String content);

	boolean updateFile(int id, String fileName, int pageNumber, String content);

	boolean deleteFile(int id);

	boolean importTextFiles(File file, String fileName);

	Documents getFile(int id);

	List<Documents> getAllFiles();

	String getFileExtension(String fileName);

	String transliterate(int pageId, String arabicText);

}
