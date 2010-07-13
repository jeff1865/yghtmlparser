package me.yglib.htmlparser.parser;

import me.yglib.htmlparser.Token;

public interface NodeFilter {
	public boolean isNeededNode(Token token);
}
