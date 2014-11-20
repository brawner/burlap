package burlap.behavior.statehashing;

import burlap.oomdp.core.ObjectInstance;

public abstract class ObjectHashFactory {

	public abstract ObjectHashTuple hashObject(ObjectInstance object);
	public abstract ValueHashFactory getValueHashFactory();
}
