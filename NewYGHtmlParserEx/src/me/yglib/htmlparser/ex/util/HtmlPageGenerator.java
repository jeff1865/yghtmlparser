// =============================================================================
//   YG Html Parser (Rapid Java Html Parser Project)
//   Copyright 2010 Young-Gon Kim (gonni21c@gmail.com)
//   http://ygonni.blogspot.com
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// =============================================================================

package me.yglib.htmlparser.ex.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.IntResManager;
import me.yglib.htmlparser.datasource.impl.ResourceManager;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class HtmlPageGenerator {
	
	private List<Node> rootNodes = null;
	private static final String initHtmlText = "<HTML><HEAD>" +
		"<META http-equiv=Content-Type content=\"text/html; charset=euk-kr\">" +
		"<script type=\"text/javascript\" language=\"JavaScript\">" +
		"function layer_toggle(obj) {" +
        	"if (obj.style.display == 'none') obj.style.display = 'block';" +
        	"else if (obj.style.display == 'block') obj.style.display = 'none';" +
		"}" +
		"</script>" +
		"<style>" +
		".more {border:1px dotted #EFEFEF;margin:5px;background:#F9F9F9;}" +
		".more p {margin:5px;}" +
		"</style>" +
		"</HEAD> "+ 
		"<BODY>";
	
	
	public HtmlPageGenerator(List<Node> rootNodes){
		this.rootNodes = rootNodes;
	}
	
	public String getHtmlStrPage(){
		String strRet = initHtmlText;
		
		strRet = this.makeTableTreeText(this.rootNodes, strRet);
		strRet += "</body></html>";
		return strRet;
	}
	
	private static boolean isHyperLinked(Node node){
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
	
	private String makeTableTreeText(List<Node> nodes, String srcText){
		if(nodes != null){
			//System.out.println("+++ ADD +++");
			srcText += "\n<table border=1 cellspacing=0 cellpadding=0 bordercolor=gray>";
			for(Node node : nodes){
				// write something
				if(node.getToken() instanceof TokenTag)
				{
					
					srcText += "<tr><td bgcolor=#f0ffff><a title=" +getRulePath(node) + ">"  
					+ ((TokenTag)node.getToken()).getTagName() + "</a></td>";
				}
				else  if(node.getToken() instanceof TokenText)
				{
					String text = ((TokenText)node.getToken()).getValueText();
					if(!isHyperLinked(node)) text = "<font color=red>" + text + "</font>";
					
					srcText += "<tr><td bgcolor=#faffff>" + text + "</td>";
					System.out.println("+++++++++++++++++++++++++++++++++");
				}
				else
					srcText += "<tr><td bgcolor=grey>N/R</td></tr>";
				
				if(node.getChildren() != null){
					srcText += "<td>";
					// recursive
					srcText = this.makeTableTreeText(node.getChildren(), srcText);
					srcText += "</td>";
				}
				srcText += "</tr>";
			}
			srcText += "</table>\n";
		}
		//System.out.println("1>" + srcText);
		return srcText;
	}
	
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
	
	public static void nodeSearchTestRc(List<Node> nodes){
		//deg("start Root node size :" + nodes);
		if(nodes != null)
		for(Node node : nodes){
			System.out.println("[NODE]" + node.getToken());
			nodeSearchTestRc(node.getChildren());
		}
	}
	
	public void nodeSearchTest(){
		nodeSearchTestRc(this.rootNodes);
	}
	
	public static void deg(String msg){
		System.out.println(">" + msg);
		
	}
	
	public static void StringToFile(String src, File file){
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.write(src);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String ... v){
		PageSource bufPs = null;
		String url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082112172067284";
		url = "http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park&wr_id=4094745";
		url = "http://www.bobaedream.co.kr/board/bulletin/view.php?code=battle&No=230085";
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
		
		nodeSearchTestRc(rootNode);
		
		HtmlPageGenerator hpg = new HtmlPageGenerator(rootNode);
		String res = hpg.getHtmlStrPage();
		StringToFile(res, new File("d:/test_dom.html"));
		
		System.out.println("Completed!!");
		//System.out.println("========= RESULT ==========================\n" + res);
		
//		System.out.println("Root Node Size :" + rootNode.size());
//		Node node = rootNode.get(0);
//		System.out.println("FirstNode " + node.toString());
//		List<Node> children = node.getChildren();
//		System.out.println("Children :" + children.size());
//		
//		for(Node nd : children)
//			System.out.println(" -subNode :" + nd.toString());
	}
}
