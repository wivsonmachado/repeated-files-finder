package br.com.wmcodes.repeatedFilesFinder.application;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import br.com.wmcodes.repeatedFilesFinder.controller.FinderFilesUtils;
import br.com.wmcodes.repeatedFilesFinder.model.FinderModel;

public class Application {

	public static void main(String[] args) {
		long inicio = System.currentTimeMillis();
		
		FinderModel model = new FinderModel(args[0]);
		
		FinderFilesUtils getFiles = new FinderFilesUtils(model);
		
		getFiles.captureFiles();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Choose a option: ");
		System.out.println("Create a log: 0");
		System.out.println("Delete: 1");
		System.out.println("Move: 2");
		
		int option = sc.nextInt();
		switch(option) {
			case 0:
				long fim = System.currentTimeMillis();
				long tempo = fim - inicio;
				
				try {
					getFiles.createLogFile(tempo);
				} catch (IOException e) {
					System.out.println(e.getMessage() + ": Not possible create a log file.");
				}
				break;
			case 1:
				long fim1 = System.currentTimeMillis();
				long tempo1 = fim1 - inicio;
				
				try {
					getFiles.createLogFile(tempo1);
				} catch (IOException e) {
					System.out.println(e.getMessage() + ": Not possible create a log file.");
				}
				getFiles.deleteFiles(model, new File(model.getRoot() + "Log.txt"));
				break;
			case 2:
				System.out.println("Not implemented yet");
				
		}
		
		sc.close();
		
		

		
	}

}
