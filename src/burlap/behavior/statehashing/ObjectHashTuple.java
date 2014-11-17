package burlap.behavior.statehashing;

import burlap.oomdp.core.ObjectInstance;

public class ObjectHashTuple {

	private final ObjectInstance object;
	private final ObjectHashFactory hashingFactory;
	private final int hashCode;
	
	private ObjectHashTuple(ObjectInstance object, ObjectHashFactory hashingFactory, int hashCode) {
		this.object = object;
		this.hashingFactory = hashingFactory;
		this.hashCode = hashCode;
	}
	
	public ObjectHashTuple makeTuple(ObjectInstance object, ObjectHashFactory hashingFactory, int hashCode) {
		if (object == null || hashingFactory == null) {
			return null;
		}
		
		return new ObjectHashTuple(object, hashingFactory, hashCode);
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}
}
