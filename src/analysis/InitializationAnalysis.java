package analysis;

import java.util.ArrayList;
import java.util.Iterator;

import soot.Body;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class InitializationAnalysis extends ForwardFlowAnalysis{

	Body b;
	public static ArrayList<ConditionExpr> allConditionExprs= new ArrayList<>();
	int lineNum=0;

	public InitializationAnalysis(UnitGraph g) {
		super(g);
		ExceptionalUnitGraph ug=(ExceptionalUnitGraph)g;
		b=g.getBody();
		doAnalysis();
	}

	@Override
	protected void flowThrough(Object in, Object unit, Object out) {
		FlowSet inval=(FlowSet)in;
		FlowSet outval=(FlowSet)out;
		Stmt stmt=(Stmt)unit;
		inval.copy(outval);
		lineNum++;
		if(stmt instanceof IfStmt)
		{
			IfStmt ifs = (IfStmt)stmt;
			Value condition = ifs.getCondition();
			ConditionExpr conditionExpr = (ConditionExpr) condition;
			outval.add(conditionExpr);
		}
		
		Iterator iter=outval.iterator();
		ArrayList<ConditionExpr> arrConditionExpr=new ArrayList<>();
		while(iter.hasNext())
		{
			ConditionExpr expr=(ConditionExpr)iter.next();
			if(!allConditionExprs.contains(expr))
				allConditionExprs.add(expr);
		}

	}

	@Override
	protected void copy(Object src, Object dest) {
		FlowSet srcSet=(FlowSet)src;
		FlowSet destSet=(FlowSet)dest;
		srcSet.copy(destSet);
	}

	@Override
	protected Object entryInitialFlow() {
		return new ArraySparseSet();
	}

	@Override
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inval1=(FlowSet)in1;
		FlowSet inval2=(FlowSet)in2;
		FlowSet outSet=(FlowSet)out;
		inval1.union(inval2, outSet);
	}

	@Override
	protected Object newInitialFlow() {
		ArraySparseSet s = new ArraySparseSet();
		return s;
	}
}
