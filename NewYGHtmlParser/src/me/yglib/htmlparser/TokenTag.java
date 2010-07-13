package me.yglib.htmlparser;

import java.util.List;

public interface TokenTag extends Token {
		
	public String getTagName();
	public boolean isEndClosed();
	public boolean isClosedTag();
	public List<TagAttribute> getAttrs();
}
