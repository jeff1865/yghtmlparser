package me.yglib.htmlparser.lexer.impl;

import me.yglib.htmlparser.TokenIgnoreTagValue;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.datasource.PageSource;

public class TokenIgnoreTagValueImpl implements TokenIgnoreTagValue {
	
	private String strValue = null;
	private TokenTag pTag = null;
	private int index = -1;
	
	public TokenIgnoreTagValueImpl(){
		;
	}
	
	
	
	public String toString(){
		return "[IgnoreTagValue:" + this.pTag.getTagName() + "]" + this.strValue; 
	}
	
	void setParentTag(TokenTag parTag){
		this.pTag = parTag;
	}
	
	@Override
	public TokenTag getParentTag() {
		return this.pTag;
	}
	
	void setValue(String val){
		this.strValue = val;
	}
	
	@Override	
	public String getValue() {
		return this.strValue;
	}

	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	void setIndex(int idx) {
		this.index = idx;
	}
	
	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return this.index;
	}

	@Override
	public PageSource getPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
