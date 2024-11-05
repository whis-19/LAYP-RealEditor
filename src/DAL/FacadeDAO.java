package DAL;

import java.util.List;

import DTO.Documents;


public class FacadeDAO implements IFacadeDAO {

    IEditorDBDAO mariaDB;

    public FacadeDAO(IEditorDBDAO mariaDB) {
        this.mariaDB = mariaDB;
    }

    @Override
    public boolean createFileInDB(String nameOfFile, String content) {
        return mariaDB.createFileInDB(nameOfFile, content);
    }

    @Override
    public boolean updateFileInDB(int id, String fileName, int pageNumber, String content) {
        return mariaDB.updateFileInDB(id, fileName, pageNumber, content);
    }

    @Override
    public boolean deleteFileInDB(int id) {
        return mariaDB.deleteFileInDB(id);
    }

    @Override
    public List<Documents> getFilesFromDB() {
        return mariaDB.getFilesFromDB();
    }

//	@Override
//	public String transliterate(String arabicText) {
//		// TODO Auto-generated method stub
//		return mariaDB.transliterate(arabicText);
//	}

	@Override
	public String transliterateInDB(int pageId, String arabicText) {
		// TODO Auto-generated method stub
		return mariaDB.transliterateInDB(pageId, arabicText);
	}
}