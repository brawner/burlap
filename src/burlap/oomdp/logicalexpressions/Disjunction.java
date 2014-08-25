package burlap.oomdp.logicalexpressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import burlap.oomdp.core.State;

public class Disjunction extends LogicalExpression {

	public Disjunction(LogicalExpression...expressions){
		for(LogicalExpression exp : expressions){
			this.childExpressions.add(exp);
			exp.parentExpression = this;
		}
		
	}
	
	public Disjunction(List <LogicalExpression> expressions){
		for(LogicalExpression exp : expressions){
			this.childExpressions.add(exp);
			exp.parentExpression = this;
		}
		
	}
	
	
	@Override
	public LogicalExpression duplicate() {
		List<LogicalExpression> dupTerms = new ArrayList<LogicalExpression>(this.childExpressions.size());
		for(LogicalExpression exp : this.childExpressions){
			dupTerms.add(exp.duplicate());
		}
		
		Disjunction disjunction = new Disjunction(dupTerms);
		
		return disjunction;
	}

	@Override
	public boolean evaluateIn(State s) {
		
		for(LogicalExpression exp : this. childExpressions){
			if(exp.evaluateIn(s)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected void remapVariablesInThisExpression(Map<String, String> fromToVariableMap) {
		//nothing necssary, not an atomic expression
	}

}
