package me.yglib.htmlparser.ex.filter;

import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.ex.node.NodePathUtil;
import me.yglib.htmlparser.ex.node.NodeRange;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.util.Logging;


public class FilterUtil {
	
	// 2nd Filter : check depth of adjacent NODE
	public static List<List<Node>> getAdjacentDepthGroupedList(List<Node> filteredUnlinkedNodes){
		Logging.debug("---------> Start 2nd Filtering..");
		
		ArrayList<List<Node>> lstNodes = new ArrayList<List<Node>>();
		
		Node rNode = null, tNode = null;	// rt, temp
		int rDepth = -1, tDepth = -1;
		for(int i=0;i<filteredUnlinkedNodes.size();i++){
			rNode = filteredUnlinkedNodes.get(i);
			rDepth = NodePathUtil.getDepth(NodePathUtil.getRulePath(rNode));
			
			ArrayList<Node> lstRnode = new ArrayList<Node>();
			lstRnode.add(rNode);
			
			for(int j=i+1;j<filteredUnlinkedNodes.size();j++){
				tNode = filteredUnlinkedNodes.get(j);
				tDepth = NodePathUtil.getDepth(NodePathUtil.getRulePath(tNode));
				
				//isSameLogic(getRulePath(rNode), getRulePath(tNode))
				//if(rDepth == tDepth){
				if(rDepth == tDepth && NodePathUtil.isSameLogic(NodePathUtil.getRulePath(rNode), NodePathUtil.getRulePath(tNode))){
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
	
	public static List<NodeGroup> convertNodeGroup(List<List<Node>> groupedList){
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
			
			strGrpID = NodePathUtil.getRulePath(ndCur);
			ngCur.setLogicGroup(strGrpID);
			
			for(int j=i+1;j<lstNodeGrp.size();j++){
				ngRear = lstNodeGrp.get(j);
				ndRear = ngRear.getNodes().get(0);
				// is depth same?
				if(NodePathUtil.getDepth(NodePathUtil.getRulePath(ndCur)) 
						== NodePathUtil.getDepth(NodePathUtil.getRulePath(ndRear))){
					// is path same logic?
					if(NodePathUtil.isSameLogic(NodePathUtil.getRulePath(ndCur), NodePathUtil.getRulePath(ndRear))){
						//ngRear.setGroup(strGrpID);
						ngRear.setLogicGroup(strGrpID);
					}
				}
			}
			
		}
	}
	
	
	public static List<Node> getUnlinkedTextNode(Node rootNode){
		ArrayList<Node> alRes = new ArrayList<Node>();
		extractUnlinkedTextNode(alRes, rootNode);
		return alRes;
	} 
		
	private static void extractUnlinkedTextNode(List<Node> lstNode, Node rootNode){
		if(rootNode == null){
			System.out.println("+++++++++++ Invalid State :" + rootNode);
			return ;
		}
		
		Token token = rootNode.getToken();
		if(token instanceof TokenText){
			if(!NodePathUtil.isHyperLinked(rootNode)){
				lstNode.add(rootNode);
			}
		}
		
		List<Node> lstCh = rootNode.getChildren();
		if(lstCh != null){
			for(Node node : lstCh){
				extractUnlinkedTextNode(lstNode, node);
			}
			// Error : http://art.chosun.com/site/data/html_dir/2010/04/19/2010041900721.html
		}
	}
	
	public static List<NodeRange> getNodeRangeList(List<NodeGroup> lstGroup){
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
	
	// Grouping same depth
	public static List<NodeRange> getGroupByDepth(List<Node> fNodes){
		ArrayList<NodeRange> lstNodes = new ArrayList<NodeRange>();
		
		NodeRange nodeRange = null;
		Node rNode = null, tNode = null;
		for(int i=0;i<fNodes.size();i++){
			rNode = fNodes.get(i);
			int rNodeDepth = NodePathUtil.getDepth(NodePathUtil.getRulePath(rNode));
			
			nodeRange = new NodeRange();
			nodeRange.startNode = rNode;
			int tempI = i;
			
			for(int j=i+1;j<fNodes.size();j++){
				tNode = fNodes.get(j);
				//1. check depth
				if(rNodeDepth == NodePathUtil.getDepth(NodePathUtil.getRulePath(tNode))){
					System.out.println("yy" + rNode);
					//2. check branch(logical root)
					if(NodePathUtil.isSameLogic(NodePathUtil.getRulePath(rNode), NodePathUtil.getRulePath(tNode))){
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
	
	
}
