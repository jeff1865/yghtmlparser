package me.yglib.htmlparser.ex.wrapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.RawSourceWrapper;
import me.yglib.htmlparser.datasource.impl.IntResManager;
import me.yglib.htmlparser.ex.node.NodePathUtil;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class ProtoypeWrapper {
	
	public static void main(String ... v) {
		PageSource bufPs = null;
		String url = "http://www.asiae.co.kr/news/view.htm?idxno=2010082112172067284";
		url = "http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park&wr_id=4163961";
		url = "http://gall.dcinside.com/board/lists/?id=car_new";
		url = "http://m.dcinside.com/list.php?id=car_new";
		//User-Agent:Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19
		try {
//			bufPs = IntResManager.loadStringBufferPage(new URL(url).toURI(), 3000);
			Map<String,String> pros = new HashMap<String, String>();
			pros.put("User-Agent", 
					"Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
			bufPs = IntResManager.loadStringBufferPage(new URL(url).toURI(), 3000, pros); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		String src = bufPs.getSubString(0, bufPs.size());
		System.out.println(src);
		System.out.println("Size of Source :" + bufPs.size());
//		while(bufPs.hasNextChar()) {
//			System.out.print(bufPs.getNextChar());
//		}
		
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = null;
		try {
			rootNode = domBuilder.build();
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		displayNode(rootNode);
		System.out.println(" -> Root Node Count :" + rootNode.size());
		
		displayNode(rootNode);
		
		String pathRule = "html[1]/body[3]/div[0]/div[2]/section[0]/div[0]/";
		pathRule = "html[1]/body[10]/footer[0]/footer[0]/div[1]/div[0]/ul[0]/li[0]/a[0]/";
		
		Node targetNode = NodePathUtil.getNode(rootNode.get(0), pathRule, 1);
			
		ArrayList<Node> lNode = new ArrayList<Node>(); 
		lNode.add(targetNode);
		System.out.println("-------------------------------------------");
		displayNode(lNode);
		
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
	
}
