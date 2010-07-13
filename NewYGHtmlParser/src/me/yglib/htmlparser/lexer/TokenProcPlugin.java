package me.yglib.htmlparser.lexer;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;

public interface TokenProcPlugin {
	public String getEntryString();
	public <T extends Token> T parse() throws CommonException;
}
