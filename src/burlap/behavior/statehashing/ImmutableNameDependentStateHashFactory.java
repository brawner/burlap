package burlap.behavior.statehashing;

import java.util.Collections;
import java.util.Map;

import burlap.oomdp.core.State;

public class ImmutableNameDependentStateHashFactory extends NameDependentStateHashFactory { 
	private final Map<String, Integer> objectNameOrderLookup;
	
	public ImmutableNameDependentStateHashFactory(NameDependentStateHashFactory mutableHashFactory){
		this.objectNameOrderLookup = Collections.unmodifiableMap(mutableHashFactory.getObjectNameOrderLookup());
	}
	
	@Override
	public Integer getObjectPosition(String name) {
		return this.objectNameOrderLookup.get(name);
	}
	
	@Override
	public int getMaximumObjectListSize(State state) {
		return this.objectNameOrderLookup.size();
	}
	
	@Override
	public StateHashTuple hashState(State s) {
		return new NameDependentStateHashTuple(s);
	}
}