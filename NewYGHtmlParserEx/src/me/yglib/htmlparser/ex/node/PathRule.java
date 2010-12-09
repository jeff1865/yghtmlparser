package me.yglib.htmlparser.ex.node;

import java.util.*;

import me.yglib.htmlparser.util.Logging;

public class PathRule {
	
	private ArrayList<PathRuleElement> al_pathRule = null;
	private String strRule = null;
	
	public PathRule(String strRule){
		this.strRule = strRule;
		
		this.parse();
	}
	
	private void parse(){
		if(this.strRule == null) return;
		
		this.al_pathRule = new ArrayList<PathRuleElement>();
		StringTokenizer stkz = new StringTokenizer(this.strRule, "/");
		
		while(stkz.hasMoreTokens()){
			//this.al_pathRule.add(stkz.nextToken());
			String text = stkz.nextToken();
			String strTag = text.substring(0, text.indexOf("["));
			String strNum = text.substring(text.indexOf("[")+1, text.indexOf("]"));
			//Logging.debug("--->" + text + ":" + a + ":" + b);
			
			this.al_pathRule.add(new PathRuleElement(strTag, Integer.parseInt(strNum)));
		}
	}
	
	public List<PathRuleElement> getPathRule(){
		return this.al_pathRule;
	}
	
	public static void main(String ... v){
		PathRule pr = new PathRule("html[1]/body[0]/div[7]/div[2]/div[2]/div[0]/div[0]/div[0]/h1[0]/");
		for(PathRuleElement pre : pr.getPathRule()){
			System.out.println("--->" + pre);
		}
	}
	
}
