package br.com.wmcodes.repeatedFilesFinder.application;

import java.io.IOException;

import br.com.wmcodes.repeatedFilesFinder.controller.GetFiles;

public class Application {

	public static void main(String[] args) {
		long inicio = System.currentTimeMillis();
		
		GetFiles getFiles = new GetFiles(args[0]);
		
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
