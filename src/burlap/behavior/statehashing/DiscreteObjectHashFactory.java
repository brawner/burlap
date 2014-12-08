package burlap.behavior.statehashing;

import java.util.List;
import java.util.Map;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.Value;
import burlap.oomdp.core.Attribute.AttributeType;

public class DiscreteObjectHashFactory extends ObjectHashFactory {

	private static final DiscreteValueHashFactory valueHashingFactory = new DiscreteValueHashFactory();
	protected Map<String, List<Attribute>>	attributesForHashCode;
	
	public DiscreteObjectHashFactory() {
		this.attributesForHashCode = null;
	}
	
	public DiscreteObjectHashFactory(Map<String, List<Attribute>> attributesForHashCode) {
		this.attributesForHashCode = attributesForHashCode;
	}
	@Override
	public ObjectHashTuple hashObject(ObjectInstance object) {
		ObjectHashTuple hashTuple = object.getHashTuple();
		if (hashTuple != null) {
			return hashTuple;
		}
		
		int index = 0;
		int vol = 1;
		for (Value value : object.getValues()) {
			index += DiscreteObjectHashFactory.valueHashingFactory.hashValue(value, vol).hashCode();
			
			Attribute att = value.getAttribute();
			if(att.type==AttributeType.DISC || att.type == AttributeType.BOOLEAN){
				vol *= att.discValues.size();
			}
			else if(att.type==AttributeType.INT || att.type==AttributeType.STRING || att.type==AttributeType.INTARRAY){
				vol *= 31;
			}
		}
		return new DiscreteObjectHashTuple(object, this, index);
	}

	@Override
	public ValueHashFactory getValueHashFactory() {
		return this.valueHashingFactory;
	}
	
	public class DiscreteObjectHashTuple extends ObjectHashTuple {

		public DiscreteObjectHashTuple(ObjectInstance object,
				ObjectHashFactory hashingFactory, int hashCode) {
			super(object, hashingFactory, hashCode);
		}
	}

}
