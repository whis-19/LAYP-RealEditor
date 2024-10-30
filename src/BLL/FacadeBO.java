package BLL;

import java.io.File;
import java.util.List;

import DTO.Documents;

public class FacadeBO implements IFacadeBO {

	IEditorBO bo;

	public FacadeBO(IEditorBO bo) {
		this.bo = bo;
	}

	@Override
	public boolean createFile(String nameOfFile) {
		// TODO Auto-generated method stub
		return bo.createFile(nameOfFile);
	}

	@Override
	public boolean updateFile(int id, String nameOfFile, String content) {
		// TODO Auto-generated method stub
		return bo.updateFile(id, nameOfFile, content);
	}

	@Override
	public boolean deleteFile(int id) {
		// TODO Auto-generated method stub
		return bo.deleteFile(id);
	}

	@Override
	public boolean importTextFiles(File file, String fileName) {
		// TODO Auto-generated method stub
		return bo.importTextFiles(file, fileName);
	}

	@Override
	public String getFile(int id) {
		// TODO Auto-generated method stub
		return bo.getFile(id);
	}

	@Override
	public List<Documents> getAllFiles() {
		// TODO Auto-generated method stub
		return bo.getAllFiles();
	}

	@Override
	public String getFileExtension(String fileName) {
		// TODO Auto-generated method stub
		return bo.getFileExtension(fileName);
	}



	@Override
	public String transliterate(String arabicText) {
		return bo.transliterate(arabicText);
	}

}