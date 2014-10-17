package burlap.behavior.affordances;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D.PolicyGlyphRenderStyle;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.planning.QComputablePlanner;
import burlap.behavior.singleagent.planning.ValueFunctionPlanner;
import burlap.behavior.singleagent.planning.commonpolicies.AffordanceGreedyQPolicy;
import burlap.behavior.singleagent.planning.commonpolicies.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.rtdp.AffordanceRTDP;
import burlap.behavior.singleagent.planning.stochastic.rtdp.RTDP;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.logicalexpressions.PFAtom;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.common.SinglePFTF;
import burlap.oomdp.singleagent.common.UniformCostRF;

public class AffordancesExample {

	// Create collections for action lists
	private ArrayList<AbstractGroundedAction> northActions = new ArrayList<AbstractGroundedAction>();;
	private ArrayList<AbstractGroundedAction> southActions = new ArrayList<AbstractGroundedAction>();;
	private ArrayList<AbstractGroundedAction> eastActions = new ArrayList<AbstractGroundedAction>();;
	private ArrayList<AbstractGroundedAction> westActions = new ArrayList<AbstractGroundedAction>();;
	
	// Pointers to action objects in the domain
	private AbstractGroundedAction northAction;
	private AbstractGroundedAction southAction;
	private AbstractGroundedAction eastAction;
	private AbstractGroundedAction westAction;
	
	// PFAtoms for used in affordance instances
	private PFAtom northPFAtom;
	private PFAtom southPFAtom;
	private PFAtom eastPFAtom;
	private PFAtom westPFAtom;
	private PFAtom goalPFAtom;
	
	private AffordancesController affController;
	
	/**
	 * Creates pointers to the AbstractGroundedAction instances that are associated with the domain and groups them
	 * according to the desired set of affordances for this example
	 * @param domain
	 * @param gwdg
	 * @param initialState
	 * @return
	 */
	public void setupActions(Domain domain, GridWorldDomain gwdg, State initialState){
		// ---- GET ACTIONS ----

		// NORTH
		Action northAct = domain.getAction(GridWorldDomain.ACTIONNORTH);
		String[] northFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(northAct.getParameterClasses());
		this.northAction = new GroundedAction(northAct, northFreeParams);
		
		// SOUTH
		Action southAct = domain.getAction(GridWorldDomain.ACTIONSOUTH);
		String[] southFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(southAct.getParameterClasses());
		this.southAction = new GroundedAction(southAct, southFreeParams);
		
		// EAST
		Action eastAct = domain.getAction(GridWorldDomain.ACTIONEAST);
		String[] eastFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(eastAct.getParameterClasses());
		this.eastAction = new GroundedAction(eastAct, eastFreeParams);

		// WEST
		Action westAct = domain.getAction(GridWorldDomain.ACTIONWEST);
		String[] westFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(westAct.getParameterClasses());
		this.westAction = new GroundedAction(westAct, westFreeParams);
		
		// ---- ADD ACTIONS ----
		
		// NORTH
		this.northActions = new ArrayList<AbstractGroundedAction>();
		this.northActions.add(this.northAction);
		this.northActions.add(this.southAction);
		this.northActions.add(this.eastAction);
		this.northActions.add(this.westAction);
		
		// SOUTH
		this.southActions = (ArrayList<AbstractGroundedAction>) northActions.clone();
		
		// EAST
		this.eastActions = (ArrayList<AbstractGroundedAction>) northActions.clone();
		
		// WEST
		this.westActions = (ArrayList<AbstractGroundedAction>) northActions.clone();
	}
	
