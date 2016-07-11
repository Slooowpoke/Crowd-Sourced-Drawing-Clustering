package com.coxon.util;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class BaseVisualiser extends BasicGame{
	public static Font bigger,smaller,tiny;
	
	// used for rendering text, increments when drawtext is called so I don't have to input the y axis everytime I want a new field. ugh.
	private int textY = 85;
	
	public BaseVisualiser(String title) {
		super(title);
	}
	
	public void init(GameContainer gc) throws SlickException {
		bigger = new AngelCodeFont("data/res/fonts/bigger.fnt", new Image("data/res/fonts/bigger_0.png"));
		smaller = new AngelCodeFont("data/res/fonts/smaller.fnt", new Image("data/res/fonts/smaller_0.png"));
		tiny = new AngelCodeFont("data/res/fonts/tiny.fnt", new Image("data/res/fonts/tiny_0.png"));
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {		
		g.setBackground(Color.white);
		textY = 85;
	}

	public void update(GameContainer gc, int delta) throws SlickException {}
	
	// this method is responsible for rendering our neuron.
	protected void renderWeights(Graphics g,Weighted w,int colours,int width,int height,float scale){
		int x = 0,y = 0;
		int total = (int) (width*height);
		for(int i = total-1; i >= 0; i--){	
			// render the Input's weightings, for this class it's black and white.
			float red = w.getWeights().get(i).floatValue();
			float green = red,blue = red;
			if(colours == 3){
				green = w.getWeights().get(i+(total)).floatValue();
				blue = w.getWeights().get(i+(total*2)).floatValue();
			}
			g.setColor(new Color(red,green,blue));
			g.fillRect((x*scale),(y*scale), scale,scale);
			x++;
			// if the x value is correct, increment the y to create an image slowly. 
			if(x >= width){
				x = 0;
				y++;
			}
		}
		g.setColor(Color.black);
	}
	
	public void renderMNIST(Graphics g,Weighted w,float scale){
		int x = 0,y = 0;
		int total = (int) (28*28);
		for(int i = 0; i < total; i++){
			// render the Input's weightings, for this class it's black and white.
			float red = w.getWeights().get(i).floatValue();
			
			g.setColor(new Color(red,red,red));
			g.fillRect((x*scale),(y*scale), scale,scale);
			x++;
			// if the x value is correct, increment the y to create an image slowly. 
			if(x >= 28){
				x = 0;
				y++;
			}
		}
	}
	
	public void drawText(Graphics g,String text){
		g.setColor(new Color(48,48,48));
		g.drawString(text, 20, getTextY());
		setTextY(getTextY() + 15);
	}

	public int getTextY() {
		return textY;
	}

	public void setTextY(int textY) {
		this.textY = textY;
	}
}
