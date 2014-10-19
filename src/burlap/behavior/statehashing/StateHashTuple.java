package burlap.behavior.statehashing;


import burlap.oomdp.core.State;


/**
 * This class provides a hash value for {@link burlap.oomdp.core.State} objects. This is useful for tabular
 * planning and learning algorithms that make use of hash-backed sets or maps for fast retrieval.
 * In general, hash codes should only be computed once, and only once the hashCode method is called.
 * If something about the StateHashTuple changes, then needsToRecomputeHashCode boolean flag should be
 * set to true so that the next time the hashCode is called it is recomputed. Likewise, once the {@link #computeHashCode()}
 * method has been called, it should set the needToRecomputeHashCode flag to false.
 * <p/>
 * By default, equality checks use the standard {@link burlap.oomdp.core.State} object equality check. If you need
 * to handle this specially, (such as providing state abstraction), then the equals method should be overridden.
 * @author James MacGlashan
 *
 */
public abstract class StateHashTuple {

	private final State							s;
	private final int 							hashCode;
	
	/**
	 * Initializes the StateHashTuple with the given {@link burlap.oomdp.core.State} object.
	 * @param s the state object this object will wrap
	 */
	public StateHashTuple(State s){
		this.s = s.copy();
		this.hashCode = this.computeHashCode();
		//needToRecomputeHashCode = true;
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.hashCode());
	}
	
	/**
	 * This method computes the hashCode for this object and saves it to the <code>hashCode</code> field belonging to the abstract class.
	 */
	public abstract int computeHashCode();
	
	
	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(!(other instanceof StateHashTuple)){
			return false;
		}
		StateHashTuple o = (StateHashTuple)other;
		return getState().equals(o.getState());
		
	}
	
	@Override
	public final int hashCode(){
		return hashCode;
	}
	
	public final State getState() {
		return s.copy();
	}
	
}
