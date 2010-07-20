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

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.lexer.TokenProcPlugin;

public class PPText implements TokenProcPlugin{
	
	private String parsedText = null;
	private PageSource page = null;
	private TokenTag parentTokenTag = null;
	
	public PPText(){
		;
	}
	
	public void setPageSource(PageSource ps){
		this.page = ps;
	}
	
	public void setParentTokenTag(TokenTag pTokenTag){
		this.parentTokenTag = pTokenTag;
	}
	
	@Override
	public String getEntryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TokenText parse() throws CommonException {
		StringBuffer sb = new StringBuffer();
		this.parsedText = "";
		char ch;
		//while((ch = this.page.getNextChar()) != PageSource.EOS)
		while((ch = this.page.getCurChar()) != PageSource.EOS)
		{
			if(ch == '<')
			{
				if(this.parentTokenTag != null)	// script value俊 狼茄 林籍贸府
				{
					String tagName = this.parentTokenTag.getTagName();
					if(tagName.equalsIgnoreCase("script"))
					{
						int startIndex = this.page.getCurrentCursorPosition() + 2;
						String strPreCon = this.page
								.getSubString(startIndex, startIndex + "/script".length());
						
						if(strPreCon.indexOf("script") > -1 ||
								strPreCon.indexOf("SCRIPT") > -1)
						{
							//this.page.setCursor(-1);
							this.parsedText = sb.toString();
							//return ;
							break;
						}
						else
						{
							sb.append(ch);
							this.page.getNextChar();
						}
					}
					
				}
				else	// 老馆 Tag绰 公矫贸府
				{
					//this.page.setCursor(-1);
					this.parsedText = sb.toString();
					//return ;
					break;
				}
			}
			else
			{
				//System.out.println("CH:" + ch);
				sb.append(ch);
				this.page.getNextChar();
			}
		}
		
		TokenTextImpl tti = new TokenTextImpl();
		tti.setValueText(this.parsedText);
		return tti;
	}
}
