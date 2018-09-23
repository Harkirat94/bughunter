package analysis;

import java.util.HashSet;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;

public class Driver {

	private static final String SRC_DIR = "C:\\Users\\Harkirat\\Documents\\Eclipse_ws_PA\\ConditionAnalysis\\src;";
	public static HashSet<String> nonInteresting = new HashSet<String>();

	public static void fillNonIteresting(){
		nonInteresting.add("java.lang.OutOfMemoryError");
		nonInteresting.add("Any_subtype_of_java.lang.Error");
		nonInteresting.add("java.lang.IllegalMonitorStateException");
		nonInteresting.add("java.lang.ThreadDeath");
		nonInteresting.add("java.lang.StackOverflowError");
		nonInteresting.add("java.lang.InternalError");
		nonInteresting.add("Any_subtype_of_java.lang.Throwable");
		nonInteresting.add("java.lang.UnknownError");
		nonInteresting.add("java.lang.NoClassDefFoundError");
		nonInteresting.add("java.lang.IncompatibleClassChangeError");
		nonInteresting.add("java.lang.IllegalAccessError");
		nonInteresting.add("java.lang.ClassCircularityError");
		nonInteresting.add("Any_subtype_of_java.lang.ClassFormatError");
		nonInteresting.add("java.lang.VerifyError");
		nonInteresting.add("java.lang.LinkageError");
		nonInteresting.add("");
	}

	public static void main(String[] args) {
		fillNonIteresting();
		// Add a new phase.
		PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter", new ConditionWrapper()));
		
		// Set options to keep the original variable names, output Jimple, use
		// the Java source and update the class path.
		Options.v().setPhaseOption("jb", "use-original-names");
		Options.v().set_output_format(Options.output_format_J);
		Options.v().set_src_prec(Options.src_prec_java);
		Scene.v().setSootClassPath(SRC_DIR + Scene.v().getSootClassPath());
		
		// Load the given class.
		String[] sampleClass = {"sample.Sample10"};
		SootClass class_analyze = Scene.v().loadClassAndSupport(sampleClass[0]);
		System.out.println("Loaded Class: " + class_analyze.getName() + "\n");
		soot.Main.main(sampleClass);
		
		for(SootMethod sm : ConditionWrapper.method_exception_map.keySet()){
			HashSet<String> possibleException = ConditionWrapper.method_exception_map.get(sm);
			possibleException.removeAll(nonInteresting);
			ConditionWrapper.method_exception_map.put(sm, possibleException);
		}
		
		
		for(SootMethod sm : ConditionWrapper.method_exception_map.keySet()){
			System.out.println("Method : "+ sm.getName());
			System.out.println("Possible Exceptions : ");
			HashSet<String> possibleException = ConditionWrapper.method_exception_map.get(sm);
			for(String str : possibleException){
				System.out.println(str);
			}
			System.out.println();
		}
	}
}
