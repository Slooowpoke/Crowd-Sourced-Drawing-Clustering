package com.coxon.util;


public class Neuron extends Weighted{
	public float x,y;
	
	public Neuron(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	// adjust the weights to be more like a given sample, take into account the learning rate and distance from best matching unit.
	public void adjustWeights(Weighted input,double learningRate,double distance){
		// loop through all the weights, they should be the same as the input.
		for(int i = 0; i < weights.size(); i++){
			// get the current weight
			double currentWeight = weights.get(i);
			// adjust the current weight to be more similar to the input weight.
			currentWeight += distance * learningRate * (input.getWeight(i)-currentWeight);
			// set the weighting to be this.
			weights.set(i, currentWeight);
		}
	}
	
	// returns the distance squared.
	public double getDistance(Neuron n){
		return ((x-n.x)*(x-n.x)) + ((y-n.y)*(y-n.y));	
	}
	
}
