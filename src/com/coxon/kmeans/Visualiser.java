package com.coxon.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import com.coxon.util.BaseVisualiser;

public class Visualiser extends BaseVisualiser{

	static KMeans kmeans;
	
	int mouseX,mouseY;

	public Visualiser(String title) {
		super(title);
	}
	
	public void init(GameContainer gc) throws SlickException {
		super.init(gc);
	}	
	
	public void update(GameContainer gc, int delta) throws SlickException {
		try {
			kmeans.update();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void render(GameContainer gc, Graphics g) throws SlickException {
		super.render(gc, g);
 
		int offsetX = 20, offsetY = 225;
		
		// render our centroids and inputs
		for(Centroid centroid:kmeans.trainer.centroids){
			g.translate(offsetX+(centroid.cluster*(KMeans.INPUT_WIDTH+10)), offsetY);
			
			if(kmeans.trainer.selectedCentroid != null
					&& kmeans.trainer.selectedCentroid.equals(centroid)){
				g.setColor(Color.red);
				g.fillRect(-2, -2, KMeans.INPUT_WIDTH+4,KMeans.INPUT_HEIGHT+4);
			}
			if(KMeans.TRAINING_DATA.equals("MNIST")){
				// render differently for the MNIST dataset TODO fix this.
				renderMNIST(g,centroid,1);
			}else{
				renderWeights(g,centroid,KMeans.TOTAL_COLOURS,KMeans.INPUT_WIDTH,KMeans.INPUT_HEIGHT,1);
			}
			
			g.resetTransform();
			
			int totalAssigned = 0;
			for(KInput input:kmeans.trainer.inputs){
				if(input.getCluster() == centroid.cluster){
					totalAssigned++;
					
					input.x = (input.getCluster()*(KMeans.INPUT_WIDTH+10));
					input.y = 10+(totalAssigned*(KMeans.INPUT_HEIGHT+5));
					g.translate(offsetX+input.x, offsetY+input.y);
					
					if(KMeans.TRAINING_DATA.equals("MNIST")){
						// render differently for the MNIST dataset TODO fix this.
						renderMNIST(g,input,1);
					}else{
						renderWeights(g,input,KMeans.TOTAL_COLOURS,KMeans.INPUT_WIDTH,KMeans.INPUT_HEIGHT,1);
					}
					
					g.resetTransform();
				}
			}
		}

		// but also render our inputs on the side so we can see which ones are loaded (might be a bit much but oh well).
		offsetX = 600;
		offsetY = 20; 
		int cx = 0,cy = 0;
		for(KInput input:kmeans.trainer.inputs){
			
			Rectangle r = new Rectangle(offsetX+(cx*(KMeans.INPUT_WIDTH+10)), offsetY+(cy*(KMeans.INPUT_HEIGHT+10)),KMeans.INPUT_WIDTH,KMeans.INPUT_HEIGHT);
			Rectangle mouse = new Rectangle(mouseX,mouseY,1,1);
			g.setFont(smaller);
			if(r.intersects(mouse)){
				g.drawString("INPUT NAME: " + input.getName(), 240, offsetY);
			}
			
			g.translate(offsetX+(cx*(KMeans.INPUT_WIDTH+10)), offsetY+(cy*(KMeans.INPUT_HEIGHT+10)));

			// if the current input that is being checked is not null, highlight it in red
			if(kmeans.trainer.currentInput != null
					&& kmeans.trainer.currentInput.equals(input)){
				g.setColor(Color.red);
				g.fillRect(-2, -2, KMeans.INPUT_WIDTH+4,KMeans.INPUT_HEIGHT+4);
			}
			
			if(KMeans.TRAINING_DATA.equals("MNIST")){
				// render differently for the MNIST dataset TODO fix this.
				renderMNIST(g,input,1);
			}else{
				renderWeights(g,input,KMeans.TOTAL_COLOURS,KMeans.INPUT_WIDTH,KMeans.INPUT_HEIGHT,1);
			}
			g.resetTransform();
			
			if(cx < 8){
				cx++;
			}else{
				cx = 0;
				cy++;
			}
		}
		
		
		// visualiser text here
		g.setFont(smaller);
		g.setColor(new Color(48,48,48));
		
		g.drawString("KMEANS:" + KMeans.currentStatus, 20, 20);
		
		g.drawString("FPS:" + gc.getFPS(), 20, 40);
		g.setFont(tiny);
		
		g.drawString("K-CLUSTERS: " + KMeans.K, 20, 70);
		drawText(g,"SAMPLE SIZE: " + kmeans.trainer.totalInputs);
		drawText(g,"TOTAL CHANGED SINCE LAST ITERATION: " + kmeans.trainer.totalChanged);
		
		drawText(g,"ITERATION LENGTH (NANOSECONDS): " + kmeans.trainer.iterationDuration);
		// which distance measure
		if(kmeans.trainer.earthMovers){
			drawText(g,"DISTANCE METRIC: EARTH MOVER'S DISTANCE");
		}else{
			drawText(g,"DISTANCE METRIC: EUCLIDEAN DISTANCE");
		}
		drawText(g,"TOTAL WEIGHTS PER NEURON: " + KMeans.TOTAL_WEIGHTS);
		drawText(g,"INPUT WIDTH:" + KMeans.INPUT_WIDTH + " INPUT HEIGHT:" + KMeans.INPUT_HEIGHT);
		drawText(g,"CURRENT ITERATION: " + kmeans.trainer.iteration);
		
		g.drawString("CENTROIDS BELOW:", 20, getTextY()+10);
	}
	
	public void mouseMoved(int oldx, int oldy, int newx, int newy){
		mouseX = newx;
		mouseY = newy;
	}
	
	public void keyPressed(int key,char c){
		if(key == Keyboard.KEY_RETURN){
			kmeans.startTraining();
		}
	}
	
	public static void main(String[] args){
		// the values retrieved from the properties files
		String dataset = null; boolean headless = false; boolean colour = false;boolean emd = false; int k = 0;
		int width = 0, height = 0;
		// load the properties, a bit messy because it's the 7th and I should not be writing code this late into the project
		try {
			// file reader variables
			File file = new File("data/KMEANS.properties");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;	
			int lineCounter = 0;
			
			// the loop for reading in the lines
			while ((line = bufferedReader.readLine()) != null) {
				if(lineCounter == 0){
					dataset = line;
				}else if(lineCounter == 1){
					headless = Boolean.valueOf(line);
				}else if(lineCounter == 2){
					colour = Boolean.valueOf(line);
				}else if(lineCounter == 3){
					emd = Boolean.valueOf(line);
				}else if(lineCounter == 4){
					k = Integer.valueOf(line);
				}else if(lineCounter == 5){
					// width
					width = Integer.valueOf(line);
				}else if(lineCounter == 6){
					// height
					height = Integer.valueOf(line);
				}
				lineCounter++;
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!headless){
			try {
				// load k-means before starting
				kmeans = new KMeans(dataset,false,colour,emd,k,width,height);
				if(!kmeans.MNIST){
					KMeans.INPUT_WIDTH = width;
					KMeans.INPUT_HEIGHT = height;
					KMeans.TOTAL_WEIGHTS = (KMeans.INPUT_WIDTH*KMeans.INPUT_HEIGHT)*KMeans.TOTAL_COLOURS;
				}
				AppGameContainer app;
				app = new AppGameContainer(new Visualiser("Crowd-sourced drawing clustering with neural computing."));
				app.setDisplayMode(1024, 720, false);
				app.setShowFPS(false);
				app.setAlwaysRender(true);
				app.start();
			}catch (SlickException ex){
				Logger.getLogger(Visualiser.class.getName()).log(Level.SEVERE, null, ex);
			}
		}else{
			// start k-means in headless mode
			kmeans = new KMeans(dataset,true,colour,emd,k,width,height);
			if(!kmeans.MNIST){
				KMeans.INPUT_WIDTH = width;
				KMeans.INPUT_HEIGHT = height;
				KMeans.TOTAL_WEIGHTS = (KMeans.INPUT_WIDTH*KMeans.INPUT_HEIGHT)*KMeans.TOTAL_COLOURS;
			}
			while(true){
				try {
					kmeans.update();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
