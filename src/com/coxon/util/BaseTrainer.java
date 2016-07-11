package com.coxon.util;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coxon.clusters.Cluster;
import com.coxon.clusters.ClusterFile;
import com.coxon.emd.Feature2D;
import com.coxon.emd.Signature;
import com.coxon.mnist.InputMNIST;
import com.coxon.mnist.LoadMNIST;

public class BaseTrainer {
	
	public boolean running = true;
	
	public int iteration;
	
	// calculates the time taken for the last iteration
	public long iterationDuration;
	
	public ArrayList<Long> iterationTimes = new ArrayList<Long>();
	
	LoadMNIST mnistLoader;
	private List<InputMNIST> mnistData = new ArrayList<InputMNIST>();
	int mnistCounter;

	public InputImage getInputImage(String folderLocation,int inputLocation,int width,int height){
		// get a random input from the folder
		File [] files = new File(folderLocation).listFiles();
		// blank name for our return input
		InputImage input = new InputImage("");
				
		// check if the input is a file
		if(files[inputLocation].isFile()){
			// remove the file extension to create the name.
			String name = files[inputLocation].getName().substring(0,files[inputLocation].getName().indexOf("."));
			String location =  folderLocation + "/"+ files[inputLocation].getName();
			
			input.setName(name);
			input.create(location, 0, 0,width,height);
	    }
		
		return input;
	}
	
	// creates a "weighted" class from a bufferedimage
	public Weighted createFromImage(BufferedImage image,int totalColours){
		Weighted w = new Weighted();
		
		BufferedImage bImage = (BufferedImage) image;
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		// create the weightings for this image.
		for(int colour = 1; colour <= totalColours; colour++){
			for(int i = height-1; i >= 0; i--){
		    	for(int e =width-1; e >= 0; e--){
		    		int clr =  bImage.getRGB(e,i); 
		    		
		    		if(colour == 1){
		    			double  r   = (clr & 0x00ff0000) >> 16;
		    			w.addWeight(r/255);
		    		}else if(colour == 2){
		    			double  g = (clr & 0x0000ff00) >> 8;
		    			w.addWeight(g/255);
		    		}else if(colour == 3){
		    			double  b  =  clr & 0x000000ff;
		    			w.addWeight(b/255);
		    		}
			    }
		    }
	    }	
		return w;
	}
	
	public boolean loadMNIST(){
		mnistLoader = new LoadMNIST("data/sample/mnist/t10k-labels.idx1-ubyte", "data/sample/mnist/t10k-images.idx3-ubyte");
		try {
			setMnistData(mnistLoader.load());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public InputMNIST getNextMNIST() {
		InputMNIST w = getMnistData().get(mnistCounter);
		if(mnistCounter < getMnistData().size()-1){
			mnistCounter++;
		}else{
			mnistCounter = 0;
		}
		return w;
	}
	
	public static Signature getSignature(ArrayList<Double> weights, int width,int height){	
		Signature signature = new Signature();
		signature.setNumberOfFeatures(weights.size());
		signature.setFeatures(new Feature2D[weights.size()]);
        signature.setWeights(new double[weights.size()]);
        int i = 0;
	    for(int x = 0; x < width; x++){
	    	for(int y = 0; y < height; y++){
				double val = weights.get(i);
				signature.setFeature(i, new Feature2D(x, y));
				signature.setWeight(i, val);
				i++;
			}
	    }
        return signature;
	}

	// saves the cluster information in a file
	public void saveCluster(ClusterFile file) throws IOException{
		System.out.println("Saving clusters...");
		
		// creates the location for the file
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH-mm-ss");
		Date date = new Date();
		
		String save = dateFormat.format(date); 
		String location = "data/saves/clusters/" + file.getLocation() + "/" + save + ".cluster";
		
		// the information from our clusters
		// save the dataset, then how many clusters exist
		String colour = "";
		if(file.isColour()){
			colour = "Colour";
		}else{
			colour = "Black and White";
		}
		
		String text = file.getDataset() + "\n" + file.getClusters().size()+ "\n" + colour + "\n" + file.getDistance() + "\n";
		// then add all the clusters
		
		for(int i = 0; i < file.getClusters().size(); i++){
			Cluster c = file.getClusters().get(i);
			text += "\nCLUSTER: " + c.getLabel() + "\n";
			text += c.toString();
		}
		
		
        BufferedWriter out = new BufferedWriter(new FileWriter(location));
        out.write(text);
        out.close();
        
        System.out.println("Saved cluster: " + location);
//        System.exit(0);
	}
	
	// saves the long list of iterationDuration's.
	public void saveTimeLog(String location) throws IOException{
		System.out.println("Saving file.");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH-mm-ss");
		Date date = new Date();
		
		String save = "data/saves/times/" + location + "/" + dateFormat.format(date) + ".csv";
		
		File file = new File(save);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		System.out.println(iterationTimes.size());
		
		for(int i = 0; i < iterationTimes.size(); i++){
		
			bw.write("" + iterationTimes.get(i));
			bw.newLine();
	         
		}
		bw.close();

		System.out.println("Saved file: saves/times/" + location + "/" + dateFormat.format(date) + ".csv");
	}
	
	protected void logIteration(long l){
		iterationDuration = l;
		System.out.println(iterationDuration);
		iterationTimes.add(l);
	}

	public List<InputMNIST> getMnistData() {
		return mnistData;
	}

	public void setMnistData(List<InputMNIST> mnistData) {
		this.mnistData = mnistData;
	}

}
