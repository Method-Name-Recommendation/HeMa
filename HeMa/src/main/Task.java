package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.github.javaparser.ParseException;

public class Task implements Callable<Void> {
	String code = null;
	

	public Task(Path path) {
		try {
			this.code = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
			this.code = "";
		}
	}

	@Override
	public Void call() throws Exception {
		try {
			FileParser featureExtractor = new FileParser(code);
			featureExtractor.extractFeatures();
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
