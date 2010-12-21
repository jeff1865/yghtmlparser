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

import me.yglib.htmlparser.CommonException;
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
public class BasicFilter implements IContentsFilter{
	
	private Node rootNode = null;
	private String filterRule = null;
	//private static int minChar = 20, minSent = 4;
	
	public BasicFilter(Node rootNode){
		this.rootNode = rootNode;
	}
		
	public BasicFilter(Node rootNode, String filterRule){
		this.rootNode = rootNode;
	}
	
	// The Most Important Method
	public List<NodeGroup> getExtractedNodeGroups(){
		boolean showDebug = true;
		
		// 1. UnLinked Text Node 추출
		List<Node> lstFilter = FilterUtil.getUnlinkedTextNode(this.rootNode);
		for(Node node : lstFilter){
			if(showDebug) System.out.println("1st UnLinked Node:" + node.getToken().getIndex() + ">" + node);
		}
		
		// 2 . Filter - 인접그룹 필터
		List<List<Node>> groupedList = FilterUtil.getAdjacentDepthGroupedList(lstFilter);	//2nd Filter
		showGroupNode(groupedList);
		
		
		// 3. 인접노드 그룹화  (List -> Group)
		List<NodeGroup> lstNodeGrp = FilterUtil.convertNodeGroup(groupedList);
		int i = 0;
		for(NodeGroup ng : lstNodeGrp){
			if(showDebug) System.out.println("3rd F-----------("+i++ + ")-----------------");
			if(showDebug) System.out.println(ng);
		}
		
		// 4. Filter - 동일 레벨, Depth 분석
		FilterUtil.regroupNode(lstNodeGrp);
		
		i = 0;
		for(NodeGroup ng : lstNodeGrp){
			if(showDebug) System.out.println("4th F-----------("+i++ + ")-----------------");
			if(showDebug) System.out.println(ng);
		}
		
		// 4. Counter
		List<NodeGroup> extRules = getExtRules(lstNodeGrp);
		i = 0;
		for(NodeGroup ng : extRules){
			if(showDebug) System.out.println("5th F-----------("+i++ + ")-----------------");
			if(showDebug) System.out.println(ng);
		}
		
		return extRules;
	}
		
	public static void main(String ... v){
		PageSource bufPs = null;
		String url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082112172067284";
		url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082215175501055";
		url = "http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park&wr_id=4094745";
		//url = "http://www.bobaedream.co.kr/board/bulletin/view.php?code=battle&No=230085";	// not stable
		//url = "http://jakarta.tistory.com/68";
		url = "http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park&wr_id=4163961";
		try {
			bufPs = IntResManager.loadStringBufferPage(new URL(url).toURI(), 3000);
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
		
		//displayNode(rootNode);
		System.out.println(" -> Root Node Count :" + rootNode.size());
		BasicFilter testFilter = new BasicFilter(rootNode.get(0));
		List<NodeGroup> extractedNodeGroups = testFilter.getExtractedNodeGroups();
		
		
		
		
		// 1. Filter - Unlinked Node
//		//System.out.println("--> Is Same Logic :" + isSameLogic("/a[0]/br[2]", "/a[0]/br[3]"));
////		String strRule = "html[10]/body[7]/div[0]/div[0]/div[2]/div[2]/div[11]/div[0]/ul[0]/li[0]/span[0]/";
////		Node node = getNode(rootNode.get(0), strRule, 1);
////		
////		System.out.println("ORI:" + strRule);
////		System.out.println("Converted:" + getRulePath(node));
////		
////		System.out.println("-------------------[FILTERED TEXT]---------------------");
////		String ft = getTextOnly(node);
////		System.out.println(ft);
	}
	
	//TODO count sentences, count chars, group by depth
	public static List<NodeGroup> getExtRules(List<NodeGroup> lstNodeGrp){
		Counter<String> counter = new Counter<String>();
		int totalChar = 0;
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
				
		// 본문 영역과 비본문 영역(덧글)추출기능
		
		return resGroup;
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
			
	//Filter Hyper Link Text
	public static void displayNode(List<Node> nodes){
		if(nodes != null)
		{
			for(Node node : nodes){
				if(NodePathUtil.isHyperLinked(node))
					System.out.println("Linked NODE:"+node.getToken().getIndex()+">" + node.getToken());
				else
					;//System.out.println("NODE >" + node.getToken());
				
				displayNode(node.getChildren());
			}
		}
	}
}

