package burlap.oomdp.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateBuilder {
	private final List<ObjectInstance> objectInstances;
	private final List<ObjectInstance> hiddenObjects;
	private Map<String, Integer> objectMap;
	private final Map<String, Integer> objectClassMap;
	private boolean modifyObjectMap;
	
	public StateBuilder() {
		this.objectInstances = new ArrayList<ObjectInstance>();
		this.hiddenObjects = new ArrayList<ObjectInstance>();
		this.objectMap = new HashMap<String, Integer>();
		this.objectClassMap = new HashMap<String, Integer>();
		this.modifyObjectMap = true;
	}
	
	public StateBuilder(State state) {
		this.objectInstances = new ArrayList<ObjectInstance>(state.getObservableObjects());
		this.hiddenObjects = new ArrayList<ObjectInstance>(state.getHiddenObjects());
		this.objectMap = state.getObjectMap();
		this.objectClassMap = state.getObjectClassMap();
		this.modifyObjectMap = false;
	}
	
	private void initObjectMap() {
		if (!this.modifyObjectMap) {
			this.objectMap = new HashMap<String, Integer>(this.objectMap);
			this.modifyObjectMap = true;
		}
	}
	
	public void add(ObjectInstance object) {
		int position = this.objectInstances.size();
		
		Integer displaced = this.objectMap.put(object.getName(), position);
		if ( displaced == null) {
			this.objectInstances.add(object);
			this.updatePositions(position);
		} else {
			this.initObjectMap();
			this.objectMap.put(object.getName(), displaced);
		}	
	}
	
	public void addAll(Collection<ObjectInstance> objects) {
		for (ObjectInstance object : objects) {
			this.add(object);
		}
	}
	
	private void updatePositions(int startPosition) {
		if (startPosition < this.objectInstances.size()) {
			this.initObjectMap();
			for (int i = startPosition; i < this.objectInstances.size(); i++) {
				String name = this.objectInstances.get(i).getName();
				this.objectMap.put(name, i);
			}
		}
		
		if (!this.hiddenObjects.isEmpty()) {
			this.initObjectMap();
			startPosition -= this.objectInstances.size();
			startPosition = Math.max(0, startPosition);
			int numObservedObjects = this.objectInstances.size();
			for (int i = startPosition ; i < this.hiddenObjects.size(); i++) {
				String name = this.objectInstances.get(i).getName();
				this.objectMap.put(name, i + numObservedObjects);
			}
		}
	}
	
	public void addHidden(ObjectInstance object) {
		int position = this.hiddenObjects.size() + this.objectInstances.size();
		this.initObjectMap();
		Integer displaced = this.objectMap.put(object.getName(), position);
		if ( displaced == null) {
			this.objectInstances.add(object);
		} else {
			this.objectMap.put(object.getName(), displaced);
		}
	}
	
	public void addAllHidden(Collection<ObjectInstance> objects) {
		for (ObjectInstance object : objects) {
			this.addHidden(object);
		}
	}
	
	public void set(int position, ObjectInstance object) {
		int adjustedPosition = position;
		ObjectInstance removed = null, added = null;
		
		if (adjustedPosition < this.objectInstances.size()) {
			removed = this.objectInstances.set(adjustedPosition, object);
			added = object;
		} else {
			adjustedPosition -= this.objectInstances.size();
			if (adjustedPosition < this.hiddenObjects.size()) {
				removed = this.hiddenObjects.set(adjustedPosition, object);
				added = object;
			}
		}
		
		if (added != null && !added.getName().equals(removed.getName())) {
			this.objectMap.put(added.getName(), adjustedPosition);
			if (removed != null) {
				this.objectMap.remove(removed.getName());
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
		ObjectInstance removed = null;
		
		if (position < this.objectInstances.size()) {
			removed = this.objectInstances.remove(position);
		} else {
			position -= this.objectInstances.size();
			removed = this.hiddenObjects.remove(position);
		}
		if (removed != null) {
			this.initObjectMap();
			this.objectMap.remove(removed.getName());
		}
		
		this.updatePositions(position);
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
			this.remove(object.getName());
		}
	}
	
	
	public State toState() {
		List<List<Integer>> objectsIndexedByTrueClass = this.buildObjectIndexByTrueClass(this.objectInstances, this.hiddenObjects, this.objectClassMap);
		/*for (ObjectInstance object : this.objectInstances) {
			if (!this.objectMap.containsKey(object.getName())) {
				System.err.println("Uh oh");
			}
		}
		for (ObjectInstance object : this.hiddenObjects) {
			if (!this.objectMap.containsKey(object.getName())) {
				System.err.println("Uh oh");
			}
		}*/
		
		return new State(this.objectInstances, this.hiddenObjects, this.objectMap, objectsIndexedByTrueClass, this.objectClassMap, this.objectInstances.size(), this.hiddenObjects.size());
	}
	
	private final List<List<Integer>> buildObjectIndexByTrueClass(List<ObjectInstance> objects, List<ObjectInstance> hiddenObjects, Map<String, Integer> objectClassMap) {
		int size = objects.size() + hiddenObjects.size();
		int initialSize = objectClassMap.size();
		List<List<Integer>> objectIndexByTrueClass = new ArrayList<List<Integer>>(initialSize);
		for (int i = 0; i < initialSize; i++) {
			objectIndexByTrueClass.add(new ArrayList<Integer>(size));
		}

		boolean madeModifiable = this.addObjectListToList(objects, objectIndexByTrueClass, objectClassMap, false);
		madeModifiable = this.addObjectListToList(hiddenObjects, objectIndexByTrueClass, objectClassMap, madeModifiable);
		
		return objectIndexByTrueClass;
	}
	
	private boolean addObjectListToList(List<ObjectInstance> objects,
			List<List<Integer>> objectIndexByTrueClass, Map<String, Integer> objectClassMap, boolean madeModifiable) {
		
		for (int i = 0; i < objects.size(); i++) {
			ObjectInstance object = objects.get(i);
			
			String objectClassName = object.getTrueClassName();
			Integer position = objectClassMap.get(objectClassName);
			
			if (position == null) {
				position = objectIndexByTrueClass.size();
				if (!madeModifiable) {
					objectClassMap = new HashMap<String, Integer>(objectClassMap);
					madeModifiable = true;
				}
				objectClassMap.put(objectClassName, position );
				objectIndexByTrueClass.add(new ArrayList<Integer>(objects.size()));
			}
			List<Integer> objectsOfClass = objectIndexByTrueClass.get(position);
			objectsOfClass.add(i);
		}
		return madeModifiable;
	}
}
