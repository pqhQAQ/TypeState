package datastructure;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.IntraGenerator;
import soot.SootMethod;

public class TypeGraph {
	public String classname;
	public String varname;
	private String[] allStates;
	private List<TransEdge> transedges = new ArrayList<TransEdge>();
	
	public TypeGraph(String classname, String varname){
		TypeProtocol tp = IntraGenerator.typemap.get(classname);
		allStates = tp.allStates;
		this.classname = classname;
		this.varname = varname;
	}
	
	public void addTransEdge(TransEdge te){
		if(!transedges.contains(te))
			transedges.add(te);
	}
	
	
	public void transAll(int starthash, int endhash){
		for(String state:allStates){
			TransEdge te = new TransEdge();
			te.addStart(new Point(state, starthash));
			te.addEnd(new Point(state, endhash));
			transedges.add(te);
		}
	}
	
	public void transMethod(String method, int starthash, int endhash){
		TypeProtocol tp = IntraGenerator.typemap.get(classname);
		List<String> trans = tp.statetrans.get(method);
		for(String tran:trans){
			TransEdge te = new TransEdge();
			te.addStart(new Point(tran.split(",")[0], starthash));
			te.addEnd(new Point(tran.split(",")[1], endhash));
			transedges.add(te);
		}
	}
	
	public void simplifyGraph(){
		int stateNum = allStates.length;
		if(transedges.size()<=stateNum){
			return;
		}
		TransEdge[] now = new TransEdge[stateNum];
		TransEdge[] next = new TransEdge[stateNum];
		//Iterator<TransEdge> it = transedges.iterator();
		for(int i = 0; i < stateNum; i++){
			now[i] = transedges.get(i);
		}
		for(int i = stateNum; i < transedges.size(); ){
			for(int j = 0; j < stateNum; j++){
				next[j] = transedges.get(i+j);
			}
			int equ = 0;
			for(; equ < stateNum; equ++){
				if(!edgeDirect(now[equ], next[equ])){
					break;
				}
			}
			if(equ == stateNum){
				for(int j = 0; j < stateNum; j++){
					now[j].end.setHashcode(next[j].end.getHashcode());
					transedges.remove(next[j]);
				}
			}else{
				for(int j = 0; j < stateNum; j++){
					now[j] = next[j];
				}
				i = i+stateNum;
			}
		}
	}
	
	public boolean edgeDirect(TransEdge first, TransEdge sec){
		if(!first.start.getName().equals(first.end.getName()))
			return false;
		if(!sec.start.getName().equals(sec.end.getName()))
			return false;
		if(!(first.end.equalPoint(sec.start)))
			return false;
		return true;
	}
	
	public void print(String file_path, SootMethod sm){
		String regEx="[`~!@#$%^&*()+=|{}';',\\[\\]<>?~£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+|{}¡¾¡¿¡®£»£º¡±¡°¡¯¡££¬¡¢£¿]"; 
		Pattern p = Pattern.compile(regEx); 
		Matcher m = p.matcher(file_path);
		file_path = m.replaceAll("").trim();
		File file = new File(file_path);
		try{
			if(!file.exists())
				file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("method: "+sm.getDeclaringClass().getName()+"."+sm.getName()+":"+sm.hashCode()+"\r\n");
			fileWriter.write(allStates.length+"\r\n");
			for(String state : allStates){
				fileWriter.write(state+"\r\n");
			}
			for(TransEdge transedge : transedges){
				fileWriter.write(transedge.printTemp());
			}
			fileWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void printDot(String file_path, SootMethod sm){
		String regEx="[`~!@#$%^&*()+=|{}';',\\[\\]<>?~£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+|{}¡¾¡¿¡®£»£º¡±¡°¡¯¡££¬¡¢£¿]"; 
		Pattern p = Pattern.compile(regEx); 
		Matcher m = p.matcher(file_path);
		file_path = m.replaceAll("").trim();
		File file = new File(file_path);
		try{
			if(!file.exists())
				file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("digraph "+sm.getDeclaringClass().getName()+"_"+sm.getName()+"_"+sm.hashCode()+" {"+"\r\n");
			for(TransEdge transedge : transedges){
				if(!transedge.start.getName().equals(transedge.end.getName())){
					fileWriter.write(transedge.start.getName()+"_"+transedge.start.getHashcode()+" -> "+transedge.start.getName()+"_"+transedge.end.getHashcode()+"[color=\"white\"]"+" \r\n");
				}
				fileWriter.write(transedge.printDot());
			}
			fileWriter.write("} \r\n");
			fileWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
