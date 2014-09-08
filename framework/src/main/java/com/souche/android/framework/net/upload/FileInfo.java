package com.souche.android.framework.net.upload;

import java.io.File;

public 	class FileInfo {
	private String name;
	private File file;
	public FileInfo(String name, File file) {
		super();
		this.name = name;
		this.file = file;
	}
	public String getFileTextName() {
		return name;
	}
	public File getFile() {
		return file;
	}
	
}