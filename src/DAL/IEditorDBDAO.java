package DAL;

import java.util.List;

import DTO.Documents;
import DTO.Pages;

public interface IEditorDBDAO {
    boolean createFileInDB(String nameOfFile, String content);
    boolean updateFileInDB(int id, String fileName, int pageNumber, String content);
    boolean deleteFileInDB(int id);
    List<Documents> getFilesFromDB(); 
    //String transliterate(String arabicText);
    String transliterateInDB(int pageId, String arabicText);
    
}