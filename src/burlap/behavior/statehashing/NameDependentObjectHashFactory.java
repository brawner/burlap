package burlap.behavior.statehashing;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.Value;

public class NameDependentObjectHashFactory extends ObjectHashFactory {

	private final NameDependentValueHashFactory hashingFactory = new NameDependentValueHashFactory();
	
	@Override
	public ObjectHashTuple hashObject(ObjectInstance object) {
		int code = object.getName().hashCode();
		for (Value value : object.getValues()) {
			code = 31*code + value.hashCode();					
		}
		return NameDependentObjectHashTuple.makeTuple(object, this, code);
	}
	
	public static class NameDependentObjectHashTuple extends ObjectHashTuple {
		
		private NameDependentObjectHashTuple(ObjectInstance object, NameDependentObjectHashFactory hashingFactory, int hashCode) {
			super(object, hashingFactory, hashCode);
		}
		
		public static NameDependentObjectHashTuple makeTuple(ObjectInstance object, NameDependentObjectHashFactory hashingFactory, int hashCode) {
			if (object == null || hashingFactory == null) {
				return null;
			}
			
			return new NameDependentObjectHashTuple(object, hashingFactory, hashCode);
		}
	}

	@Override
	public ValueHashFactory getValueHashFactory() {
		return this.hashingFactory;
	}

}
