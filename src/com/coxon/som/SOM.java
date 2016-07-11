package com.coxon.som;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.coxon.clusters.Cluster;
import com.coxon.clusters.ClusterFile;
import com.coxon.mnist.InputMNIST;
import com.coxon.util.Neuron;
import com.coxon.util.Weighted;

public class SOM {
	// decides the number of colours to sample
	static int TOTAL_COLOURS = 1;
	// the location of our training data
	static String TRAINING_DATA;
	// the width an height of our inputs
	static int INPUT_WIDTH = 32, INPUT_HEIGHT = 26;
	static int SAMPLE_SIZE;
	// the total number of weightings per node in the lattice, defined by the width*height*number of colours
	public static int TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
    
	enum Status {WAITING,IN_PHASE_ONE,IN_PHASE_TWO,COMPLETE}
	static Status currentStatus = Status.WAITING;
	
	public static Random rnd = new Random();
	
	Lattice lattice;
	Trainer trainer;
	Thread trainerThread;
	
	final double PHASE_ONE_LEARNING = 0.9, PHASE_TWO_LEARNING = 0.1;
	
	// if we are using the MNIST dataset in this training.
	boolean MNIST;
	
	// the u-matrix variable
	UMatrix matrix;
	
	public SOM(String dataset,boolean start,boolean colour, double threshold,int width,int height) throws InterruptedException{
		System.out.println("Starting SOM...");
		TRAINING_DATA = dataset;
		this.threshold = threshold;
		System.out.println("USING THRESHOLD " + threshold);
		
		if(TRAINING_DATA.equals("MNIST")){
			MNIST = true;
		}else{
			INPUT_WIDTH = width;
			INPUT_HEIGHT = height;
		}
		if(colour){
			TOTAL_COLOURS = 3;
			TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
		}
		int latticeSize;
		
		if(MNIST){
			INPUT_WIDTH = 28;
			INPUT_HEIGHT = 28;
			TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
			SAMPLE_SIZE = 40;
			latticeSize = 20;
		}else{
			TRAINING_DATA = "data/sample/" +TRAINING_DATA;
			SAMPLE_SIZE = new File(TRAINING_DATA).listFiles().length;
			TOTAL_WEIGHTS = (INPUT_WIDTH*INPUT_HEIGHT)*TOTAL_COLOURS;
	    	latticeSize = (int) Math.sqrt((new File(TRAINING_DATA).listFiles().length))*3;
		}
		
		System.out.println("Loading dataset from: '" + TRAINING_DATA + "'");
		System.out.print("Input image size set: " + INPUT_WIDTH + " x " + INPUT_HEIGHT);
		System.out.print(", Total Colours: " + TOTAL_COLOURS);
		System.out.println(", Sample size: " + SAMPLE_SIZE);

    	// declare the size of the lattice based on the length of the training data folder, multiply by three to yield three nodes per training input.

    	// the more nodes for training input the easier to differentiate between
    	
    	// the size denotes the length of one side of the square lattice.
    	lattice = new Lattice(latticeSize,TOTAL_WEIGHTS);

    	// intialise the matrix.
    	matrix = new UMatrix(lattice);
    	
    	if(start)	startTraining(Status.IN_PHASE_ONE,PHASE_ONE_LEARNING);
	}
	
	public void update() throws InterruptedException{
		matrix = new UMatrix(lattice);

		if(trainer != null && trainer.iteration == trainer.totalIterations){
			// training is over for the first phase?
			if(currentStatus == Status.IN_PHASE_ONE){
				ArrayList<Long> savedTimes = trainer.iterationTimes;
				trainer = new Trainer(lattice,MNIST,PHASE_TWO_LEARNING,true);
				trainer.iterationTimes = savedTimes;
		    	// start the trainer thread
		    	trainerThread = new Thread(trainer);
		    	trainerThread.start();
				changeStatus(Status.IN_PHASE_TWO);
			}else if(currentStatus == Status.IN_PHASE_TWO){
				// training is over hurray!
				changeStatus(Status.COMPLETE);
				segment();
			}
		}
			
		Thread.sleep(5);
	}
	
	public void startTraining(Status status,double learningRate){
    	System.out.println("Starting training.");
    	// create the trainer.
    	trainer = new Trainer(lattice,MNIST,learningRate);
    	// start the trainer thread
    	trainerThread = new Thread(trainer);
    	trainerThread.start();
    	changeStatus(status);
	}
	
	double temp[][];
	double threshold = 0.905;
	
