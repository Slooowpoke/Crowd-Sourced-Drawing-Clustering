package com.coxon.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InputImage extends Input{
	BufferedImage image;
	int x,y,width,height;
	
	public InputImage(String name) {
		super(name);
	}
	
	public boolean create(String location,int x,int y,int width,int height){
		try {
			this.x = x;
			this.y = y;
			
			// whatever width and height were fed in earlier
			this.width 	= width;
			this.height = height;
			
			// read the image in
			image = ImageIO.read(new File(location));

			// resize the image
			BufferedImage resized = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
			Graphics2D g2 = resized.createGraphics();
			g2.drawImage(image, 0, 0, width, height, null);
			g2.dispose();

			image = resized;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}

	public BufferedImage getImage() {
		return image;
	}
}
