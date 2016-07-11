package com.coxon.mnist;

import com.coxon.util.Weighted;

public class InputMNIST extends Weighted{
	private String label;
	
	public InputMNIST(int label, byte[] imageData) {
		this.setLabel(Integer.toString(label));
		for(int i = 0; i < imageData.length; i++){
			getWeights().add((double) (imageData[i] & 0xff)/255);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
