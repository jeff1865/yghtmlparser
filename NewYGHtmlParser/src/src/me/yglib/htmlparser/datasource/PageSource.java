package me.yglib.htmlparser.datasource;

import me.yglib.htmlparser.CommonException;

/**
 * This class manage following :
 *  - load target resource from website
 *  - manage buffer pointer
 * 
 * YG HtmlParser Project
 * @author Young-Gon Kim (gonni21c@gmail.com)
 * 2009. 09. 12
 */
public interface PageSource {
	
	public static char EOS = (char)-1;	// Signature for 'End_of_Source'
	
	//public void load() throws CommonException;
	public int getCurrentCursorPosition();
	public void setCursorPosition(int position);
	public boolean moveCursorPosition(int offset);
	
	public int size();
	public boolean hasNextChar();
	public char getChar(int charIndex);
	public char getNextChar();
	public char getCurChar();
	public String getSubString(int start, int end);
	
	public RawSourceWrapper getDataSource();	// return String object like String, StringBuffer, Rope
	
}
