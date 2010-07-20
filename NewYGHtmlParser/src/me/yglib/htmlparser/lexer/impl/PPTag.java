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
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.lexer.*;
import me.yglib.htmlparser.util.Logging;

public class PPTag implements TokenProcPlugin{
	
	public enum ETagType {
		NOT_PARSED,
		TAG_START,
		CLOSED_TAG,
		TAG_NAME,
		TAG_NAME_END,
		ATTR_NAME,
		ATTR_NAME_END,
		ATTR_NAME_END_HAVE_VALUE,
		ATTR_VALUE,
		ATTR_VALUE_END,
		TAG_END
	}
	
	private PageSource page = null;
	private ETagType cAttr = ETagType.NOT_PARSED;
	private TokenTagImpl tkWorking = null;
	private char ch = '0';	// current caret char
	private String str_tagName = "";
	
	public PPTag(){
		;
	}
	
	public void setPageSource(PageSource ps){
		this.page = ps;
	}
	
	public void initValue(){
		this.cAttr = ETagType.NOT_PARSED;
		this.str_tagName = "";
		this.ch = '0';
	}
	
	@Override
	public String getEntryString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TokenTag parse() throws CommonException {
		
		this.initValue();
		
		this.tkWorking = new TokenTagImpl();
		this.tkWorking.initValue();
				
		do
		{
			if(this.cAttr == ETagType.NOT_PARSED)
			{
				ch = page.getNextChar();
				if(ch == '<')
				{
					this.cAttr = ETagType.TAG_START;
				}
			}
			else if(this.cAttr == ETagType.TAG_START)
			{
				//Logging.print(Logging.DEBUG, "[INT3] enter TagName :" + this.page.getCurrentCursorPosition());
				ch = page.getNextChar();
				this.parseTagName();
			}
			else if(this.cAttr == ETagType.TAG_END)
			{
				this.tkWorking.setTagName(this.str_tagName);
				//Logging.print(Logging.DEBUG, "[IN1] Page Index :" + this.page.getCurrentCursorPosition());
				return this.tkWorking;
			}
		}while(cAttr != ETagType.TAG_END);
		
		this.tkWorking.setTagName(this.str_tagName);
		//Logging.print(Logging.DEBUG, "[IN2] Page Index :" + this.page.getCurrentCursorPosition());
		return this.tkWorking;
	}
	
	private void parseTagName()
	{
		
		do
		{
			if(this.cAttr == ETagType.TAG_START)
			{
				if(this.ch == '/')
				{
					//this.isClosed = true;
					this.tkWorking.setCloseTag(true);
				}
				else if(Character.isLetterOrDigit(this.ch))
				{
					this.cAttr = ETagType.TAG_NAME;
					str_tagName += this.ch;
				}
				else
				{
					;
				}
			}
			else if(this.cAttr == ETagType.TAG_NAME)
			{
				if(Character.isLetterOrDigit(this.ch))
				{
					this.cAttr = ETagType.TAG_NAME;
					str_tagName += this.ch;
				}
				else
				{
					// tag name捞 皑瘤等 版快父 贸府
					if(str_tagName.length() > 0)
					{
						if(this.ch == ' ')
						{
							this.cAttr = ETagType.TAG_NAME_END;
							this.parseAttrName();
							break;
						}
						else if(this.ch == '>')
						{
							this.cAttr = ETagType.TAG_END;
							break ;
						}
						else if(this.ch == '/')
						{
							// auto closed tag 贸府
							//this.endClosed = true;
							this.tkWorking.setCloseTag(true);
						}
					}
				}
			}
			
		}while((this.ch = this.page.getNextChar()) != (char)PageSource.EOS &&
				cAttr != ETagType.TAG_NAME_END &&
				cAttr != ETagType.TAG_END);
		
		//System.out.println("### LastTagToken>" + this.ch);
	}
	
	
	
