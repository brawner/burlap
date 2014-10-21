/**
 * 
 */
package burlap.behavior.affordances;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.State;
import burlap.oomdp.logicalexpressions.LogicalExpression;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

/**
 * @author dabel
 *
 */
public class Affordance {

	private Map<AbstractGroundedAction, Integer> 	actionOptimalAffActiveCounts;
	private Map<AbstractGroundedAction,Integer>		totalActionOptimalCounts;
	private Action predicateAction;
	public LogicalExpression preCondition;
	public LogicalExpression goalDescription;
	
	/**
	 * Constructor Affordances. Maps a <Predicate,GoalDescription> pair to a subset of the action space
	 * @param preCond: the precondition for the affordance
	 * @param goalDescr: the goal description for the affordance
	 * @param actions: the list of all actions used for the affordance
	 */
	public Affordance(LogicalExpression preCond, LogicalExpression goalDescr, List<AbstractGroundedAction> actions) {
			this.preCondition = preCond;
			this.goalDescription = goalDescr;
			
			initCounts(actions);
	}
	
	public Affordance(LogicalExpression preCond, LogicalExpression goalDescr, Action action) {
		this.preCondition = preCond;
		this.goalDescription = goalDescr;
		this.predicateAction = action;
		
	}
		
	/**
	 * Initiliazes counts for the dirichlet multinomial and dirichlet process.
	 */
	private void initCounts(List<AbstractGroundedAction> fullActionSet) {
		this.actionOptimalAffActiveCounts = new HashMap<AbstractGroundedAction,Integer>();
		this.totalActionOptimalCounts = new HashMap<AbstractGroundedAction,Integer>();
		for (AbstractGroundedAction a: fullActionSet) {
			this.actionOptimalAffActiveCounts.put(a, 0);
			this.totalActionOptimalCounts.put(a, 0);
		}	
	}

	/**
	 * Computes the maximum likelihood estimate for the probability that this action is relevant
	 * @param action
	 * @return
	 */
	public double probActionIsRelevant(AbstractGroundedAction action) {
		Integer optimalActAffActive = this.actionOptimalAffActiveCounts.get(action);
		Integer totalActOptimal = this.totalActionOptimalCounts.get(action);
		if (optimalActAffActive == null || totalActOptimal == null) {
			return 0.0;
		}
		
		return (totalActOptimal == 0) ? 0.0 : (double)optimalActAffActive / totalActOptimal;
	}
	
	// --- Accessors ---
	
	public Map<AbstractGroundedAction, Integer> getActionOptimalAffActiveCounts() {
		return actionOptimalAffActiveCounts;
	}
	
	public Map<AbstractGroundedAction, Integer> getTotalActionOptimalCounts() {
		return totalActionOptimalCounts;
	}
	
	public List<GroundedAction> getActionsForState(State state) {
		return this.predicateAction.getAllApplicableGroundedActions(state);
	}
	
	public Action getPredicateAction() {
		return this.predicateAction;
	}
	
	public Set<AbstractGroundedAction> getActions() {
		return this.actionOptimalAffActiveCounts.keySet();
	}

	// --- Mutators ---
	
	public void setOptimalActionAffActiveCountMap(Map<AbstractGroundedAction, Integer> actionCounts) {
		this.actionOptimalAffActiveCounts = actionCounts;
	}
	
	public void setTotalOptimalActionCountMap(Map<AbstractGroundedAction,Integer> totalActionCounts) {
		this.totalActionOptimalCounts = totalActionCounts;
	}

	public void incrementActionOptimalAffActive(AbstractGroundedAction a) {
		Integer count = this.actionOptimalAffActiveCounts.get(a);
		this.actionOptimalAffActiveCounts.put(a, count + 1);
	}
	
	public void incrementTotalActionOptimal(AbstractGroundedAction a) {
		Integer count = this.totalActionOptimalCounts.get(a);
		this.totalActionOptimalCounts.put(a, count + 1);
	}
	
	public String toString() {
		return this.preCondition.toString() + "," + this.goalDescription.toString();
	}	
	
}
