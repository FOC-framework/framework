/*
 * Created on Aug 2009 By Antoine SAMAHA
 */
package com.foc.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.db.DBManager;

/**
 * @author 01Barmaja
 */
public class FocListGroupBy {
	private ArrayList<String>                     arrayOfAtomicExpressions = null;
	private String                                groupByExpression        = null;
	private HashMap<Integer, GroupByFieldFormula> fieldsInFormulas         = null;
	
	public FocListGroupBy(){
		fieldsInFormulas = new HashMap<Integer, GroupByFieldFormula>();
	}
	
	public void dispose(){
		if(fieldsInFormulas != null){
			Iterator<GroupByFieldFormula> iter = fieldsInFormulas.values().iterator();
			while(iter != null && iter.hasNext()){
				GroupByFieldFormula frml = iter.next();
				frml.dispose();
			}
			fieldsInFormulas.clear();
		}
		groupByExpression = null;
		if(arrayOfAtomicExpressions != null){
			arrayOfAtomicExpressions.clear();
			arrayOfAtomicExpressions = null;
		}
	}

	public ArrayList<String> getArrayOfAtomicExpressions(boolean create){
		if(arrayOfAtomicExpressions == null && create){
			arrayOfAtomicExpressions = new ArrayList<String>(); 
		}
		return arrayOfAtomicExpressions;
	}
	
	public void addAtomicExpression(String atomic){
		getArrayOfAtomicExpressions(true).add(atomic);
	}
	
	private void buildExpressionFromAtomicArrayOfExpressions(){
		ArrayList<String> arr = getArrayOfAtomicExpressions(false);
		
		StringBuffer buff = new StringBuffer();
		if(arr.size() == 1){
			buff.append(arr.get(0));
		}else if(arr.size() > 1){
			buff.append("concat(");
			for(int i=0; i<arr.size(); i++){
				if(i>0) buff.append(",'|',");
				buff.append(arr.get(i));
			}
			buff.append(")");
		}
		setGroupByExpression(buff.toString());
	}
	
	public void addField_Formulas(int fieldName, String formulaFullExpression){
		GroupByFieldFormula groupByFormula = new GroupByFieldFormula(formulaFullExpression); 
		fieldsInFormulas.put(fieldName, groupByFormula);
	}

	public void addField_FormulaSingleText(int fieldID, String fieldName, String formula){
		if(formula.toUpperCase().equals("LISTAGG")){
			fieldName = DBManager.provider_ConvertFieldName(Globals.getDBManager().getProvider(), fieldName);
			addField_Formulas(fieldID, formula+"(", ", ', ') WITHIN GROUP (ORDER BY "+fieldName+")");
		}else{
			addField_Formulas(fieldID, formula+"(", ")");
		}
	}
	
	public void addField_Formulas(int fieldName, String formulaPartBefore, String formulaPartAfter){
		GroupByFieldFormula groupByFormula = new GroupByFieldFormula(formulaPartBefore, formulaPartAfter); 
		fieldsInFormulas.put(fieldName, groupByFormula);
	}
	
	/*
	public void addField_Formulas(String alias, FocDesc focDesc, int fieldID, String formulaPartBefore, String formulaPartAfter){
		FField fld = focDesc.getFieldByID(fieldID);
		
		addField_Formulas(alias+"."+fld.getName(), formulaPartBefore, formulaPartAfter);
	}
	*/

	public String getGroupByExpression() {
		if(groupByExpression == null || groupByExpression.isEmpty()){
			buildExpressionFromAtomicArrayOfExpressions();
		}
		return groupByExpression;
	}

	public void setGroupByExpression(String groupByExpression) {
		this.groupByExpression = groupByExpression;
	}

	public String addFormulaToFieldName(int key, String str){
		String ret = str;
		GroupByFieldFormula formula = fieldsInFormulas.get(key);
		if(formula != null){
			ret = formula.composeFormulaForFieldName(str);
		}
		return ret;
	}
	
	private class GroupByFieldFormula {
		private String     formulaBefore         = null;
		private String     formulaAfter          = null;
		private String     formulaFullExpression = null;
		
		private GroupByFieldFormula(String formulaFullExpression){
			this.formulaFullExpression = formulaFullExpression;
		}
		
		private GroupByFieldFormula(String formulaBefore, String formulaAfter){
			this.formulaBefore = formulaBefore;
			this.formulaAfter  = formulaAfter;
		}
		
		public void dispose(){
			formulaBefore         = null;
			formulaAfter          = null;
			formulaFullExpression = null;
		}

		public String composeFormulaForFieldName(String str){
			String ret = null;
			if(formulaFullExpression != null){
				ret = formulaFullExpression;
			}else{
				ret = formulaBefore + str + formulaAfter;
			}
			return ret;
		}
	}
}