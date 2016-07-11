package com.coxon.kmeans;

import com.coxon.emd.Signature;
import com.coxon.util.BaseTrainer;
import com.coxon.util.Weighted;

public class KInput extends Weighted{
	private int cluster = 99;
	int lastCluster = 0;
	int x,y;
	
	private String name;
	
	// for use with the earth mover's distance, a better version of a histogram
	Signature signature;
	
	public KInput(String name,Weighted in){
		this.setName(name);
		for(int i = 0; i < in.getWeights().size(); i++){
			addWeight(in.getWeight(i));
//			System.out.print(in.getWeight(i));
		}
		
		signature = BaseTrainer.getSignature(getWeights(), KMeans.INPUT_WIDTH, KMeans.INPUT_HEIGHT);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
}
