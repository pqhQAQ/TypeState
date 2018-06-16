package datastructure;

import datastructure.Point;

public class TransEdge {
	public Point end;
	public Point start;
	
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
	
	public String printDot(){
		String path = start.getName()+"_"+start.getHashcode()+" -> "+end.getName()+"_"+end.getHashcode()+" \r\n";
		return path;
	}
}