	/**
	 * Create instances of PFAtoms with free variables for use in affordances 
	 * @param domain
	 * @param gwdg
	 * @param initialState
	 */
	public void setupPFAtoms(Domain domain, GridWorldDomain gwdg, State initialState) {
		// NORTH
		PropositionalFunction northProp = domain.getPropFunction(GridWorldDomain.PFEMPTYNORTH);
		String[] northFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(northProp.getParameterClasses());
		GroundedProp northGroundedProp = new GroundedProp(northProp, northFreeParams);
		this.northPFAtom = new PFAtom(northGroundedProp);
		
		// SOUTH
		PropositionalFunction southProp = domain.getPropFunction(GridWorldDomain.PFEMPTYSOUTH);
		String[] southFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(southProp.getParameterClasses());
		GroundedProp southGroundedProp = new GroundedProp(southProp, southFreeParams);
		this.southPFAtom = new PFAtom(southGroundedProp);
		
		// EAST
		PropositionalFunction eastProp = domain.getPropFunction(GridWorldDomain.PFEMPTYEAST);
		String[] eastFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(eastProp.getParameterClasses());
		GroundedProp eastGroundedProp = new GroundedProp(eastProp, eastFreeParams);
		this.eastPFAtom = new PFAtom(eastGroundedProp);
		
		// WEST
		PropositionalFunction westProp = domain.getPropFunction(GridWorldDomain.PFEMPTYWEST);
		String[] westFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(westProp.getParameterClasses());
		GroundedProp westGroundedProp = new GroundedProp(westProp, westFreeParams);
		this.westPFAtom = new PFAtom(westGroundedProp);
		
		// GOAL
		PropositionalFunction goalProp = domain.getPropFunction(GridWorldDomain.PFATLOCATION);
		String[] goalFreeParams = AffordanceDelegate.makeFreeVarListFromObjectClasses(goalProp.getParameterClasses());
		GroundedProp goalGroundedProp = new GroundedProp(goalProp, goalFreeParams);
		this.goalPFAtom = new PFAtom(goalGroundedProp);
	}
	
	/**
	 * Create instances of PFAtoms with bound variables for use in the affordances 
	 * @param domain
	 * @param gwdg
	 * @param initialState
	 */
	public void setupPFAtomsGrounded(Domain domain, GridWorldDomain gwdg, State initialState) {
		// NORTH
		PropositionalFunction northProp = domain.getPropFunction(GridWorldDomain.PFEMPTYNORTH);
		List<GroundedProp> northGroundedProps = northProp.getAllGroundedPropsForState(initialState);
		GroundedProp northGroundedProp = northGroundedProps.get(0);
		this.northPFAtom = new PFAtom(northGroundedProp);
		
		// SOUTH
		PropositionalFunction southProp = domain.getPropFunction(GridWorldDomain.PFEMPTYSOUTH);
		List<GroundedProp> southGroundedProps = southProp.getAllGroundedPropsForState(initialState);
		GroundedProp southGroundedProp = southGroundedProps.get(0);
		this.southPFAtom = new PFAtom(southGroundedProp);
		
		// EAST
		PropositionalFunction eastProp = domain.getPropFunction(GridWorldDomain.PFEMPTYEAST);
		List<GroundedProp> eastGroundedProps = eastProp.getAllGroundedPropsForState(initialState);
		GroundedProp eastGroundedProp = eastGroundedProps.get(0);
		this.eastPFAtom = new PFAtom(eastGroundedProp);
		
		// WEST
		PropositionalFunction westProp = domain.getPropFunction(GridWorldDomain.PFEMPTYWEST);
		List<GroundedProp> westGroundedProps = westProp.getAllGroundedPropsForState(initialState);
		GroundedProp westGroundedProp = westGroundedProps.get(0);
		this.westPFAtom = new PFAtom(westGroundedProp);
		
		// GOAL
		PropositionalFunction goalProp = domain.getPropFunction(GridWorldDomain.PFATLOCATION);
		List<GroundedProp> goalGroundedProps = goalProp.getAllGroundedPropsForState(initialState);
		GroundedProp goalGroundedProp = goalGroundedProps.get(0);
		this.goalPFAtom = new PFAtom(goalGroundedProp);
	}
	
