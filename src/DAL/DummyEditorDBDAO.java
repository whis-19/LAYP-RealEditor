package DAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.Documents;
import DTO.Pages;

public class DummyEditorDBDAO implements IEditorDBDAO {
    private List<Documents> files = new ArrayList<>();
    private List<Pages> pages = new ArrayList<>();
    private Map<Integer, String> transliteratedPages = new HashMap<>();
    private int fileIdCounter = 1;
    private int pageIdCounter = 1;

    @Override
    public boolean createFileInDB(String nameOfFile, String content) {
		String hash = null;
		List<Pages> pages = null;

		try {
			hash = HashCalculator.calculateHash(content);
			pages = PaginationDAO.paginate(content);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

        List<Pages> paginatedPages = PaginationDAO.paginate(content);

        Documents newFile = new Documents(fileIdCounter++, nameOfFile, hash, "2024-11-05", "2024-11-05", new ArrayList<>());
        for (Pages page : paginatedPages) {
            page.setPageId(pageIdCounter++);
            page.setFileId(newFile.getId());
            pages.add(page);
            newFile.getPages().add(page);

            String transliteratedText = transliterate(page.getPageContent());
            transliteratedPages.put(page.getPageId(), transliteratedText);
        }
        files.add(newFile);
        return true;
    }

    @Override
    public boolean updateFileInDB(int id, String fileName, int pageNumber, String content) {
        for (Documents file : files) {
            if (file.getId() == id) {
                file.setName(fileName);
                file.setLastModified("2024-11-05");

                for (Pages page : file.getPages()) {
                    if (page.getPageNumber() == pageNumber) {
                        page.setPageContent(content);
                        String transliteratedText = transliterate(content);
                        transliteratedPages.put(page.getPageId(), transliteratedText);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteFileInDB(int id) {
        files.removeIf(file -> file.getId() == id);
        pages.removeIf(page -> page.getFileId() == id);
        transliteratedPages.keySet().removeIf(pageId -> pages.stream().noneMatch(page -> page.getPageId() == pageId));
        return true;
    }

    @Override
    public List<Documents> getFilesFromDB() {
        return new ArrayList<>(files);
    }

    @Override
    public String transliterateInDB(int pageId, String arabicText) {
        String transliteratedText = transliterate(arabicText);
        transliteratedPages.put(pageId, transliteratedText);
        return transliteratedText;
    }

    // Helper function for transliteration
    private String transliterate(String arabicText) {
        Transliteration tsl = new Transliteration();
        return tsl.transliterate(arabicText);
    }
}
