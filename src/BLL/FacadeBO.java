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
	public boolean createFile(String nameOfFile, String content) {
		// TODO Auto-generated method stub
		return bo.createFile(nameOfFile, content);
	}

	@Override
	public boolean updateFile(int id, String fileName, int pageNumber, String content) {
		// TODO Auto-generated method stub
		return bo.updateFile(id, fileName, pageNumber, content);
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
	public Documents getFile(int id) {
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
	public String transliterate(int pageId, String arabicText) {
		return bo.transliterate(pageId, arabicText);
	}

}