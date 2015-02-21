package com.ece671.urar_server;

import com.ece671.urar_server.MyPoint;


public class Edge {
	
	private MyPoint start;
	
	private MyPoint end;
	
	private double weight;
	
	public Edge(MyPoint start,MyPoint end,double weight){
		this.start=start;
		this.end=end;
		this.weight=weight;
	}
	public MyPoint getStart() {
		return start;
	}
	public void setStart(MyPoint start) {
		this.start = start;
	}
	public MyPoint getEnd() {
		return end;
	}
	public void setEnd(MyPoint end) {
		this.end = end;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