	/**
	 * Creates instances of affordances based on the PFAtoms and Actions created previously
	 * @param hardAffordanceFlag
	 */
	public void setupAffordances(boolean hardAffordanceFlag) {

		Affordance affNorth;
		Affordance affSouth;
		Affordance affEast;
		Affordance affWest;
		
		// --> Hard
		if (hardAffordanceFlag) {
			affNorth = new HardAffordance(this.northPFAtom, this.goalPFAtom, northActions);
			affSouth = new HardAffordance(this.southPFAtom, this.goalPFAtom, southActions);
			affEast = new HardAffordance(this.eastPFAtom, this.goalPFAtom, eastActions);
			affWest = new HardAffordance(this.westPFAtom, this.goalPFAtom, westActions);
		} 
		// --> Soft
		else {
			
			// --- NORTH ---
			affNorth = new SoftAffordance(this.northPFAtom, this.goalPFAtom, this.northActions);
			
			HashMap<AbstractGroundedAction,Integer> northAlpha = new HashMap<AbstractGroundedAction,Integer>(); 
			
			northAlpha.put(this.northAction, 5000);
			northAlpha.put(this.southAction, 0);
			northAlpha.put(this.eastAction, 0);
			northAlpha.put(this.westAction, 0);
			
			int[] northBeta = new int[]{0,2,6,4,1};
			
			((SoftAffordance)affNorth).setActionCounts(northAlpha);
			((SoftAffordance)affNorth).setActionNumCounts(northBeta);
			
			// == SOUTH ==
			
			affSouth = new SoftAffordance(this.southPFAtom, this.goalPFAtom, this.southActions);
			
			HashMap<AbstractGroundedAction,Integer> southAlpha = new HashMap<AbstractGroundedAction,Integer>(); 
			
			southAlpha.put(this.northAction, 0);
			southAlpha.put(this.southAction, 5000);
			southAlpha.put(this.eastAction, 0);
			southAlpha.put(this.westAction, 0);
	
			int[] southBeta = new int[]{0,3,4,2,1};
	
			((SoftAffordance)affSouth).setActionCounts(southAlpha);
			((SoftAffordance)affSouth).setActionNumCounts(southBeta);
			
			// == EAST ==
			
			affEast = new SoftAffordance(this.eastPFAtom, this.goalPFAtom, this.eastActions);
			
			HashMap<AbstractGroundedAction,Integer> eastAlpha = new HashMap<AbstractGroundedAction,Integer>(); 
			
			eastAlpha.put(this.northAction, 0);
			eastAlpha.put(this.southAction, 0);
			eastAlpha.put(this.eastAction, 5000);
			eastAlpha.put(this.westAction, 0);
	
			int[] eastBeta = new int[]{0,2,3,2,1};
	
			((SoftAffordance)affEast).setActionCounts(eastAlpha);
			((SoftAffordance)affEast).setActionNumCounts(eastBeta);
			
			// == WEST ==
			
			affWest = new SoftAffordance(this.westPFAtom, this.goalPFAtom, this.westActions);
			
			HashMap<AbstractGroundedAction,Integer> westAlpha = new HashMap<AbstractGroundedAction,Integer>(); 
	
			westAlpha.put(this.northAction, 0);
			westAlpha.put(this.southAction, 0);
			westAlpha.put(this.eastAction, 0);
			westAlpha.put(this.westAction, 5000);
	
			int[] westBeta = new int[]{0,2,3,4,1};
	
			((SoftAffordance)affWest).setActionCounts(westAlpha);
			((SoftAffordance)affWest).setActionNumCounts(westBeta);
			
			((SoftAffordance)affNorth).postProcess();
			((SoftAffordance)affSouth).postProcess();
			((SoftAffordance)affEast).postProcess();
			((SoftAffordance)affWest).postProcess();
		}
		
		// Now affordance instances are made, put them in delegates and into the AffordancesController
		AffordanceDelegate affDelegateNorth = new AffordanceDelegate(affNorth);
		AffordanceDelegate affDelegateSouth = new AffordanceDelegate(affSouth);
		AffordanceDelegate affDelegateEast = new AffordanceDelegate(affEast);
		AffordanceDelegate affDelegateWest = new AffordanceDelegate(affWest);

		List<AffordanceDelegate> affDelegates = new ArrayList<AffordanceDelegate>();
		affDelegates.add(affDelegateNorth);
		affDelegates.add(affDelegateSouth);
		affDelegates.add(affDelegateEast);
		affDelegates.add(affDelegateWest);
		
		this.affController = new AffordancesController(affDelegates);
	}
	
