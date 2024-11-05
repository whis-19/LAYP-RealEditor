
package DTO;

import java.util.List;

public class Documents {
	private int id;
	private String name;
	private String hash;
	private String lastModified;
	private String dateCreated;
	private List<Pages> pages;

	public Documents(int id, String name, String hash, String lastModified, String dateCreated, List<Pages> pages) {
		this.id = id;
		this.name = name;
		this.hash = hash;
		this.lastModified = lastModified;
		this.dateCreated = dateCreated;
		this.pages = pages;
	}

	public Documents(int i, String nameOfFile, String content, String string, String string2) {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}

	public String getLastModified() {
		return lastModified;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public List<Pages> getPages() {
		return pages;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setPages(List<Pages> pages) {
		this.pages = pages;
	}

}
