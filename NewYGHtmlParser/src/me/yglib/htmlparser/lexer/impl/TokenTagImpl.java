package me.yglib.htmlparser.lexer.impl;

import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.*;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.util.*;

public class TokenTagImpl implements TokenTag{
	
	private List<TagAttribute> attrs = null;
	private StringBuffer tagName = null, rawHtml = null;
	private boolean isClosed = false, endClosed = false;
	private PageSource pageSource = null;
	private int startIndex = -1, endIndex = -1;
	private int tokenIndex = -1;
	
	TokenTagImpl() {
		//Logging.print(Logging.DEBUG, "[DEBUG] Cannot construct in the other package..");
	}
	
	void initValue(){
		if(this.attrs == null)
			this.attrs = new ArrayList<TagAttribute>();
		this.attrs.clear();
		this.tagName = new StringBuffer("");
		this.rawHtml = new StringBuffer("");
		this.isClosed = false;
		this.endClosed = false;
		this.pageSource = null;
		this.startIndex = -1;
		this.endIndex = -1;
	}
		
	@Override
	public List<TagAttribute> getAttrs() {
		return this.attrs;
	}
	
	void setAttribute(List<TagAttribute> attrs){
		this.attrs = attrs;
	}
	
	void addAttribute(TagAttribute attr){
		if(this.attrs == null)
			this.attrs = new ArrayList<TagAttribute>();
		this.attrs.add(attr);
	}
	
	void addAttribute(String attrName, String attrValue){
		if(this.attrs == null)
			this.attrs = new ArrayList<TagAttribute>();
		this.attrs.add(new TagAttribute(attrName, attrValue));
	}
	
	@Override
	public String getTagName() {
		return this.tagName.toString();
	}
	
	void setTagName(String tagName){
		this.tagName = new StringBuffer(tagName);
	}
	
	void appendTagName(char ch){
		this.tagName.append(ch);
	}
	
	@Override
	public boolean isClosedTag() {
		return this.isClosed;
	}
	
	void setCloseTag(boolean closed){
		this.isClosed = closed;
	}
	
	@Override
	public boolean isEndClosed() {
		return this.endClosed;
	}
	
	void setEndClosed(boolean endClosed){
		this.endClosed = endClosed;
	}
	
	@Override
	public int getEndPosition() {
		return this.endIndex;
	}
	
	void setEndPosition(int position) {
		this.endIndex = position;
	}

	@Override
	public PageSource getPage() {
		return this.pageSource;
	}
	
	void setPageSource(PageSource ps) {
		this.pageSource = ps;
	}
	
	@Override
	public int getStartPosition() {
		return this.startIndex;
	}
	
	void setStartPosition(int position){
		this.endIndex = position;
	}
	
	@Override
	public String toHtml() {
		//return this.rawHtml.toString();
		
		// impl temporary
		String strRet = "<";
		if(this.isClosed) strRet += "/";
		strRet += this.tagName;
		
		if(this.attrs != null && this.attrs.size() > 0){
			for(TagAttribute attr : this.attrs){
				strRet += " " + attr.getAttrName() + "=\"" + attr.getAttrValue() + "\"";
			}
		}
		
		if(this.endClosed) strRet += "/";
		
		return strRet + ">";
	}
	
	void setToHtml(String rawHtml){
		this.rawHtml = new StringBuffer(rawHtml);
	}
	
	public String toString(){
		String retString = "[TAG]";
		if(this.isClosed) retString += "/";
		retString += this.tagName + "(";
		if(this.attrs != null)
		for(TagAttribute tAttr : this.attrs){
			retString += tAttr.getAttrName() + ":" + tAttr.getAttrValue() + " ";
		}
		retString = retString.trim();
		retString += ")";
		return retString;
	}
	
	public static void main(String...v){
		System.out.println("Init .. ");
		new TokenTagImpl();
	}
	
	void setIndex(int idx){
		this.tokenIndex = idx;
	}
	
	@Override
	public int getIndex() {
		return this.tokenIndex;
	}
}
