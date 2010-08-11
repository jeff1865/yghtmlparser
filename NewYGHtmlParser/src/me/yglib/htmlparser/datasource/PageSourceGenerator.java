package me.yglib.htmlparser.datasource;

import java.net.URL;

public interface PageSourceGenerator {
	public PageSource getPageSource(URL url, int timeout);
}