	public static void main(String[] args) {
		// ---- SETUP DOMAIN ----
		GridWorldDomain gwdg = new GridWorldDomain(11, 11);
		gwdg.setMapToFourRooms(); 
		Domain domain = gwdg.generateDomain();

		// Define the task
		RewardFunction rf = new UniformCostRF(); 
		TerminalFunction tf = new SinglePFTF(domain.getPropFunction(GridWorldDomain.PFATLOCATION)); 

		// Set up the initial state of the task
		State initialState = GridWorldDomain.getOneAgentOneLocationState(domain);
		GridWorldDomain.setAgent(initialState, 0, 0);
		GridWorldDomain.setLocation(initialState, 0, 10, 10);
		
		DiscreteStateHashFactory hashingFactory = new DiscreteStateHashFactory();

		AffordancesExample gridWorldExample = new AffordancesExample();
		
		// Setup Actions
		gridWorldExample.setupActions(domain, gwdg, initialState);
		
		// ---- SETUP PROP FUNCS ----
		gridWorldExample.setupPFAtomsGrounded(domain, gwdg, initialState);
		
		// ---- AFFORDANCES ----
		boolean hardAffordanceFlag = false;
		gridWorldExample.setupAffordances(hardAffordanceFlag);
		
		// ---- SETUP PLANNER ----
		
		// Params for Planners
		int numRollouts = 1000; // RTDP
		int maxDepth = 30; // RTDP
		double vInit = 0;
		double minDelta = 0.01;
		double gamma = 0.99;
		
		boolean affordanceMode = true;
		ValueFunctionPlanner planner;
		Policy p;
		if(affordanceMode) {
			planner = new AffordanceRTDP(domain, rf, tf, gamma, hashingFactory, vInit, numRollouts, minDelta, maxDepth, gridWorldExample.affController);
			planner.planFromState(initialState);
			
			// Create a Q-greedy policy from the planner
			p = new AffordanceGreedyQPolicy(gridWorldExample.affController, (QComputablePlanner)planner);
		} else {
			planner = new RTDP(domain, rf, tf, gamma, hashingFactory, vInit, numRollouts, minDelta, maxDepth);
			planner.planFromState(initialState);
			
			// Create a Q-greedy policy from the planner
			p = new GreedyQPolicy((QComputablePlanner)planner);
		}
		valueFunctionVisualize(planner, p, initialState, domain, hashingFactory);
		
		// Print out the planning results
		EpisodeAnalysis ea = p.evaluateBehavior(initialState, rf, tf,100);
		System.out.println(ea.getActionSequenceString());
		
	}
	
	
	/**
	 * Visualizer for GridDomain
	 * @param planner
	 * @param p
	 * @param initialState
	 * @param domain
	 * @param hashingFactory
	 */
	public static void valueFunctionVisualize(QComputablePlanner planner, Policy p, State initialState, Domain domain, DiscreteStateHashFactory hashingFactory){
		List <State> allStates = StateReachability.getReachableStates(initialState, 
			(SADomain)domain, hashingFactory);
		LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
		rb.addNextLandMark(0., Color.RED);
		rb.addNextLandMark(1., Color.BLUE);
		
		StateValuePainter2D svp = new StateValuePainter2D(rb);
		svp.setXYAttByObjectClass(GridWorldDomain.CLASSAGENT, GridWorldDomain.ATTX, 
			GridWorldDomain.CLASSAGENT, GridWorldDomain.ATTY);
		
		PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
		spp.setXYAttByObjectClass(GridWorldDomain.CLASSAGENT, GridWorldDomain.ATTX, 
			GridWorldDomain.CLASSAGENT, GridWorldDomain.ATTY);
		spp.setActionNameGlyphPainter(GridWorldDomain.ACTIONNORTH, new ArrowActionGlyph(0));
		spp.setActionNameGlyphPainter(GridWorldDomain.ACTIONSOUTH, new ArrowActionGlyph(1));
		spp.setActionNameGlyphPainter(GridWorldDomain.ACTIONEAST, new ArrowActionGlyph(2));
		spp.setActionNameGlyphPainter(GridWorldDomain.ACTIONWEST, new ArrowActionGlyph(3));
		spp.setRenderStyle(PolicyGlyphRenderStyle.DISTSCALED);
		
		ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(allStates, svp, planner);
		gui.setSpp(spp);
		gui.setPolicy(p);
		gui.setBgColor(Color.GRAY);
		gui.initGUI();
}
	
	
}
