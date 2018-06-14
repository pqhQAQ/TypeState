package datastructure;

import datastructure.Point;

public class TransEdge {
	private Point end;
	private Point start;
	
	public void addEnd(Point base) {
		// TODO Auto-generated method stub
		this.end = base;
	}
	
	public void addStart(Point arg) {
		// TODO Auto-generated method stub
		this.start = arg;
	}
	
	public String callStr(){
		return start.getName()+"("+start.getHashcode()+")";
	}
	
	public String receiveStr(){
		return end.getName()+"("+end.getHashcode()+")";
	}
	
	public String print(){
		String path = start.getName()+"("+start.getHashcode()+") -> "+end.getName()+"("+end.getHashcode()+") \r\n";
		return path;
	}
}
