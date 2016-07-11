package com.coxon.util;

import java.util.ArrayList;

import com.coxon.som.SOM;

// the bulk and muscle of the dissertation! gruhhh weighted.java smash!
public class Weighted{
	ArrayList<Double> weights = new ArrayList<Double>();

	public double getAverage(){
		// render an average of all weightings, usually for a colour on the som.
		double total = 0;
		for(int i = 0; i < weights.size(); i++){
			total += weights.get(i);
		}
		total/= weights.size();	
		return total;
	}
	
	// if the weighted class contains more than one set of input data, we can segment it into bins and get an average of each one.
	// we can use this for colours
	public double getAverage(int bin,int totalBins){
		// render an average of all weightings, usually for a colour on the som.
		double total = 0;
		// the location of the bin in our list, all bins are presumed to be the same size
		int startBinLocation = (weights.size()/totalBins)*bin;
		for(int i = startBinLocation; i < startBinLocation+(weights.size()/totalBins); i++){
			total += weights.get(i);
		}
		// divide by how many are in this bin
		total/= (weights.size()/totalBins);	
		return total;
	}
	
	// compare to another set of weightings and returns a percentage, binary comparison.
	public double compare(Weighted w){
		int totalSimilar = 0;
		
		// retrieve the other set of weights
		ArrayList<Double> otherWeights = w.getWeights();
		
		// check the two have the same number of weights.
		if(otherWeights.size() == weights.size()){
			for(int i = 0; i < otherWeights.size(); i++){
				// round the weightings, because it's a binary comparison.
				if((int) Math.round(otherWeights.get(i)) == (int) Math.round(weights.get(i))){
					totalSimilar++;
				}
			}
			// at the end of comparison to give a percentage out of 100.
			return  totalSimilar/(double)(weights.size())*100;
		}
		// if anything fails returns 0.
		return 0;
	}
	
	// returns square euclidean distance from another set of weightings.
	public double getDistanceFromSample(Weighted in){
		double summation = 0, temp;
		if(weights.size() == in.getWeights().size()){
			for (int x=0; x < weights.size(); x++) {
				temp = weights.get(x)-in.getWeight(x);
				temp *= temp;
				summation += temp;
			}
		}else{
			System.out.println("Weightings do not match. Input: " + in.getWeights().size() + " Weightings: " + weights.size());
		}

		return summation;
	}
	
	public void addWeight(double d){
		weights.add(d);
	}
	
	public double getWeight(int w){
		return weights.get(w);
	}
	
	public ArrayList<Double> getWeights() {
		return weights;
	}

	public void setWeights(ArrayList<Double> weights) {
		this.weights = weights;
	}
	
	public double getRandomWeight(){
		return SOM.rnd.nextDouble();
	}
	
	public void intialiseToRandom(int totalWeights){
		for(int i = 0; i < totalWeights; i++){
			weights.add(getRandomWeight());
		}
	}
}
