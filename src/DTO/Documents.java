package DTO;

import java.sql.Timestamp;

public class Documents {
	private int id;
	private String name;
	private String hash;
	private String content;
	private Timestamp lastModified;
	private Timestamp dateCreated;

	public Documents(int id, String name, String hash,String content, Timestamp lastModified, Timestamp dateCreated) {
		this.id = id;
		this.name = name;
		this.hash = hash;
		this.content = content;
		this.lastModified = lastModified;
		this.dateCreated = dateCreated;
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

	public Timestamp getLastModified() {
		return lastModified;
	}


	public Timestamp getDateCreated() {
		return dateCreated;
	}


	public String getContent() {
		return content;
	}

}