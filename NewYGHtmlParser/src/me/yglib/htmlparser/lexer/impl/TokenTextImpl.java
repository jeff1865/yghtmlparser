package me.yglib.htmlparser.lexer.impl;

import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;

public class TokenTextImpl implements TokenText{
		
	private PageSource page = null;
	private TokenTag parentTokenTag = null;
	private String valueText = null;
	private int index = -1;
	
	TokenTextImpl(){
		this.valueText = "aaa";
	}
	
	void setIndex(int idx){
		this.index = idx;
	}
	
	public Object clone(){
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public String toString(){
		return "[Text]" + this.valueText;
	}
	
	@Override
	public String getValueText() {
		return this.valueText;
	}
	
	public void setValueText(String valTxt){
		this.valueText = valTxt;
	}
	
	@Override
	public int getEndPosition() {
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
	
	public static void main(String...v){
		long tm = System.currentTimeMillis();
		for(int i=0;i<1000000;i++)
		{
			TokenTextImpl tti = new TokenTextImpl();
			//new TokenTextImpl();
			tti.setValueText("ahehe~");
		}
		System.out.println("Ex1>" + (System.currentTimeMillis() - tm));
		
		TokenTextImpl tti = new TokenTextImpl();
		
		long tm1 = System.currentTimeMillis();
		for(int i=0;i<1000000;i++)
		{
			TokenTextImpl tti2 = (TokenTextImpl)tti.clone();
			//Object clone = tti.clone();
			//(TokenTextImpl)tti.clone();
			tti2.setValueText("ahehe~");
		}
		System.out.println("Ex2>" + (System.currentTimeMillis() - tm1));
		
		//System.out.println("tti:1>" + tti.getValueText());
		//System.out.println("tti:2>" + tti2.getValueText());
	}

	@Override
	public int getIndex() {
		return this.index;
	}
	
}
