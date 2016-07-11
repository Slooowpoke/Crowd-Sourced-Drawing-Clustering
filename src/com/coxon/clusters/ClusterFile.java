package com.coxon.clusters;

import java.util.ArrayList;

public class ClusterFile {
	private String location,dataset;
	private String distance = "Euclidean Distance";
	private boolean colour;
	
	int notClustered = 0;
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	
	public ClusterFile(String location,String dataset, int TOTAL_COLOURS){
		this.setLocation(location);
		this.setDataset(dataset);
		if(TOTAL_COLOURS == 3){
			colour = true;
		}
	}
	
	public void addCluster(Cluster c){
		getClusters().add(c);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public boolean isColour() {
		return colour;
	}

	public void setColour(boolean colour) {
		this.colour = colour;
	}
}
