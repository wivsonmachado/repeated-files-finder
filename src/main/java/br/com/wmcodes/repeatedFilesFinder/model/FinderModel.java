package br.com.wmcodes.repeatedFilesFinder.model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinderModel {
	
	private static String REPEATED_FILES_DIRECTORY = "/RepeatedFiles/";
	
	private String root;
	private String toMove;
	
	public Map<Path, String> nonRepeatedFiles = new HashMap<>();
	
	public List<Path> repeatedFiles = new ArrayList<>();
	public List<String> corruptedFiles = new ArrayList<>();
	public List<String> logger = new ArrayList<>();
	
	public void createDirToMove() {
		File dir = new File(getToMove());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	
	public FinderModel(String root) {
		this.root = root;
		setToMove();
	}

	public String getRoot() {
		return root;
	}
	
	public void setRoot(String root) {
		this.root = root;
	}
	
	public void setToMove() {
		this.toMove = getRoot() + REPEATED_FILES_DIRECTORY;
	}

	public String getToMove() {
		return toMove;
	}	
	
}
