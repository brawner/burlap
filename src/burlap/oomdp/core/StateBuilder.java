package burlap.oomdp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateBuilder {
	private final List<ObjectInstance> objectInstances;
	private final List<ObjectInstance> hiddenObjects;
	private final Map<String, Integer> objectMap;
	
	public StateBuilder() {
		this.objectInstances = new ArrayList<ObjectInstance>();
		this.hiddenObjects = new ArrayList<ObjectInstance>();
		this.objectMap = new HashMap<String, Integer>();
	}
	
	public StateBuilder(State state) {
		this.objectInstances = new ArrayList<ObjectInstance>(state.getObservableObjects());
		this.hiddenObjects = new ArrayList<ObjectInstance>(state.getHiddenObjects());
		this.objectMap = new HashMap<String, Integer>(state.getObjectMap());
	}
	
	public void add(ObjectInstance object) {
		int position = this.objectInstances.size();
		if (this.objectMap.put(object.getName(), position) != null) {
			this.objectInstances.add(object);
		}
	}
	
	public void addHidden(ObjectInstance object) {
		int position = this.hiddenObjects.size();
		if (this.objectMap.put(object.getName(), position) != null) {
			this.objectInstances.add(object);
		}
	}
	
	public void set(int position, ObjectInstance object) {
		if (position < this.objectInstances.size()) {
			this.objectInstances.set(position, object);
		} else {
			position -= this.objectInstances.size();
			if (position < this.hiddenObjects.size()) {
				this.hiddenObjects.set(position, object);
			}
		}
		
	}
	
	public void replace(ObjectInstance old, ObjectInstance object) {
		Integer position = this.objectMap.get(old.getName());
		if (position != null) {
			this.set(position, object);
		}
	}
	
	public void remove(int position) {
		if (position < this.objectInstances.size()) {
			this.objectInstances.set(position, null);
		} else {
			position -= this.objectInstances.size();
			this.hiddenObjects.set(position, null);
		}
	}
	
	public void remove(String objectName) {
		Integer position = this.objectMap.get(objectName);
		if (position != null) {
			this.remove(position);
		}
	}
	
	public void remove(ObjectInstance object) {
		this.remove(object.getName());
	}
	
	
	public State toState() {
		return new State(this.objectInstances, this.hiddenObjects, this.objectMap);
	}
}
