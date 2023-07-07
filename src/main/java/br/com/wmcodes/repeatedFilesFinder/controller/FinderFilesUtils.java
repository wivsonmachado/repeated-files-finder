package br.com.wmcodes.repeatedFilesFinder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import br.com.wmcodes.repeatedFilesFinder.model.FinderModel;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

public class FinderFilesUtils {
	
	private FinderModel finder;
	
	public FinderFilesUtils(FinderModel model) {
		this.finder = model;
	}
	
	
	public void captureFiles() {
		
		Path path = Paths.get(finder.getRoot());
		
		try(Stream<Path> stream = Files.walk(path, Integer.MAX_VALUE);
				Stream<Path> stream2 = Files.walk(path, Integer.MAX_VALUE)){
			ProgressBarBuilder builder = new ProgressBarBuilder().setTaskName("Analysing Files")
					.setInitialMax(stream.count());
					ProgressBar.wrap(stream2, builder)
						.filter(Files::isRegularFile)
						.forEach(e -> {
							String hash = checksum(e);
							if(hash != null) {
								if(finder.nonRepeatedFiles.isEmpty() || !finder.nonRepeatedFiles.containsValue(hash)) {
									finder.nonRepeatedFiles.put(e, hash);
								}else {
									finder.repeatedFiles.add(e);
								}
							}
						});
					stream.close();
					stream2.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void deleteFiles(FinderModel finder, File logFile) {
		List<Path> filesToDelete = finder.repeatedFiles;
		Stream<Path> stream = filesToDelete.stream();
		try {
			FileUtils.write(logFile, "\n\nDeleted files: " + filesToDelete.size() + "\n", Charset.forName("UTF-8"), true);
		} catch (IOException e1) {
			finder.corruptedFiles.add(finder.corruptedFiles.size(), e1.getMessage());
		}
		ProgressBarBuilder builder = new ProgressBarBuilder().setTaskName("Deleting Files")
				.setInitialMax(filesToDelete.size());
		ProgressBar.wrap(stream, builder)
			.forEach(t -> {
				try {
					Files.deleteIfExists(t);
					FileUtils.write(logFile, "\nFile deleted: " + t.getFileName().toString(), Charset.forName("UTF-8") , true);
					finder.logger.add("File deleted: " + t.getFileName().toString());
				} catch (IOException e) {
					finder.corruptedFiles.add(finder.corruptedFiles.size(), e.getMessage());
				}
			});
		
	}
	
	public void moveFiles(FinderModel finder, File logFile) {
		finder.createDirToMove();
		List<Path> filesToMove = finder.repeatedFiles;
		Stream<Path> stream = filesToMove.stream();
		try {
			FileUtils.write(logFile, "\n\nFiles to move: " + filesToMove.size() + "\n", Charset.forName("UTF-8"), true);
		} catch (IOException e1) {
			finder.corruptedFiles.add(finder.corruptedFiles.size(), e1.getMessage());
		}
		ProgressBarBuilder builder = new ProgressBarBuilder().setTaskName("Moving Files")
				.setInitialMax(filesToMove.size());
		final AtomicInteger count = new AtomicInteger();
		ProgressBar.wrap(stream, builder)
			.map(Path::toFile)
			.forEach(t -> {
				try {
					File dest = new File(finder.getToMove() + count.incrementAndGet()  +t.getName()); 
					FileUtils.moveFile(t, dest);
					FileUtils.write(logFile, "\nFile moved: " + t.getName().toString(), Charset.forName("UTF-8") , true);
					finder.logger.add("File deleted: " + t.getName().toString());
				} catch (IOException e) {
					finder.corruptedFiles.add(finder.corruptedFiles.size(), e.getMessage());
				}
			});
		
	}
	
	public void createLogFile(long elapsedTime) throws IOException {
		File logFile = new File(finder.getRoot() + "Log.txt");
		String logElapsedTime = elapsedTime(elapsedTime);
		FileUtils.write(logFile, logElapsedTime, Charset.forName("UTF-8"));		
		
		if(finder.repeatedFiles.isEmpty()) {
			FileUtils.write(logFile, "\n\nNot found any repeated file.", Charset.forName("UTF-8"), true);
		}else {
			finder.logger.add(0, "Was founded " + finder.repeatedFiles.size() + " repeateds files. \n\n");
			FileUtils.writeLines(logFile, finder.logger, true);
			FileUtils.writeLines(logFile, finder.repeatedFiles, true);			
		}

		if(!finder.corruptedFiles.isEmpty()) {
			finder.logger.add(0, "\n\n\nWas founded " + finder.corruptedFiles.size() + " files that may be corrupted, need attention.\n\n");
			FileUtils.writeLines(logFile, finder.logger, true);
			FileUtils.writeLines(logFile, finder.corruptedFiles,true);
		}
	}
	
	private String elapsedTime(long time) {
		String msg = null;
		Duration duration = Duration.ofMillis(time);

		if (time >= 0 && time <= 1000) {
			msg = "Elapsed " + time + " milliseconds\n\n";
		}

		if (time >= 1001 && time <= 59000) {
			return msg = "Elapsed time " + duration.toSecondsPart() + " seconds\n\n";
		}

		if (time >= 60000 && time <= 3599999) {
			return msg = "Elapsed time" + duration.toMinutesPart() + " Min " + duration.toSecondsPart() + " s\n\n";
		}
		
		if (time >= 3600000) {
			return msg = "Elapsed time" + duration.toHoursPart() + " H " + duration.toMinutesPart() + " Min " + duration.toSecondsPart() + " s\n\n";
		}

		return msg;
	}
	
	private String checksum(Path path) {
		String checksum = null;
		try {
			checksum = DigestUtils.md5Hex(new FileInputStream(path.toString()));
		} catch (IOException e) {
			finder.corruptedFiles.add(e.getMessage() + ". May be file is corrupted: " + path);
			return checksum;
		}
		return checksum;
	}
	
	public void openMenu(FinderModel model, long inicio) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Choose a option: ");
		System.out.println("To create a Log.txt [0]");
		System.out.println("To delete files     [1]");
		System.out.println("To move files       [2]");
		
		int option = sc.nextInt();
		switch(option) {
			case 0:
				long fim = System.currentTimeMillis();
				long tempo = fim - inicio;
				
				try {
					createLogFile(tempo);
				} catch (IOException e) {
					System.out.println(e.getMessage() + ": Not possible create a log file.");
				}
				break;
			case 1:
				long fim1 = System.currentTimeMillis();
				long tempo1 = fim1 - inicio;
				
				try {
					createLogFile(tempo1);
				} catch (IOException e) {
					System.out.println(e.getMessage() + ": Not possible create a log file.");
				}
				deleteFiles(model, new File(model.getRoot() + "Log.txt"));
				break;
			case 2:
				long fim2 = System.currentTimeMillis();
				long tempo2 = fim2 - inicio;
				
				try {
					createLogFile(tempo2);
				} catch (IOException e) {
					System.out.println(e.getMessage() + ": Not possible create a log file.");
				}
				moveFiles(model, new File(model.getRoot() + "Log.txt"));
				break;
				
		}
		
		sc.close();
		
	}
	
	

}
