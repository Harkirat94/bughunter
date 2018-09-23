package analysis;

import java.util.ArrayList;
import java.util.HashMap;
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

public class AvailableCondtionalExprMustAnalysis extends ForwardFlowAnalysis {

	Body b;
	public static HashMap<Integer, ArrayList<ConditionExpr>> exitMustSet=new HashMap<>();
	
	public AvailableCondtionalExprMustAnalysis(UnitGraph g) {
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
		ConditionWrapper.lineNum++;
		//gen operation
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
			arrConditionExpr.add((ConditionExpr) iter.next());
		}
		exitMustSet.put(ConditionWrapper.lineNum, arrConditionExpr);

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
		inval1.intersection(inval2, outSet);
	}

	@Override
	protected Object newInitialFlow() {
		ArraySparseSet s = new ArraySparseSet();
		return s;
	}

}
