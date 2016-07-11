package com.coxon.util.gui;

import org.newdawn.slick.Graphics;


public class Option {
	private Button a;
	private Button b;
	
	private Button clicked;
	
	public Option(String a,String b,int x,int y){
		// center the options
		this.setA(new Button(a,x,y,100,20));
		this.setB(new Button(b,x+110,y,100,20));
	}
	
	public void mouseClick(int mx,int my){
		if(getA().mouseOver(mx, my)){
			setClicked(getA());
			System.out.println("option a");
		}else if(getB().mouseOver(mx, my)){
			setClicked(getB());
			System.out.println("option b");
		}
	}
	
	public void render(Graphics g){
		getA().render(g);
		getB().render(g);
	}

	public void mouseHover(int x, int y) {
		getA().mouseOver(x, y);
		getB().mouseOver(x, y);
	}

	public Button getClicked() {
		return clicked;
	}

	public void setClicked(Button clicked) {
		this.clicked = clicked;
	}

	public Button getA() {
		return a;
	}

	public void setA(Button a) {
		this.a = a;
	}

	public Button getB() {
		return b;
	}

	public void setB(Button b) {
		this.b = b;
	}
}
