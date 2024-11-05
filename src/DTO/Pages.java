package DTO;

public class Pages {
	
	int pageId;
	int fileId;
	int pageNumber;
	String pageContent;

	public Pages(int pageId, int fileId, int pageNumber, String pageContent) {
		this.pageId = pageId;
		this.fileId = fileId;
		this.pageNumber = pageNumber;
		this.pageContent = pageContent;
	}

	public int getPageId() {
		return pageId;
	}

	public int getFileId() {
		return fileId;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}
}