package datastructure;

public class Point {
	private String name;
	private int hashcode;
	
	public Point(String name, int hashcode){
		this.name = name;
		this.hashcode = hashcode;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getHashcode(){
		return this.hashcode;
	}
}
