package com.coxon.clusters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.coxon.util.Input;

public class ClusterValidator {
	
	private static String truth = "data/clusters/DRAWINGS_TRUTH.txt";
	
	public static void main(String args[]){
		// loads in the ground truth
		ClusterFile groundTruth = loadClusters(truth);
		ClusterFile output = loadClusters("data/clusters/KMEANS/DRAWINGS B&W EMD.cluster");
		
		int[][] contingencyTable = new int[groundTruth.getClusters().size()][output.getClusters().size()];
		
		int n = 0;
		for(int i = 0; i < groundTruth.getClusters().size(); i++){
			Cluster t = groundTruth.getClusters().get(i);
			n+= t.getData().size();
		}
		
		for(int i = 0; i < groundTruth.getClusters().size(); i++){
			Cluster t = groundTruth.getClusters().get(i);
			for(int j = 0; j < output.getClusters().size(); j++){
				Cluster c = output.getClusters().get(j);
				// nest the loop searching through t to check similar inputs and build a contingency table
				for(Input truthInput:t.getData()){
					for(Input input:c.getData()){
						if(input.getName().equals(truthInput.getName())){
							contingencyTable[i][j]++;
						}
					}
				}
			}
		}
		
		// calculates the a column of similar values, changes per row and prints out currently.
		double a = 0,b = 0;
		
		double A = 0,B = 0,NIJ = 0;
		for(int x = 0; x < groundTruth.getClusters().size(); x++){
			for(int y = 0; y < output.getClusters().size(); y++){
				System.out.print(contingencyTable[x][y]);
				a+=contingencyTable[x][y];
				System.out.print("-");
			}
			System.out.print("-" + a);
			A +=combination(a);
			a = 0;
			System.out.println();
		}
		
		for(int y = 0; y < output.getClusters().size(); y++){
			System.out.print("-");
		}
		System.out.println();
		
		for(int y = 0; y < output.getClusters().size(); y++){
			for(int x = 0; x < groundTruth.getClusters().size(); x++){
				b+=contingencyTable[x][y];
			}
			B +=combination(b);
			System.out.print(b + "-");
			b = 0;
		}
		// because they are all the same.
		NIJ = A;
		System.out.println();
		System.out.println("A: " + A);
		System.out.println("B: " + B);
		System.out.println("NIJ: " + NIJ);
		
		System.out.println("N: " + n);
		double N = combination(n);
		
		double ARI = (NIJ-((A*B)/N))/((A+B)/2)-((A*B)/N);
		System.out.println(ARI);
	}
	
	
	// a hackey way of calculating the all the combinations needed for the project.
	public static double combination(double n){
		if(n == 1){
			return 0.5;
		}else if(n == 2){
			return 1;
		}else if(n == 3){
			return 3;
		}else if(n == 4){
			return 6;
		}else if(n == 5){
			return 10;
		}else if(n == 6){
			return 15;
		}else if(n == 7){
			return 21;
		}else if(n == 8){
			return 28;
		}else if(n == 9){
			return 36;
		}else if(n == 10){
			return 45;
		}else if(n == 11){
			return 55;
		}else if(n == 12){
			return 66;
		}else if(n == 13){
			return 78;
		}else if(n == 14){
			return 91;
		}else if(n == 15){
			return 105;
		}else if(n == 16){
			return 120;
		}else if(n == 17){
			return 136;
		}else if(n == 18){
			return 153;
		}else if(n == 19){
			return 171;
		}else if(n == 20){
			return 190;
		}else if(n == 21){
			return 210;
		}else if(n == 22){
			return 231;
		}else if(n == 23){
			return 253;
		}else if(n == 24){
			return 276;
		}else if(n == 25){
			return 300;
		}else if(n == 26){
			return 325;
		}else if(n == 27){
			return 351;
		}else if(n == 28){
			return 378;
		}else if(n == 29){
			return 406;
		}else if(n == 30){
			return 435;
		}else if(n == 31){
			return 465;
		}else if(n == 32){
			return 496;
		}else if(n == 33){
			return 528;
		}else if(n == 34){
			return 561;
		}else if(n == 35){
			return 595;
		}else if(n == 36){
			return 630;
		}else if(n == 37){
			return 666;
		}else if(n == 38){
			return 703;
		}else if(n == 39){
			return 741;
		}else if(n == 40){
			return 780;
		}
		return 0.0;
	}
	
//	public static double calculateCombination(double m){
//		// needs a pre-calculated list of combination.
//		// m is the top number of the combination
//		m = factorial(m);
//		
//		// the bottom denominator
//		double k = 2;
//
//		k = 2 * factorial(m-k);
//		
//		m = m/k;
//		System.out.print(m);
//		return m;
//	}
//	
//	// Evaluate n!
//    public static double factorial(double n){
//        if( n <= 1 )     // base case
//            return 1;
//        else
//            return n * factorial( n - 1 );
//    }
	
	public static ClusterFile loadClusters(String location){
		try {
			File file = new File(location);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String temp;
			int lineCounter = 0;

			// declares the cluster file
			ClusterFile clusterFile = new ClusterFile("", "", lineCounter);
			
			// create an arraylist to store the lines
			ArrayList<String> lines = new ArrayList<String>();
			
			// read in all the lines
			while ((temp = bufferedReader.readLine()) != null) {
				lines.add(temp);
			}
			// close the reader
			fileReader.close();
			
			// counter for cluster labels
			int clusterCounter = 0;
			// loop through all the lines
			for(int i = 0; i < lines.size(); i++){
				// get an individual line
				String line = lines.get(i);
				
				if(line.contains("CLUSTER:")){
					// make a new cluster if the line contains CLUSTER
					Cluster c = new Cluster(clusterCounter);
					// while the next line is not a cluster and lines+1 is less than lines.size
					while(i+1 < lines.size() && !lines.get(i+1).contains("CLUSTER:")){
						if(!lines.get(i+1).equals("")){
							c.add(new Input(lines.get(i+1)));
						}
						i++;
					}
					clusterFile.addCluster(c);
					clusterCounter++;
				}
			}
			
			return clusterFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
