package datastructure;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
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
			for(TransEdge transedge : transedges){
				fileWriter.write(transedge.print());
			}
			fileWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
