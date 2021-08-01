package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import util.Counter;
import util.TrainSet;

public class App {
	private static String evaluationDir = null;
	private static int numThreads = 32;
	private static String dataDir = null;

	public static void main(String[] args) {
		evaluationDir = args[0];
		numThreads = Integer.parseInt(args[1]);
		dataDir = args[2];

		
		if (evaluationDir != null) {
			File root = new File(evaluationDir);
			if(root.exists() && root.isDirectory()) {
				TrainSet.initialize(dataDir);
				
				File[] projs = root.listFiles();
				for(File proj : projs) {
					evaluationDir = proj.getPath();
					extractDir();
				}
				
				Counter.print();
			}
		}
	}

	private static void extractDir() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
		LinkedList<Task> tasks = new LinkedList<>();
		try {
			Files.walk(Paths.get(evaluationDir)).filter(Files::isRegularFile)
					.filter(p -> p.toString().toLowerCase().endsWith(".java")).forEach(f -> {
						Task task = new Task(f);
						tasks.add(task);
					});
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}
}
