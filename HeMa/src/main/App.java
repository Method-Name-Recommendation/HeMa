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
	private static String projectDir = null;
	private static int numThreads = 32;

	public static void main(String[] args) {
		projectDir = args[0];
		numThreads = Integer.parseInt(args[1]);
		String trainDir = args[2];

		TrainSet.initialize(trainDir);

		if (projectDir != null) {
			File root = new File(projectDir);
			if(root.exists() && root.isDirectory()) {
				File[] projs = root.listFiles();
				for(File proj : projs) {
					projectDir = proj.getPath();
					extractDir();
				}
			}
			
			Counter.print();
		}
	}

	private static void extractDir() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
		LinkedList<Task> tasks = new LinkedList<>();
		try {
			Files.walk(Paths.get(projectDir)).filter(Files::isRegularFile)
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
