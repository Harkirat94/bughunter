package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XeqY;
import org.jacop.constraints.XgtC;
import org.jacop.constraints.XgtY;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XgteqY;
import org.jacop.constraints.XltC;
import org.jacop.constraints.XltY;
import org.jacop.constraints.XlteqC;
import org.jacop.constraints.XlteqY;
import org.jacop.constraints.XneqC;
import org.jacop.constraints.XneqY;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

import soot.Body;
import soot.BodyTransformer;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class ConditionWrapper extends BodyTransformer {

	public static final int max_val = 2147483647/2;
	public static final int min_val = -2147483648/2;
	public static HashMap<SootMethod, HashSet<String>> method_exception_map = new HashMap<SootMethod, HashSet<String>>();
	public static HashMap<Stmt, HashSet<String>> unit_exception_map = new HashMap<Stmt, HashSet<String>>();

	public static int lineNum=0;
	public static int lineNum_HK=0;
	
	public static HashSet<String> find_all_symbols(String error_var, List<ConditionExpr> conditionExprList){
		HashSet<String> uniqueSymbols = new HashSet<String>();
		uniqueSymbols.add(error_var);
		for(ConditionExpr ce : conditionExprList){
			if(!ce.getOp1().toString().matches("\\d+")){
				uniqueSymbols.add(ce.getOp1().toString());
			}
			if(!ce.getOp2().toString().matches("\\d+")){
				uniqueSymbols.add(ce.getOp2().toString());
			}
		}
		return uniqueSymbols;
	}

	public static IntVar create_default_domains(Store store, String symbol_str){
		IntVar symbol_jacop = new IntVar(store, symbol_str, min_val , max_val);
		return symbol_jacop;
	}

	public static PrimitiveConstraint lessThan(Value operand1, Value operand2, HashMap<String, IntVar> string_symbol_map){
		PrimitiveConstraint primitiveConstraint = null;
		if(!operand1.toString().matches("\\d+") && operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand2.toString());
			IntVar op1 = string_symbol_map.get(operand1.toString());
			primitiveConstraint = new XltC(op1, num);
		}
		else if(operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XgtC(op2, num);
		}
		else if(!operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			IntVar op1 = string_symbol_map.get(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XltY(op1, op2);
		}
		return primitiveConstraint;
	}

	public static PrimitiveConstraint greaterThan(Value operand1, Value operand2, HashMap<String, IntVar> string_symbol_map){
		PrimitiveConstraint primitiveConstraint = null;
		if(!operand1.toString().matches("\\d+") && operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand2.toString());
			IntVar op1 = string_symbol_map.get(operand1.toString());
			primitiveConstraint = new XgtC(op1, num);
		}
		else if(operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XltC(op2, num);
		}
		else if(!operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			IntVar op1 = string_symbol_map.get(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XgtY(op1, op2);
		}
		return primitiveConstraint;
	}

	public static PrimitiveConstraint lessThanEqualTo(Value operand1, Value operand2, HashMap<String, IntVar> string_symbol_map){
		PrimitiveConstraint primitiveConstraint = null;
		if(!operand1.toString().matches("\\d+") && operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand2.toString());
			IntVar op1 = string_symbol_map.get(operand1.toString());
			primitiveConstraint = new XlteqC(op1, num);
		}
		else if(operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XgteqC(op2, num);
		}
		else if(!operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			IntVar op1 = string_symbol_map.get(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XlteqY(op1, op2);
		}
		return primitiveConstraint;
	}

	public static PrimitiveConstraint greaterThanEqualTo(Value operand1, Value operand2, HashMap<String, IntVar> string_symbol_map){
		PrimitiveConstraint primitiveConstraint = null;
		if(!operand1.toString().matches("\\d+") && operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand2.toString());
			IntVar op1 = string_symbol_map.get(operand1.toString());
			primitiveConstraint = new XgteqC(op1, num);
		}
		else if(operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XlteqC(op2, num);
		}
		else if(!operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			IntVar op1 = string_symbol_map.get(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XgteqY(op1, op2);
		}
		return primitiveConstraint;
	}

	public static PrimitiveConstraint notEqualTo(Value operand1, Value operand2, HashMap<String, IntVar> string_symbol_map){
		PrimitiveConstraint primitiveConstraint = null;
		if(!operand1.toString().matches("\\d+") && operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand2.toString());
			IntVar op1 = string_symbol_map.get(operand1.toString());
			primitiveConstraint = new XneqC(op1, num);
		}
		else if(operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XneqC(op2, num);
		}
		else if(!operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			IntVar op1 = string_symbol_map.get(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XneqY(op1, op2);
		}
		return primitiveConstraint;
	}

	public static PrimitiveConstraint equalTo(Value operand1, Value operand2, HashMap<String, IntVar> string_symbol_map){
		PrimitiveConstraint primitiveConstraint = null;
		if(!operand1.toString().matches("\\d+") && operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand2.toString());
			IntVar op1 = string_symbol_map.get(operand1.toString());
			primitiveConstraint = new XeqC(op1, num);
		}
		else if(operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			int num = Integer.parseInt(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XeqC(op2, num);
		}
		else if(!operand1.toString().matches("\\d+") && !operand2.toString().matches("\\d+")){
			IntVar op1 = string_symbol_map.get(operand1.toString());
			IntVar op2 = string_symbol_map.get(operand2.toString());
			primitiveConstraint = new XeqY(op1, op2);
		}
		return primitiveConstraint;
	}

	public static boolean preciseMustArithmeticException(int myLineNum, Stmt exp_statement){
		HashMap<String, IntVar> string_symbol_map = new HashMap<String, IntVar>();
		ArrayList<ConditionExpr> mustConditions = AvailableCondtionalExprMustAnalysis.exitMustSet.get(myLineNum);
		Store store = new Store();
		if(exp_statement instanceof AssignStmt){
			Value righOp = ((AssignStmt) exp_statement).getRightOp();
			if(righOp.getUseBoxes().size()>0){
				// above statement confirms it is in the form of op1/op2
				List<ImmediateBox> operands = righOp.getUseBoxes();
				Value error_var = operands.get(1).getValue(); // error variable is the second one in DivByZero
				//find all symbols
				HashSet<String> uniqueSymbols = find_all_symbols(error_var.toString(), mustConditions);

				// create actual domains, by default (min, max)
				for(String uni_sym : uniqueSymbols){
					IntVar symbol_domain = create_default_domains(store, uni_sym);
					string_symbol_map.put(uni_sym, symbol_domain);
				}

				// create constrained domains
				for(ConditionExpr cond_expr : mustConditions){
					Value operand1 = cond_expr.getOp1();
					Value operand2 = cond_expr.getOp2();
					String operator = cond_expr.getSymbol();
					String trimmedString = operator.trim();
					if(trimmedString.equals("<")){
						PrimitiveConstraint primitiveConstraint = lessThan(operand1, operand2, string_symbol_map);
						if(primitiveConstraint!=null){
							store.impose(primitiveConstraint);
						}
					}
					else if(trimmedString.equals(">")){
						PrimitiveConstraint primitiveConstraint = greaterThan(operand1, operand2, string_symbol_map);
						if(primitiveConstraint!=null){
							store.impose(primitiveConstraint);
						}
					}
					else if(trimmedString.equals("<=")){
						PrimitiveConstraint primitiveConstraint = lessThanEqualTo(operand1, operand2, string_symbol_map);
						if(primitiveConstraint!=null){
							store.impose(primitiveConstraint);
						}
					}
					else if(trimmedString.equals(">=")){
						PrimitiveConstraint primitiveConstraint = greaterThanEqualTo(operand1, operand2, string_symbol_map);
						if(primitiveConstraint!=null){
							store.impose(primitiveConstraint);
						}
					}
					else if(trimmedString.equals("!=")){
						PrimitiveConstraint primitiveConstraint = notEqualTo(operand1, operand2, string_symbol_map);
						if(primitiveConstraint!=null){
							store.impose(primitiveConstraint);
						}
					}
					else if(trimmedString.equals("==")){
						PrimitiveConstraint primitiveConstraint = equalTo(operand1, operand2, string_symbol_map);
						if(primitiveConstraint!=null){
							store.impose(primitiveConstraint);
						}
					}
				}// constraint domain found
				IntVar error_symbol = string_symbol_map.get(error_var.toString());
				PrimitiveConstraint boundaryConstraint = new XeqC(error_symbol,0);
				store.impose(boundaryConstraint);
				//System.out.println(store.consistency());
				return store.consistency();
			}
			else{
				return false; // False positive
			}
		}
		else{
			return false; // else for statements like nop
		}
	}


	@Override
	protected void internalTransform(Body body, String arg1, Map arg2) {

		ExceptionalUnitGraph g=new ExceptionalUnitGraph(body);

		if(body.getMethod().getName().contains("div"))
		{
			InitializationAnalysis live=new InitializationAnalysis(g);
			AvailableCondtionalExprMustAnalysis live3=new AvailableCondtionalExprMustAnalysis(g);

			HashMap<Integer, ArrayList<ConditionExpr>> mustCondExprs=AvailableCondtionalExprMustAnalysis.exitMustSet;
		}

		SootMethod sootMethod = body.getMethod();
		/* 2. Analyzing candidate exceptions for individual functions */
		UnitThrowAnalysis unitThrowAnalysisObj = UnitThrowAnalysis.v();
		PatchingChain<Unit> patchingChain = body.getUnits();
		Iterator<Unit> iter = patchingChain.iterator();

		HashSet<String> function_exp = new HashSet<String>(); 
		lineNum_HK = 0;
		while(iter.hasNext()){
			HashSet<String> line_exp = new HashSet<String>();
			lineNum_HK++;
			Stmt statement = (Stmt)iter.next();
			String allExceptions = unitThrowAnalysisObj.mightThrow(statement).toString();
			String[] splitMetEx = allExceptions.split("\n");
			String[] splitEx  = splitMetEx[1].split("\\+");
			for(String s : splitEx){
				line_exp.add(s.trim());
			}
			line_exp.removeAll(Driver.nonInteresting);
			if(line_exp.contains("java.lang.ArithmeticException")){
				boolean result = preciseMustArithmeticException(lineNum_HK, statement);
				// false means handles
				if(!result){
					line_exp.remove("java.lang.ArithmeticException");
				}
			}
			function_exp.addAll(line_exp);
		}
		method_exception_map.put(sootMethod, function_exp);
	}

}
