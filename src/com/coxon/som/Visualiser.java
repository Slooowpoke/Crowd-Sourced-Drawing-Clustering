package com.coxon.som;


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
import com.coxon.util.Neuron;

public class Visualiser extends BaseVisualiser{
	
	private static SOM som;
	
	private int mouseX,mouseY;

	public Visualiser(String title) {
		super(title);
	}
	
	public void init(GameContainer gc) throws SlickException {
		super.init(gc);
	}	
	
	public void update(GameContainer gc, int delta) throws SlickException {
		try {
			som.update();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		super.render(gc, g);

		int offsetX = gc.getWidth()-(som.lattice.size*SOM.INPUT_WIDTH)-20,offsetY = 20;
		
		for(int i = 0; i < som.lattice.nodes.size(); i++){
			Neuron n = som.lattice.nodes.get(i);
			
			if(som.MNIST){
				g.translate(offsetX+(n.x*28), offsetY+n.y*28);
				renderMNIST(g,n,1);
				g.resetTransform();
			}else{
				g.translate(offsetX+(n.x*SOM.INPUT_WIDTH), offsetY+n.y*SOM.INPUT_HEIGHT);
				renderWeights(g,n,SOM.TOTAL_COLOURS,SOM.INPUT_WIDTH,SOM.INPUT_HEIGHT,1);
				g.resetTransform();
			}
		}
		if(som.trainer != null && som.trainer.currentInput != null){
			g.translate(20, 660);
			renderWeights(g,som.trainer.currentInput,SOM.TOTAL_COLOURS,SOM.INPUT_WIDTH,SOM.INPUT_HEIGHT,1);
			g.resetTransform();
		}
		// render the u-matrix
		int scale = 3;
		Rectangle mouse = new Rectangle((mouseX/scale)*scale,(mouseY/scale)*scale,scale,scale);
		
		g.setFont(smaller);
		for(int x = 0; x < som.matrix.getSize(); x++){
			for(int y = 0; y < som.matrix.getSize(); y++){
				double node = som.matrix.nodes[x][y]/255;
				if(node < som.threshold){
					node = 0.0;
				}
				g.setColor(new Color((float)node,(float)node,(float)node));
				Rectangle r = new Rectangle(offsetX+(x*scale), offsetY+(som.lattice.size*SOM.INPUT_HEIGHT)+(y*scale)+5,scale,scale);
				g.fill(r);
			}
		}
		g.setColor(Color.white);
		g.draw(mouse);

		for(int x = 0; x < som.matrix.getSize(); x++){
			for(int y = 0; y < som.matrix.getSize(); y++){
				if(som.temp != null){
					double node = som.temp[x][y]/255;
					g.setColor(new Color((float)node*10,(float)node*10,(float)node*10));
					Rectangle r = new Rectangle(offsetX+(x*scale)+(som.matrix.getSize()*scale)+10, offsetY+(som.lattice.size*SOM.INPUT_HEIGHT)+(y*scale)+5,scale,scale);
					g.fill(r);
				}
			}
		}
		
		
		// render where the best matching unit, a bit messy but it works.
		if(som.trainer != null && som.trainer.lastBestMatching != null){
			g.setColor(new Color(255/255,70/255,70/255,0.2f));
			float bestMatchingX = offsetX+(som.trainer.lastBestMatching.x*SOM.INPUT_WIDTH)+SOM.INPUT_WIDTH/2;
			float bestMatchingY = offsetY+(som.trainer.lastBestMatching.y*SOM.INPUT_HEIGHT)+SOM.INPUT_HEIGHT/2;
			float radius = (float)som.trainer.exciteRadius*30;
			g.fillOval(bestMatchingX-(radius/2),bestMatchingY-(radius/2),radius,radius);
		}
		
		g.setFont(smaller);
		g.setColor(new Color(48,48,48));
		
		g.drawString("SOM:" + SOM.currentStatus, 20, 20);
		g.drawString("FPS:" + gc.getFPS(), 20, 40);
		
		g.setFont(tiny);
		drawText(g,"LATTICE SIZE: " + som.lattice.size + " x " + som.lattice.size);
		
		drawText(g,"TOTAL WEIGHTS PER NEURON: " + SOM.TOTAL_WEIGHTS);

		if(som.trainer != null){
			drawText(g,"ITERATION LENGTH (NANOSECONDS): " + som.trainer.iterationDuration);
			drawText(g,"CURRENT ITERATION: " + som.trainer.iteration + "/" + som.trainer.totalIterations);
			drawText(g,"LEARNING RATE: " + som.trainer.learningRate);
			drawText(g,"EXCITEMENT RADIUS: " + som.trainer.exciteRadius);
		}

		drawText(g,"THRESHOLD FOR U-MATRIX: " + som.threshold);
	}
	
	public void keyPressed(int key,char c){
		if(key == Keyboard.KEY_RETURN){
			som.startTraining(SOM.Status.IN_PHASE_ONE,som.PHASE_ONE_LEARNING);
		}
	}
	
	public void mouseMoved(int oldx,int oldy,int newx,int newy){
		mouseX = newx;
		mouseY = newy;
	}
	
	public static void main(String[] args){
		// the values retrieved from the properties files
		String dataset = null; boolean headless = false; boolean colour = false;
		double threshold = 0;
		int width = 0,height = 0;
		
		// load the properties, a bit messy because it's the 7th and I should not be writing code this late into the project
		try {
			// file reader variables
			File file = new File("data/SOM.properties");
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
					// this extra line is for a threshold value for the u-matrix
					threshold = Double.valueOf(line);
				}else if(lineCounter == 4){
					// width
					width = Integer.valueOf(line);
				}else if(lineCounter == 5){
					// height
					height = Integer.valueOf(line);
				}
				lineCounter++;
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// if its not running in headless mode, then start up the appgamecontainer.
		if(!headless){
			try {
				// load our som, before starting
				try {
					som = new SOM(dataset,false,colour,threshold,width,height);
				} catch (InterruptedException e) {
					System.out.println("Failed to create SOM: " + e.getMessage());
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
			// load our som in headless mode.
			try {
				som = new SOM(dataset,true,colour,threshold,width,height);
			} catch (InterruptedException e) {
				System.out.println("Failed to create SOM: " + e.getMessage());
			}
			while(true){
				try {
					som.update();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
