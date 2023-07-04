package br.com.wmcodes.repeatedFilesFinder.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinderModel {
	
	private static String REPEATED_FILES_DIRECTORY = "/RepeatedFiles/";
	
	private String root;
	private String toMove = root + REPEATED_FILES_DIRECTORY;
	
	public Map<Path, String> nonRepeatedFiles = new HashMap<>();
	
	public List<String> comparingOriginalFiles = new ArrayList<>();
	public List<String> corruptedFiles = new ArrayList<>();
	
	
	public FinderModel(String root) {
		this.root = root;
	}

	public String getRoot() {
		return root;
	}
	
	public void setRoot(String root) {
		this.root = root;
	}
	
	public String getToMove() {
		return toMove;
	}	
	
}
