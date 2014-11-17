package burlap.behavior.statehashing;

import burlap.oomdp.core.Value;

public class ValueHashTuple {
	private final ValueHashFactory hashingFactory;
	private final Value value;
	private final int hashCode;
	
	private ValueHashTuple(Value value, ValueHashFactory hashingFactory, int hashCode) {
		this.value = value;
		this.hashingFactory = hashingFactory;
		this.hashCode = hashCode;
	}
	
	public static ValueHashTuple createTuple(Value value, ValueHashFactory hashingFactory, int code) {
		if (value == null || hashingFactory == null) {
			return null;
		}
		return new ValueHashTuple(value, hashingFactory, code);
	}
}
