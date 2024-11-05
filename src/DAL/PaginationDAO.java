package DAL;

import java.util.ArrayList;
import java.util.List;

import DTO.Pages;

public class PaginationDAO {

	private PaginationDAO() {
		// TODO Auto-generated constructor stub
	}
	
	static List<Pages> paginate(String fileContent){
		int pageSize = 5;
		int pageNumber = 1;
		String pageContent = "";
		List<Pages> pages = new ArrayList<Pages>();
		if(fileContent==null || fileContent.isEmpty())
		{
			pages.add(new Pages(0, 0, pageNumber, pageContent.toString()));
			return pages;
		}
		for(int i = 0; i < fileContent.length(); i++)
		{
			pageContent += fileContent.charAt(i);
			if (pageContent.length() == pageSize || i == fileContent.length() - 1){
				pages.add(new Pages(0, 0, pageNumber, pageContent));
				pageNumber++;
				pageContent = "";
			}
		}
		return pages;
	} 
}