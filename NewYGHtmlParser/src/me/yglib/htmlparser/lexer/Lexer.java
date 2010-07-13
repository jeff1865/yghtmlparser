package me.yglib.htmlparser.lexer;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.datasource.PageSource;

/**
 * 
 * YG HtmlParser Project
 * @author Young-Gon Kim (gonni21c@gmail.com)
 * 2009. 09. 12
 */
public interface Lexer {
	/**
	 * user can add custom token processor
	 * @param tpp instance implementing TokenProcPlugin interface
	 */
	public void addTokepProcPluging(TokenProcPlugin tpp);
	public PageSource getPage();
	public Token getNextToken();
	public boolean hasNextToken();
}
