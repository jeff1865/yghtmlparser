package me.yglib.htmlparser.datasource.impl;

import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.RawSourceWrapper;
import me.yglib.htmlparser.util.*;


public class PSstringBuffer implements PageSource{
	
	private int curPos = 0;
	private StringBuffer bufferedSource = null;
	
	public PSstringBuffer(StringBuffer sbSource)
	{
		this.bufferedSource = sbSource;
	}
		
	@Override
	public int getCurrentCursorPosition() {
//		if(this.curPos == 0) return curPos;
//		else return curPos - 1;
		return curPos;
	}


	@Override
	public char getNextChar() {
		if(this.curPos < this.bufferedSource.length())
		{
			return this.bufferedSource.charAt(this.curPos++);
		}
		return PageSource.EOS;
	}
	
	@Override
	public char getCurChar() {
		if(this.curPos < this.bufferedSource.length())
		{
			return this.bufferedSource.charAt(this.curPos);
		}
		return 0;
	}

	@Override
	public String getSubString(int start, int end) {
		if(start >= 0 
				&& start <= this.bufferedSource.length() 
				&& end >= 1
				&& end <= this.bufferedSource.length()
				&& start < end)
			return this.bufferedSource.substring(start, end);
		
		return null;
	}


	@Override
	public boolean hasNextChar() {
		if(this.curPos < this.bufferedSource.length())
			return true;
		
		return false;
	}


	@Override
	public void setCursorPosition(int position) {
		this.curPos = position;
	}


	@Override
	public RawSourceWrapper getDataSource() {
		Logging.print(Logging.DEBUG, "This class cannot support sourceWrapper..");
		return null;
	}

	@Override
	public int size() {
		return this.bufferedSource.length();
	}

	@Override
	public boolean moveCursorPosition(int offset) {
		if(this.curPos+offset >= 0 
				&& this.curPos+offset < this.bufferedSource.length())
		{
			this.curPos += offset;
			return true;
		}
		return false;
	}

	@Override
	public char getChar(int charIndex) {
		return this.bufferedSource.charAt(charIndex);
	}

	public static void main(String ... v){
		PSstringBuffer psBuf = new PSstringBuffer(new StringBuffer("0123456789"));
		//System.out.println("->" + psBuf.getSubString(3, 9));
		System.out.println(psBuf);
	}
	
}
