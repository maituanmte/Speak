package com.mte.models;

public class History {
	private float id = 0;
	private String content = "";
	
	public History(){
	}
	
	public float getId() {
		return id;
	}
	public void setId(float id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getSummary(){
		if(this.content.length() < 150)
			return this.content;
		return this.content.substring(0, 147) + "...";
	}
}
