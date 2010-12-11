package me.yglib.htmlparser.ex.node;

import java.util.List;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.parser.Node;

public class NodePathUtil {
	
	public static String getRulePath(Node node){
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
	
	public static Node getNode(Node rootNode, String pathRule, int cutEnd) {
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
	
	public static boolean isSameLogic(String strRuleA, String strRuleB){
		//String strRuleA = getRulePath(nodeA), strRuleB = getRulePath(nodeB);
		String[] sRuleA = strRuleA.split("/");
		String[] sRuleB = strRuleB.split("/");
		
		String strTempA = null, strTempB = null;
		for(int i=0;i<sRuleA.length;i++){
			strTempA = sRuleA[i];
			if(i<sRuleB.length){
				strTempB = sRuleB[i];
				if(strTempA.indexOf("[") > 0 && strTempB.indexOf("[") > 0){
					strTempA = strTempA.substring(0, strTempA.indexOf("["));
					strTempB = strTempB.substring(0, strTempB.indexOf("["));
					
					if(!strTempA.equals(strTempB)) return false; 
				} 
			}
		}
		
		return true;
	}
	
	public static boolean isSameLogic(String strRuleA, String strRuleB, boolean sameLength){
		String[] sRuleA = strRuleA.split("/");
		String[] sRuleB = strRuleB.split("/");
		if(sameLength)
			if(sRuleA.length != sRuleB.length) return false;
		
		return isSameLogic(strRuleA, strRuleB);
	}
	
	// Check node is hyperlinked or not
	public static boolean isHyperLinked(Node node){
		Node parentNode = node;
		Token token = null;
		
		while((parentNode = parentNode.getParent()) != null){
			
			if((token = parentNode.getToken()) instanceof TokenTag){
				TokenTag tTag = (TokenTag)token;
				//System.out.println(" == TAG NAME >" + tTag.getTagName());
				if(tTag.getTagName().equalsIgnoreCase("a"))
					return true;
			}
		}
		return false;
	}
	
	private static String getTextOnly(Node node){
		String retStr = "";
		Token token = node.getToken();
		if(token instanceof TokenText){
			retStr = ((TokenText) token).getValueText();
		}
		
		List<Node> children = node.getChildren();
		if(children != null)
			for(Node nc : children){
				retStr += getTextOnly(nc);
			}
		
		return retStr;
	}
	
	public static int getDepth(String rule){
		String[] split = rule.split("/");
		return split.length;
	}
}
