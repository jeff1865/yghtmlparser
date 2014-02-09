package me.yglib.htmlparser.ex.wrapper.bbs;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.IntResManager;
import me.yglib.htmlparser.ex.node.NodePathUtil;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class DcWrapper {
	
	public DcWrapper() {
		;
	}
	
	public List<DcBbsTitleDo> getBbsTitles(String url) {
		ArrayList<DcBbsTitleDo> lstTitles = new ArrayList<DcBbsTitleDo>();
		
		PageSource bufPs = null;
		
//		String url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082112172067284";
//		url = "http://m.dcinside.com/list.php?id=programming";
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
		
		// BBS article title list root node
		String pathRule = "html[1]/body[4]/div[1]/section[0]/div[0]/ul[0]";
		Node targetNode = NodePathUtil.getNode(rootNode.get(0), pathRule, 1);
		
		List<Node> subNodes = targetNode.getChildren();
		
		for(Node subNode : subNodes) {
			DcBbsTitleDo wrappedDo = this.getWrappedDo(subNode, null);
			lstTitles.add(wrappedDo);
		}
		
		return lstTitles;
	}
	
	private Node getDomNode(String url) {
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
	
	
	public DcBbsContextDo getContext(String url) {
		Node rootNode = this.getDomNode(url);
		
		String pathRule = "html[1]/body[5]/div[0]/div[3]/section[1]/div[0]/div[1]";
		Node filteredtNode = NodePathUtil.getNode(rootNode, pathRule, 0);
		
		ArrayList<Node> lstNodes = new ArrayList<Node>();
		lstNodes.add(filteredtNode);
		displayNode(lstNodes);
		
		return null;
	}
	
	
	
	private DcBbsTitleDo getWrappedDo(Node liNode, Object rule) {
		DcBbsTitleDo retDo = new DcBbsTitleDo();
//		System.out.println("-----------------------------");
		
		String ruleTitle = "li[0]/a[0]/span[1]";
		
		Node targetNode = NodePathUtil.getNode(liNode, ruleTitle, 0);
//		System.out.println("\tTnode >" + targetNode.toString());
		retDo.setTitle(targetNode.toString());
		
		String ruleCount = "li[0]/a[0]/span[2]/span[0]";
		try {
			targetNode = NodePathUtil.getNode(liNode, ruleCount, 0);
//			System.out.println("\tCount >" + targetNode);
			retDo.setCount(targetNode.toString());
		}catch(Exception e) {
//			System.out.println("\tCount >NA");
			retDo.setCount("NA");
		}
		
		String ruleUname = "li[0]/a[0]/span[4]/span[0]/span[0]";
		targetNode = NodePathUtil.getNode(liNode, ruleUname, 0);
//		System.out.println("\tName >" + targetNode);
		retDo.setAuthor(targetNode.toString());
		
		String vNode = "li[0]/a[0]/span[4]";
		targetNode = NodePathUtil.getNode(liNode, vNode, 0);
		
		if(targetNode.getChildren().size() > 2) {
			String ruleDateTime = "li[0]/a[0]/span[4]/span[2]/span[0]";
			targetNode = NodePathUtil.getNode(liNode, ruleDateTime, 0);
			//System.out.println("\tDatetime >" + targetNode);
			retDo.setDateTime(targetNode.toString());
		} else {
			String ruleDateTime = "li[0]/a[0]/span[4]/span[1]/img[0]/span[0]";
			targetNode = NodePathUtil.getNode(liNode, ruleDateTime, 0);
			//System.out.println("\tDatetime >" + targetNode);
			retDo.setDateTime(targetNode.toString());
		}
		
		String ruleRrl = "li[0]/a[0]";
		
		return retDo;
	}
	
	public static void displayNode(List<Node> nodes){
		if(nodes != null)
		{
			for(Node node : nodes){
				System.out.println("NODE >" + node.getToken() + " ---->" + NodePathUtil.getRulePath(node));
				displayNode(node.getChildren());
			}
		}
	}
	
	public static void main(String ... v) {
		DcWrapper wrapper = new DcWrapper();
		
//		List<DcBbsTitleDo> bbsTitles = wrapper.getBbsTitles("http://m.dcinside.com/list.php?id=programming");
//		for(DcBbsTitleDo titleDo : bbsTitles) {
//			System.out.println("TitleDO :" + titleDo);
//		}
		
		String contUrl = "http://m.dcinside.com/view.php?id=car_new&no=2658826&page=1";
		wrapper.getContext(contUrl);
		
	}
	
	
}
