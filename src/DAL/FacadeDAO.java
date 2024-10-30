package DAL;

import java.util.List;

import DTO.Documents;


public class FacadeDAO implements IFacadeDAO {

    IEditorDBDAO mariaDB;

    public FacadeDAO(IEditorDBDAO mariaDB) {
        this.mariaDB = mariaDB;
    }

    @Override
    public boolean createFileInDB(String nameOfFile) {
        return mariaDB.createFileInDB(nameOfFile);
    }

    @Override
    public boolean updateFileInDB(int id, String nameOfFile, String content) {
        return mariaDB.updateFileInDB(id, nameOfFile, content);
    }

    @Override
    public boolean deleteFileInDB(int id) {
        return mariaDB.deleteFileInDB(id);
    }

    @Override
    public List<Documents> getFilesFromDB() {
        return mariaDB.getFilesFromDB();
    }

	@Override
	public boolean importFileInDB(String nameOfFile, String content) {
		return mariaDB.importFileInDB(nameOfFile, content);
	}

	@Override
	public String getLatestContentByFileId(int fileId) {
		return mariaDB.getLatestContentByFileId(fileId);
	}

	@Override
	public String transliterate(String arabicText) {
		// TODO Auto-generated method stub
		return mariaDB.transliterate(arabicText);
	}


}