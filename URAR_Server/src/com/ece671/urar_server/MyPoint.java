package com.ece671.urar_server;

public class MyPoint{
	private int id;
	private double x;
	private double y;
	
	public MyPoint(){
		
	}
	public MyPoint(int id, double x, double y) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public int getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}

}
