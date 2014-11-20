package burlap.oomdp.core.values;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import burlap.behavior.statehashing.ValueHashFactory;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Value;


/**
 * This class implements an attribute value that is defined with an int array. In general, it is reccomended that a series of {@link IntValue} attributes
 * is defined instead of using this class, because a series of {@link IntValue}s will have better compatibility with existing BURLAP tools and algorithms, but this class
 * can be used in cases where there is a very large number of int values that have to be stored in each state to cut down on memory overhead.
 * @author James MacGlashan
 *
 */
public class IntArrayValue extends Value {

	private final int [] intArray;
	
	
	public IntArrayValue(Attribute attribute, ValueHashFactory hashingFactory) {
		super(attribute, hashingFactory);
		this.intArray = null;
		this.computeHash(hashingFactory);
	}
	
	public IntArrayValue(Value v){
		super(v);
		IntArrayValue iaValue  = (IntArrayValue)v;
		this.intArray = (iaValue == null) ? null : iaValue.intArray.clone();		
	}
	
	public IntArrayValue(Attribute attribute, int[] intArray, ValueHashFactory hashingFactory) {
 		super(attribute, hashingFactory);
 		this.intArray = (intArray == null) ? null : intArray.clone();
 		this.computeHash(hashingFactory);
 	}
	
	@Override
	public boolean isSet() {
		return !(this.intArray == null);
	}


	@Override
	public Value copy() {
		return new IntArrayValue(this);
	}

	@Override
	public Value changeValue(String v) {
		if(v.startsWith("\"") && v.endsWith("\"")){
			v = v.substring(1, v.length());
		}
		String [] comps = v.split(",");
		int[] intArray = new int[comps.length];
		for(int i = 0; i < comps.length; i++){
			intArray[i] = Integer.parseInt(comps[i]);
		}
		return new IntArrayValue(this.attribute, intArray, this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value changeValue(int[] intArray) {
		return new IntArrayValue(this.attribute, intArray, this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value changeValue(double[] doubleArray) {
		int[] intArray = new int[doubleArray.length];
		for(int i = 0; i < doubleArray.length; i++){
			intArray[i] = (int)doubleArray[i];
		}
		return new IntArrayValue(this.attribute, intArray, this.hashTuple.getHashFactory());
	}
	
	
	@Override
	public void setValue(int v) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot set single int value.");
	}

	@Override
	public void setValue(double v) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot set double value.");
	}

	@Override
	@Deprecated
	public void setValue(String v) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot set double value."); /*
		if(v.startsWith("\"") && v.endsWith("\"")){
			v = v.substring(1, v.length());
		}
		String [] comps = v.split(",");
		this.intArray = new int[comps.length];
		for(int i = 0; i < comps.length; i++){
			this.intArray[i] = Integer.parseInt(comps[i]);
		}*/
	}

	@Override
	public void addRelationalTarget(String t) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot set relational value.");
	}
	
	@Override
	public void addAllRelationalTargets(Collection<String> targets) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot add relational targets");
	}
	
	@Override
	public void clearRelationTargets() {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot clear values.");
	}

	@Override
	public void removeRelationalTarget(String target) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot clear values.");
	}

	@Override
	public int getDiscVal() {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot return disc values");
	}

	@Override
	public double getRealVal() {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot return real values");
	}

	@Override
	public String getStringVal() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < this.intArray.length; i++){
			if(i > 0){
				buf.append(",");
			}
			buf.append(this.intArray[i]);
		}
		return buf.toString();
	}
	
	@Override
	public StringBuilder buildStringVal(StringBuilder builder) {
		return builder.append(Arrays.toString(this.intArray));
	}

	@Override
	public Set<String> getAllRelationalTargets() {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot return relational values");
	}

	@Override
	public boolean getBooleanValue() {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot return boolean values");
	}

	@Override
	public double getNumericRepresentation() {
		int sum = 0;
		for(int v : this.intArray){
			sum *= 31;
			sum += v;
		}
		return sum;
	}

	@Override
	@Deprecated
	public void setValue(int[] intArray) {
		throw new UnsupportedOperationException("Value is of type IntArray, cannot set double value.");/*
		this.intArray = intArray;*/
	}

	@Override
	public void setValue(double[] doubleArray) {
		throw new UnsupportedOperationException("Cannot set int array value to double array value.");	
	}

	@Override
	public int[] getIntArray() {
		return this.intArray;
	}

	@Override
	public double[] getDoubleArray() {
		double [] doubleArray = new double[this.intArray.length];
		for(int i = 0; i < doubleArray.length; i++){
			doubleArray[i] = this.intArray[i];
		}
		return doubleArray;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj) {
			return true;
		}
		
		if(!(obj instanceof IntArrayValue)){
			return false;
		}
		
		IntArrayValue o = (IntArrayValue)obj;
		
		if(!o.attribute.equals(attribute)){
			return false;
		}
		
		if(this.intArray.length != o.intArray.length){
			return false;
		}
		
		for(int i = 0; i < this.intArray.length; i++){
			if(this.intArray[i] != o.intArray[i]){
				return false;
			}
		}
		
		return true;
		
	}
	
	@Override
	public void setValue(boolean v) {
		throw new UnsupportedOperationException("Value is of type DoubleArray; cannot be set to a boolean value.");
	}

}
