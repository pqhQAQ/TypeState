package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Generator.TSCGenerator;
import datastructure.TypeProtocol;
import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;

public class IntraGenerator {
	// generator
	public static int Generator_ID = 0;
	public static Map<String, TypeProtocol> typemap = new HashMap<String, TypeProtocol>();
	public static List<String> allClass = new ArrayList<String>();
	public static List<String> InterestMethod = new ArrayList<String>();

	public static void init(String dirpath) {
		File dirFile = new File(dirpath);
		String path = "";
		if (!dirFile.isDirectory()) {
			return;
		}
		String[] fileList = dirFile.list();
		for (int i = 0; i < fileList.length; i++) {
			// 遍历文件目录
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(new File(dirpath + "/" + fileList[i])), "UTF-8"));
				TypeProtocol tp = new TypeProtocol();
				String line = br.readLine();
				tp.classname = line;
				allClass.add(line);
				line = br.readLine();
				tp.allStates = line.split(" ");
				while ((line = br.readLine()) != null) {
					String[] sp = line.split("@");
					tp.addTrans(sp[0], sp[1]);
					if (!InterestMethod.contains(sp[0]))
						InterestMethod.add(sp[0]);
				}
				typemap.put(tp.classname, tp);
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// parse arguments
		// TODO

		String sp = "D:/project/workspace/TypeState/lib/";
		String dirpath = "D:/project/workspace/TypeState/protocal";
		init(dirpath);
		// String path = sp + "bin" + File.pathSeparator + sp +
		// "lib/soot-2.5.0.jar" + File.pathSeparator + sp
		// + "lib/sootsources-trunk.jar" + File.pathSeparator + sp +
		// "lib/rt.jar" + File.pathSeparator + sp
		// + "lib/test" + File.pathSeparator + sp +
		// "lib/xmlgraphics-commons-1.3.1.jar";
		File dirFile = new File(sp);
		String path = "";
		if (!dirFile.isDirectory()) {
			return;
		}
		String[] fileList = dirFile.list();
		for (int i = 0; i < fileList.length; i++) {
			// 遍历文件目录
			String string = fileList[i];
			if (string.endsWith(".jar")) {
				path += sp + string + ";";
			}
		}
		path += sp + "test;";
		Scene.v().setSootClassPath(path);

		// set options
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().setPhaseOption("tag", "off");
		Options.v().set_output_format(Options.output_format_jimple);
		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);

		// add phase
		Transform trans = null;
		switch (Generator_ID) {
		case 0:
			TSCGenerator tscgenerator = new TSCGenerator();
			trans = new Transform("jtp.peggenerator", tscgenerator);
			break;
		case 1:

			break;
		case 2:
			break;
		default:
			System.err.println("wrong generator!!!");
			System.exit(0);
		}

		PackManager.v().getPack("jtp").add(trans);

		// run
		soot.Main.main(args);
		// PEGGenerator peg = new PEGGenerator();
		// peg.createCall();
		// List<SootMethod> am = peg.allMethod;
		// for(SootMethod method : am){
		// peg.createIntra();
		// }

	}

}
