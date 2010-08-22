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

package ygsoft.htmlviewer.contents.filter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.IntResManager;
import me.yglib.htmlparser.parser.*;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;
import me.yglib.htmlparser.util.Logging;

/**
 * This class only analyze only one page by using defined filter rule
 * - Long unLiked Text
 * - Extract Root Wrapper
 * @author YoungGon (gonni21c@gmail.com)
 *
 */
public class SimpleFilter implements IContentsFilter{
	
	private Node rootNode = null;
	
	public SimpleFilter(Node rootNode){
		this.rootNode = rootNode;
	}
	
	
	
	// Check node is hyperlinked or not
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
	
	private List<Node> getUnlinkedTextNode(){
		ArrayList<Node> alRes = new ArrayList<Node>();
		this.extractUnlinkedTextNode(alRes, rootNode);
		return alRes;
	} 
	
	/**
	 * Exam : Return html/body/table/tr/td(2)/[Text]
	 * @return
	 */
	private String getContetntPath(){
		return null;
	}
	
	private void extractUnlinkedTextNode(List<Node> lstNode, Node rootNode){
		Token token = rootNode.getToken();
		if(token instanceof TokenText){
			if(!isHyperLinked(rootNode)){
				lstNode.add(rootNode);
			}
		}
		
		List<Node> lstCh = rootNode.getChildren();
		if(lstCh != null){
			for(Node node : lstCh){
				this.extractUnlinkedTextNode(lstNode, node);
			}
		}
	}
	
	@Override
	public String getFilteredContents() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String ... v){
		PageSource bufPs = null;
		String url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082112172067284";
		try {
			// Error : http://art.chosun.com/site/data/html_dir/2010/04/19/2010041900721.html
			//bufPs = IntResManager.loadStringBufferPage(new URL("http://art.chosun.com/site/data/html_dir/2010/04/19/2010041900721.html").toURI(), 3000);
			//bufPs = IntResManager.loadStringBufferPage(new URL("http://clien.career.co.kr/cs2/bbs/board.php?bo_table=kin&wr_id=1940283").toURI(), 3000);
			//bufPs = ResourceManager.getLoadedPage(new File("testRes\\naver.html"));
			bufPs = IntResManager.loadStringBufferPage(new URL(url).toURI(), 3000);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = domBuilder.build();
		
		//displayNode(rootNode);
		System.out.println(" -> Root Node Count :" + rootNode.size());
		SimpleFilter testFilter = new SimpleFilter(rootNode.get(0));
		List<Node> lstFilter = testFilter.getUnlinkedTextNode();
		
//		for(Node node : lstFilter){
//			System.out.println("UnLinked Node:" + node.getToken().getIndex() + ">" + node);
//		}
		
		testFilter.analyzeUnLinedNodes(lstFilter);
		
		List<List<Node>> groupedList = getGroupedList(lstFilter);
		int i = 0;
		for(List<Node> nodes : groupedList){
			System.out.println(++i + "-------------------------------------------");
			for(Node node : nodes){
				System.out.println(i + ". " + node);
			}
			System.out.println("-------------------------------------------");
		}
	}
	
	// 2nd Filter
	private static List<List<Node>> getGroupedList(List<Node> filteredNodes){
		Logging.debug("---------> Start 2nd Filtering..");
		
		ArrayList<List<Node>> lstNodes = new ArrayList<List<Node>>();
		
		Node rNode = null, tNode = null;	// rt, temp
		int rDepth = -1, tDepth = -1;
		for(int i=0;i<filteredNodes.size();i++){
			rNode = filteredNodes.get(i);
			rDepth = getDepth(getRulePath(rNode));
			
			ArrayList<Node> lstRnode = new ArrayList<Node>();
			lstRnode.add(rNode);
			
			for(int j=i+1;j<filteredNodes.size();j++){
				tNode = filteredNodes.get(j);
				tDepth = getDepth(getRulePath(tNode));
				
				if(rDepth == tDepth){
					lstRnode.add(tNode);
					i++;
				}
				else {
					break;
				}
			}
			
			lstNodes.add(lstRnode);
		}
		
		return lstNodes;
	}
	
	public static int getDepth(String rule){
		String[] split = rule.split("/");
		return split.length;
	}
	
	public void analyzeUnLinedNodes(List<Node> nodes){
		Logging.debug("-------------- start Analysis ---------------");
		
		for(Node node : nodes){
			System.out.println("UnLinked Node:" + node.getToken().getIndex() + ">" + node +
					"\n " + getRulePath(node));
		}
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
	
	public String getFilteredText(String rule){
		String strFilteredText = "";
		
		return strFilteredText;
	}
	
	public static Node getNode(Node rootNode, String getRule){
		return null;
	}
	
	//Filter Hyper Link Text
	public static void displayNode(List<Node> nodes){
		if(nodes != null)
		{
			for(Node node : nodes){
				if(isHyperLinked(node))
					System.out.println("Linked NODE:"+node.getToken().getIndex()+">" + node.getToken());
				else
					;//System.out.println("NODE >" + node.getToken());
				
				displayNode(node.getChildren());
			}
		}
	}
}
