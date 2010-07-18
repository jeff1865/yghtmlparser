package me.yglib.htmlparser;

public class TagAttribute {
	private String attrName = null, attrValue = null;
	
	public TagAttribute(String attrName, String attrValue){
		this.attrName = attrName;
		this.attrValue = attrValue;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	public String getAttrName() {
		return attrName;
	}
}
