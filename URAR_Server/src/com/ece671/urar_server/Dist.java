package com.ece671.urar_server;

import com.ece671.urar_server.MyPoint;
public class Dist{
	
	private double weight;
	
	private MyPoint point;
	
	private Dist preDist;
	
	public double getWeight() {
		return weight;
	}
	public void setWeight(double d) {
		this.weight = d;
	}
	public MyPoint getPoint() {
		return point;
	}
	public void setPoint(MyPoint start) {
		this.point = start;
	}
	public Dist getPreDist() {
		return preDist;
	}
	public void setPreDist(Dist preDist) {
		this.preDist = preDist;
	}
}
