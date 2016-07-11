package com.coxon.som;

import java.util.ArrayList;

import com.coxon.util.Neuron;
import com.coxon.util.Weighted;

public class Lattice {
	ArrayList<Neuron> nodes = new ArrayList<Neuron>();
	int size;
	
	public Lattice(int size,int totalWeights){
		this.size = size;
		
		// intialise our array of nodes
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Neuron n = new Neuron(x,y);
				n.intialiseToRandom(totalWeights);
				nodes.add(n);
			}
		}
		System.out.println("Created lattice with size: " + size + " x " + size + " and " + totalWeights + " total weights per node.");
	}

	public void adjustNearby(Neuron BMU,Weighted input,double learningRate,double radius) {
		// loop through all the nodes and check if the distance is smaller than the excitement radius.
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				// if the distance to the best matching unit is less than the excitement radius squared (since it's 2D).
				if(getNode(x,y).getDistance(BMU) < (radius*radius)){// gets a literal distance, not a sample distance.
					// this value is used to work out how much to adjust the weights by based on their distance away from the best matching unit, we need to sqaure the radius before passing it in because its 2D.
					double distanceFallOff = distanceFromBMU(getNode(x,y).getDistance(BMU), (radius*radius));		
					// feed in the input vector, the learning rate and the distance from the BMU.
					getNode(x,y).adjustWeights(input, learningRate,distanceFallOff);
				}
			}
		}
	}
	
	private double distanceFromBMU(double distanceSquared, double radiusSquared) {
		return Math.exp(-(distanceSquared)/((radiusSquared)));
	}
	
	public Neuron getNode(int x,int y){
		for(Neuron n:nodes){
			if(n.x == x && n.y == y){
				return n;
			}
		}
		return null;
	}
	
	public Neuron getBMU(Weighted input){
		// get the best neuron randomly for the start
		Neuron best = getNode(SOM.rnd.nextInt(size),SOM.rnd.nextInt(size));
		// calculate the current best distance needed to beat to be better than our random best unit.
		double currentDistance,bestDistance = best.getDistanceFromSample(input);
		
		// loop through and find the one with the shortest distance from our weights
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				currentDistance = getNode(x,y).getDistanceFromSample(input);
				// if the distance from the sample is smaller than the best matching unit, then yaaaaaay its our new BMU.
				if (currentDistance < bestDistance) {
					best = getNode(x,y);
					bestDistance = currentDistance;
				}
			}
		}
		return best;
	}
}
