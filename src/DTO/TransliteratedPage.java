
package DTO;

public class TransliteratedPage {
    private int transliteratedId;
    private int pageId;
    private String transliteratedText;
    
	public TransliteratedPage(int transliteratedId, int pageId, String transliteratedText) {
		this.transliteratedId = transliteratedId;
		this.pageId = pageId;
		this.transliteratedText = transliteratedText;
	}
	
	public int getTransliteratedId() {
		return transliteratedId;
	}
	public int getPageId() {
		return pageId;
	}
	public String getTransliteratedText() {
		return transliteratedText;
	}
	public void setTransliteratedId(int transliteratedId) {
		this.transliteratedId = transliteratedId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public void setTransliteratedText(String transliteratedText) {
		this.transliteratedText = transliteratedText;
	}    
}
