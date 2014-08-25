package burlap.oomdp.logicalexpressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import burlap.oomdp.core.State;

public class Conjunction extends LogicalExpression {
	
	public Conjunction(LogicalExpression...expressions){
		for(LogicalExpression exp : expressions){
			this.childExpressions.add(exp);
			exp.setParentExpression(this);
		}
		
	}
	
	public Conjunction(List <LogicalExpression> expressions){
		for(LogicalExpression exp : expressions){
			this.childExpressions.add(exp);
			exp.setParentExpression(this);
		}
		
	}
	
	@Override
	public LogicalExpression duplicate() {
	
		List<LogicalExpression> dupTerms = new ArrayList<LogicalExpression>(this.childExpressions.size());
		for(LogicalExpression exp : this.childExpressions){
			dupTerms.add(exp.duplicate());
		}
		
		Conjunction conjunction = new Conjunction(dupTerms);
		
		return conjunction;
	}

	@Override
	public boolean evaluateIn(State s) {
		
		for(LogicalExpression exp : this.childExpressions){
			if(!exp.evaluateIn(s)){
				return false;
			}
		}
		
		return true;
		
	}

	@Override
	protected void remapVariablesInThisExpression(Map<String, String> fromToVariableMap) {
		//nothing necssary, not an atomic expression
	}

	

}
