package burlap.behavior.statehashing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;


/**
 * This hashing factory provides hashing and state equality checks for domains in which the object name references are important.
 * This is in contrast to the typical OO-MDP state equality checking in which as long as there was a bijection between set of
 * object instances in two state objects in which the matched objects had equivalent value assignments, the states were considered
 * equal. In other words, in typical OO-MDP the ObjectInstance name identifier did not affect state equality. In a NameDependent domain,
 * changing the name of an object instances, changes which state it represents. NameDependent domains are useful for domains with
 * relational attributes in which the specific object reference to which an attribute is linked is important.
 * <p/>
 * Hash codes are currently computed using the name identifier of the object instance as well as the value assignments for all
 * observable object instances.
 * @author James MacGlashan
 *
 */
public class NameDependentStateHashFactory implements StateHashFactory {

	//protected List <String>				objectNameOrder;
	//protected Set <String>				objectNames;
	private final Map<String, Integer>  objectNameOrderLookup;
	private final NameDependentObjectHashFactory hashingFactory = new NameDependentObjectHashFactory();
	public NameDependentStateHashFactory(){
		this.objectNameOrderLookup = new HashMap<String, Integer>();
	}
	
	public NameDependentStateHashFactory(NameDependentStateHashFactory other){
		this.objectNameOrderLookup = new HashMap<String, Integer>(other.objectNameOrderLookup);
	}
	
	public synchronized Integer getObjectPosition(String name) {
		Integer position = this.objectNameOrderLookup.get(name);
		if (position == null) {
			position = this.objectNameOrderLookup.size();
			this.objectNameOrderLookup.put(name, position);
		}
		return position;
	}
	
	public Map<String, Integer> getObjectNameOrderLookup() {
		return this.objectNameOrderLookup;
	}
	
	public int getMaximumObjectListSize(State state) {
		return this.objectNameOrderLookup.size() + state.numObservableObjects();
	}
	
	@Override
	public StateHashTuple hashState(State s) {
		return new NameDependentStateHashTuple(s);
	}
	
	public class NameDependentStateHashTuple extends StateHashTuple{
		
		public NameDependentStateHashTuple(State s) {
			super(s);
		}

		@Override
		public int computeHashCode() {
			State state = this.getState();	
			int listSize = NameDependentStateHashFactory.this.getMaximumObjectListSize(this.getState());
			ObjectInstance[] orderedObjects = new ObjectInstance[listSize];
			List<ObjectInstance> objects = state.getObservableObjects();
			int code = 0;
			for (ObjectInstance object : objects) {
				code += object.hashCode();
				
				/*
				String objectName = object.getName();
				Integer position = NameDependentStateHashFactory.this.getObjectPosition(objectName);
				if (position == null) {
					throw new RuntimeException("Entering undiscovered country");
				}
				if (position >= orderedObjects.length) {
					throw new RuntimeException("Array size is too small. Tried to access position " + position + " in array of size " + listSize);
				}
				
				orderedObjects[position] = object;*/
			
			}
			/*
			StringBuilder buf = new StringBuilder();
			
			for (ObjectInstance object : orderedObjects) {
				if (object != null) {
					code += object.hashCode();
					//buf = object.buildObjectDescription(buf);
				}
			}*/
			return code;
			
			/*for(String objectName : orderedObjects){
				ObjectInstance o = this.getState().getObject(objectName);
				if(o != null){
					buf.append(o.getObjectDescription());
				}
			}*/
			
			//return buf.toString().hashCode();
			//this.needToRecomputeHashCode = false;
			
		}
		
		
		@Override
		public boolean equals(Object other){
			if(this == other){
				return true;
			}
			if(!(other instanceof NameDependentStateHashTuple)){
				return false;
			}
			NameDependentStateHashTuple o = (NameDependentStateHashTuple)other;
			
			State state = this.getState();
			State otherState = o.getState();
			if (state == otherState) {
				return true;
			}
			
			List <ObjectInstance> obs = state.getObservableObjects();
			
			
			if (obs.size() != otherState.numObservableObjects()) {
				return false;
			}

			for(ObjectInstance ob : obs){
				String name = ob.getName();
				ObjectInstance oob = otherState.getObject(name);
				if(oob == null){
					return false;
				}
				if(!ob.valueEquals(oob)){
					return false;
				}
				
			}
			
			return true;
			
			
		}
		
		
		
	}

	@Override
	public ObjectHashFactory getObjectHashFactory() {
		return this.hashingFactory;
	}
	

}
