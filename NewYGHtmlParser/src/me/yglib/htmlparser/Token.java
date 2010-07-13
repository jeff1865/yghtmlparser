package me.yglib.htmlparser;

import me.yglib.htmlparser.datasource.PageSource;

public interface Token extends Cloneable{
	public String toHtml();
	public int getIndex();
	public int getStartPosition();
	public int getEndPosition();
	public PageSource getPage();
}
