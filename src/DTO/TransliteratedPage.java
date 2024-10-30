package DTO;

public class TransliteratedPage {
    private int pageId;
    private int contentId;
    private int pageNumber;
    private String transliteratedText;

    public TransliteratedPage(int contentId, int pageNumber, String transliteratedText) {
        this.contentId = contentId;
        this.pageNumber = pageNumber;
        this.transliteratedText = transliteratedText;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getContentId() {
        return contentId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getTransliteratedText() {
        return transliteratedText;
    }
}