package DAL;

import java.util.List;

import DTO.Documents;

public interface IEditorDBDAO {
    boolean createFileInDB(String nameOfFile);
    boolean updateFileInDB(int id, String nameOfFile, String content);
    boolean deleteFileInDB(int id);
    boolean importFileInDB(String nameOfFile, String content);
    List<Documents> getFilesFromDB(); 
    String getLatestContentByFileId(int fileId);
    String transliterate(String arabicText);
}



