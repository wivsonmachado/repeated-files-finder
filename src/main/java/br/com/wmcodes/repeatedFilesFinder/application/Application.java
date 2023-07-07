package br.com.wmcodes.repeatedFilesFinder.application;

import br.com.wmcodes.repeatedFilesFinder.controller.FinderFilesUtils;
import br.com.wmcodes.repeatedFilesFinder.model.FinderModel;

public class Application {

	public static void main(String[] args) {
		long inicio = System.currentTimeMillis();
		
		FinderModel model = new FinderModel(args[0]);
		
		FinderFilesUtils getFiles = new FinderFilesUtils(model);
		
		getFiles.captureFiles();
		
		if(!model.repeatedFiles.isEmpty()) {
			getFiles.openMenu(model, inicio);
		}else {
			System.out.println("No repeated files founded.");
		}

		
	}

}
