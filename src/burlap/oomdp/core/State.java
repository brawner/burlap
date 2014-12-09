package burlap.oomdp.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;


/**
 * State objects are a collection of Object Instances.
 * @author James MacGlashan
 *
 */
public final class State {

	
	/**
	 * List of observable object instances that define the state
	 */
	private final List <ObjectInstance>							objectInstances;
	
	/**
	 * List of hidden object instances that facilitate domain dynamics and infer observable values
	 */
	private final List <ObjectInstance>							hiddenObjectInstances;
	
	/**
	 * Map from object names to their instances
	 */
	private final Map <String, Integer>							objectMap;
	
	
	/**
	 * Map of object instances organized by class name
	 */
	private final List<List <Integer>>							objectIndexByTrueClass;

	private final Map<String, Integer>							objectClassMap;
	
	private final int 											numObservedObjects;
	private final int											numHiddenObjects;
	
	private static Comparator<List<ObjectInstance>> objectClassComparator = new Comparator<List<ObjectInstance>>() {
		@Override
		public int compare(List<ObjectInstance> o1, List<ObjectInstance> o2) {
			return o1.get(0).getTrueClassName().compareTo(o2.get(0).getTrueClassName());
		}
	};
	//private final Domain domain;
	
	
	public State(){
		this.objectInstances = Collections.unmodifiableList(new ArrayList<ObjectInstance>());
		this.hiddenObjectInstances = Collections.unmodifiableList(new ArrayList<ObjectInstance>());
		this.objectIndexByTrueClass = Collections.unmodifiableList(new ArrayList<List<Integer>>());
		this.objectClassMap = Collections.unmodifiableMap(new HashMap<String, Integer>());
		this.objectMap = Collections.unmodifiableMap(new HashMap<String, Integer>());
		this.numObservedObjects = 0;
		this.numHiddenObjects = 0;
	}
	
	
	/**
	 * Initializes this state as a deep copy of the object instances in the provided source state s
	 * @param s the source state from which this state will be initialized.
	 */
	public State(State s){
		this.objectInstances = s.objectInstances;
		this.hiddenObjectInstances = s.hiddenObjectInstances;
		this.objectIndexByTrueClass = s.objectIndexByTrueClass;
		this.objectMap = s.objectMap;
		this.objectClassMap = s.objectClassMap;
		this.numObservedObjects = s.numObservedObjects;
		this.numHiddenObjects = s.numHiddenObjects;
	}
	
	public State(List<ObjectInstance> objects) {
		Map<String, Integer> objectMap = new HashMap<String, Integer>();
		Map<String, Integer> objectClassMap = new HashMap<String, Integer>();
		List<ObjectInstance> objectInstances = this.createObjectLists(objects, objectMap, 0);
		
		this.objectInstances = Collections.unmodifiableList(objectInstances);
		this.numObservedObjects = objectInstances.size();
		
		this.hiddenObjectInstances = Collections.unmodifiableList(new ArrayList<ObjectInstance>());
		this.numHiddenObjects = 0;
		
		List<List<Integer>> objectIndexByTrueClass = 
				this.buildObjectIndexByTrueClass(objectInstances, hiddenObjectInstances, objectClassMap);
		this.objectClassMap = Collections.unmodifiableMap(objectClassMap);
		this.objectIndexByTrueClass = Collections.unmodifiableList(objectIndexByTrueClass);
		
		this.objectMap = Collections.unmodifiableMap(objectMap);
	}
	
	public State(List<ObjectInstance> objects, List<ObjectInstance> hiddenObjects, Map<String, Integer> objectClassMap) {
		Map<String, Integer> objectMap = new HashMap<String, Integer>();
		objectClassMap = new HashMap<String, Integer>(objectClassMap);
		List<ObjectInstance> objectInstances = this.createObjectLists(objects, objectMap, 0);
		this.numObservedObjects = objectInstances.size();
		
		List<ObjectInstance> hiddenObjectsInstances = this.createObjectLists(hiddenObjects, objectMap, objectInstances.size());
		this.objectInstances = Collections.unmodifiableList(objectInstances);
		
		this.hiddenObjectInstances = Collections.unmodifiableList(hiddenObjectsInstances);
		this.numHiddenObjects = hiddenObjectInstances.size();
		
		int numberClasses = objectClassMap.size();
		List<List<Integer>> objectIndexByTrueClass = new ArrayList<List<Integer>>(numberClasses);
		for (int i = 0 ; i < numberClasses; i++) {
			objectIndexByTrueClass.add(new ArrayList<Integer>());
		}
		
		this.addObjectListToList(this.objectInstances, objectIndexByTrueClass, objectClassMap);
		this.addObjectListToList(this.hiddenObjectInstances, objectIndexByTrueClass, objectClassMap);
		this.objectIndexByTrueClass = Collections.unmodifiableList(objectIndexByTrueClass);
		this.objectClassMap = Collections.unmodifiableMap(objectClassMap);
		this.objectMap = Collections.unmodifiableMap(objectMap);
	}
	
	public State(List<ObjectInstance> objects, List<ObjectInstance> hiddenObjects, List<List<Integer>> objectIndexedByTrueClass, Map<String, Integer> objectClassMap, int numObservedObjects, int numHiddenObjects) {
		this.objectInstances = Collections.unmodifiableList(objects);
		this.hiddenObjectInstances = Collections.unmodifiableList(hiddenObjects);
		this.objectIndexByTrueClass = Collections.unmodifiableList(objectIndexedByTrueClass);
		this.objectClassMap = Collections.unmodifiableMap(objectClassMap);
		this.numObservedObjects = numObservedObjects;
		this.numHiddenObjects = numHiddenObjects;
		this.objectMap = Collections.unmodifiableMap(this.buildObjectMap());
		
	}
	
