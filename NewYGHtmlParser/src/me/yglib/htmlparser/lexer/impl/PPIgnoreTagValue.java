package me.yglib.htmlparser.lexer.impl;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.PSstringBuffer;
import me.yglib.htmlparser.lexer.TokenProcPlugin;

public class PPIgnoreTagValue implements TokenProcPlugin{
	
	private PageSource page = null;
	private TokenTag parentTag = null;
	
	public PPIgnoreTagValue(){
		;
	}
	
	void setPageSource(PageSource ps){
		this.page = ps;
	}
	
	void setParentTag(TokenTag pTag){
		this.parentTag = pTag;
	}
	
	@Override
	public String getEntryString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// TODO need to fix
	@SuppressWarnings("unchecked")
	@Override
	public Token parse() throws CommonException {
		
		TokenIgnoreTagValueImpl retVal = new TokenIgnoreTagValueImpl();
		StringBuffer sbResult = new StringBuffer();
		char ch;
		
		while((ch = this.page.getCurChar()) != PageSource.EOS)
		{
			if(ch == '<')
			{
				if(this.parentTag != null)	// script value俊 狼茄 林籍贸府
				{
					String tagName = this.parentTag.getTagName();
					//if(tagName.equalsIgnoreCase("script"))
					if(tagName.equalsIgnoreCase(this.parentTag.getTagName()))
					{
						int startIndex = this.page.getCurrentCursorPosition() + 2;
						String strPreCon = this.page
								.getSubString(startIndex, startIndex + ("/"+this.parentTag.getTagName()).length());
						if(strPreCon.indexOf(this.parentTag.getTagName()) > -1 ||
								strPreCon.indexOf(this.parentTag.getTagName().toUpperCase()) > -1)
						{
							retVal.setParentTag(this.parentTag);
							retVal.setValue(sbResult.toString());
							return retVal;
						}
						else
						{
							sbResult.append(ch);
							this.page.getNextChar();
						}
					} else {
						this.page.getNextChar();	// move PTR
					}
				}
				else	// 老馆 Tag绰 公矫贸府
				{
					retVal.setValue(sbResult.toString());
					return retVal;
				}
			}
			else
			{
				sbResult.append(ch);
				this.page.getNextChar();	// move cursor
			}
		}
		return retVal;
	}
	
	public static void main(String ... v) {
		PPIgnoreTagValue igtv = new PPIgnoreTagValue();
		StringBuffer sb = new StringBuffer("afdf<a d>  s<ffa<d>sf_112<3<</script>");		
		PSstringBuffer ps = new PSstringBuffer(sb);
		igtv.setPageSource(ps);
		TokenTagImpl tti = new TokenTagImpl();
		tti.setTagName("script");
		igtv.setParentTag(tti);
		
		try {
			System.out.println("Start parsing ..");
			Token tkVal = igtv.parse();
			System.out.println("Parsed>" + tkVal);
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}
}
