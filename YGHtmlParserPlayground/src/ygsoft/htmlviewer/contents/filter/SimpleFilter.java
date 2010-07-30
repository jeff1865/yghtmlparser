package ygsoft.htmlviewer.contents.filter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.ResourceManager;
import me.yglib.htmlparser.parser.*;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class SimpleFilter {
	
	private Node rootNode = null;
	
	public SimpleFilter(Node rootNode){
		this.rootNode = rootNode;
	}
	
	public List<String> getFilteringRule(){
		ArrayList<String> lstRule = new ArrayList<String>();
		
		return lstRule;
	}
	
	private static boolean isHyperLinked(Node node){
		Node parentNode = node;
		Token token = null;
		
//		System.out.println("===> CheckNODE: " + parentNode.getToken() 
//				+ " --- " + parentNode.getParent());
		while((parentNode = parentNode.getParent()) != null){
//			System.out.println(" == NODE NAME >" + parentNode);
			
			if((token = parentNode.getToken()) instanceof TokenTag){
				TokenTag tTag = (TokenTag)token;
				//System.out.println(" == TAG NAME >" + tTag.getTagName());
				if(tTag.getTagName().equalsIgnoreCase("a"))
					return true;
			}
		}
		return false;
	}
	
	public String getFilteredText(String rule){
		String strFilteredText = "";
		
		return strFilteredText;
	}
	
	public static void displayNode(List<Node> nodes){
		if(nodes != null)
		{
			for(Node node : nodes){
				if(isHyperLinked(node))
					System.out.println("Linked NODE >" + node.getToken());
				else
					;//System.out.println("NODE >" + node.getToken());
				
				displayNode(node.getChildren());
			}
		}
	}
	
	public static void main(String ... v){
		PageSource bufPs = null;
		try {
			//bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.nate.com/").toURI(), 3000);
			bufPs = ResourceManager.getLoadedPage(new File("testRes\\test.html"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = domBuilder.build();
		
		displayNode(rootNode);
		
		;
	}
}
