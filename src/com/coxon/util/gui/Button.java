package com.coxon.util.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import com.coxon.util.BaseVisualiser;

public class Button {
	Rectangle collision;
	String text;
	
	boolean hover;
	
	public Button(String text,int x,int y,int width,int height){
		collision = new Rectangle(x,y,width,height);
		this.text = text;
	}
	
	public void render(Graphics g){
		if(hover){
			g.setColor(new Color(250,75,87));
			g.fill(collision);
			g.setColor(Color.white);
		}else{
			g.setColor(Color.black);
			g.draw(collision);
		}
		
		g.setFont(BaseVisualiser.tiny);
		g.drawString(text, collision.getCenterX()-(BaseVisualiser.tiny.getWidth(text)/2), collision.getCenterY()-(BaseVisualiser.tiny.getHeight(text)/2));
	}
	
	public boolean mouseOver(int mx,int my){
		Rectangle mouse = new Rectangle(mx,my,1,1);
		if(mouse.intersects(collision)){
			hover = true;
			return true;
		}else{
			hover = false;
			return false;
		}
	}
}
