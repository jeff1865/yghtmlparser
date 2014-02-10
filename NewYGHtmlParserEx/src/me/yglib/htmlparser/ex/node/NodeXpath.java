package me.yglib.htmlparser.ex.node;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.IntResManager;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class NodeXpath {
	
	public Node getNode(Node rootNode, String pathRule, int cutEnd) {
		PathRule rulePath = new PathRule(pathRule);
		List<PathRuleElement> paeLst = rulePath.getPathRule();
		
		Node tempNode = rootNode;
		String tagName = null;
		for(PathRuleElement pre : paeLst){
			if( paeLst.indexOf(pre) == (paeLst.size() - cutEnd)) break;
			tagName = pre.getStrTagName();
			if(tempNode.getToken() instanceof TokenTag){
				TokenTag tTag = (TokenTag) tempNode.getToken();
				if(! tTag.getTagName().equalsIgnoreCase(tagName)){
					//System.out.println("tTag:tagName=" tTa );
					return null;
				}
			}
			tempNode = tempNode.getChildren().get(pre.getIndex());
		}
		
		return tempNode;
	}
	
	public List<Node> getMatchedNode(Node rootNode, String xPath) {
		PathRule rulePath = new PathRule(xPath);
		List<PathRuleElement> lstPath = rulePath.getPathRule();
		
		Node tempNode = rootNode;
		String tagName = null;
		for(PathRuleElement pre : lstPath) {
			//System.out.println("--->>>" + elem.toString());
			tagName = pre.getStrTagName();
			
			if(tempNode.getToken() instanceof TokenTag) {
				TokenTag tTag = (TokenTag) tempNode.getToken();
				Node pnode = tempNode.getParent();
				;
				;
			}
			
			tempNode = tempNode.getChildren().get(pre.getIndex());
		}
		
		return null;
	}
	
	public Node getDomNode(String url) {
		PageSource bufPs = null;
		
		try {
			Map<String,String> pros = new HashMap<String, String>();
			pros.put("User-Agent", 
					"Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
			bufPs = IntResManager.loadStringBufferPage(new URL(url).toURI(), 3000, pros); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = null;
		try {
			rootNode = domBuilder.build();
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		return rootNode.get(0);
	}
	
	public static void displayNode(List<Node> nodes){
		if(nodes != null)
		{
			for(Node node : nodes){
				System.out.println("NODE >" + node.getToken());
				displayNode(node.getChildren());
			}
		}
	}
	
	public static void main(String ... v) {
		NodeXpath xPath = new NodeXpath();
		String url = "http://m.dcinside.com/view.php?id=car_new&no=2658826&page=1";
		
		Node rootNode = xPath.getDomNode(url);
		
		String pathRule = "html[1]/body[5]/div[0]/div[3]/section[1]/div[0]/div[1]";
		Node filteredtNode = xPath.getNode(rootNode, pathRule, 0);
		
		ArrayList<Node> lstNodes = new ArrayList<Node>();
		lstNodes.add(filteredtNode);
		displayNode(lstNodes);
		
		
		System.out.println("------------ New XPath -------------");
		String xPathRule = "html[1]/body[5]/div[0]/div[3]/section[*]/div[0]/div[*]";
		
		List<Node> lstMatchedNode = xPath.getMatchedNode(rootNode, xPathRule);
		if(lstMatchedNode != null) {
			for(Node node : lstMatchedNode)	{
				;
			}	
		}
	}
	
}
