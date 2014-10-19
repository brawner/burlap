package burlap.oomdp.core.values;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Value;


/**
 * A multi-target relational value object subclass. Values are stored as an ordered set (TreeSet) of string names
 * of the object instance name identifiers. If the attribute is not linked to any target, the set will be empty.
 * @author James MacGlashan
 *
 */
public class MultiTargetRelationalValue extends Value {

	/**
	 * The set of object targets to which this value points. Object targets are indicated
	 * by their object name identifier.
	 */
	private final Set<String>		targetObjects;
	
	
	/**
	 * Initializes the value to be associted with the given attribute
	 * @param attribute the attribute with which this value is associated
	 */
	public MultiTargetRelationalValue(Attribute attribute){
		super(attribute);
		Set<String> targets = new TreeSet<String>();
		this.targetObjects = Collections.unmodifiableSet(targets);
	}
	
	
	/**
	 * Initializes this value as a copy from the source Value object v.
	 * @param v the source Value to make this object a copy of.
	 */
	public MultiTargetRelationalValue(Value v){
		super(v);
		MultiTargetRelationalValue rv = (MultiTargetRelationalValue)v;
		Set<String> targets = new TreeSet<String>(rv.targetObjects);
		this.targetObjects = Collections.unmodifiableSet(targets);
	}
	
	public MultiTargetRelationalValue(Attribute attribute, Set<String> targetObjects) {
		super(attribute);
		Set<String> targets = new TreeSet<String>(targetObjects);
		this.targetObjects = Collections.unmodifiableSet(targets);
	}
	
	@Override
	public Value copy() {
		return new MultiTargetRelationalValue(this);
	}
	
	@Override
	public Value changeValue(String v) {
		Set<String> targetObjects = new TreeSet<String>();
		targetObjects.add(v);
		return new MultiTargetRelationalValue(this.attribute, targetObjects);
	}
	
	@Override
	public Value appendRelationalTarget(String v) {
		TreeSet<String> newTargetObjects = new TreeSet<String>(this.targetObjects);
		newTargetObjects.add(v);
		return new MultiTargetRelationalValue(this.attribute, newTargetObjects);
	}
	
	@Override
	public Value appendAllRelationalTargets(Collection<String> targets) {
		TreeSet<String> newTargetObjects = new TreeSet<String>(this.targetObjects);
		newTargetObjects.addAll(targets);
		return new MultiTargetRelationalValue(this.attribute, newTargetObjects);
	}
	
	public Value removeAllRelationalTargets(){
		return new MultiTargetRelationalValue(attribute);
	}
	public Value replaceRelationalTarget(String target){
		TreeSet<String> newTargetObjects = new TreeSet<String>(this.targetObjects);
		newTargetObjects.remove(target);
		return new MultiTargetRelationalValue(this.attribute, targetObjects);
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
	@Deprecated
	public void setValue(String v) {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to a double value")); /*
		this.targetObjects.clear();
		this.targetObjects.add(v);*/
	}
	
	@Override
	public void setValue(boolean v) {
		throw new UnsupportedOperationException("Value is of relational; cannot be set to a boolean value.");
	}
	
	@Override
	@Deprecated
	public void addRelationalTarget(String t) {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to a double value")); /*
		this.targetObjects.add(t);*/
	}
	
	@Override
	@Deprecated
	public void addAllRelationalTargets(Collection<String> targets) {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to a double value")); /*
		this.targetObjects.addAll(targets);*/
	}
	
	
	@Override
	@Deprecated
	public void clearRelationTargets() {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to a double value")); /*
		this.targetObjects.clear();*/
	}
	
	@Override
	@Deprecated
	public void removeRelationalTarget(String target) {
		throw new UnsupportedOperationException(new Error("Cannot set relation value to a value to a double value"));
		/*
		this.targetObjects.remove(target);*/
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
		return this.targetObjects;
	}
	

	@Override
	public String getStringVal() {
		StringBuffer buf = new StringBuffer();
		boolean didFirst = false;
		for(String t : this.targetObjects){
			if(didFirst){
				buf.append(";");
			}
			buf.append(t);
			didFirst = true;
		}
		return buf.toString();
	}
	
	@Override
	public StringBuilder buildStringVal(StringBuilder builder) {
		boolean didFirst = false;
		for(String t : this.targetObjects){
			if(didFirst){
				builder.append(";");
			}
			builder.append(t);
			didFirst = true;
		}
		return builder;
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
		
		if(!(obj instanceof MultiTargetRelationalValue)){
			return false;
		}
		
		MultiTargetRelationalValue op = (MultiTargetRelationalValue)obj;
		if(!op.attribute.equals(attribute)){
			return false;
		}
		
		if(this.targetObjects.size() != op.targetObjects.size()){
			return false;
		}
		
		Iterator<String> thisIt = this.targetObjects.iterator();
		Iterator<String> thatIt = op.targetObjects.iterator();
		
		while(thisIt.hasNext()) {
			if (!thisIt.next().equals(thatIt.next())) {
				return false;
			}
		}
		
		return true;
		
	}


	@Override
	public boolean getBooleanValue() {
		throw new UnsupportedOperationException("Value is MultiTargetRelational, cannot return boolean representation.");
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