	private void parseAttrName()
	{
		String strAttrName = "";
		while(this.cAttr != ETagType.TAG_END &&
//				(this.ch = this.page.getNextChar()) != (char)PageSource.EOS)
				(this.ch = this.page.getCurChar()) != PageSource.EOS)
		{
			//System.out.println("DEBUG:AttrName>" + this.ch);
			
			if(this.cAttr == ETagType.TAG_NAME_END)
			{
				this.page.getNextChar();
				if(Character.isLetterOrDigit(this.ch))
				{
					strAttrName += this.ch;
					this.cAttr = ETagType.ATTR_NAME;
				}
				else if(this.ch == ' ')
				{
					;	// ignore white space
				}
				else if(this.ch == '/')
				{
					//this.endClosed = true;
					this.tkWorking.setEndClosed(true);
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					return ;
				}
			}
			else if(this.cAttr == ETagType.ATTR_NAME)
			{
				this.page.getNextChar();
				if(Character.isLetterOrDigit(this.ch))
				{
					strAttrName += this.ch;
				}
				else if(this.ch == ' ')
				{
					this.cAttr = ETagType.ATTR_NAME_END;
				}
				else if(this.ch == '=')
				{
					this.cAttr = ETagType.ATTR_NAME_END_HAVE_VALUE;
					//this.addAttr(strAttrName, this.parseAttrValue());
					this.tkWorking.addAttribute(strAttrName, this.parseAttrValue());
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					//this.addAttr(strAttrName, null);
					this.tkWorking.addAttribute(strAttrName, null);
					return;
				}
			}
			else if(this.cAttr == ETagType.ATTR_VALUE_END)
			{
				if(Character.isLetterOrDigit(this.ch))
				{
					this.cAttr = ETagType.ATTR_NAME;
					//this.page.setCursor(-1);
					this.parseAttrName();	// recursive parsing
					/*
					if(this.cAttr == ETagType.TAG_END)
					{
						return ;
					}
					*/
				}
				else if(this.ch == '>')
				{
					this.page.getNextChar();
					this.cAttr = ETagType.TAG_END;
					return;
				}
				else{
					this.page.getNextChar();
				}
			}
			else if(this.cAttr == ETagType.ATTR_NAME_END)
			{
				if(this.ch == '=')
				{
					this.page.getNextChar();
					this.cAttr = ETagType.ATTR_NAME_END_HAVE_VALUE;
					//this.addAttr(strAttrName, this.parseAttrValue());
					this.tkWorking.addAttribute(strAttrName, this.parseAttrValue());
				}
				else if(this.ch == '>')
				{
					this.page.getNextChar();
					this.cAttr = ETagType.TAG_END;
					//this.addAttr(strAttrName, null);
					this.tkWorking.addAttribute(strAttrName, null);
					return ;
				}
				else if(Character.isLetterOrDigit(this.ch))
				{
					//this.addAttr(strAttrName, null);
					this.tkWorking.addAttribute(strAttrName, null);
					//this.page.setCursor(-1);
					this.cAttr = ETagType.ATTR_NAME;
					this.parseAttrName();
				} 
				else
				{
					this.page.getNextChar();
				}
			}
			
		}	
	}
	
	private String parseAttrValue()
	{
		boolean isSqOpen = false, isDqOpen = false;
		StringBuffer sb = new StringBuffer();
		while((this.ch = this.page.getNextChar()) != (char)PageSource.EOS)
		{
			//System.out.println("DEBUG>" + this.ch);
			if(this.cAttr == ETagType.ATTR_NAME_END_HAVE_VALUE)
			{
				if(this.ch == '\'')
				{
					isSqOpen = true;
					this.cAttr = ETagType.ATTR_VALUE;
				}
				else if(this.ch == '\"')
				{
					isDqOpen = true;
					this.cAttr = ETagType.ATTR_VALUE;
				}
				else if(this.ch != ' ')
				{
					sb.append(this.ch);//strAttrValue += this.ch;
					this.cAttr = ETagType.ATTR_VALUE;
				}
			}
			else if(this.cAttr == ETagType.ATTR_VALUE)
			{
				if(this.ch == ' ')
				{
					if(isSqOpen || isDqOpen)
					{
						sb.append(this.ch);//strAttrValue += this.ch;
					}
					else
					{
						this.cAttr = ETagType.ATTR_VALUE_END;
						return sb.toString();//strAttrValue;
					}
				}
				else if(this.ch == '\'')
				{
					if(isSqOpen)
					{
						this.cAttr = ETagType.ATTR_VALUE_END;
						return sb.toString();//strAttrValue;
					}
					else
					{
						sb.append(this.ch);//strAttrValue += this.ch;
					}
				}
				else if(this.ch == '\"')
				{
					if(isDqOpen)
					{
						this.cAttr = ETagType.ATTR_VALUE_END;
						return sb.toString();//strAttrValue;
					}
					else
					{
						sb.append(this.ch);//strAttrValue += this.ch;
					}
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					return sb.toString();//strAttrValue
				}
				else
				{
					sb.append(this.ch);//strAttrValue += this.ch;
				}
			}
		}
		return sb.toString();//strAttrValue
	}
}
