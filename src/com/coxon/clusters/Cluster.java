package com.coxon.clusters;

import java.util.ArrayList;

import com.coxon.kmeans.KInput;
import com.coxon.mnist.InputMNIST;
import com.coxon.util.Input;
import com.coxon.util.InputImage;

public class Cluster {
	
	private int label;
	
	private ArrayList<Input> data = new ArrayList<Input>();
	
	public Cluster(int label){
		this.setLabel(label);
	}
	
	public void setDataFromKInput(ArrayList<KInput> inputs){
		for(int i = 0; i < inputs.size(); i++){
			KInput input = inputs.get(i);
			if(input.getCluster() == getLabel()){
				data.add(new Input(input.getName()));
			}
		}
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
	
	public ArrayList<Input> getData(){
		return data;
	}

	// this method puts all the data into a string with new lines and such
	public String toString() {
		String text = "";
		for(Input i:data){
			text+= i.getName() + "\n";
		}
		return text;
	}

	// adds the input image to the data list
	public void add(InputImage inputImage) {
		Input i = new Input(inputImage.getName());
		data.add(i);
	}

	public void add(InputMNIST inputMNIST) {
		Input i = new Input(inputMNIST.getLabel());
		data.add(i);
	}

	public void add(Input input) {
		data.add(input);
	}
}