	public State(List<ObjectInstance> objects, List<ObjectInstance> hiddenObjects, Map<String, Integer> objectMap, List<List<Integer>> objectIndexedByTrueClass, Map<String, Integer> objectClassMap, int numObservedObjects, int numHiddenObjects) {
		this.objectInstances = Collections.unmodifiableList(objects);
		this.hiddenObjectInstances = Collections.unmodifiableList(hiddenObjects);
		this.objectIndexByTrueClass = Collections.unmodifiableList(objectIndexedByTrueClass);
		this.objectClassMap = Collections.unmodifiableMap(objectClassMap);
		this.objectMap = Collections.unmodifiableMap(objectMap);
		this.numObservedObjects = numObservedObjects;
		this.numHiddenObjects = numHiddenObjects;
	}
	
	/**
	 * Returns a deep copy of this state.
	 * @return a deep copy of this state.
	 */
	public State copy(){
		return new State(this);
	}
	
	private final Map<String, Integer> buildObjectMap() {
		int initialSize = (int)((this.numObservedObjects + this.numHiddenObjects)/0.75 + 1);
		Map<String, Integer> objectMap = new HashMap<String, Integer>(initialSize);
		for (int i = 0, end = this.objectInstances.size(); i < end; i++) {
			String name = this.objectInstances.get(i).getName();
			objectMap.put(name, i);
		}
		for (int i = 0, end = this.hiddenObjectInstances.size(); i < end; i++) {
			String name = this.hiddenObjectInstances.get(i).getName();
			objectMap.put(name, i + this.numObservedObjects);
		}
		return objectMap;
	}
	
	private final List<ObjectInstance> createObjectLists(List<ObjectInstance> objectList, Map<String, Integer> objectMap, int offset) {
		List<ObjectInstance> objectInstances = new ArrayList<ObjectInstance>(objectList.size());
		for (ObjectInstance object : objectList) {
			if (object == null) {
				continue;
			}
			Integer displaced = objectMap.put(object.getName(), objectInstances.size() + offset);
			if (displaced != null) {
				objectMap.put(object.getName(), displaced);
			} else {
				objectInstances.add(object);
			}
		}
		return objectInstances;
	}
	
	private final List<List<Integer>> buildObjectIndexByTrueClass(List<ObjectInstance> objects, List<ObjectInstance> hiddenObjects, Map<String, Integer> objectClassMap) {
		List<List<Integer>> objectIndexByTrueClass = new ArrayList<List<Integer>>();
		this.addObjectListToList(objects, objectIndexByTrueClass, objectClassMap);
		this.addObjectListToList(hiddenObjects, objectIndexByTrueClass, objectClassMap);
		
		/*
		Map<String, List<Integer>> immutableListObjectsMap = new HashMap<String, List<Integer>>();
		for (Map.Entry<String, List<Integer>> entry : objectIndexByTrueClass.entrySet()) {
			immutableListObjectsMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
		}*/
		return objectIndexByTrueClass;
	}

	public Map<String, Integer> getObjectMap() {
		return this.objectMap;
	}

	private void addObjectListToList(List<ObjectInstance> objects,
			List<List<Integer>> objectIndexByTrueClass, Map<String, Integer> objectClassMap) {
		
		for (int i = 0; i < objects.size(); i++) {
			ObjectInstance object = objects.get(i);
			String objectClassName = object.getTrueClassName();
			Integer position = objectClassMap.get(objectClassName);
			
			if (position == null) {
				position = objectIndexByTrueClass.size();
				objectClassMap.put(objectClassName, position );
				objectIndexByTrueClass.add(new ArrayList<Integer>());
			}
			List<Integer> objectsOfClass = 
					objectIndexByTrueClass.get(position);
			objectsOfClass.add(i);
		}
	}
		
	/**
	 * Performs a semi-deep copy of the state in which only the objects with the names in deepCopyObjectNames are deep copied and the rest of the
	 * objects are shallowed copied.
	 * @param deepCopyObjectNames the names of the objects to be deep copied.
	 * @return a new state that is a mix of a shallow and deep copy of this state.
	 */
	public State semiDeepCopy(String...deepCopyObjectNames){
		Set<ObjectInstance> deepCopyObjectSet = new HashSet<ObjectInstance>(deepCopyObjectNames.length);
		for(String n : deepCopyObjectNames){
			deepCopyObjectSet.add(this.getObject(n));
		}
		return this.semiDeepCopy(deepCopyObjectSet);
	}
	
	
	/**
	 * Performs a semi-deep copy of the state in which only the objects in deepCopyObjects are deep copied and the rest of the
	 * objects are shallowed copied.
	 * @param deepCopyObjects the objects to be deep copied
	 * @return a new state that is a mix of a shallow and deep copy of this state.
	 */
	public State semiDeepCopy(ObjectInstance...deepCopyObjects){
		
		Set<ObjectInstance> deepCopyObjectSet = new HashSet<ObjectInstance>(deepCopyObjects.length);
		for(ObjectInstance d : deepCopyObjects){
			deepCopyObjectSet.add(d);
		}
		
		return this.semiDeepCopy(deepCopyObjectSet);
	}
	
	
	/**
	 * Performs a semi-deep copy of the state in which only the objects in deepCopyObjects are deep copied and the rest of the
	 * objects are shallowed copied.
	 * @param deepCopyObjects the objects to be deep copied
	 * @return a new state that is a mix of a shallow and deep copy of this state.
	 */
	public State semiDeepCopy(Set<ObjectInstance> deepCopyObjects){
		
		State s;
		try {
			s = this.getClass().newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			throw new RuntimeException("Error copying state instance");
		} 
		for(ObjectInstance o : this.objectInstances){
			if(deepCopyObjects.contains(o)){
				s.addObject(o.copy());
			}
			else{
				s.addObject(o);
			}
		}
		
		for(ObjectInstance o : this.hiddenObjectInstances){
			if(deepCopyObjects.contains(o)){
				s.addObject(o.copy());
			}
			else{
				s.addObject(o);
			}
		}
		
		return s;
	}
	
