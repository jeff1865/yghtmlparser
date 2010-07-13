package me.yglib.htmlparser.parser;

import java.util.List;

import me.yglib.htmlparser.Token;

/**
 * Extend token object by adding tree concept
 * 
 * YG HtmlParser Project
 * @author Young-Gon Kim (gonni21c@gmail.com)
 * 2009. 09. 12
 */
public interface Node {
		
	public Node getParent();
	public List<Node> getChildren();
	public Node getNextSibling();
	
	public Token getToken();
}
