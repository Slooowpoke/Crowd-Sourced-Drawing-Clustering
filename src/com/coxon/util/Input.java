package com.coxon.util;

// very raw and simple input class, left open for extensions such as InputImage.
public class Input{
	private String name;
	
	public Input(String name){
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
