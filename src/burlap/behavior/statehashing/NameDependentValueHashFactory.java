package burlap.behavior.statehashing;

import burlap.oomdp.core.Value;

public class NameDependentValueHashFactory extends ValueHashFactory {

	@Override
	public ValueHashTuple hashValue(Value value) {
		int code = (value.isSet()) ? value.getStringVal().hashCode() : 0;
		return NameDependentValueHashTuple.makeTuple(value, this, code);
	}
	
	public static class NameDependentValueHashTuple extends ValueHashTuple {
		public NameDependentValueHashTuple(Value value, NameDependentValueHashFactory hashingFactory, int code) {
			super(value, hashingFactory, code);
		}
		
		public static NameDependentValueHashTuple makeTuple(Value value, NameDependentValueHashFactory hashingFactory, int code) {
			if (value == null || hashingFactory == null) {
				return null;
			}
			
			return new NameDependentValueHashTuple(value, hashingFactory, code);
		}
	}

}
