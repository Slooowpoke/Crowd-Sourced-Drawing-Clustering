package com.coxon.kmeans;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class KMeans {
	
	static String TRAINING_DATA;
	static int INPUT_WIDTH = 25,INPUT_HEIGHT = 20;
//	static int INPUT_WIDTH = 18,INPUT_HEIGHT = 16;
	static int TOTAL_COLOURS = 1;
	static int TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
	static int K = 0;
	static int SAMPLE_SIZE;
	
	enum Status {WAITING,IN_PROGRESS,COMPLETE}
	static Status currentStatus = Status.WAITING;
	
	static Random rnd = new Random();
	
	Trainer trainer;
	Thread trainerThread;
	boolean MNIST;
	
	public KMeans(String dataset,boolean start,boolean colour,boolean earthMovers,int k,int width,int height){

		
		
		if(colour){
			TOTAL_COLOURS = 3;
			TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
		}

		TRAINING_DATA = dataset;
		
		System.out.println("Loading dataset from: '" + TRAINING_DATA + "'");
		if(TRAINING_DATA.equals("MNIST")){
			MNIST = true;
			INPUT_WIDTH = 28;
			INPUT_HEIGHT = 28;
			TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
			SAMPLE_SIZE = 40;
		}else{
			INPUT_WIDTH = width;
			INPUT_HEIGHT = height;
			TRAINING_DATA = "data/sample/" +TRAINING_DATA;
			SAMPLE_SIZE = new File(TRAINING_DATA).listFiles().length;
			TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
		}

		System.out.print("Input image size set: " + INPUT_WIDTH + " x " + INPUT_HEIGHT);
		System.out.print(", Total Colours: " + TOTAL_COLOURS);
		
		System.out.println(", Sample size: " + SAMPLE_SIZE);
		
		// que? (hehe)
		K = k;
		trainer = new Trainer(SAMPLE_SIZE,earthMovers);
		if(start) startTraining();
	}
	
	public void update() throws InterruptedException{
		if(trainer.running == false && currentStatus == Status.IN_PROGRESS){
			changeStatus(Status.COMPLETE);
		}
		Thread.sleep(5);
	}
	
	public void startTraining(){
		// start the trainer thread
    	trainerThread = new Thread(trainer);
    	trainerThread.start();
		changeStatus(Status.IN_PROGRESS);
	}
	
	public void changeStatus(Status status){
		currentStatus = status;
		System.out.println("Changed status: " + status.name());
		if(currentStatus == Status.COMPLETE){
			try {
				trainer.saveTimeLog("KMEANS");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
