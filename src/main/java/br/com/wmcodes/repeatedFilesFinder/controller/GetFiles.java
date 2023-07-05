package br.com.wmcodes.repeatedFilesFinder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import br.com.wmcodes.repeatedFilesFinder.model.FinderModel;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

public class GetFiles {
	
	private FinderModel finder;
	
	public GetFiles(FinderModel model) {
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
									finder.createDirToMove();
									finder.comparingOriginalFiles.add(e.getFileName().toString());
								}
							}
						});			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void createLogFile(long elapsedTime) throws IOException {
		File logFile = new File(finder.getToMove() + "Log.txt");
		String logElapsedTime = elapsedTime(elapsedTime);
		FileUtils.write(logFile, logElapsedTime, Charset.forName("UTF-8"));		
		
		if(finder.comparingOriginalFiles.isEmpty()) {
			FileUtils.write(logFile, "\n\nNot found any repeated file.", Charset.forName("UTF-8"), true);
		}else {
			finder.comparingOriginalFiles.add(0, "Was founded " + finder.comparingOriginalFiles.size() + " repeateds files. ");
			FileUtils.writeLines(logFile, finder.comparingOriginalFiles, true);			
		}

		if(!finder.corruptedFiles.isEmpty()) {
			finder.corruptedFiles.add(0, "\n\n\nWas founded " + finder.corruptedFiles.size() + " files that may be corrupted, need attention.\n\n");
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
	
	

}
