package me.yglib.htmlparser.lexer.impl;

import java.util.Hashtable;
import java.util.Set;

import me.yglib.htmlparser.TagAttribute;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.PSstringBuffer;
import me.yglib.htmlparser.lexer.TokenProcPlugin;
import me.yglib.htmlparser.util.Logging;

/**
 * 고성능 처리를 위한 구현 원칙
 * - Stirng 연산은 철저히 StringBuffer
 * - 생성량이 과다한 객체에 대해서는 Pool 처리
 * 
 * YG HtmlParser Project
 * @author Young-Gon Kim (gonni21c@gmail.com)
 * 2009. 11. 15
 */
public class PluginParserToken implements TokenProcPlugin{
	
	private enum ETagType{	//TODO need to change value based on BYTE array for performance 
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
		
	private PageSource pageSource = null;
	private TokenTagImpl preBuildToken = null;	//TODO need to impl TokenFactory
		
	//private StringBuffer str_tagName = null;
	private ETagType cAttr = ETagType.NOT_PARSED;
	//private Hashtable<String, String> v_attrs = null;
	//private boolean isClosed = false;
	//private boolean endClosed = false;
	private char ch;
		
	public PluginParserToken(PageSource ps){
		this.setPageSource(ps);
		this.preBuildToken = new TokenTagImpl();
	}
	
	public void setPageSource(PageSource ps){
		this.pageSource = ps;
	}
	
	private void initValues(){
		//this.str_tagName = new StringBuffer();
		this.cAttr = ETagType.NOT_PARSED;
		//this.v_attrs = null;
		//this.isClosed = false;
		//this.endClosed = false;
		
		if(this.preBuildToken != null) this.preBuildToken.initValue();
		else this.preBuildToken = new TokenTagImpl();
	}
	
	public TokenTag parse(){
		this.initValues();
		int startPosition = this.pageSource.getCurrentCursorPosition();
		this.preBuildToken.setStartPosition(startPosition);
		
		do
		{
			if(this.cAttr == ETagType.NOT_PARSED)
			{
				ch = pageSource.getNextChar();
				if(ch == '<') this.cAttr = ETagType.TAG_START;
			}
			else if(this.cAttr == ETagType.TAG_START)
			{
				ch = pageSource.getNextChar();
				this.parseTagName();
			}
			else if(this.cAttr == ETagType.TAG_END)
			{
				return this.preBuildToken;
			}
		}while(cAttr != ETagType.TAG_END);
		
		int endPosition = this.pageSource.getCurrentCursorPosition();
		this.preBuildToken.setEndPosition(endPosition);
		Logging.print(Logging.DEBUG, "start:" + startPosition + ", end:" + endPosition);
		this.preBuildToken.setToHtml(this.pageSource.getSubString(startPosition, endPosition));
		
		return this.preBuildToken;
	}
	
	private void parseTagName()
	{
		StringBuffer sbTagName = new StringBuffer();
		do
		{
			if(this.cAttr == ETagType.TAG_START)
			{
				if(this.ch == '/')
				{
					//this.isClosed = true;
					this.preBuildToken.setCloseTag(true);
				}
				else if(Character.isLetterOrDigit(this.ch))
				{
					this.cAttr = ETagType.TAG_NAME;
					sbTagName.append(this.ch);
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
					sbTagName.append(this.ch);
				}
				else
				{
					// tag name이 감지된 경우만 처리
					if(sbTagName.length() > 0)
					{
						if(this.ch == ' ')
						{
							this.cAttr = ETagType.TAG_NAME_END;
							this.parseAttrName(null);
						}
						else if(this.ch == '>')
						{
							this.cAttr = ETagType.TAG_END;
						}
						else if(this.ch == '/')
						{
							// auto closed tag 처리
							//this.endClosed = true;
							this.preBuildToken.setEndClosed(true);
						}
						this.preBuildToken.setTagName(sbTagName.toString());
					}
				}
			}
			
		//}while((this.ch = this.pageSource.getNextChar()) != (char)Page.EOF &&
		}while((this.ch = this.pageSource.getNextChar()) != (char)PageSource.EOS &&
				cAttr != ETagType.TAG_NAME_END &&
				cAttr != ETagType.TAG_END);
	}
		
