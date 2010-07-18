package me.yglib.htmlparser;

public interface TokenIgnoreTagValue extends Token{
	public TokenTag getParentTag();
	public String getValue();
}
