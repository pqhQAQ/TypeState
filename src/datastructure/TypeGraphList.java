package datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.SootMethod;

public class TypeGraphList {
	private SootMethod soot_method;
	public List<TypeGraph> tgl = new ArrayList<TypeGraph>();
//	public Map<String, List<TransEdge>> typegraph_map = new HashMap<String, List<TransEdge>>();
//	public void addTypeGraph(String varname){
//		if(!typegraph_map.containsKey(varname))
//			typegraph_map.put(varname, new ArrayList<TransEdge>());
//	}
	
	public TypeGraphList(SootMethod sm){
		soot_method = sm;
	}
	
	public void addTypeGraph(TypeGraph tg){
		if(!tgl.contains(tg))
			tgl.add(tg);
	}
	
	public void transMethod(String var, String method, int starthash, int endhash){
		for(TypeGraph tg : tgl){
			if(tg.varname.equals(var)){
				tg.transMethod(method, starthash, endhash);
				transAllExcept(tg, starthash, endhash);
				break;
			}				
		}
	}
	
	public void transAllExcept(TypeGraph tg, int starthash, int endhash){
		for(TypeGraph t : tgl){
			if(!t.equals(tg))
				t.transAll(starthash, endhash);
		}
	}
	
	public void transAll(int starthash, int endhash){
		for(TypeGraph tg : tgl){
			tg.transAll(starthash, endhash);
		}
	}
	
	public void print(String path){
		for(TypeGraph tg : tgl){
			String filepath = path+soot_method.getDeclaringClass().getName()+"."+soot_method.getName()+"."+tg.varname+".txt";
			tg.print(filepath,soot_method);
		}
	}
	
	public void printDot(String path){
		for(TypeGraph tg : tgl){
			String filepath = path+soot_method.getDeclaringClass().getName()+"."+soot_method.getName()+"."+tg.varname+".txt";
			tg.printDot(filepath,soot_method);
		}
	}
}