	public final State appendObject(ObjectInstance object) {
		return this.appendAllObjects(Arrays.asList(object));
	}
	
	public final State appendAllObjects(Collection<ObjectInstance> objectsToAdd) {
		List<ObjectInstance> objects = new ArrayList<ObjectInstance>(this.objectInstances);
		objects.addAll(objectsToAdd);
		return new State(objects, this.hiddenObjectInstances, this.objectClassMap);
	}
	
	public final State makeObjectsHidden(Collection<ObjectInstance> objectsToHide) {
		List<ObjectInstance> objects = new ArrayList<ObjectInstance>(this.objectInstances);
		objects.removeAll(objectsToHide);
		List<ObjectInstance> hiddenObjects = new ArrayList<ObjectInstance>(this.hiddenObjectInstances);
		hiddenObjects.addAll(objectsToHide);
		return new State(objects, hiddenObjects, this.objectClassMap);
	}
	
	public final State makeObjectsObservable(Collection<ObjectInstance> objectsToObserve) {
		List<ObjectInstance> hiddenObjects = new ArrayList<ObjectInstance>(this.hiddenObjectInstances);
		hiddenObjects.removeAll(objectsToObserve);
		List<ObjectInstance> observableObjects = new ArrayList<ObjectInstance>(this.objectInstances);
		observableObjects.addAll(objectsToObserve);
		return new State(observableObjects, hiddenObjects, this.objectClassMap);
	}
	
	/**
	 * Adds object instance o to this state.
	 * @param o the object instance to be added to this state.
	 */
	@Deprecated
	public void addObject(ObjectInstance o){
		
		String oname = o.getName();
		
		if(objectMap.containsKey(oname)){
			return ; //don't add an object that conflicts with another object of the same name
		}
		
		
		//objectMap.put(oname, o);
		
		
		if(o.getObjectClass().hidden){
			hiddenObjectInstances.add(o);
		}
		else{
			objectInstances.add(o);
		}
		
		
		this.addObjectClassIndexing(o);
		
		
	}
	
	@Deprecated
	protected void addObjectClassIndexing(ObjectInstance o){
		
		String otclass = o.getTrueClassName();
		
		//manage true indexing
		if(objectClassMap.containsKey(otclass)){
			//objectIndexByTrueClass.get(otclass).add(o);
		}
		else{
			
			ArrayList <ObjectInstance> classList = new ArrayList <ObjectInstance>();
			classList.add(o);
			//objectIndexByTrueClass.put(otclass, classList);
			
		}
		
	}
	
