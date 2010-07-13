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
	
	public LexerImpl(PageSource pageSource)
	{
		this.currentIndex = 0;
		page = pageSource;
		this.tpTable = new Hashtable<String, TokenProcPlugin>();
	}
	
	private boolean isIgnoreValueTag(TokenTag tag){
		String tagName = tag.getTagName();
		if(tagName.equalsIgnoreCase("script") || tagName.equalsIgnoreCase("style"))
			return true;
		return false;
	}
	
	
	@Override
	public Token getNextToken() {
		this.currentIndex ++;	// Increase Token Index
		char ch = 0;
		
		while(this.page.hasNextChar())
		{
			ch = this.page.getCurChar();	// get current char
			
			if(this.isIgnoredMode){
				Logging.debug("##### Entered Ignore mode ..");
				// Processing Script Value 
				// this.getTokenText(this.latestTag);	// set SCRIPT or STYLE Tag
				this.isIgnoredMode = false;
				
				PPIgnoreTagValue ppIgrTagVal = new PPIgnoreTagValue();
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
					TokenTag tToken = this.parseTag();
					if(tToken != null){
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
				//System.out.println("--> Enter Text ..");
				if(this.latestTag != null){
					System.out.println("--> Enter Script Check Mode ..");
					if(this.latestTag.getTagName().equalsIgnoreCase("script")
							|| this.latestTag.getTagName().equalsIgnoreCase("style")){
						System.out.println("#####> Enter Script Value Check Mode ..");
						PPIgnoreTagValue ppIg = new PPIgnoreTagValue();
						ppIg.setPageSource(this.page);
						ppIg.setParentTag(this.latestTag);
						
						Token token = null;
						try {
							token = ppIg.parse();
						} catch (CommonException e) {
							e.printStackTrace();
						}
						
						return token;
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
	
	public TokenTag parseTag()
	{
		PPTag ppTag = new PPTag();
		ppTag.setPageSource(this.page);
		
		Token token = null;
		try {
			token = ppTag.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return (TokenTag)token;
	}
	
	public TokenText getTokenText(TokenTag latestTag){
		PPText ppt = new PPText();
		ppt.setPageSource(this.page);
		ppt.setParentTokenTag(latestTag);
		
		TokenText tText = null;
		try {
			tText = ppt.parse();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return tText;
	}
	
	private TokenComment getTokenComment(){
		PPComment ppc = new PPComment();
		ppc.setPageSource(this.page);
		
		Token token = null;
		try {
			token = ppc.parse();
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
			bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.chosun.com/").toURI(), 3000);
			//bufPs = ResourceManager.getLoadedPage(new File("test\\naver.html"));
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
