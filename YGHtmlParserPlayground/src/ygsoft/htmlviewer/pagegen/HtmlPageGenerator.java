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

package ygsoft.htmlviewer.pagegen;

import java.io.File;
import java.net.URL;
import java.util.*;

import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.ResourceManager;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.impl.HtmlDomBuilder;

public class HtmlPageGenerator {
	
	private List<Node> rootNodes = null;
	
	public HtmlPageGenerator(List<Node> rootNodes){
		this.rootNodes = rootNodes;
	}
	
	public String getFullPage(String initText){
		
		return initText;
	}
	
	
	public void nodeSearchTest(){
		deg("start Root node size :" + this.rootNodes.size());
	}
	
	public static void deg(String msg){
		System.out.println(">" + msg);
	}
	
	public static void main(String ... v){
		PageSource bufPs = null;
		try {
			//bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.nate.com/").toURI(), 3000);
			bufPs = ResourceManager.getLoadedPage(new File("test\\naver.html"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		HtmlDomBuilder domBuilder = new HtmlDomBuilder(bufPs);
		List<Node> rootNode = domBuilder.build();
		
		HtmlPageGenerator hpg = new HtmlPageGenerator(rootNode);
		hpg.nodeSearchTest();
		
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
