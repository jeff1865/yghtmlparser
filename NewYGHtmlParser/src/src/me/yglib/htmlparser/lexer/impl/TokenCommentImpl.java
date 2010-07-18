package me.yglib.htmlparser.lexer.impl;

import me.yglib.htmlparser.*;
import me.yglib.htmlparser.datasource.PageSource;

public class TokenCommentImpl implements TokenComment{
	
	private String strComment;
	
	public TokenCommentImpl(){
		;
	}
	
	public String toString(){
		return "[Comment] " + strComment;
	}
	
	@Override
	public String getCommentText() {
		return this.strComment;
	}
	
	void setCommentText(String str){
		this.strComment = str;
	}

	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
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
