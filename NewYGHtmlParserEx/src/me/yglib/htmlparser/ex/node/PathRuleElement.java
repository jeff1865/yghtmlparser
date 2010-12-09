package me.yglib.htmlparser.ex.node;

public class PathRuleElement {
	private String strTagName = null;
	private int index = -1;
	
	public PathRuleElement(String tagName, int idx){
		this.strTagName = tagName;
		this.index = idx;
	}
	
	public String getStrTagName() {
		return strTagName;
	}

	public int getIndex() {
		return index;
	}
	
	public String toString(){
		return this.strTagName + "->" + index;
	}
}