	// this method creates the clusters from our u-matrix
	public void segment(){
		// create a temporary copy of the matrix
		temp = new double[matrix.getSize()][matrix.getSize()];
			
		// copy over the matrix
		for(int y = 0; y < matrix.getSize(); y++){
			for(int x = 0; x < matrix.getSize(); x++){			
				temp[x][y] = matrix.getValue(x, y)/255;
				if(temp[x][y] < threshold){
					temp[x][y] = 0.0;
				}
			}
		}
		// create an arraylist of clusters
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		int label = 10;
		
		// work through the matrix and check the best matching units locations to see if they are below the threshold.
		for(int i = 0; i < SAMPLE_SIZE; i++){
			Weighted input;
			if(!MNIST){
				input = trainer.createFromImage(trainer.getInputImage(SOM.TRAINING_DATA,i,SOM.INPUT_WIDTH,SOM.INPUT_HEIGHT).getImage(),SOM.TOTAL_COLOURS);
			}else{
				input = trainer.getNextMNIST();
			}
			Neuron BMU = lattice.getBMU(input);
			// get the x and y and multiply by two because the u-matrix is bigger than the lattice.
			int x = (int)(BMU.x*2);
			int y = (int)(BMU.y*2);
			if(temp[x][y] < threshold){
				// try filling without checking for the starting location.
				fill(x,y, temp,label);// starts filling.
				clusters.add(new Cluster(label-10));
				label++;
			}
		}
		System.out.println("Total clusters " + label + " total inputs" + SAMPLE_SIZE) ;
		// now retrieve the best matching unit from the SOM for each input, multiply its location by two for its location in the temporary array.
		for(int i = 0; i < SAMPLE_SIZE; i++){
			Weighted input;
			InputMNIST inputMNIST = null;
			if(!MNIST){
				input = trainer.createFromImage(trainer.getInputImage(SOM.TRAINING_DATA,i,SOM.INPUT_WIDTH,SOM.INPUT_HEIGHT).getImage(),SOM.TOTAL_COLOURS);
			}else{
				inputMNIST = trainer.getNextMNIST();
				input = inputMNIST;
			}
			Neuron BMU = lattice.getBMU(input);
				
			// get the x and y and multiply by two because the u-matrix is bigger than the lattice.
			int x = (int)(BMU.x*2);
			int y = (int)(BMU.y*2);
				
			// now we retrieve the label at x and y
				// now we have a cluster label for this input, get the cluster related to it and plop it inside it.
			System.out.println("LABEL: " + (temp[x][y]-10));// subtract 10 because we offset the temp array labeler with 10 to avoid mis-classifiying
			if(!MNIST){
				clusters.get((int) (temp[x][y]-10)).add(trainer.getInputImage(SOM.TRAINING_DATA,i,SOM.INPUT_WIDTH,SOM.INPUT_HEIGHT));
			}else{ 
				clusters.get((int) (temp[x][y]-10)).add(inputMNIST);
			}
		}
		
		// now we have the clusters, create a cluster file and save them.
		ClusterFile file = new ClusterFile("SOM",TRAINING_DATA,SOM.TOTAL_COLOURS);
		for(int i = 0; i < clusters.size(); i++){
			file.addCluster(clusters.get(i));
		}
		try {
			trainer.saveCluster(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fill(int x,int y,double temp[][],int label){
		if(temp[x][y] < threshold){
			temp[x][y] = label;
		}else{
			System.out.println("node is out of range of threshold." + temp[x][y]);
		}
		if(x-1 >= 0){
			if(temp[x-1][y] < threshold){
				fill(x-1,y,temp,label);
				//System.out.println("filled space" + (x-1) + "," + y + " with " + label);
			}
		}
		if(x+1 < matrix.getSize()){
			if(temp[x+1][y] < threshold){
				fill(x+1,y,temp,label);
				//System.out.println("filled space" + x+1 + "," + y + " with " + label);
			}
		}
		
		if(y-1 >= 0){
			if(temp[x][y-1] < threshold){
				fill(x,y-1,temp,label);
				//System.out.println("filled space" + x + "," + (y-1) + " with " + label);
			}
		}
		if(y+1 < matrix.getSize()){
			if(temp[x][y+1] < threshold){
				fill(x,y+1,temp,label);
				//System.out.println("filled space" + x + "," + y+1 + " with " + label);
			}
		}
	}
	
	public void changeStatus(Status status){
		currentStatus = status;
		System.out.println("Changed status: " + status.name());
		if(currentStatus == Status.COMPLETE){
			try {
				trainer.saveTimeLog("SOM");
				
				// update the u-matrix
				matrix = new UMatrix(lattice);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
