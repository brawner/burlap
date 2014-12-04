package burlap.oomdp.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateBuilder {
	private final List<ObjectInstance> objectInstances;
	private final List<ObjectInstance> hiddenObjects;
	private final Map<String, Integer> objectMap;
	private final Map<String, Integer> objectClassMap;
	private final List<List<Integer>>  objectsIndexedByTrueClass;
	public StateBuilder() {
		this.objectInstances = new ArrayList<ObjectInstance>();
		this.hiddenObjects = new ArrayList<ObjectInstance>();
		this.objectMap = new HashMap<String, Integer>();
		this.objectClassMap = new HashMap<String, Integer>();
		this.objectsIndexedByTrueClass = new ArrayList<List<Integer>>();
	}
	
	public StateBuilder(State state) {
		this.objectInstances = new ArrayList<ObjectInstance>(state.getObservableObjects());
		this.hiddenObjects = new ArrayList<ObjectInstance>(state.getHiddenObjects());
		this.objectMap = new HashMap<String, Integer>(state.getObjectMap());
		this.objectsIndexedByTrueClass = state.getAllObjectIndicesByTrueClass();
		this.objectClassMap = state.getObjectClassMap();
	}
	
	public void add(ObjectInstance object) {
		int position = this.objectInstances.size();
		if (this.objectMap.put(object.getName(), position) == null) {
			this.objectInstances.add(object);
		}
	}
	
	public void addAll(Collection<ObjectInstance> objects) {
		for (ObjectInstance object : objects) {
			this.add(object);
		}
	}
	
	public void addHidden(ObjectInstance object) {
		int position = this.hiddenObjects.size();
		if (this.objectMap.put(object.getName(), position) != null) {
			this.objectInstances.add(object);
		}
	}
	
	public void addAllHidden(Collection<ObjectInstance> objects) {
		for (ObjectInstance object : objects) {
			this.addHidden(object);
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
	
	public void replaceAll(List<ObjectInstance> oldObjects, List<ObjectInstance> newObjects) {
		if (oldObjects.size() == newObjects.size()) {
			for (int i = 0; i < oldObjects.size(); i++) {
				this.replace(oldObjects.get(i), newObjects.get(i));
			}
		}
	}
	
	public void remove(int position) {
		String name = null;
		if (position < this.objectInstances.size()) {
			ObjectInstance object = this.objectInstances.get(position);
			name = (object == null) ? null : object.getName();
			this.objectInstances.set(position, null);
		} else {
			position -= this.objectInstances.size();
			ObjectInstance object = this.hiddenObjects.get(position);
			name = (object == null) ? null : object.getName();
			this.hiddenObjects.set(position, null);
		}
		if (name != null) {
			this.objectMap.remove(name);
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
	
	public void removeAll(Collection<ObjectInstance> objects) {
		for (ObjectInstance object: objects) {
			this.remove(object);
		}
	}
	
	
	public State toState() {
		return new State(this.objectInstances, this.hiddenObjects, this.objectClassMap);
	}
}
