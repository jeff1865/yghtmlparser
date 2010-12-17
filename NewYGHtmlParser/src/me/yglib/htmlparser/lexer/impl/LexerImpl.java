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
import me.yglib.htmlparser.datasource.impl.IntResManager;
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
	
	private void initProcessor()
	{
		ppIgrTagVal = new PPIgnoreTagValue();
		ppTag = new PPTag();
		ppTag.setPageSource(this.page);
		ppText = new PPText();
		ppComment = new PPComment();
	}
	
	@Override
	public Token getNextToken() 
	{
		this.currentIndex ++;	// Increase Token Index
		char ch = 0;
		
		while(this.page.hasNextChar())
		{
			ch = this.page.getCurChar();	// get current char
			
			if(this.isIgnoredMode){
				TokenIgnoreTagValueImpl ignoredTagValue = this.getIgnoredTagValue();
				ignoredTagValue.setIndex(this.currentIndex);
				return ignoredTagValue;
			}
			
			//System.out.println("CH1:" + ch);	// need to DEL1
			if(ch == '<')
			{
				//Logging.print(Logging.DEBUG, "entry parse tag");
				char chNext = this.page.getChar(this.page.getCurrentCursorPosition() + 1);
				//System.out.println("CH2:" + chNext);	// need to DEL2
				
				if(chNext == '!')	// PARSE Comment
				{
					//System.out.println("++++++++++++++ PARSE IGNORED !");
					Logging.print(Logging.DEBUG, "parse [!] comment detected ..");
					TokenCommentImpl tComment = this.getTokenComment();
					tComment.setIndex(this.currentIndex);
					return tComment;
				}
				else	// parse Tag
				{
					//System.out.println("============= TAG first char :" + chNext);
					//Logging.print(Logging.DEBUG, "[ENT] Parse TAG .. this PTR" + this.page.getCurrentCursorPosition());
					TokenTagImpl tToken = this.getTag();
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
					
					tToken.setIndex(this.currentIndex);
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
					//System.out.println("--> Enter Script Check Mode ..");
					if(this.latestTag.getTagName().equalsIgnoreCase("script")
							|| this.latestTag.getTagName().equalsIgnoreCase("style")){
						TokenIgnoreTagValueImpl ignoredTagValue = this.getIgnoredTagValue();
						ignoredTagValue.setIndex(this.currentIndex);
						return ignoredTagValue;
					}
					else
					{
						TokenTextImpl tText = this.getTokenText(null);
						tText.setIndex(this.currentIndex);
						return tText;
					}
				}
				else 
				{
					TokenTextImpl tText = this.getTokenText(null);
					tText.setIndex(this.currentIndex);
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
	
	private TokenIgnoreTagValueImpl getIgnoredTagValue(){
		//Logging.debug("##### Entered Ignore mode ..");
		// Processing Script Value 
		// this.getTokenText(this.latestTag);	// set SCRIPT or STYLE Tag
		this.isIgnoredMode = false;
		
		//PPIgnoreTagValue ppIgrTagVal = new PPIgnoreTagValue();
		ppIgrTagVal.setPageSource(this.page);
		ppIgrTagVal.setParentTag(this.latestTag);
		
		TokenIgnoreTagValueImpl igrTkVal = null;
		try {
			igrTkVal = (TokenIgnoreTagValueImpl)ppIgrTagVal.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return igrTkVal;
	}
	
	public TokenTagImpl getTag()
	{
		ppTag.setPageSource(this.page);
		
		TokenTagImpl token = null;
		try {
			token = (TokenTagImpl)ppTag.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		return (TokenTagImpl)token;
	}
	
	public TokenTextImpl getTokenText(TokenTag latestTag){
		//PPText ppt = new PPText();
		ppText.setPageSource(this.page);
		ppText.setParentTokenTag(latestTag);
		
		TokenTextImpl tText = null;
		try {
			tText = (TokenTextImpl)ppText.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return tText;
	}
	
	private TokenCommentImpl getTokenComment(){
		//PPComment ppc = new PPComment();
		ppComment.setPageSource(this.page);
		
		Token token = null;
		try {
			token = ppComment.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return (TokenCommentImpl)token;
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
			//bufPs = IntResManager.loadStringBufferPage(new URL("http://art.chosun.com/site/data/html_dir/2010/04/19/2010041900721.html").toURI(), 3000);
			bufPs = ResourceManager.getLoadedPage(new File("test\\test.html"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		loadTime = System.currentTimeMillis() - loadTime;
		
		Lexer lexer = new LexerImpl(bufPs);
		Token nt = null;
		while(lexer.hasNextToken() && (nt = lexer.getNextToken()) != null){
			System.out.println(">" + nt.getIndex() +":"+ nt.toString());
		}
		
		System.out.println("Page Load Time :" + loadTime);
	}
}
