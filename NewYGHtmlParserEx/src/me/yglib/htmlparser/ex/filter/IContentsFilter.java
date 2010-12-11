package me.yglib.htmlparser.ex.filter;

import java.util.List;

public interface IContentsFilter {
	//public String getFilteredContents();
	public List<NodeGroup> getExtractedNodeGroups();
}
