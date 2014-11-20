package burlap.oomdp.core.values;

import java.util.Collection;
import java.util.Set;

import burlap.behavior.statehashing.ValueHashFactory;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Value;


/**
 * This class provides a value for a string. 
 * @author Greg Yauney (gyauney)
 *
 */
public class StringValue extends Value {

	/**
	 * The string value
	 */
	private final String			stringVal;
	
	
	/**
	 * Initializes for a given attribute. The default value will be set to 0.
	 * @param attribute
	 */
	public StringValue(Attribute attribute, ValueHashFactory hashingFactory) {
		super(attribute, hashingFactory);
		this.stringVal = "";
		this.computeHash(hashingFactory);
	}
	
	
	/**
	 * Initializes from an existing value.
	 * @param v the value to copy
	 */
	public StringValue(Value v) {
		super(v);
		this.stringVal = ((StringValue)v).stringVal;
	}
	
	public StringValue(Attribute attribute, String v, ValueHashFactory hashingFactory) {
		super(attribute, hashingFactory);
		this.stringVal = v;
		this.computeHash(hashingFactory);
	}
	
	@Override
	public boolean isSet() {
		return !this.stringVal.equals("");
	}

	@Override
	public Value copy() {
		return new StringValue(this);
	}

	@Override
	public Value changeValue(int v) {
		return new StringValue(this.attribute, Integer.toString(v), this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value changeValue(double v) {
		return new StringValue(this.attribute, Double.toString(v), this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value changeValue(String v) {
		return new StringValue(this.attribute, v, this.hashTuple.getHashFactory());
	}
	
	@Deprecated
	@Override
	public void setValue(int v) {
		//this.stringVal = Integer.toString(v);
	}
	
	@Deprecated
	@Override
	public void setValue(double v) {
		//this.stringVal = Double.toString(v);
	}
	
	@Deprecated
	@Override
	public void setValue(String v) {
		//this.stringVal = v;
	}
	
	@Override
	public void setValue(boolean v) {
		throw new UnsupportedOperationException("Value is of type String; cannot be set to a boolean value.");
	}

	@Override
	public void addRelationalTarget(String t) {
		throw new UnsupportedOperationException("Value is String, cannot add relational target");
	}

	@Override
	public void addAllRelationalTargets(Collection<String> targets) {
		throw new UnsupportedOperationException("Value is String, cannot add relational targets");
	}
	
	@Override
	public void clearRelationTargets() {
		throw new UnsupportedOperationException("Value is String, cannot clear relational targets");
	}

	@Override
	public void removeRelationalTarget(String target) {
		throw new UnsupportedOperationException("Value is String, cannot remove relational target");
	}

	@Override
	public int getDiscVal() {
		throw new UnsupportedOperationException("Value is String, cannot return int value");
	}

	@Override
	public double getRealVal() {
		throw new UnsupportedOperationException("Value is String, cannot return real value");
	}

	@Override
	public String getStringVal() {
		return this.stringVal;
	}
	
	@Override
	public StringBuilder buildStringVal(StringBuilder builder) {
		return builder.append(this.stringVal);
	}

	@Override
	public Set<String> getAllRelationalTargets() {
		throw new UnsupportedOperationException("Value is String, cannot return relational values");
	}

	@Override
	public double getNumericRepresentation() {
		throw new UnsupportedOperationException("Value is String, cannot return numeric representation");
	}
	
	
	@Override
	public boolean equals(Object obj){
		if (this == obj) {
			return true;
		}
		
		if(!(obj instanceof StringValue)){
			return false;
		}
		
		StringValue o = (StringValue)obj;
		
		if(!o.attribute.equals(attribute)){
			return false;
		}
		
		return this.stringVal.equals(o.stringVal);
		
	}


	@Override
	public boolean getBooleanValue() {
		throw new UnsupportedOperationException("Value is String, cannot return boolean representation.");
	}
	
	@Override
	public void setValue(int[] intArray) {
		throw new UnsupportedOperationException("Value is string; cannot be set to an int array.");
	}


	@Override
	public void setValue(double[] doubleArray) {
		throw new UnsupportedOperationException("Value is string; cannot be set to a double array.");
	}


	@Override
	public int[] getIntArray() {
		throw new UnsupportedOperationException("Value is string; cannot return an int array.");
	}


	@Override
	public double[] getDoubleArray() {
		throw new UnsupportedOperationException("Value is string; cannot return a double array.");
	}

}
