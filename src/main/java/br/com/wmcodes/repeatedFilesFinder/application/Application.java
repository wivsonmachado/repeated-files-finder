package br.com.wmcodes.repeatedFilesFinder.application;

import java.io.IOException;

import br.com.wmcodes.repeatedFilesFinder.controller.GetFiles;
import br.com.wmcodes.repeatedFilesFinder.model.FinderModel;

public class Application {

	public static void main(String[] args) {
		long inicio = System.currentTimeMillis();
		
		FinderModel model = new FinderModel(args[0]);
		
		GetFiles getFiles = new GetFiles(model);
		
		getFiles.captureFiles();
		
		long fim = System.currentTimeMillis();
		long tempo = fim - inicio;
		
		try {
			getFiles.createLogFile(tempo);
		} catch (IOException e) {
			System.out.println(e.getMessage() + ": Not possible create a log file.");
		}
		
	}

}
