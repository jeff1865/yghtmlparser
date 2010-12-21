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

package me.yglib.htmlparser.parser.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import me.yglib.htmlparser.CommonException;
import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.datasource.impl.ResourceManager;
import me.yglib.htmlparser.lexer.Lexer;
import me.yglib.htmlparser.lexer.impl.LexerImpl;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.parser.NodeFilter;
import me.yglib.htmlparser.util.Logging;

public class HtmlDomBuilder {
	
	private PageSource ps = null;
	private NodeFilter nFilter = null;
	//private CheckStack stack = null;
	
	public HtmlDomBuilder(PageSource ps, NodeFilter nodeFilter){
		this.ps = ps;
		this.nFilter = nodeFilter;
	}
	
	public HtmlDomBuilder(PageSource ps){
		this.ps = ps;
		this.nFilter = new NodeFilter(){	// Defualt Filter : apply full token to DOM
			@Override
			public boolean isNeededNode(Token token) {
				return true;
			}
		};
	}
	
	/**
	 * In the case of like <br>, it would be processed like <br/>
	 * @return
	 */
	public List<Node> build() throws CommonException {
		List<Node> rootNodes = new ArrayList<Node>();
		try{
			CheckStack stack = new CheckStack();
			
			Lexer lexer = new LexerImpl(this.ps);
			Token tk = null;
			NodeImpl rNode = null;
			while(lexer.hasNextToken() && (tk = lexer.getNextToken()) != null){
				//Logging.debug("Node :" + tk);
				rNode = new NodeImpl(tk);
				
				if(tk instanceof TokenTag){
					// check filtered node
					if(this.nFilter != null && !this.nFilter.isNeededNode(tk)) continue;  
					
					TokenTag tkTag = (TokenTag)tk;
					if(tkTag.isClosedTag()){	// pop TagNode
						stack.checkNode2(rNode);
					} else {	// push TagNode
						if(stack.peek() == null){
							rootNodes.add(rNode);
						} else {
							Node pNode = stack.peek();
							rNode.setParentNode(pNode);
						}
						
						if(!tkTag.isClosedTag()){
							if(this.nFilter != null){
								if(nFilter.isNeededNode(tk)) stack.push(rNode);
							} else {
								stack.push(rNode);
							}
						}
					}
					
				} else if(tk instanceof TokenText){
					
					if(stack.peek() == null){ 
						Logging.print(Logging.ERROR, "Invalid source ..");
					} else {
						rNode.setParentNode(stack.peek());
					}
					
				} else {
					Logging.debug("Ignored ..");
				}
			}
		} catch(Exception e){
			throw new CommonException("Cannot build DOM tree, " + e.getMessage());
		}
		return rootNodes;
	}
	
	public static void displayNode(List<Node> nodes){
		if(nodes != null)
		{
			for(Node node : nodes){
				System.out.println("NODE >" + node.getToken());
				displayNode(node.getChildren());
			}
		}
	}
	
	public static void main(String ... v){
		PageSource bufPs = null;
		try {
			bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.nate.com/").toURI(), 3000);
			//bufPs = ResourceManager.getLoadedPage(new File("test\\naver.html"));
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
		
		displayNode(rootNode);
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