	private void parseAttrName(String preRead)
	{
		StringBuffer strAttrName = null;
		if(preRead != null) strAttrName = new StringBuffer(preRead);
		else strAttrName = new StringBuffer("");
		
		while(this.cAttr != ETagType.TAG_END &&
				(this.ch = this.pageSource.getNextChar()) != (char)PageSource.EOS)
		{
			if(this.cAttr == ETagType.TAG_NAME_END)
			{
				if(Character.isLetterOrDigit(this.ch))
				{
					strAttrName.append(this.ch);
					this.cAttr = ETagType.ATTR_NAME;
				}
				else if(this.ch == ' ')
				{
					// ignore blank
					;
				}
				else if(this.ch == '/')
				{
					//this.endClosed = true;
					this.preBuildToken.setEndClosed(true);
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					return ;
				}
			}
			else if(this.cAttr == ETagType.ATTR_NAME)
			{
				
				if(Character.isLetterOrDigit(this.ch))
				{
					strAttrName.append(this.ch);
				}
				else if(this.ch == ' ')
				{
					this.cAttr = ETagType.ATTR_NAME_END;
				}
				else if(this.ch == '=')
				{
					this.cAttr = ETagType.ATTR_NAME_END_HAVE_VALUE;
					this.addAttr(strAttrName.toString(), this.parseAttrValue());
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					this.addAttr(strAttrName.toString(), null);
					return;
				}
			}
			else if(this.cAttr == ETagType.ATTR_VALUE_END)
			{
				if(Character.isLetterOrDigit(this.ch))
				{
					//this.cAttr = ETagType.ATTR_NAME;
					this.setAttr(ETagType.ATTR_NAME);
//					this.page.setCursor(-1);	// to be removed
					this.parseAttrName(this.ch + "");	// recursive parsing
					/*
					if(this.cAttr == ETagType.TAG_END)
					{
						return ;
					}
					*/
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					return;
				}
			}
			else if(this.cAttr == ETagType.ATTR_NAME_END)
			{
				if(this.ch == '=')
				{
					this.cAttr = ETagType.ATTR_NAME_END_HAVE_VALUE;
					this.addAttr(strAttrName.toString(), this.parseAttrValue());
				}
				else if(this.ch == '>')
				{
					this.cAttr = ETagType.TAG_END;
					this.addAttr(strAttrName.toString(), null);
					return ;
				}
				else if(Character.isLetterOrDigit(this.ch))
				{
					this.addAttr(strAttrName.toString(), null);
//					this.page.setCursor(-1);	// to be removed
					this.setAttr(ETagType.ATTR_NAME);
					this.parseAttrName(this.ch + "");
				}
			}	
		}
	}
	
	private String parseAttrValue()
	{
		boolean isSqOpen = false, isDqOpen = false;
		StringBuffer sb = new StringBuffer();
		
		while((this.ch = this.pageSource.getNextChar()) != (char)PageSource.EOS)
		{
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
					this.setAttr(ETagType.TAG_END);
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
	
	private void addAttr(String name, String value)
	{
		this.preBuildToken.addAttribute(new TagAttribute(name, value)); 
	}
	
	private void setAttr(ETagType tag)
	{
		//System.out.println(">> change attr : " + tag);
		this.cAttr = tag;
	}
		
	public static void main(String ... v){
		// verify pagesource
		StringBuffer source = new StringBuffer("<apt href=http://www.naver.com alink=red checked nochekced = no>" +
				"<apt2 href=http://www .naver.com alink=red checked>");
		PageSource ps = new PSstringBuffer(source);
		while(ps.hasNextChar()){
			System.out.print(ps.getNextChar() + "|");
		}
		// ~ versify pagesource
		ps.setCursorPosition(0);	// init cursor
		Logging.print(Logging.DEBUG, "Test!");
				
		PluginParserToken ppt = new PluginParserToken(ps);
		TokenTag token = ppt.parse();
		Logging.print(Logging.DEBUG, "[Result]" + token.toString());
		Logging.print(Logging.DEBUG, "[Result]" + token.toHtml());
	}

	@Override
	public String getEntryString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
