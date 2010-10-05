package ygsoft.htmlviewer.contents.filter;

import java.util.List;

import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.parser.Node;

public class SiteAnalyzedFilter {
	
	public String getPath(Node node){
		Node rNode = node, pNode = node;
		String strRet = "";
		
		while((rNode.getParent()) != null){
			pNode = rNode.getParent();
			List<Node> lstChrs = pNode.getChildren();
			int nodeIndex = lstChrs.indexOf(rNode);
			strRet = ((TokenTag)(pNode.getToken())).getTagName() + "[" + nodeIndex + "]/" + strRet;
			rNode = rNode.getParent();
		}
		
		return strRet;
	}
	
	// (Rule) ---> (Node)
	public static Node getNode(Node rootNode, String getRule){
		return null;
	}
	
	public static void main(String ... v){
		System.out.println("It's test!"); 
	}
}
