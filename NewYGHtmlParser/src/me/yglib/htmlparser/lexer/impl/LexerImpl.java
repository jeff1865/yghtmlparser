// =============================================================================
//   YG Html Parser (Rapid Java Html Parser Project)
//   Copyright 2010 Young-Gon Kim (gonni21c@gmail.com)
//   http://ygonni.blogspot.com
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// =============================================================================

package me.yglib.htmlparser.lexer.impl;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenComment;
import me.yglib.htmlparser.TokenScriptValue;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.ResourceManager;
import me.yglib.htmlparser.lexer.*;
import me.yglib.htmlparser.util.Logging;

/**
 * Concrete class of me.yglib.htmlparser.lexer.Lexer
 * YG HtmlParser Project
 * @author Young-Gon Kim (gonni21c@gmail.com)
 * 2009. 09. 12
 */
public class LexerImpl implements Lexer{
	
	private Hashtable<String, TokenProcPlugin> tpTable = null;
	
	private PageSource page = null;
	private int currentIndex = 0;
	private TokenTag latestTag = null;
	private boolean isIgnoredMode = false;
	
	PPIgnoreTagValue ppIgrTagVal = null;//new PPIgnoreTagValue();
	PPTag ppTag = null;//new PPTag();
	PPText ppText = null;//new PPText();
	PPComment ppComment = null;//new PPComment();
	
	public LexerImpl(PageSource pageSource)
	{
		this.currentIndex = 0;
		page = pageSource;
		this.tpTable = new Hashtable<String, TokenProcPlugin>();
		this.initProcessor();
	}
	
	private void initProcessor(){
		ppIgrTagVal = new PPIgnoreTagValue();
		ppTag = new PPTag();
		ppTag.setPageSource(this.page);
		ppText = new PPText();
		ppComment = new PPComment();
	}
	
	@Override
	public Token getNextToken() {
		this.currentIndex ++;	// Increase Token Index
		char ch = 0;
		
		while(this.page.hasNextChar())
		{
			ch = this.page.getCurChar();	// get current char
			
			if(this.isIgnoredMode){
				return this.getIgnoredTagValue();
			}
			
			if(ch == '<')
			{
				//Logging.print(Logging.DEBUG, "entry parse tag");
				char chNext = this.page.getChar(this.page.getCurrentCursorPosition() + 1);
								
				if(chNext == '!')	// PARSE Comment
				{
					//Logging.print(Logging.DEBUG, "parse comment..");
					TokenComment tComment = this.getTokenComment();
					return tComment;
				}
				else	// parse Tag
				{
					//Logging.print(Logging.DEBUG, "[ENT] Parse TAG .. this PTR" + this.page.getCurrentCursorPosition());
					TokenTag tToken = this.getTag();
					if(tToken != null){	// set latest
						if(!tToken.isClosedTag()) // Open Tag
						{
							this.latestTag = tToken;
							if(this.isIgnoreValueTag(tToken)){	// script 
								this.isIgnoredMode = true;
							}
						}
						else // Closed Tag
						{
							if(this.latestTag != null &&
									this.latestTag.getTagName().equalsIgnoreCase(tToken.getTagName()))
								this.latestTag = null;
						}
					}
					return tToken;
				}
			}
			else if(ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t')
			{
				;	// Ignore white space
				this.page.getNextChar();	// Increase cursor pointer
			}
			else //if(Character.isLetterOrDigit(ch))	// text value
			{
				if(this.latestTag != null){
					System.out.println("--> Enter Script Check Mode ..");
					if(this.latestTag.getTagName().equalsIgnoreCase("script")
							|| this.latestTag.getTagName().equalsIgnoreCase("style")){
						return this.getIgnoredTagValue();
					}
					else
					{
						TokenText tText = this.getTokenText(null);
						return tText;
					}
				}
				else 
				{
					TokenText tText = this.getTokenText(null);
					return tText;
				}
			}
		}
		
		return null;
	}
	
	private boolean isIgnoreValueTag(TokenTag tag){
		String tagName = tag.getTagName();
		if(tagName.equalsIgnoreCase("script") || tagName.equalsIgnoreCase("style"))
			return true;
		return false;
	}
	
	private Token getIgnoredTagValue(){
		Logging.debug("##### Entered Ignore mode ..");
		// Processing Script Value 
		// this.getTokenText(this.latestTag);	// set SCRIPT or STYLE Tag
		this.isIgnoredMode = false;
		
		//PPIgnoreTagValue ppIgrTagVal = new PPIgnoreTagValue();
		ppIgrTagVal.setPageSource(this.page);
		ppIgrTagVal.setParentTag(this.latestTag);
		
		Token igrTkVal = null;
		try {
			igrTkVal = ppIgrTagVal.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return igrTkVal;
	}
	
	public TokenTag getTag()
	{
		//PPTag ppTag = new PPTag();
		//ppTag = new PPTag();
		ppTag.setPageSource(this.page);
		
		Token token = null;
		try {
			token = ppTag.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		//Logging.print(Logging.DEBUG, "[OUT] Page Index :" + this.page.getCurrentCursorPosition());
		//this.ppTag.setPageSource(null);
		return (TokenTag)token;
	}
	
	public TokenText getTokenText(TokenTag latestTag){
		//PPText ppt = new PPText();
		ppText.setPageSource(this.page);
		ppText.setParentTokenTag(latestTag);
		
		TokenText tText = null;
		try {
			tText = ppText.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return tText;
	}
	
	private TokenComment getTokenComment(){
		//PPComment ppc = new PPComment();
		ppComment.setPageSource(this.page);
		
		Token token = null;
		try {
			token = ppComment.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return (TokenComment)token;
	}
		
	@Override
	public PageSource getPage() {
		return this.page;
	}
	
	@Override
	public boolean hasNextToken() {
		return page.hasNextChar();
	}
	
	@Override
	public void addTokepProcPluging(TokenProcPlugin tpp) {
		// TODO Auto-generated method stub
	}
	
	public static void main(String ... v) 
	{
		long loadTime = System.currentTimeMillis();
		PageSource bufPs = null;
		try {
			//bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.nate.com/").toURI(), 3000);
			bufPs = ResourceManager.getLoadedPage(new File("test\\naver.html"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		loadTime = System.currentTimeMillis() - loadTime;
		
		Lexer lexer = new LexerImpl(bufPs);
		Token nt = null;
		while(lexer.hasNextToken() && (nt = lexer.getNextToken()) != null){
			//Token nt = lexer.getNextToken();
			System.out.println("==== NextToken =============" );
			System.out.println(nt.toString());
			System.out.println("============================" );
		}
		
		System.out.println("Page Load Time :" + loadTime);
	}
}
