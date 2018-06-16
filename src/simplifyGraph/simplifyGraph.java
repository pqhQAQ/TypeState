package simplifyGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datastructure.Point;
import datastructure.TransEdge;
import datastructure.TypeGraph;
import datastructure.TypeProtocol;
import soot.SootMethod;

public class simplifyGraph {	
	private String[] allStates;
	private List<TransEdge> transedges = new ArrayList<TransEdge>();

	public void simplify(String path) {
		File dirFile = new File(path);
		// try {
		if (!dirFile.isDirectory()) {
			simplifyFile(path);
		}
		String[] fileList = dirFile.list();
		for (int i = 0; i < fileList.length; i++) {
			// 遍历文件目录
			File file = new File(path + fileList[i]);
			if(file.isDirectory())
				continue;
			simplifyFile(path + fileList[i]);
			// BufferedReader br = new BufferedReader(
			// new InputStreamReader(new FileInputStream(new File(dirpath + "/"
			// + fileList[i])), "UTF-8"));
			// TypeProtocol tp = new TypeProtocol();
			// String line = br.readLine();
			// tp.classname = line;
			// allClass.add(line);
			// line = br.readLine();
			// tp.allStates = line.split(" ");
			// while ((line = br.readLine()) != null) {
			// String[] sp = line.split("@");
			// tp.addTrans(sp[0], sp[1]);
			// if (!InterestMethod.contains(sp[0]))
			// InterestMethod.add(sp[0]);
			// }
			// typemap.put(tp.classname, tp);
			// br.close();
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	private void simplifyFile(String path) {
		transedges.clear();
		try {
			File file = new File(path);
			if (!file.exists())
				return;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = br.readLine();
			int statenum = Integer.parseInt(br.readLine());
			allStates = new String[statenum];
			for(int i =0;i<statenum;i++){
				allStates[i] = br.readLine();
			}
			while ((line = br.readLine()) != null){
				TransEdge transedge = new TransEdge();
				String start = line.split(" -> ")[0];
				String end = line.split(" -> ")[1];
				transedge.start = new Point(start.split("_")[0], Integer.parseInt(start.split("_")[1]));
				transedge.end = new Point(end.split("_")[0], Integer.parseInt(end.split("_")[1]));
				transedges.add(transedge);
			}
			simplify();
			deduplication();
			String newpath = path.substring(0, path.lastIndexOf("/")+1)+"simplify/"+path.substring(path.lastIndexOf("/")+1);
			printDot(newpath);
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deduplication(){
		for(int i = 0; i < transedges.size(); i++){
			for(int j = transedges.size()-1; j>i; j--){
				TransEdge e1 = transedges.get(i);
				TransEdge e2 = transedges.get(j);
				if(e1.start.equalPoint(e2.start)&&e1.end.equalPoint(e2.end))
					transedges.remove(j);
			}
		}
	}
	
	private void simplify(){
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
			if(equ == stateNum && fathernum(stateNum, next[0].start.getHashcode())<=1){
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
	
	private int fathernum(int statenum, int hashcode){
		int num = 0;
		for(int i = 0; i < transedges.size(); i+=statenum){
			if(transedges.get(i).end.getHashcode() == hashcode){
				num++;
				continue;
			}
		}
		return num;
	}
	
	private boolean edgeDirect(TransEdge first, TransEdge sec){
		if(!first.start.getName().equals(first.end.getName()))
			return false;
		if(!sec.start.getName().equals(sec.end.getName()))
			return false;
		if(!(first.end.equalPoint(sec.start)))
			return false;
		return true;
	}
	
	private void printDot(String file_path){
		String regEx="[`~!@#$%^&*()+=|{}';',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
		Pattern p = Pattern.compile(regEx); 
		Matcher m = p.matcher(file_path);
		file_path = m.replaceAll("").trim();
		File file = new File(file_path);
		try{
			if(!file.exists())
				file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("digraph G {"+"\r\n");
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
