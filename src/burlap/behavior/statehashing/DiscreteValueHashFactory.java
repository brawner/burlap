package burlap.behavior.statehashing;

import java.util.Arrays;
import java.util.List;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Value;
import burlap.oomdp.core.Attribute.AttributeType;

public class DiscreteValueHashFactory extends ValueHashFactory{

	private static final List<AttributeType> validTypes = 
			Arrays.asList(AttributeType.DISC, AttributeType.BOOLEAN, AttributeType.INT, AttributeType.STRING, AttributeType.INTARRAY);
	
	@Override
	public ValueHashTuple hashValue(Value value) {
		return this.hashValue(value, 1);
	}
	
	public ValueHashTuple hashValue(Value value, int vol) {
		ValueHashTuple hashTuple = value.getHashTuple();
		if (hashTuple != null) {
			return hashTuple;
		}
		int index = 0;
		Attribute att = value.getAttribute();
		if(att.type == AttributeType.STRING){
			index += value.getStringVal().hashCode()*vol;
		}
		else if(att.type == AttributeType.INTARRAY){
			index += this.intArrayCode(value.getIntArray())*vol;
		}
		else{
			if (value.isSet()) {
				index += value.getDiscVal()*vol;
			}
		}
		
		if (!DiscreteValueHashFactory.validTypes.contains(att.type)){
			throw new RuntimeException("DiscreteStateHashFactory cannot compute hash for non discrete (discrete, boolean, string, or int) values");
		}
		
		return new ValueHashTuple(value, this, index);
	}

	protected int intArrayCode(int [] array){
		int sum = 0;
		for(int i : array){
			sum *= 31;
			sum += i;
		}
		return sum;
	}
	
	
	
	public class DiscreteValueHashTuple extends ValueHashTuple {

		public DiscreteValueHashTuple(Value value,
				ValueHashFactory hashingFactory, int hashCode) {
			super(value, hashingFactory, hashCode);
		}
		
	}

}
