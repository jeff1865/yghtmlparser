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
			bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.bobaedream.co.kr/board/bulletin/view.php?code=nnews&No=84117&Answer=9&rtn=/board/bulletin/list.php%3Fcode%3Dnnews%26or_gu%3D10%26or_se%3Ddesc%26s_select%3DSubject%26s_key%3D%26s_cate%3D%26s_selday%3D%26maker_no%3D%26model_no%3D%26level_no%3D%26page%3D1").toURI(), 3000);
			//bufPs = ResourceManager.getLoadedPage(new File("testRes\\naver.html"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = domBuilder.build();
		
		displayNode(rootNode);
		
		;
	}
}
