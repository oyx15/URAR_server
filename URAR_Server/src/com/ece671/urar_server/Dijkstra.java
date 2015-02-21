package com.ece671.urar_server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import android.util.Log;
import com.ece671.urar_server.Dist;
import com.ece671.urar_server.Edge;
import com.ece671.urar_server.MyPoint;

public class Dijkstra {

	private Map<Integer, Double> pointKeys = new HashMap<Integer, Double>();

	private ArrayList<Edge> edges = new ArrayList<Edge>();

	private Map<Integer, Dist> paths = new HashMap<Integer, Dist>();
	
	private static final double INFINITY = 99999;

	private void reset() {
		Log.d("Dijkstra","Dijkstra algorithm reset function");
		pointKeys.clear();
		edges.clear();
		paths.clear();
	}

	/**
	 * 
	 * 
	 * @param source 
	 * @param edegs 
	 * @param i2 
	 */
	private void init(ArrayList<MyPoint> source, ArrayList<Edge> edegs, MyPoint start) {
		Log.d("Dijkstra","Dijkstra algorithm begins");
		reset();
		this.edges = edegs;
		
		for (int i = 0; i < source.size(); i++) {
			MyPoint v = source.get(i);
			if (v.equals(start)) {	
				pointKeys.put(v.getId(), 0.0);
				//Log.d("Dijkstra",pointKeys.get(v.getId())+"");
			} else {
				pointKeys.put(v.getId(), INFINITY);
				//Log.d("Dijkstra",pointKeys.get(v.getId())+"");
			}
			
			Dist dist = new Dist();
			dist.setPoint(start);
			dist.setPreDist(null);
			dist.setWeight(0);
			paths.put(start.getId(), dist);
		}
	}

	/**
	 * dijkstra 
	 * @param source 
	 * @param edegs 
	 * @param start 
	 * @param end
	 * @return
	 */
	public Stack<Integer> dijkstra(ArrayList<MyPoint> source, ArrayList<Edge> edegs, MyPoint start, MyPoint end) {
		Log.d("Dijkstra","Dijkstra algorithm get stack function");
		init(source, edegs, start);
		Log.d("source size",String.valueOf(source.size()));
		Log.d("edeges size",String.valueOf(edges.size()));
		Log.d("pointkey size",String.valueOf(pointKeys.size()));
		Log.d("start point",String.valueOf(start.getId()));
		Log.d("end point",String.valueOf(end.getId()));
		Double endPointKey = null;
		if(pointKeys != null){
			while (pointKeys.size() > 0) {
				Integer minkeyPoint = getMinKey(pointKeys);
				//Log.d("minkeypoint x",String.valueOf(minkeyPoint.getX()));
				if(minkeyPoint != null){
					//Log.d("minkeypoint x",String.valueOf(minkeyPoint));
					double keyValue = pointKeys.get(minkeyPoint);
					ArrayList<Edge> adjacentEdegs = getEdegs(edges, minkeyPoint);
					for (Edge edge : adjacentEdegs) {
						double currentKey = keyValue + edge.getWeight();
						//Log.d("currentkey",String.valueOf(currentKey));
						int endPoint = edge.getEnd().getId();

						//Log.d("endpoint key",String.valueOf(endPoint));
						endPointKey = pointKeys.get(endPoint);
						//Log.d("endpoint key",String.valueOf(endPointKey));

						if (endPointKey == null) {
							continue;
						}
						updatePath(endPointKey, edge, currentKey);
					}
					pointKeys.remove(minkeyPoint);
				}
				
			}
		}
		
		return getPaths(end);
	}

	/**
	 *
	 * @param endPointKey
	 * @param edge
	 * @param currentKey
	 */
	private void updatePath(double endPointKey, Edge edge, double currentKey) {
		//Log.d("Dijkstra","Dijkstra algorithm updatepath function");
		Dist advance = null;
		if (currentKey < endPointKey) {
			pointKeys.put(edge.getEnd().getId(), currentKey);
			advance = new Dist(); 
			advance.setPoint(edge.getEnd());
			advance.setPreDist(paths.get(edge.getStart().getId())); 
			advance.setWeight(edge.getWeight()); 
			paths.put(edge.getEnd().getId(), advance); 
		}
	}

	/**
	 * @param end
	 * @return
	 */
	private Stack<Integer> getPaths(MyPoint end) {
		//Log.d("Dijkstra","Dijkstra algorithm getpath function");
		Stack<Integer> stack = null;
		Iterator<Integer> iterator = paths.keySet().iterator();
		while (iterator.hasNext()) {
			stack = new Stack<Integer>();
			int c = iterator.next();
			//if (!c.equals(end)) {
			if (c != end.getId()) {
				continue;
			}
			Dist td = paths.get(c);
			while (td != null) {
				stack.push(td.getPoint().getId());
				td = td.getPreDist();
			}
			break;
		}
		//Log.d("Dijkstra",String.valueOf(stack.size()));
		return stack;
		
	}

	/**
	 * @param pointKeys2
	 * @return
	 */
	private int getMinKey(Map<Integer, Double> pointKeys2) {
		//Log.d("Dijkstra","Dijkstra algorithm get minimal key function");
		Iterator<Integer> iterator = pointKeys2.keySet().iterator();
		double minValue = INFINITY;
		Integer minKeyPoint = null;
		while (iterator.hasNext()) {
			int point = iterator.next();
			double value = pointKeys2.get(point);
			if (value < minValue) {
				minValue = value;
				minKeyPoint = point;
			}
		}
		if(minKeyPoint != null){
			//Log.d("Dijkstra algorithm get minimal key point X:",String.valueOf(minKeyPoint));	
		}
		return minKeyPoint;
		
	}

	/**
	 * @param edges
	 * @param minkeyPoint
	 * @return
	 */
	private ArrayList<Edge> getEdegs(ArrayList<Edge> edges, int minkeyPoint) {
		//Log.d("Dijkstra","Dijkstra algorithm get edge function");
		ArrayList<Edge> tempEdges = new ArrayList<Edge>();
		for (Edge edge : edges) {
			//Log.d("Dijkstra algorithm",String.valueOf(edge.getStart().getId()));
			if (edge.getStart().getId() == minkeyPoint) {
				tempEdges.add(edge);
			}
		}
		//Log.d("Dijkstra algorithm get edge size:",String.valueOf(tempEdges.size()));
		return tempEdges;
	}

}
