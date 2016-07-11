package com.coxon.som;

import java.util.ArrayList;
import java.util.List;

import com.coxon.mnist.InputMNIST;
import com.coxon.util.BaseTrainer;
import com.coxon.util.InputImage;
import com.coxon.util.Neuron;
import com.coxon.util.Weighted;

public class Trainer extends BaseTrainer implements Runnable{
	
	int totalIterations;
	
	double exciteRadius,learningRate,intialLearningRate,time;
	
	boolean exciteOverride;
	
	Neuron lastBestMatching;
	
	// our local lattice.
	Lattice lattice;
	
	List<InputMNIST> dataset = new ArrayList<InputMNIST>();
	
	boolean MNIST;
	
	// shows the current input
	Weighted currentInput;
	
	// intialise our trainer
	public Trainer(Lattice lattice,boolean MNIST, double intialLearningRate){
		this.lattice = lattice;
		this.MNIST = MNIST;
		this.intialLearningRate = intialLearningRate;
		learningRate = intialLearningRate;
		totalIterations = 500*lattice.size/2;
		
		if(MNIST) loadMNIST();
		
		System.out.println("Intialised trainer with learning rate: " + intialLearningRate);
	}
	
	public Trainer(Lattice lattice,boolean MNIST, double intialLearningRate,boolean exciteOverride){
		this.lattice = lattice;
		this.MNIST = MNIST;
		this.intialLearningRate = intialLearningRate;
		this.exciteOverride = exciteOverride;
		learningRate = intialLearningRate;
		totalIterations = 500*lattice.size/2;
		
		if(MNIST) loadMNIST();
		
		System.out.println("Intialised trainer with learning rate: " + intialLearningRate + " and a limited excitement radius.");
	}
	
	// the main iterative loop, we can count the time taken per iteration in here.
	public void run() {
		while(running){
			// iterate and calculate the time per iteration.
			long startTime = System.nanoTime();

			time = totalIterations / Math.log(lattice.size);
			Weighted input = null;
			// fetch a random input from the input folder
			if(!MNIST){
				// if its not the MNIST dataset, then load a random input from our data.
				input = createFromImage(getRandomInput().getImage(),SOM.TOTAL_COLOURS);
			}else{
				input = getNextMNIST();
			}
			
			currentInput = input;
			
			// exciteoverride is used for phase two.
			if(!exciteOverride){
				exciteRadius = (lattice.size) * Math.exp(-iteration/time);
			}else{
				exciteRadius = 1.2;
			}
			
			// give the input to the lattice to find the BMU
			if(lattice != null){
				lastBestMatching = lattice.getBMU(input);
				// adjust weights for the BMU and the lattice!	
				// give the lattice the BMU, the input and the learning rate
				lattice.adjustNearby(lastBestMatching,input,learningRate,exciteRadius);
			}
			
			iteration++;
			
			// if our learning rate is less than 0.1, and our intial learning rate is 0.9, then automatically kill the operation.
			if(learningRate < 0.1 && intialLearningRate == 0.9){
				// if it dips below 0.1 during the time, force the training to end.
				iteration = totalIterations;
			}
			
			// set the learning rate to be a decreasing exponetial.
			learningRate = intialLearningRate * Math.exp(-(double)iteration/totalIterations);
			
			// if the iteration is equal to the total iterations we're done! running = false.
			if(iteration == totalIterations){
				running = false;
			}
			
			
			// calculate iteration time before sleeping.
			long endTime = System.nanoTime();
			logIteration(endTime-startTime);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public InputImage getRandomInput(){
		// get a random input from the folder
		int inputLocation = SOM.rnd.nextInt(SOM.SAMPLE_SIZE);
		return getInputImage(SOM.TRAINING_DATA,inputLocation,SOM.INPUT_WIDTH,SOM.INPUT_HEIGHT);
	}
}
