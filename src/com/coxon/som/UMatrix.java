package com.coxon.som;

import com.coxon.util.Neuron;

public class UMatrix {

	double nodes[][];
	
	private int size;
	private final double original = 2.0;
	private final double empty = 0.0;
	private final double average = 3.0;

	public UMatrix(Lattice lattice){
		this.setSize(lattice.size+(lattice.size-1));// add size-1 to create a larger lattice for the u-matrix
		
		// create the array of nodes for the u-matrix
		nodes = new double[getSize()][getSize()];
		
		// create a temporary array of our lattice, with the new size.
		Neuron [][] temp = new Neuron[getSize()][getSize()];
		for(int x = 0; x < getSize(); x++){
			for(int y = 0; y < getSize(); y++){
				temp[x][y] = new Neuron(x,y);
			}
		}
		
		// first we must mark all the locations on the u-matrix, from the old lattice.
		for(int x = 0; x < lattice.size; x++){
			for(int y = 0; y < lattice.size; y++){
				// mark every other node as an original node.
				nodes[x*2][y*2] = original;
				temp[x*2][y*2] = lattice.getNode(x, y);
				
				// mark x*2+1 as an average and do the same for the y-axis.
				if((x*2)+1 < getSize() && (y*2)+1 < getSize()){
					nodes[(x*2)+1][(y*2)+1] = average;
				}
			}
		}
		
		// loop through our new u-matrix,
		for(int x = 0; x < getSize(); x++){
			for(int y = 0; y < getSize(); y++){
				// if the node is empty, it has not been populated by the SOM, thus it must be a new node for the u-matrix.
				if(nodes[x][y] == empty){
					if(x-1 >= 0 && x+1 < getSize()
							&& nodes[x+1][y] == original
							&& nodes[x-1][y] == original){
						// if the nodes to the left and right are both original nodes, then the value of this node is equal to the distance between the two originals
						nodes[x][y] = temp[x-1][y].getDistanceFromSample(temp[x+1][y]);
					}
					
					// do the same for the y-axis.
					if(y-1 >= 0 && y+1 < getSize()
							&& nodes[x][y-1] == original
							&& nodes[x][y+1] == original){
						nodes[x][y] = temp[x][y-1].getDistanceFromSample(temp[x][y+1]);
					}
				}
			}
		}
		
		for(int x = 0; x < getSize(); x++){
			for(int y = 0; y < getSize(); y++){
				// calculate the average of surrounding nodes and fill the empty ones with it.
				if(nodes[x][y] == average){
					// calculate the x and y's total first.
					double total = 0;
					double nodesAdded = 0;
					if(y-1 >= 0 && y+1 < getSize() ){
						total += nodes[x][y-1] + nodes[x][y+1]; 
						nodesAdded += 2;
					}
					if(x-1 >= 0 && x+1 < getSize()){
						total += nodes[x-1][y] + nodes[x+1][y]; 
						nodesAdded += 2;
					}
					
					// then divide by the total.
					if(total != 0 && nodesAdded != 0){
						total /= nodesAdded;
						nodes[x][y] = total; 
					}
				}
			}
		}
	}
	
	public double getValue(int x,int y){
			return nodes[x][y];
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
