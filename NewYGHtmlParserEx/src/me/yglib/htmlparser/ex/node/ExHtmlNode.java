package me.yglib.htmlparser.ex.node;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.IntResManager;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class ExHtmlNode implements Node {
	
	private Node htmlNode = null;
	
	public ExHtmlNode(Node rootNode){
		this.htmlNode = rootNode;
	}
	
	public Node getRawNode(){
		return this.htmlNode;
	}
	
	public String getElementValue(String tagName){
		List<Node> values = new ArrayList<Node>();
		this.getNode(this.htmlNode, tagName, values);
		
		String strRes = "";
		for(Node cn : values){
			if(cn.getToken() instanceof TokenText){
				TokenText tt = (TokenText)cn.getToken();
				strRes += tt.getValueText() + "\n";
			}
		}
		
		return strRes;
	}
		
	public void getNode(Node node, String tagName, List<Node> result){
		if(node.getToken() instanceof TokenTag){
			TokenTag tTag = (TokenTag)node.getToken();
			if(tTag.getTagName().equalsIgnoreCase(tagName)){
				List<Node> children = node.getChildren();
				for(Node cn : children){
					if(cn.getToken() instanceof TokenText)
						result.add(cn);
				}
			}
		}
		
		List<Node> children = node.getChildren();
		if(children != null)
		for(Node cnode : children){
			this.getNode(cnode, tagName, result);
		}
	}
	
	public static void main(String ... v){
		PageSource bufPs = null;
		String url = "http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park&wr_id=4159146";
		try {
			bufPs = IntResManager.loadStringBufferPage(new URL(url).toURI(), 3000);
			//bufPs = ResourceManager.getLoadedPage(new File("testRes\\test.html"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = null;
		try {
			rootNode = domBuilder.build();
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ExHtmlNode exNode = new ExHtmlNode(rootNode.get(0));
		System.out.println(">" + exNode.getElementValue("title"));
	}
	
	@Override
	public Node getParent() {
		return this.htmlNode.getParent();
	}

	@Override
	public List<Node> getChildren() {
		return this.htmlNode.getChildren();
	}

	@Override
	public Node getNextSibling() {
		return this.htmlNode.getNextSibling();
	}

	@Override
	public Token getToken() {
		return this.htmlNode.getToken();
	}
}
