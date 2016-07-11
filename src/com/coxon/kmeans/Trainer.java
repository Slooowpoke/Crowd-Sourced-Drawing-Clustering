package com.coxon.kmeans;

import java.io.IOException;
import java.util.ArrayList;

import com.coxon.clusters.Cluster;
import com.coxon.clusters.ClusterFile;
import com.coxon.mnist.InputMNIST;
import com.coxon.util.BaseTrainer;
import com.coxon.util.InputImage;

public class Trainer extends BaseTrainer implements Runnable{

	// stores the location of the current input in the array of inputs in the sample folder.
	int totalInputs = 0;
	
	// stores the total changed since the last iteration, used for determining when this is finished.
	int totalChanged = 0;
	
	// wherther to use earth movers distance for the distance formulas
	boolean earthMovers;
	
	ArrayList<Centroid> centroids = new ArrayList<Centroid>();
	ArrayList<KInput> inputs = new ArrayList<KInput>();
	
	// used for the current input, in the visualiser mostly.
	KInput currentInput;
	Centroid selectedCentroid;

	public Trainer(int totalInputs,boolean earthMovers){
		this.totalInputs = totalInputs;
		this.earthMovers = earthMovers;

		if(KMeans.TRAINING_DATA.equals("MNIST")){
			loadMNIST();

			for(int i = 0; i < totalInputs; i++){
				// find the closest centroid to the input
				InputMNIST w = getMnistData().get(i);
				KInput input = new KInput(w.getLabel(),w);

				inputs.add(input);
			}
		}else{	// load inputs normally.
			// load the inputs, since CPU is too high otherwise
			for(int i = 0; i < totalInputs; i++){
				// find the closest centroid to the input
				InputImage temp = getInputImage(KMeans.TRAINING_DATA,i,KMeans.INPUT_WIDTH,KMeans.INPUT_HEIGHT);
				KInput input = new KInput(temp.getName(),createFromImage(temp.getImage(),KMeans.TOTAL_COLOURS));
				
				input.signature = BaseTrainer.getSignature(input.getWeights(), KMeans.INPUT_WIDTH,KMeans.INPUT_HEIGHT);
				inputs.add(input);
			}
		}
		
		
		// we need a list to store the inputs that have been selected for the clusters intilisation already.
		ArrayList<KInput> picked = new ArrayList<KInput>();
		for(int i = 0; i < inputs.size(); i++){
			picked.add(new KInput(inputs.get(i).getName(),inputs.get(i)));
		}
		
		// intialise random centroids
		for(int i = 0; i < KMeans.K; i++){
			Centroid c = new Centroid(i);
			
			// pick a random input
			KInput in = picked.get(KMeans.rnd.nextInt(picked.size()));
			picked.remove(in);
			
			c.intialiseToInput(in);
			c.cached = in.signature;
			centroids.add(c);
		}
	}
	
	public void run(){
		while(running){
			// iterate and calculate the time per iteration.
			long startTime = System.nanoTime();
			
			if(iteration >= 1){
				for(Centroid currentCentroid:centroids){
					// loop through all of our centroids and remove the signatures that are attached from the last iteration
					currentCentroid.clearSignature();
				}
			}
			
			// loop through all inputs
			for(KInput input:inputs){
				currentInput = input;
				
				// find closest centroid to the input
				int closest = findClosestCentroid(input);
				
				input.lastCluster = input.getCluster();
				input.setCluster(closest);
				// assign the input to the closest cluster
				if(input.getCluster() != input.lastCluster) totalChanged++;
			}
			
			// divide the centroids by how many were assigned.
			for(Centroid c:centroids){
				
				int totalAssigned = 0;
				// find any inputs associated with it
				for(KInput input:inputs){
					if(input.getCluster() == c.cluster) totalAssigned++;
				}
				
				if(totalAssigned > 0){
					c.clearWeights();
				}
				
				for(KInput input:inputs){
					if(input.getCluster() == c.cluster) c.addWeights(input.getWeights());
				}
				// make sure we aren't dividing by zero.
				if(totalAssigned > 0){
					c.divideWeights(totalAssigned);
				}
			}
			
			// if nothing has changed, then the process is finished
			if(totalChanged == 0){
				running = false;
				
				// save the clusters
				ClusterFile file = new ClusterFile("KMEANS",KMeans.TRAINING_DATA,KMeans.TOTAL_COLOURS);
				if(earthMovers){
					file.setDistance("Earth Mover's Distance");
				}
				
				for(Centroid c:centroids){
					Cluster cluster = new Cluster(c.cluster);
					cluster.setDataFromKInput(inputs);
					
					file.addCluster(cluster);
				}
				
				try {
					saveCluster(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			totalChanged = 0;
			iteration++;
			
			long endTime = System.nanoTime();

			logIteration(endTime-startTime);
			//System.out.println("Iteration Duration: " + iterationDuration); 

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int findClosestCentroid(KInput temp) {
		// find the closest centroid and then assign the input number to it.
		double bestDistance = 99999999,currentDistance = 0;
		int bestCluster = -1;

		for(Centroid currentCentroid:centroids){
			long startTime = System.nanoTime();
			
			selectedCentroid = currentCentroid;
			// since EMD can take a while, check if its still running first
			if(!running) break;
			// check if we are using EMD
			if(!earthMovers){// euclidean distance
				currentDistance = currentCentroid.getDistanceFromSample(temp);
			}else{// earth movers distance
				currentDistance = currentCentroid.getEarthMoversDistance(temp);
			}
			System.out.println("Distance from centroid : " + currentCentroid.cluster + " " + currentDistance);
			// if our centroid is null, then assign it straight away because something is better than nothing.
			if(currentDistance < bestDistance){
				bestCluster = currentCentroid.cluster;
				bestDistance = currentDistance;
			}
			long endTime = System.nanoTime();

			System.out.println("TIME TAKEN: " + (endTime-startTime));
		}
		
		return bestCluster;
	}
}
