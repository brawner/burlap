/**
 * 
 */
package burlap.behavior.affordances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.logicalexpressions.LogicalExpression;
import cc.mallet.types.Dirichlet;

/**
 * @author dabel
 *
 */
public class SoftAffordance extends Affordance {

	private List<AbstractGroundedAction> allActions;
	private Collection<AbstractGroundedAction> prunedActions;
	private HashMap<AbstractGroundedAction, Integer> actionCounts;
	private int[] actionNumCounts;
	private Dirichlet actionDistr;
	private Dirichlet actionNumDistr;
	private double dirichletHyperParam = 0.01;

	
	/**
	 * Constructor SoftAffordances. Maps a <Predicate,GoalDescription> pair to a subset of the action space
	 * @param preCond: the precondition for the affordance
	 * @param goalDescr: the goal description for the affordance
	 * @param actions: the list of all actions used for the affordance
	 */
	public SoftAffordance(LogicalExpression preCond, LogicalExpression goalDescr, List<AbstractGroundedAction> actions) {
			this.preCondition = preCond;
			this.goalDescription = goalDescr;
			this.allActions = actions;
			
			initCounts();
			// I don't think we can postProcess and sample right away since the action counts haven't been set yet.
//			postProcess();
//			this.sampleNewLiftedActionSet();
	}
	
	/**
	 * Resamples and returns an action set
	 */
	@Override
	public Collection<AbstractGroundedAction> sampleNewLiftedActionSet() {
		
		int[] sizes = this.actionNumDistr.drawObservation(1);
		int n = -1;

		// Loop over sizes until we find the one that was sampled
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] > 0) {
				n = i + 1; // i + 1 since 0:(k-1) set sizes indicates sets of size 1:k
				break;
			}
		}

		// Sample 'n' actions from the dirichletMultinomial
		List<AbstractGroundedAction> selectedActions = new ArrayList<AbstractGroundedAction>();
		int[] actIndices = this.actionDistr.drawObservation(n);
		
		for(int i = 0; i < actIndices.length; i++) {
			if(i > 0) {
				selectedActions.add(this.allActions.get(i));
			}
		}

		this.prunedActions = selectedActions;
		return selectedActions;
	}
		
	/**
	 * Initiliazes counts for the dirichlet multinomial and dirichlet process.
	 */
	private void initCounts() {
		this.actionCounts = new HashMap<AbstractGroundedAction,Integer>();
		for (AbstractGroundedAction a: this.allActions) {
			this.actionCounts.put(a, 0);
		}
		
		this.actionNumCounts = new int[this.actionCounts.size() + 1];
		for (int i = 0; i <= this.actionCounts.size(); i++) {
			this.actionNumCounts[i] = 0;
		}
		
	}

	
	/**
	 * Sets dirichlet distributions with the parameters collected during learning
	 */
	public void postProcess() {
		// Cast action counts from Collection<Integer> counts to double[] for use in Dirichlet
		double[] alpha = dirichletHyper(this.actionCounts.size(), dirichletHyperParam);
		List<Integer> actCounts = new ArrayList<Integer>(this.actionCounts.values());
		for (int i = 0; i < alpha.length; i++) {
			alpha[i] += (double)(actCounts.get(i));
		}

		this.actionDistr = new Dirichlet(alpha);
		
		double[] beta = dirichletHyper(this.actionCounts.size(), dirichletHyperParam);
		for (int i = 0; i < alpha.length; i++) {
			beta[i] += (double)this.actionNumCounts[i];
		}
		
		this.actionNumDistr = new Dirichlet(beta);
	}
	
	private double[] dirichletHyper(int n, double d) {
		double[] hyper = new double[n];
		for (int i = 0; i < hyper.length; i++) {
			hyper[i] = d;
		}
		return hyper;
	}
	
	// --- Accessors ---

	public LogicalExpression getPreCondition() {
		return this.preCondition;
	}
	
	public LogicalExpression getGoalDescription() {
		return this.goalDescription;
	}
	
	public List<AbstractGroundedAction> getActions() {
		return this.allActions;
	}
	
	public HashMap<AbstractGroundedAction, Integer> getActionCounts() {
		return actionCounts;
	}
	
	public int[] getActionSetSizeCounts() {
		return actionNumCounts;
	}
	
	// --- Mutators ---
	
	public void setActionCounts(HashMap<AbstractGroundedAction, Integer> actionCounts) {
		this.actionCounts = actionCounts;
	}

	public void setActionNumCounts(int[] actionNumCounts) {		
		this.actionNumCounts = actionNumCounts;
	}
	
	public void updateActionCount(AbstractGroundedAction a) {
		Integer count = this.actionCounts.get(a);
		this.actionCounts.put(a, count + 1);
	}
	
	public void updateActionSetSizeCount(int size) {
		this.actionNumCounts[size]++;
	}
	
	/**
	 * Prints the action counts for debugging purposes
	 */
	public void printCounts() {
		System.out.println("Affordance pred: " + this.preCondition.toString());
		for (AbstractGroundedAction a: this.allActions) {
			System.out.println(a.toString() + ": " + this.actionCounts.get(a));
		}
		for (int i = 0; i < this.actionCounts.size(); i++) {
			System.out.println(i + ": " + this.actionNumCounts[i]);
		}
	}
	
	public String toString() {
		return this.preCondition.toString() + "," + this.goalDescription.toString();
	}
	
	public String toFile() {
		String out = "";

		// Header information (what the affordance's PF and LGD are)
		out += this.preCondition.toString() + "," + this.goalDescription.toString() + "\n";
				
		// Add action counts
		for (AbstractGroundedAction a: this.allActions) {
			out += a.actionName() + "," + this.actionCounts.get(a) + "\n";
		}
		out += "---\n";

		// Add set size counts
		for (int i = 0; i < this.actionCounts.size(); i++) {
			out += i + "," + this.actionNumCounts[i] + "\n";
		}
		out += "===\n";
		return out;
	}
	
	
}