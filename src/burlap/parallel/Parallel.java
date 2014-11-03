package burlap.parallel;

import java.util.Collection;
import java.util.Iterator;

public class Parallel {
	
	public static void For(int start, int end, int increment) {
		
	}
	
	public abstract class ForRunnable {
		public abstract void iterate(int start, int current, int end, int increment);
	}
	
	public static void ForEach(Collection<?> collection, ForEachRunnable runnable) {
		
	}
	
	public abstract class ForEachRunnable {
		public abstract void iterate(Iterator<?> it);
	}
}