	private boolean removeObjectFromList(List<ObjectInstance> list, String oname) {
		Iterator<ObjectInstance> it = list.iterator();
		ObjectInstance obj;
		while (it.hasNext()) {
			obj  = it.next();
			if (obj.getName().equals(oname)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	public final State remove(String objectName) {
		Integer index = this.objectMap.get(objectName);
		if (index == null) {
			return this;
		}
		
		List<ObjectInstance> objects = this.objectInstances;
		List<ObjectInstance> hiddenObjects = this.hiddenObjectInstances;
		
		if (index < objects.size()) {
			objects = new ArrayList<ObjectInstance>(objects);
			objects.remove(index);
		}
		else
		{
			hiddenObjects = new ArrayList<ObjectInstance>(hiddenObjects);
			hiddenObjects.remove(index - objects.size());
		}

		return new State(objects, hiddenObjects, this.objectClassMap);
	}
	
	public final State remove(ObjectInstance object) {
		return this.remove(object.getName());
	}
	
	public final State removeAll(Collection<ObjectInstance> objectsToRemove) {
		List<Integer> indices = new ArrayList<Integer>();
		for (ObjectInstance object : objectsToRemove) {
			Integer index = this.objectMap.get(object.getName());
			if (index != null)
			indices.add(index);
		}
		
		List<ObjectInstance> objects = new ArrayList<ObjectInstance>(this.objectInstances);
		List<ObjectInstance> hiddenObjects = new ArrayList<ObjectInstance>(this.hiddenObjectInstances);
		
		Collections.sort(indices, Collections.reverseOrder());
		for (Integer i : indices) {
			if (i < objects.size()) {
				objects.remove(i);
			}
			else {
				objects.remove(i - objects.size());
			}
		}
		
		return new State(objects, hiddenObjects, this.objectClassMap);	
	}
	
	public final State replaceObject(ObjectInstance objectToReplace, ObjectInstance newObject) {
		
		Integer index = this.objectMap.get(objectToReplace.getName());
		
		List<ObjectInstance> objects = this.objectInstances;
		List<ObjectInstance> hiddenObjects = this.hiddenObjectInstances;
		
		if (index < this.numObservedObjects) {
			objects = new ArrayList<ObjectInstance>(objects);
			objects.set(index, newObject);
		} else {
			hiddenObjects = new ArrayList<ObjectInstance>(objects);
			hiddenObjects.set(index - this.numObservedObjects, newObject);
		}
		return new State(objects, hiddenObjects, this.objectMap, this.objectIndexByTrueClass, this.objectClassMap, this.numObservedObjects, this.numHiddenObjects);
	}
	
	public final State replaceAllObjects(List<ObjectInstance> objectsToRemove, List<ObjectInstance> objectsToAdd) {
		List<ObjectInstance> objects = new ArrayList<ObjectInstance>(this.objectInstances);
		List<ObjectInstance> hiddenObjects = new ArrayList<ObjectInstance>(this.hiddenObjectInstances);
		
		if (objectsToRemove.size() != objectsToAdd.size()) {
			throw new RuntimeException("This method requires the two collections to agree in size");
		}
		
		for (int i = 0; i < objectsToRemove.size(); i++) {
			ObjectInstance objectToRemove = objectsToRemove.get(i);
			ObjectInstance objectToAdd = objectsToAdd.get(i);
			
			Integer index = this.objectMap.get(objectToRemove.getName());
			if (index < objects.size()) {
				objects.set(index, objectToAdd);
			}
			else {
				hiddenObjects.set(index - objects.size(), objectToAdd);
			}
		}

		return new State(objects, hiddenObjects, this.objectClassMap);
	}
	
	/**
	 * Removes the object instance with the name oname from this state.
	 * @param oname the name of the object instance to remove.
	 */
	@Deprecated
	public void removeObject(String oname){
		//this.removeObject(objectMap.get(oname));
	}
	
	
	/**
	 * Removes the object instance o from this state.
	 * @param o the object instance to remove from this state.
	 */
	@Deprecated
	public void removeObject(ObjectInstance o){
		if(o == null){
			return ;
		}
		
		String oname = o.getName();
		
		if(!objectMap.containsKey(oname)){
			return ; //make sure we're removing something that actually exists in this state!
		}
		
		if(o.getObjectClass().hidden){
			hiddenObjectInstances.remove(o);
		}
		else{
			objectInstances.remove(o);
		}
		
		objectMap.remove(oname);
		
		this.removeObjectClassIndexing(o);
		
		
	}
	
	
	@Deprecated
	protected void removeObjectClassIndexing(ObjectInstance o){
		
		
		String otclass = o.getTrueClassName();
		//List <ObjectInstance> classTList = objectIndexByTrueClass.get(otclass);
		
		//if this index has more than one entry, then we can just remove from it and be done
		//if(classTList.size() > 1){
		//	classTList.remove(o);
		//}
		//else{
			//otherwise we have to remove class entries for it
		//	objectIndexByTrueClass.remove(otclass);
		//}
		
		
		
	}
	
	
	/**
	 * Renames the identifier for the object instance currently named originalName with the name newName.
	 * @param originalName the original name of the object instance to be renamed in this state
	 * @param newName the new name of the object instance
	 */
	@Deprecated
	public void renameObject(String originalName, String newName){
		//ObjectInstance o = objectMap.get(originalName);
		//o.setName(newName);
		//objectMap.remove(originalName);
		//objectMap.put(newName, o);
	}
	
	
	/**
	 * Renames the identifier for object instance o in this state to newName.
	 * @param o the object instance to rename in this state
	 * @param newName the new name of the object instance
	 */
	@Deprecated
	public void renameObject(ObjectInstance o, String newName){
		String originalName = o.getName();
		o.setName(newName);
		objectMap.remove(originalName);
		//objectMap.put(newName, o);
	}
	
	
	/**
	 * This method computes a matching from objects in the receiver to value-identical objects in the parameter state so. The matching
	 * is returned as a map from the object names in the receiving state to the matched objects in state so. If
	 * enforceStateExactness is set to true, then the returned matching will be an empty map if the two states
	 * are not OO-MDP-wise identical (i.e., if there is a not a bijection
	 *  between value-identical objects of the two states). If enforceExactness is false and the states are not identical,
	 *  the the method will return the largest matching between objects that can be made.
	 * @param so the state to whose objects the receiving state's objects should be matched
	 * @param enforceStateExactness whether to require that states are identical to return a matching
	 * @return a matching from this receiving state's objects to objects in so that have identical values. 
	 */
	public Map <String, String> getObjectMatchingTo(State so, boolean enforceStateExactness){
		Map <String, String> matching = new HashMap<String, String>();
		
		if(this.numTotalObjects() != so.numTotalObjects() && enforceStateExactness){
			return new HashMap<String, String>(); //states are not equal and therefore cannot be matched
		}
		
		Set<String> matchedObs = new HashSet<String>();
		
		for(Map.Entry<String, Integer> entry : this.objectClassMap.entrySet()){
			String oclass = entry.getKey();
			List <Integer> objectIndices = this.objectIndexByTrueClass.get(entry.getValue());
			List <ObjectInstance> oobjects = so.getObjectsOfTrueClass(oclass);
			if(objectIndices.size() != oobjects.size() && enforceStateExactness){
				return new HashMap<String, String>(); //states are not equal and therefore cannot be matched
			}
			
			for(Integer i : objectIndices){
				ObjectInstance o = this.getObject(i);
				boolean foundMatch = false;
				for(ObjectInstance oo : oobjects){
					if(matchedObs.contains(oo.getName())){
						continue; //already matched this one; check another
					}
					if(o.valueEquals(oo)){
						foundMatch = true;
						matchedObs.add(oo.getName());
						matching.put(o.getName(), oo.getName());
						break;
					}
				}
				if(!foundMatch && enforceStateExactness){
					return new HashMap<String, String>(); //states are not equal and therefore cannot be matched
				}
			}
			
		}
		
		return matching;
	}
	
	
	
	
	@Override
	public boolean equals(Object other){
	
		if(this == other){
			return true;
		}
		
		if(!(other instanceof State)){
			return false;
		}
		
		State so = (State)other;
		
		if(this.numTotalObjects() != so.numTotalObjects()){
			return false;
		}
		
		int numTotalObjects = this.numTotalObjects();
		Collection<Integer> matchedObjects = new ArrayList<Integer>(numTotalObjects);
		List<Integer> unmatchedObjects = new ArrayList<Integer>(numTotalObjects);
		this.checkOrderedObjects(this.objectInstances, so.objectInstances, 0, matchedObjects,
				unmatchedObjects);
		this.checkOrderedObjects(this.hiddenObjectInstances, so.hiddenObjectInstances, this.numObservedObjects, matchedObjects,
				unmatchedObjects);
		
		if (unmatchedObjects.isEmpty()) {
			return true;
		}
		
		matchedObjects = new HashSet<Integer>(matchedObjects);
		List<Integer> stillUnmatched = new ArrayList<Integer>(numTotalObjects);
		checkUnorderedObjects(so, matchedObjects, unmatchedObjects,
				stillUnmatched);
		
		return stillUnmatched.isEmpty();
		/*
		if (stillUnmatched.isEmpty()) {
			return true;
		}
		return false;
		
		return checkUnorderedValueEquals(so, matchedObjects, stillUnmatched);*/
	}


	private boolean checkUnorderedValueEquals(State so,
			Collection<Integer> matchedObjects, List<Integer> stillUnmatched) {
		for (Integer objectIndex : stillUnmatched) {
			ObjectInstance o = this.getObject(objectIndex);
			String oclass = o.getTrueClassName();
			Integer classPosition = so.objectClassMap.get(oclass);
			if (classPosition == null) {
				return false;
			}
			List<Integer> ootherIndices = so.objectIndexByTrueClass.get(classPosition);
			boolean foundMatch = false;
			for(Integer k : ootherIndices){
				if(matchedObjects.contains(k)){
					continue;
				}
				
				if(o.valueEquals(so.getObject(k))){
					foundMatch = true;
					matchedObjects.add(k);
					break;
				}
			}
			if(!foundMatch){
				return false;
			}
		}
		
		
		return true;
	}


	private void checkUnorderedObjects(State so,
			Collection<Integer> matchedObjects, List<Integer> unmatchedObjects,
			List<Integer> stillUnmatched) {
		for (Integer objectIndex : unmatchedObjects) {
			ObjectInstance o = this.getObject(objectIndex);
			Integer otherPosition = so.objectMap.get(o.getName());
			if (otherPosition != null) {
				ObjectInstance oo = so.getObject(otherPosition);
				if (o.valueEquals(oo)){
					matchedObjects.add(otherPosition);
					continue;
				} else {
					stillUnmatched.add(otherPosition);
				}
			}
		}
	}


	private void checkOrderedObjects(List<ObjectInstance> objects, List<ObjectInstance> otherObjects, int offset,
			Collection<Integer> matchedObjects, List<Integer> unmatchedObjects) {
		for (int i = 0, end = objects.size(); i < end; i++) {
			ObjectInstance o = objects.get(i);
			ObjectInstance oo = otherObjects.get(i);
			if (o.valueEquals(oo)) {
				matchedObjects.add(i + offset);
			} else {
				unmatchedObjects.add(i + offset);
			}
		}
	}
	
	/**
	 * Returns the number of observable and hidden object instances in this state.
	 * @return the number of observable and hidden object instances in this state.
	 */
	public int numTotalObjects(){
		return this.numHiddenObjects + this.numObservedObjects;
	}
	
	/**
	 * Returns the number of observable object instances in this state.
	 * @return the number of observable object instances in this state.
	 */
	public int numObservableObjects(){
		return objectInstances.size();
	}
	
	/**
	 * Returns the number of hidden object instances in this state.
	 * @return the number of hideen object instances in this state.
	 */
	public int numHiddenObjects(){
		return hiddenObjectInstances.size();
	}
	
	public ObjectInstance getObject(Integer i) {
		if (i == null || i < 0 ) {
			return null;
		}
		if (i < this.numObservedObjects) {
			return this.objectInstances.get(i);
		}
		i -= this.numObservedObjects;
		if (i < this.numHiddenObjects) {
			return this.hiddenObjectInstances.get(i);
		}
		return null;
	}
	
	/**
	 * Returns the object in this state with the name oname
	 * @param oname the name of the object instance to return
	 * @return the object instance with the name oname or null if there is no object in this state named oname
	 */
	public ObjectInstance getObject(String oname){
		return this.getObject(this.objectMap.get(oname));
	}
	
	/**
	 * Returns the observable object instance indexed at position i
	 * @param i the index of the observable object instance to return
	 * @return the observable object instance indexed at position i, or null if i > this.numObservableObjects()
	 */
	public ObjectInstance getObservableObjectAt(int i){
		if(i > objectInstances.size()){
			return null;
		}
		return objectInstances.get(i);
	}
	
	
	/**
	 * Returns the hidden object instance indexed at position i
	 * @param i the index of the hidden object instance to return
	 * @return the hidden object instance indexed at position i, or null if i > this.numHiddenObjects()
	 */
	public ObjectInstance getHiddenObjectAt(int i){
		if(i > hiddenObjectInstances.size()){
			return null;
		}
		return hiddenObjectInstances.get(i);
	}
	
	
	/**
	 * Returns the list of observable object instances in this state.
	 * @return the list of observable object instances in this state.
	 */
	public List <ObjectInstance> getObservableObjects(){
		return this.objectInstances;
	}
	
	
	/**
	 * Returns the list of hidden object instances in this state.
	 * @return the list of hidden object instances in this state.
	 */
	public List <ObjectInstance> getHiddenObjects(){
		return this.hiddenObjectInstances;
	}
	
	
	/**
	 * Returns the list of observable and hidden object instances in this state.
	 * @return the list of observable and hidden object instances in this state.
	 */
	public List <ObjectInstance> getAllObjects(){
		List <ObjectInstance> objects = new ArrayList <ObjectInstance>(objectInstances.size() + hiddenObjectInstances.size());
		objects.addAll(this.objectInstances);
		objects.addAll(this.hiddenObjectInstances);
		return objects;
	}
	
	/**
	 * Returns all objects that belong to the object class named oclass
	 * @param oclass the name of the object class for which objects should be returned
	 * @return all objects that belong to the object class named oclass
	 */
	public List <ObjectInstance> getObjectsOfTrueClass(String oclass){
		Integer position = this.objectClassMap.get(oclass);
		if(position == null){
			return new ArrayList <ObjectInstance>();
		}
		List <Integer> tmp = objectIndexByTrueClass.get(position);
		
		List<ObjectInstance> objects = new ArrayList<ObjectInstance>(tmp.size());
		for (Integer i : tmp) {
			objects.add(this.getObject(i));
		}
		return objects;
	}
	
	
	/**
	 * Returns the first indexed object of the object class named oclass
	 * @param oclass the name of the object class for which the first indexed object should be returned.
	 * @return the first indexed object of the object class named oclass
	 */
	public ObjectInstance getFirstObjectOfClass(String oclass){
		Integer position = this.objectClassMap.get(oclass);
		List <Integer> obs = this.objectIndexByTrueClass.get(position);
		if(obs != null && obs.size() > 0){
			return this.getObject(obs.get(0));
		}
		return null;
	}
	
	/**
	 * Returns a set of of the object class names for all object classes that have instantiated objects in this state.
	 * @return a set of of the object class names for all object classes that have instantiated objects in this state.
	 */
	public List<String> getObjectClassesPresent(){
		return new ArrayList<String>(this.objectClassMap.keySet());
	}
	
	
	/**
	 * Returns a list of list of object instances, grouped by object class
	 * @return a list of list of object instances, grouped by object class
	 */
	public List <List <ObjectInstance>> getAllObjectsByTrueClass(){
		List<List<ObjectInstance>> allObjects = new ArrayList<List<ObjectInstance>>(this.objectIndexByTrueClass.size());
		for (List<Integer> indices : this.objectIndexByTrueClass) {
			List<ObjectInstance> objects = new ArrayList<ObjectInstance>(indices.size());
			for (Integer i : indices) {
				objects.add(this.getObject(i));
			}
		}
		return allObjects;
	}
	
	
	public List <List <ObjectInstance>> getAllObjectsSortedByTrueClass(){
		
		int listSize =  this.objectIndexByTrueClass.size();
		List<List<ObjectInstance>> allObjects = new ArrayList<List<ObjectInstance>>(listSize);
		
		for (int i = 0; i < listSize; i++){
			List<Integer> indices = this.objectIndexByTrueClass.get(i);
			if (indices.isEmpty()) {
				continue;
			}
			
			List<ObjectInstance> objects = new ArrayList<ObjectInstance>(listSize);
			for (Integer j : indices) {
				objects.add(this.getObject(j));
			}
			allObjects.add(objects);
		}
		Collections.sort(allObjects, State.objectClassComparator);
		return allObjects;
	}
	
	public List<List<Integer>> getAllObjectIndicesByTrueClass() {
		return this.objectIndexByTrueClass;
	}
	
	public Map<String, Integer> getObjectClassMap() {
		return this.objectClassMap;
	}
	
	
	/**
	 * Returns a string representation of this state using only observable object instances.
	 * @return a string representation of this state using only observable object instances.
	 */
	public String getStateDescription(){
		
		StringBuilder builder = new StringBuilder(200);
		String desc = "";
		for(ObjectInstance o : objectInstances){
			builder = o.buildObjectDescription(builder).append("\n");
		}
		
		return builder.toString();
	}
	
	
	/**
	 * Returns a string representation of this state using observable and hidden object instances.
	 * @return a string representation of this state using observable and hidden object instances.
	 */
	public String getCompleteStateDescription(){
		
		StringBuilder builder = new StringBuilder(200);
		for(ObjectInstance o : objectInstances){
			builder = o.buildObjectDescription(builder).append("\n");
		}
		for(ObjectInstance o : hiddenObjectInstances){
			builder = o.buildObjectDescription(builder).append("\n");
		}
		
		
		return builder.toString();
		
	}
	
	@Override
	public String toString(){
		return this.getCompleteStateDescription();
	}
	
	
	
	/**
	 * Deprecated; use the {@link Action} class' {@link Action#getAllApplicableGroundedActions(State)} method instead.
	 * Returns all GroundedAction objects for the source action a in this state.
	 * @param a the action from which to generate GroundedAction objects.
	 * @return all GroundedAction objects for the source action a in this state.
	 */
	@Deprecated
	public List <GroundedAction> getAllGroundedActionsFor(Action a){
		
		List <GroundedAction> res = new ArrayList<GroundedAction>();
		
		if(a.getParameterClasses().length == 0){
			if(a.applicableInState(this, "")){
				res.add(new GroundedAction(a, new String[]{}));
			}
			return res; //no parameters so just the single ga without params
		}
		
		/*List <List <String>> bindings = this.getPossibleBindingsGivenParamOrderGroups(a.getParameterClasses(), a.getParameterOrderGroups());
		
		for(List <String> params : bindings){
			String [] aprams = params.toArray(new String[params.size()]);
			if(a.applicableInState(this, aprams)){
				GroundedAction gp = new GroundedAction(a, aprams);
				res.add(gp);
			}
		}*/
		
		return res;
	}
	
	
	/**
	 * Deprecated; use the {@link Action} class' {@link Action#getAllApplicableGroundedActionsFromActionList(List, State)} method instead.
	 * Returns a list of GroundedAction objects for all grounded actions that can be generated from the provided list of actions.
	 * @param actions the list of actions from which to generate GroudnedAction objects.
	 * @return a list of GroundedAction objects for all grounded actions that can be generated from the provided list of actions.
	 */
	@Deprecated
	public List <GroundedAction> getAllGroundedActionsFor(List <Action> actions){
		List <GroundedAction> res = new ArrayList<GroundedAction>(actions.size());
		for(Action a : actions){
			res.addAll(this.getAllGroundedActionsFor(a));
		}
		return res;
	}
	
	
	
	/**
	 * Deprecated; use the {@link PropositionalFunction} class' {@link PropositionalFunction#getAllGroundedPropsForState(State)} method instead.
	 * Returns all GroundedProp objects for the source propositional function pf in this state.
	 * @param pf the propositional function from which to generate GroundedProp objects.
	 * @return all GroundedProp objects for the source propositional function pf in this state.
	 */
	@Deprecated
	public List <GroundedProp> getAllGroundedPropsFor(PropositionalFunction pf){
		
		List <GroundedProp> res = new ArrayList<GroundedProp>();
		
		if(pf.getParameterClasses().length == 0){
			res.add(new GroundedProp(pf, new String[]{}));
			return res; //no parameters so just the single gp without params
		}
		
		/*List <List <String>> bindings = this.getPossibleBindingsGivenParamOrderGroups(pf.getParameterClasses(), pf.getParameterOrderGroups());
		
		for(List <String> params : bindings){
			String [] aprams = params.toArray(new String[params.size()]);
			GroundedProp gp = new GroundedProp(pf, aprams);
			res.add(gp);
		}
		*/
		return res;
	}
	
	
	/**
	 * Deprecated; use the {@link PropositionalFunction} class' {@link PropositionalFunction#somePFGroundingIsTrue(State)} method instead.
	 * Returns whether some GroundedProp of pf is true in this state
	 * @param pf the propositional function to check
	 * @return true if some GroundedProp of pf is true in this state; false otherwise
	 */
	@Deprecated
	public boolean somePFGroundingIsTrue(PropositionalFunction pf){
		List <GroundedProp> gps = this.getAllGroundedPropsFor(pf);
		for(GroundedProp gp : gps){
			if(gp.isTrue(this)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Given an array of parameter object classes and an array of their corresponding parameter order groups,
	 * returns all possible object instance bindings to the parameters, excluding bindings that are equivalent due
	 * to the parameter order grouping.
	 * @param paramClasses the name of object classes to which the bound object instances must belong
	 * @param paramOrderGroups the parameter order group names.
	 * @return A list of all possible object instance bindings for the parameters, were a binding is represented by a list of object instance names
	 */
	public List <List <String>> getPossibleBindingsGivenParamOrderGroups(String [] paramClasses, String [] paramOrderGroups){
		
		List <List <Integer>> currentBindingSets = new ArrayList <List<Integer>>();
		List <String> uniqueRenames = this.identifyUniqueClassesInParameters(paramOrderGroups);
		List <String> uniqueParamClasses = this.identifyUniqueClassesInParameters(paramClasses);
		
		List<Integer> currentObjects = new ArrayList<Integer>();
		int initialSize = 1;
		//first make sure we have objects for each class parameter; if not return empty list
		for(String oclass : uniqueParamClasses){
			int n = this.getNumOccurencesOfClassInParameters(oclass, paramClasses);
			
			Integer position = this.objectClassMap.get(oclass);
			if(position == null){
				return new ArrayList <List <String>>();
			}
			
			List <Integer> objectsOfClass = objectIndexByTrueClass.get(position);
			int numObjects = objectsOfClass.size();
			if(numObjects < n){
				return new ArrayList <List <String>>();
			}
			
			initialSize *= numObjects;
			currentObjects.addAll(objectsOfClass);
		}
		
		initialSize = Math.min(1000, initialSize);
		List <List <Integer>> resIndices = new ArrayList <List<Integer>>(initialSize);
		this.getPossibleRenameBindingsHelper(resIndices, currentBindingSets, 0, currentObjects, uniqueRenames, paramClasses, paramOrderGroups);
		return this.getBindingsFromIndices(resIndices);
	}
	
	private List <List <String>> getBindingsFromIndices(List<List<Integer>> allIndices) {
		List<List<String>> res = new ArrayList<List<String>>(allIndices.size());
		for (List<Integer> indices : allIndices) {
			List<String> objects = new ArrayList<String>(indices.size());
			for (Integer i : indices) {
				objects.add(this.getObject(i).getName());
			}
			res.add(objects);
		}
		return res;
	}
	
	
	
	private void getPossibleRenameBindingsHelper(List <List <Integer>> res, List <List <Integer>> currentBindingSets, int bindIndex,
			List <Integer> remainingObjects, List <String> uniqueOrderGroups, String [] paramClasses, String [] paramOrderGroups){
		
		if(bindIndex == uniqueOrderGroups.size()){
			//base case, put it all together and add it to the result
			res.add(this.getBindingFromCombinationSet(currentBindingSets, uniqueOrderGroups, paramOrderGroups));
			return ;
		}
		
		//otherwise we're in the recursive case
		
		String r = uniqueOrderGroups.get(bindIndex);
		String c = this.parameterClassAssociatedWithOrderGroup(r, paramOrderGroups, paramClasses);
		List <Integer> cands = this.objectsMatchingClass(remainingObjects, c);
		int k = this.numOccurencesOfOrderGroup(r, paramOrderGroups);
		
		int n = cands.size();
		int [] comb = this.initialComb(k, n);
		List<Integer> combList = this.getObjectsFromComb(cands, comb);
		this.addBindingCombination(res, currentBindingSets, bindIndex,
				remainingObjects, uniqueOrderGroups, paramClasses,
				paramOrderGroups, combList);
		
		
		while(nextComb(comb, k, n) == 1){
			combList = this.getObjectsFromComb(cands, comb);
			this.addBindingCombination(res, currentBindingSets, bindIndex,
					remainingObjects, uniqueOrderGroups, paramClasses,
					paramOrderGroups, combList);
		}
	}


	private void addBindingCombination(List<List<Integer>> res,
			List<List<Integer>> currentBindingSets, int bindIndex,
			List<Integer> remainingObjects, List<String> uniqueOrderGroups,
			String[] paramClasses, String[] paramOrderGroups, List<Integer> cb) {
		
		List <List<Integer>> nextBinding = new ArrayList<List<Integer>>(currentBindingSets);
		nextBinding.add(cb);
		List <Integer> nextObsReamining = this.objectListDifference(remainingObjects, cb);
		
		//recursive step
		this.getPossibleRenameBindingsHelper(res, nextBinding, bindIndex+1, nextObsReamining, uniqueOrderGroups, paramClasses, paramOrderGroups);
	}
	
	
	private List <Integer> objectListDifference(List <Integer> objects, List <Integer> toRemove){
		
		List <Integer> remaining = new ArrayList<Integer>(objects);
		remaining.removeAll(toRemove);
		return remaining;
	}
	
	private int getNumOccurencesOfClassInParameters(String className, String [] paramClasses){
		int num = 0;
		for(int i = 0; i < paramClasses.length; i++){
			if(paramClasses[i].equals(className)){
				num++;
			}
		}
		return num;
	}
	
	private List <String> identifyUniqueClassesInParameters(String [] paramClasses){
		List <String> unique = new ArrayList <String>(paramClasses.length);
		for(int i = 0; i < paramClasses.length; i++){
			if(!unique.contains(paramClasses[i])){
				unique.add(paramClasses[i]);
			}
		}
		return unique;
	}
	
	
	
	private int numOccurencesOfOrderGroup(String rename, String [] orderGroups){
		int num = 0;
		for(int i = 0; i < orderGroups.length; i++){
			if(orderGroups[i].equals(rename)){
				num++;
			}
		}
		
		return num;
		
	}
	
	private String parameterClassAssociatedWithOrderGroup(String orderGroup, String [] orderGroups, String [] paramClasses){
		for(int i = 0; i < orderGroups.length; i++){
			if(orderGroups[i].equals(orderGroup)){
				return paramClasses[i];
			}
		}
		return "";
	}
	
	
	private List <Integer> objectsMatchingClass(Collection <Integer> sourceObs, String cname){
		List <Integer> res = new ArrayList<Integer>(sourceObs.size());
		
		for(Integer i : sourceObs){
			ObjectInstance o = this.getObject(i);
			if(o.getTrueClassName().equals(cname)){
				res.add(i);
			}
			
		}
		
		return res;
	}
	
	
	
	/**
	 * for a specific parameter order group, return a possible binding
	 * @param comboSets is a list of the bindings for each order group. For instance, if the order groups for each parameter were P, Q, P, Q, R; then there would be three lists
	 * @param orderGroupAssociatedWithSet which order group each list of bindings in comboSets is for
	 * @param orderGroups the parameter order groups for each parameter
	 * @return a binding as a list of object instance names
	 */
	private List <Integer> getBindingFromCombinationSet(List <List <Integer>> comboSets, List <String> orderGroupAssociatedWithSet, String [] orderGroups){
		
		List <Integer> res = new ArrayList <Integer>(orderGroups.length);
		//add the necessary space first
		
		for(int i = 0; i < orderGroups.length; i++){
			res.add(-1);
		}
		
		//apply the parameter bindings for each rename combination
		for(int i = 0; i < comboSets.size(); i++){
			List <Integer> renameCombo = comboSets.get(i);
			String r = orderGroupAssociatedWithSet.get(i);
			
			//find the parameter indices that match this rename and set a binding accordingly
			int ind = 0;
			for(int j = 0; j < orderGroups.length; j++){
				if(orderGroups[j].equals(r)){
					res.set(j, renameCombo.get(ind));
					ind++;
				}
			}
		}
		
		return res;
	}
	
	
	private List <List <Integer>> getAllCombinationsOfObjects(List <Integer> objects, int k){
		
		List <List<Integer>> allCombs = new ArrayList <List<Integer>>();
		
		int n = objects.size();
		int [] comb = this.initialComb(k, n);
		List<Integer> initialComb = this.getObjectsFromComb(objects, comb);
		allCombs.add(initialComb);
		while(nextComb(comb, k, n) == 1){
			allCombs.add(this.getObjectsFromComb(objects, comb));
		}
		
		return allCombs;
		
	}
	
	private List<Integer> getObjectsFromComb(List<Integer> allObjects, int[] comb) {
		List<Integer> objects = new ArrayList<Integer>(comb.length);
		for (int i : comb){ 
			objects.add(allObjects.get(i));
		}
		return objects;
	}
	
	
	@Deprecated
	private List <Integer> getListOfBindingsFromCombination(List <Integer> objects, int [] comb){
		List <Integer> res = new ArrayList <Integer>(comb.length);
		for(int i = 0; i < comb.length; i++){
			//res.add(objects.get(comb[i]).getName());
		}
		return res;
	}
	
	
	private int [] initialComb(int k, int n){
		int [] res = new int[k];
		for(int i = 0; i < k; i++){
			res[i] = i;
		}
		
		return res;
	}
	
	
	/**
	 * Iterates through combinations. 
	 * Modified code from: http://compprog.wordpress.com/tag/generating-combinations/
	 * @param comb the last combination of elements selected
	 * @param k number of elements in any combination (n choose k)
	 * @param n number of possible elements (n choose k)
	 * @return 0 when there are no more combinations; 1 when a new combination is generated
	 */
	private int nextComb(int [] comb, int k, int n){
		
		int i = k-1;
		comb[i]++;
		
		while(i > 0 && comb[i] >= n-k+1+i){
			i--;
			comb[i]++;
		}
		
		if(comb[0] > n-k){
			return 0;
		}
		
		/* comb now looks like (..., x, n, n, n, ..., n).
		Turn it into (..., x, x + 1, x + 2, ...) */
		for(i = i+1; i < k; i++){
			comb[i] = comb[i-1] + 1;
		}
		
		return 1;
	}
	
	
	
	
	
	
}
