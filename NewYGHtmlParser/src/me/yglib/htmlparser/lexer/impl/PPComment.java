package me.yglib.htmlparser.lexer.impl;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.lexer.TokenProcPlugin;

public class PPComment implements TokenProcPlugin {
	
	private PageSource page = null;
	private boolean isTag = false;
	
	public void setPageSource(PageSource ps){
		this.page = ps;
	}
	
	@Override
	public String getEntryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Token parse() throws CommonException {
		this.isTag = false;
		
		char ch;
		StringBuffer sb = new StringBuffer();
		StringBuffer sbx = new StringBuffer();
		
		boolean sqActive = false, dqActive = false;
		while((ch = this.page.getNextChar()) != PageSource.EOS)
		{
			//super.parsedToken += ch;
			sbx.append(ch);
			if(ch == '<')	// entry point
			{
				if(!sqActive && !dqActive)
				{
					if(this.page.getCurChar() == '!')
					{
						this.page.getNextChar();	// move cursor
						
						if(this.page.getCurChar() == '-')
						{
							while(this.page.getNextChar() == '-') ;
						}
						else
						{
							this.isTag = true;
							//this.commentString += this.page.getCurrentChar();
						}
						
					}
					else
					{
						//this.commentString += ch;//System.out.println("이상한 문자 :" + ch);
						sb.append(ch);
					}
				}
				else
				{
					//this.commentString += ch;
					sb.append(ch);
				}
			}
			else if(ch == '\'')
			{
				if(sqActive) sqActive = false;
				else sqActive = true;
				//this.commentString += ch;
				sb.append(ch);
			}
			else if(ch == '\"')
			{
				if(dqActive) dqActive = false;
				else dqActive = true;
				//this.commentString += ch;
				sb.append(ch);
			}
			else if(ch == '>')
			{
				if(!sqActive && !dqActive)
				{
					//System.out.println("빠짐..");
					if(!this.isTag)
					{
						if(this.page.getChar(this.page.getCurrentCursorPosition()-2) == '-')
							break;	// end of comment
					}
					else	// Tag처리
					{
						break;
					}
					//this.commentString += ch;
					sb.append(ch);
				}
				else
				{
					//this.commentString += ch;
					sb.append(ch);
				}
			}
			else if(ch == '-')
			{
				if(sqActive || dqActive)
				{
					//this.commentString += ch;	// 인용문 내에 존재하는 경우
					sb.append(ch);
				}
				else
				{
					int tempIndex = this.page.getCurrentCursorPosition();
					int iCheck = 0, maxCheck = 5;
					while(this.page.getChar(tempIndex++) != '>' 
						&& iCheck++ < maxCheck)	;
					
					if(iCheck < maxCheck)	// end State
					{
						;
					}
					else	// not end State
					{
						//this.commentString += ch;
						sb.append(ch);
					}
				}
			}
			else
			{
				//this.commentString += ch;
				sb.append(ch);
			}
		}
		//super.parsedToken = sbx.toString();
		
		TokenCommentImpl tci = new TokenCommentImpl();
		tci.setCommentText(sbx.toString());
		
		return tci;
	}

}
