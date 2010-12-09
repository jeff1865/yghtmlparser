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

package me.yglib.htmlparser.ex.filter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.ex.node.*;

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
	private static int minChar = 20, minSent = 4;
	
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
			// Error : http://art.chosun.com/site/data/html_dir/2010/04/19/2010041900721.html
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
		url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082215175501055";
		//url = "http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park&wr_id=4094745";
		//url = "http://www.bobaedream.co.kr/board/bulletin/view.php?code=battle&No=230085";	// not stable
		url = "http://www.etnews.co.kr/news/detail.html?id=201012050031&mc=m_012_00001";
		try {
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
		// 1. Filter - Unlinked Node
		List<Node> lstFilter = testFilter.getUnlinkedTextNode();
				
		for(Node node : lstFilter){
			System.out.println("UnLinked Node:" + node.getToken().getIndex() + ">" + node);
		}
		
//		testFilter.analyzeUnLinedNodes(lstFilter);	//1st Filter
		
		// Depth Filter
//		List<NodeRange> lstGroupByDepth = getGroupByDepth(lstFilter);
//		
//		System.out.println("Filtered Group :" + lstGroupByDepth.size());
//		for(NodeRange nodeRange : lstGroupByDepth){
//			System.out.println("S>" + nodeRange.startNode + "\nE>" + nodeRange.endNode);
//		}
		
		
		// Conjuction Filter
		
		// 2 . Filter - 인접그룹 필터
		System.out.println("==============================2rd===============================");
		List<List<Node>> groupedList = getAdjacentDepthGroupedList(lstFilter);	//2nd Filter
		showGroupNode(groupedList);
		
		
		System.out.println("----------------- 3rd -----------------");
		
		// 3. 인접노드 그룹화
		List<NodeGroup> lstNodeGrp = convertNodeGroup(groupedList);
		int i = 0;
		for(NodeGroup ng : lstNodeGrp){
			System.out.println("3rd-----------("+i++ + ")-----------------");
			System.out.println(ng);
		}
		
		// 4. Filter - 동일 레벨, Depth 분석
		regroupNode(lstNodeGrp);
		
		i = 0;
		for(NodeGroup ng : lstNodeGrp){
			System.out.println("4rd-----------("+i++ + ")-----------------");
			System.out.println(ng);
		}
		
		// 4. Counter
		List<NodeGroup> extRules = getExtRules(lstNodeGrp);
		i = 0;
		for(NodeGroup ng : extRules){
			System.out.println("5rd-----------("+i++ + ")-----------------");
			System.out.println(ng);
		}
		
		List<NodeRange> finalRes = getNodeRangeList(extRules);
		System.out.println("Result count : " + finalRes.size());
		for(NodeRange nr : finalRes)
			System.out.println("-->" + nr);
		
		
		//System.out.println("--> Is Same Logic :" + isSameLogic("/a[0]/br[2]", "/a[0]/br[3]"));
//		String strRule = "html[10]/body[7]/div[0]/div[0]/div[2]/div[2]/div[11]/div[0]/ul[0]/li[0]/span[0]/";
//		Node node = getNode(rootNode.get(0), strRule, 1);
//		
//		System.out.println("ORI:" + strRule);
//		System.out.println("Converted:" + getRulePath(node));
//		
//		System.out.println("-------------------[FILTERED TEXT]---------------------");
//		String ft = getTextOnly(node);
//		System.out.println(ft);
	}
	
	private static List<NodeRange> getNodeRangeList(List<NodeGroup> lstGroup){
		int shortChar = 17, shortSent = 1;
		List<NodeRange> lstNodeRange = new ArrayList<NodeRange>();
		
		int cntNode = 0;
		for(NodeGroup ng : lstGroup){
			cntNode = ng.getCountContainValidCont();
			if(cntNode > 0 && ng.getCharCount()/cntNode > shortChar 
					&& ng.getSentCount()/cntNode >= shortSent){
				List<Node> nodes = ng.getNodes();
				lstNodeRange.add(new NodeRange(nodes.get(0), nodes.get(nodes.size()-1)));
			}
		}
		
		return lstNodeRange;
	}
	
	
	// 첫단의 값과 끝단의 값 차이 / 전체노드수 분포도
	private boolean isRegularPatterned(NodeGroup nodeGroup){
		
		
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
	
//TODO count sentences, count chars, group by depth
	public static List<NodeGroup> getExtRules(List<NodeGroup> lstNodeGrp){
		List<String> lstRules = new ArrayList<String>();
		
		Counter<String> counter = new Counter<String>();
		int totalChar = 0;
		int totalSent = 0;
		// 1. Maximum pointed rule
		for(NodeGroup ng : lstNodeGrp){
			totalChar += ng.getCharCount();
			totalChar += ng.getSentCount();
			
			List<String> groups = ng.getGroups();
			for(String idGrp : groups){
				counter.addKey(idGrp);
			}
		}
		
		counter.printResult();
		//TODO 문장수 분석기, Tree병합기 -> 최종 Tree 후보군 추출
		
		// grouping the same depth and logic
		ArrayList<NodeGroup> resGroup = new ArrayList<NodeGroup>();
		
		for(int i = 0;i<lstNodeGrp.size()-1;i++){
			NodeGroup ngFront = lstNodeGrp.get(i);
			if(ngFront.isCleaned()) continue;
			ngFront.setCleaned(true);
			
			if(ngFront.getCharCount() > 10 
					&& ngFront.getSentCount() > 2 
					//){
					|| counter.getCountVal(ngFront.getGroups().get(0)) > 1){
				NodeGroup reGrp = new NodeGroup();
				reGrp.setLogicGroup(ngFront.getGroups().get(0));
				for(Node node : ngFront.getNodes())
					reGrp.addNode(node);
				
				for(int j=i+1;j<lstNodeGrp.size();j++){
					NodeGroup ngRear = lstNodeGrp.get(j);
					if(!ngRear.isCleaned() &&
							ngFront.getGroups().get(0).equals(ngRear.getGroups().get(0))){
						ngRear.setCleaned(true);
						for(Node node : ngRear.getNodes())
							reGrp.addNode(node);
					}
				}
				
				resGroup.add(reGrp);
			}
		}
		
		
		
		
		//Node node 
		
		
		// 본문 영역과 비본문 영역(덧글)추출기능
		
		
		
		
		
		
		return resGroup;
	}
	
	// set Logic group
	public static void regroupNode(List<NodeGroup> lstNodeGrp){
		Logging.debug("Filter ReGroup ..");
		String strGrpID = "grp";
		
		NodeGroup ngCur = null, ngRear = null;
		Node ndCur = null, ndRear = null;
		
		for(int i=0;i<lstNodeGrp.size();i++){
			//strGrpID = "grp_" + i;
			ngCur = lstNodeGrp.get(i);
			ndCur = ngCur.getNodes().get(0);
			
			strGrpID = SimpleFilter.getRulePath(ndCur);
			ngCur.setLogicGroup(strGrpID);
			
			for(int j=i+1;j<lstNodeGrp.size();j++){
				ngRear = lstNodeGrp.get(j);
				ndRear = ngRear.getNodes().get(0);
				// is depth same?
				if(getDepth(getRulePath(ndCur)) == getDepth(getRulePath(ndRear))){
					// is path same logic?
					if(isSameLogic(getRulePath(ndCur), getRulePath(ndRear))){
						//ngRear.setGroup(strGrpID);
						ngRear.setLogicGroup(strGrpID);
					}
				}
			}
			
		}
	}
	
	private static List<NodeGroup> convertNodeGroup(List<List<Node>> groupedList){
		ArrayList<NodeGroup> lstGrp = new ArrayList<NodeGroup>();
		
		NodeGroup ng = null;
		for(List<Node> lstNode : groupedList){
			ng = new NodeGroup();
			for(Node node : lstNode){
				ng.addNode(node);
			}
			lstGrp.add(ng);
		}
		
		return lstGrp;
	}
	
	
	
	public static void showGroupNode(List<List<Node>> groupedList){
		int i = 0;
		for(List<Node> nodes : groupedList){
			System.out.println(++i + "-------------------------------------------");
			for(Node node : nodes){
				System.out.println(i + ". " + node);
			}
			System.out.println("-------------------------------------------");
		}
	}
	
	// Grouping same depth
	private static List<NodeRange> getGroupByDepth(List<Node> fNodes){
		ArrayList<NodeRange> lstNodes = new ArrayList<NodeRange>();
		
		NodeRange nodeRange = null;
		Node rNode = null, tNode = null;
		for(int i=0;i<fNodes.size();i++){
			rNode = fNodes.get(i);
			int rNodeDepth = getDepth(getRulePath(rNode));
			
			nodeRange = new NodeRange();
			nodeRange.startNode = rNode;
			int tempI = i;
			
			for(int j=i+1;j<fNodes.size();j++){
				tNode = fNodes.get(j);
				//1. check depth
				if(rNodeDepth == getDepth(getRulePath(tNode))){
					System.out.println("yy" + rNode);
					//2. check branch(logical root)
					if(isSameLogic(getRulePath(rNode), getRulePath(tNode))){
						System.out.println("xxxxx" + rNode);
						nodeRange.endNode = tNode;
						tempI = j + 1;
					}
				}
			}
			
			lstNodes.add(nodeRange);
			i = tempI;
		}
		
		return lstNodes;
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
	
	// 2nd Filter : check depth of adjacent NODE
	private static List<List<Node>> getAdjacentDepthGroupedList(List<Node> filteredUnlinkedNodes){
		Logging.debug("---------> Start 2nd Filtering..");
		
		ArrayList<List<Node>> lstNodes = new ArrayList<List<Node>>();
		
		Node rNode = null, tNode = null;	// rt, temp
		int rDepth = -1, tDepth = -1;
		for(int i=0;i<filteredUnlinkedNodes.size();i++){
			rNode = filteredUnlinkedNodes.get(i);
			rDepth = getDepth(getRulePath(rNode));
			
			ArrayList<Node> lstRnode = new ArrayList<Node>();
			lstRnode.add(rNode);
			
			for(int j=i+1;j<filteredUnlinkedNodes.size();j++){
				tNode = filteredUnlinkedNodes.get(j);
				tDepth = getDepth(getRulePath(tNode));
				
				//isSameLogic(getRulePath(rNode), getRulePath(tNode))
				//if(rDepth == tDepth){
				if(rDepth == tDepth && isSameLogic(getRulePath(rNode), getRulePath(tNode))){
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

class NodeRange{
	public Node startNode = null, endNode = null;
	public NodeRange(){}
	public NodeRange(Node sNode, Node eNode){
		startNode = sNode;
		endNode = eNode;
	}
	public String toString(){
		return this.startNode +"("+ this.startNode.getToken().getIndex() + ")" 
		+ "-->" + this.endNode +"("+ this.endNode.getToken().getIndex() + ")";
	}
}