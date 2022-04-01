package com.tasanah.files.model;

import javax.persistence.*;

@Entity
public class XFile {
    @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 
	 @Column
	 private String fileGroup;

	 @Column
	 private String fileName;
	    
	 @Column
	 @Lob
	 private byte[] file;

	public String getFileName() {
		return fileName;
	 }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
