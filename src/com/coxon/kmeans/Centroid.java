package com.coxon.kmeans;

import java.util.ArrayList;

import com.coxon.emd.JFastEMD;
import com.coxon.emd.Signature;
import com.coxon.util.BaseTrainer;
import com.coxon.util.Weighted;

public class Centroid extends Weighted{
	// which centroid is this?
	int cluster;
	Signature cached;

	public Centroid(int cluster){
		this.cluster = cluster;
	}
	
	public void addWeights(ArrayList<Double> nWeights) {
		for(int i = 0; i < getWeights().size(); i++){
			double value = getWeights().get(i)+nWeights.get(i);
			getWeights().set(i, value);
		}
	}

	public void divideWeights(int inputsAssigned) {
		for(int i = 0; i < getWeights().size(); i++){
			getWeights().set(i, getWeights().get(i)/inputsAssigned);
		}
	}

	public void clearWeights() {
		for(int i = 0; i < getWeights().size(); i++){
			getWeights().set(i,0.0);
		}
	}

	public void intialiseToInput(Weighted weighted) {
		for(double d: weighted.getWeights()){
			addWeight(d);
		}
	}

	// this creates a signature for our current centroid and then 
	public double getEarthMoversDistance(KInput temp) {
		// get the signature for our weights
		if(cached == null){
			System.out.println("Cached Signature is null, re-generating.");
			cached = BaseTrainer.getSignature(getWeights(), KMeans.INPUT_WIDTH, KMeans.INPUT_HEIGHT);
		}
		
		return JFastEMD.distance(cached, temp.signature, 0.1);
	}

	public void clearSignature() {
		cached = null;
	}

}
