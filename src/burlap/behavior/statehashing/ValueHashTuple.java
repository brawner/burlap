package burlap.behavior.statehashing;

import burlap.oomdp.core.Value;

public class ValueHashTuple {
	private final ValueHashFactory hashingFactory;
	private final Value value;
	private final int hashCode;
	
	public ValueHashTuple(Value value, ValueHashFactory hashingFactory, int hashCode) {
		this.value = value;
		this.hashingFactory = hashingFactory;
		this.hashCode = hashCode;
	}
	
	public ValueHashFactory getHashFactory() {
		return this.hashingFactory;
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}
}
