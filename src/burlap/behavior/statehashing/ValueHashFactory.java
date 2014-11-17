package burlap.behavior.statehashing;

import burlap.oomdp.core.Value;

public abstract class ValueHashFactory {

	public abstract ValueHashTuple hashValue(Value value);
}
