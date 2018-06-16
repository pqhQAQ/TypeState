package Generator;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.IntraGenerator;
import datastructure.TypeGraph;
import datastructure.TypeGraphList;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.util.Chain;

public class TSCGenerator extends BodyTransformer {
	private SootMethod sm;
	private TypeGraphList typegraph_list;
	private List<Local> interestLocal = new ArrayList<Local>();

	@Override
	protected void internalTransform(Body arg0, String arg1, Map arg2) {
		sm = arg0.getMethod();
		typegraph_list = new TypeGraphList(sm);

		if (!sm.hasActiveBody()) {
			sm.retrieveActiveBody();
		}
		String file_path = "D:/project/workspace/TypeState/sootOutput/" + sm.getDeclaringClass().getName() + "_"
				+ sm.getName() + "_jimple.txt";
		String regEx = "[`~!@#$%^&*()+=|{}';',\\[\\]<>?~£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+|{}¡¾¡¿¡®£»£º¡±¡°¡¯¡££¬¡¢£¿]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(file_path);
		file_path = m.replaceAll("").trim();
		File file = new File(file_path);
		try {
			if (!file.exists())
				file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(
					"method: " + sm.getDeclaringClass().getName() + "." + sm.getName() + ":" + sm.hashCode() + "\r\n");

			initTGL();
			Stmt nowst, succst;
			Iterator stmts = sm.getActiveBody().getUnits().iterator();
			if (stmts.hasNext()) {
				nowst = (Stmt) stmts.next();
				succst = nowst;
				for (; stmts.hasNext();) {
					succst = (Stmt) stmts.next();
					fileWriter.write(nowst.hashCode() + ":" + nowst.toString() + "\r\n");
					processStmt(nowst, succst);
					nowst = succst;
				}
				fileWriter.write(succst.hashCode() + ":" + succst.toString() + "\r\n");
				processStmt(succst, succst);
			}
			fileWriter.close();
			//typegraph_list.print("D:/project/workspace/TypeState/sootOutput/");
			typegraph_list.printDot("D:/project/workspace/TypeState/sootOutput/");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initTGL() {
		Body body = sm.getActiveBody();
		Stmt st = (Stmt) body.getUnits().iterator().next();
		interestLocal.clear();
		Chain<Local> locals = body.getLocals();
		for (Local local : locals) {
			String classname = local.getType().toString();
			if (IntraGenerator.allClass.contains(classname) && !local.getName().startsWith("$")) {
				interestLocal.add(local);
				TypeGraph tg = new TypeGraph(classname, local.getName());
				typegraph_list.addTypeGraph(tg);
			}
		}
		typegraph_list.transAll(sm.hashCode(), st.hashCode());
	}

	private void processStmt(Stmt s, Stmt succst) {
		if (s instanceof ReturnVoidStmt) {
			typegraph_list.transAll(s.hashCode(), -sm.hashCode());
			return;
		}
		if (s instanceof GotoStmt) {
			GotoStmt gos = (GotoStmt) s;
			Unit target = gos.getTarget();
			typegraph_list.transAll(s.hashCode(), target.hashCode());
			return;
		}
		if (s instanceof IfStmt) {
			IfStmt ifs = (IfStmt) s;
			Stmt target = ifs.getTarget();
			typegraph_list.transAll(s.hashCode(), target.hashCode());
			typegraph_list.transAll(s.hashCode(), succst.hashCode());
			return;
		}
		if (s instanceof TableSwitchStmt) {
			TableSwitchStmt tst = (TableSwitchStmt) s;
			Unit defaulttarget = tst.getDefaultTarget();
			typegraph_list.transAll(s.hashCode(), defaulttarget.hashCode());
			Iterator targets = tst.getTargets().iterator();
			for (; targets.hasNext();) {
				Unit target = (Unit) targets.next();
				typegraph_list.transAll(s.hashCode(), target.hashCode());
			}
			return;
		}
		if (s instanceof LookupSwitchStmt) {
			typegraph_list.transAll(s.hashCode(), succst.hashCode());
			return;
		}
		if (s instanceof MonitorStmt) {
			typegraph_list.transAll(s.hashCode(), succst.hashCode());
			return;
		}
		if (s instanceof RetStmt) {
			typegraph_list.transAll(s.hashCode(), -sm.hashCode());
			return;
		}
		if (s instanceof NopStmt) {
			typegraph_list.transAll(s.hashCode(), -sm.hashCode());
			return;
		}
		if (s.containsInvokeExpr()) {
			InvokeExpr ie = s.getInvokeExpr();
			if (IntraGenerator.InterestMethod.contains(ie.getMethod().toString()) && ie instanceof InstanceInvokeExpr) {
				Local base = (Local)((InstanceInvokeExpr) ie).getBase();
				if(ie.toString().contains("<init>") && succst instanceof AssignStmt){
					Value lhs = ((DefinitionStmt) succst).getLeftOp();
					Value rhs = ((DefinitionStmt) succst).getRightOp();
					if(lhs instanceof Local && rhs instanceof Local && ((Local)rhs).getName().equals(base.getName())){
						base = (Local)lhs;
					}
				}
				if(interestLocal.contains(base)){
					typegraph_list.transMethod(base.getName(), ie.getMethod().toString(), s.hashCode(), succst.hashCode());
				}
			} else
				typegraph_list.transAll(s.hashCode(), succst.hashCode());
			return;
		}
		typegraph_list.transAll(s.hashCode(), succst.hashCode());
		return;
	}

}
