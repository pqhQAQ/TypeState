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
	
	public void setHashcode(int hashcode){
		this.hashcode = hashcode;
	}
	
	public boolean equalPoint(Point p){
		if(name.equals(p.getName())&&(hashcode==p.getHashcode()))
			return true;
		return false;
	}
}
