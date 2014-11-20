package burlap.oomdp.core.values;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import burlap.behavior.statehashing.ValueHashFactory;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Value;


/**
 * A relational valued value subclass in which values are stored as a single String object for the name of the object instance to which it is linked.
 * If the relational value is not linked to any object, then the String value is set to the empty String: "".
 * @author James MacGlashan
 *
 */
public class RelationalValue extends Value {

	/**
	 * A string representing the object target of this value. Targets are specified by the object name identifier.
	 * If the relational target is unset, then this value will be set to the empty string "", which is the default value.
	 */
	private final String		target;
	
	
	/**
	 * Initializes this value to be an assignment for Attribute attribute.
	 * @param attribute
	 */
	public RelationalValue(Attribute attribute, ValueHashFactory hashingFactory){
		super(attribute, hashingFactory);
		this.target = "";
		this.computeHash(hashingFactory);
	}
	
	
	/**
	 * Initializes this value as a copy from the source Value object v.
	 * @param v the source Value to make this object a copy of.
	 */
	public RelationalValue(Value v){
		super(v);
		RelationalValue rv = (RelationalValue)v;
		this.target = rv.target;
	}
	
	public RelationalValue(Attribute attribute, String target, ValueHashFactory hashingFactory) {
		super(attribute, hashingFactory);
		this.target = target;
		this.computeHash(hashingFactory);
	}
	
	@Override
	public boolean isSet() {
		return true;
	}

	
	@Override
	public Value copy() {
		return new RelationalValue(this);
	}
	
	@Override
	public Value changeValue(String v) {
		return new RelationalValue(this.attribute, v, this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value appendRelationalTarget(String v) {
		return new RelationalValue(this.attribute, v, this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value removeAllRelationalTargets(){
		return new RelationalValue(attribute, this.hashTuple.getHashFactory());
	}
	
	@Override
	public Value replaceRelationalTarget(String target){
		return new RelationalValue(this.attribute, this.hashTuple.getHashFactory());
	}
	
	@Override
	public void setValue(int v) {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to an int value"));
	}

	@Override
	public void setValue(double v) {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to a double value"));
	}
	
	@Override
	public void setValue(boolean v) {
		throw new UnsupportedOperationException("Value is relational; cannot be set to a boolean value.");
	}

	@Override
	@Deprecated
	public void setValue(String v) {
		throw new UnsupportedOperationException("Value is relational; cannot be set to a boolean value.");
		//this.target = v;
	}
	
	@Override
	@Deprecated
	public void addRelationalTarget(String t) {
		throw new UnsupportedOperationException("Value is relational; cannot be set to a boolean value.");
		//this.target = t;
	}
	
	@Override
	public void addAllRelationalTargets(Collection<String> targets) {
		throw new UnsupportedOperationException("Value is relational, cannot add multiple relational targets");
	}
	
	@Override
	@Deprecated
	public void clearRelationTargets() {
		throw new UnsupportedOperationException("Value is relational; cannot be set to a boolean value.");
		//this.target = "";
	}
	
	@Override
	@Deprecated
	public void removeRelationalTarget(String target) {
		throw new UnsupportedOperationException("Value is relational; cannot be set to a boolean value.");
		/*if(this.target.equals(target)){
			this.target = "";
		}*/
	}

	@Override
	public int getDiscVal() {
		throw new UnsupportedOperationException(new Error("Value is relational, cannot return discrete value"));
	}

	@Override
	public double getRealVal() {
		throw new UnsupportedOperationException(new Error("Value is relational, cannot return real value"));
	}
	
	@Override
	public Set<String> getAllRelationalTargets() {
		Set <String> res = new TreeSet<String>();
		res.add(this.target);
		return res;
	}

	@Override
	public String getStringVal() {
		return this.target;
	}
	
	@Override
	public StringBuilder buildStringVal(StringBuilder builder) {
		return builder.append(this.target);
	}

	@Override
	public double getNumericRepresentation() {
		return 0;
	}

	
	@Override
	public boolean equals(Object obj){
		if (this == obj) {
			return true;
		}
		
		if(!(obj instanceof RelationalValue)){
			return false;
		}
		
		RelationalValue op = (RelationalValue)obj;
		if(!op.attribute.equals(attribute)){
			return false;
		}
		
		return this.target.equals(op.target);
		
	}


	@Override
	public boolean getBooleanValue() {
		throw new UnsupportedOperationException("Value is relational, cannot return boolean representation.");
	}


	@Override
	public void setValue(int[] intArray) {
		throw new UnsupportedOperationException("Value is relational; cannot be set to an int array.");
	}


	@Override
	public void setValue(double[] doubleArray) {
		throw new UnsupportedOperationException("Value is relational; cannot be set to a double array.");
	}


	@Override
	public int[] getIntArray() {
		throw new UnsupportedOperationException("Value is relational; cannot return an int array.");
	}


	@Override
	public double[] getDoubleArray() {
		throw new UnsupportedOperationException("Value is relational; cannot return a double array.");
	}


	
	

}
