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
import java.util.*;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.util.Logging;

public class CheckStack {
	
	private Vector<Node> v_data = null;
	private int v_pointer = 0;
	private HashSet<String> hs_expTagName = null;
	
	public CheckStack()
	{
		this.v_data = new Vector<Node>();
		this.hs_expTagName = new HashSet<String>();
		
		this.addIgnoredTagName("br");
		this.addIgnoredTagName("BR");
		this.addIgnoredTagName("hr");
		this.addIgnoredTagName("HR");
	}
	
	public void addIgnoredTagName(String noneTreeExpTagName){
		this.hs_expTagName.add(noneTreeExpTagName);
	}
	
	public boolean empty()
	{
		if(this.v_data.size() == 0) return true;
		return false;
	}
	
	public void push(Node node)
	{
		Token token = node.getToken();
		if(token instanceof TokenTag){
			TokenTag tTag = (TokenTag)token;
			if(this.hs_expTagName.contains(tTag.getTagName()))
				return;
		}
		
		this.v_data.add( node);
	}
	
	public Node pop()
	{
		Node node = this.v_data.lastElement();
		this.v_data.removeElementAt(this.v_data.size()-1);
		return node;
	}
	/**
	 * get the top of data in the stack
	 * @return
	 */
	public Node peek()
	{
		if(this.v_data.size() > 0) 
			return this.v_data.get(this.v_data.size() - 1);
		return null;
	}
	
	/**
	 * Check Stack Mode2 : like <br> as like <br/>
	 * @param inputTagNode
	 * @return
	 */
	public Node checkNode2(Node inputTagNode)
	{
		TokenTag inputTkTag = (TokenTag)inputTagNode.getToken();
		
		Node retNode = null;
		int index = 0;
		Node popNode =null; 
		int count = 0;
		
		while((popNode = this.getNode(index++)) != null)
		{
			if(popNode.getToken() instanceof TokenTag)	// Check node whether TAG or not
			{
				TokenTag popTagToken = (TokenTag)popNode.getToken();
				if(popTagToken.getTagName().equalsIgnoreCase(inputTkTag.getTagName()))
				{
					for(int i=0;i<=count;i++)
					{
						retNode =  this.pop();
					}
					break;
				}
				else
				{	count++;				
					continue;
				}
				
			}
			else
			{
				Logging.print(Logging.ERROR, "Invalid TAG..");
			}
			
		}
		//this.display();
		return retNode;
	}
	
	
	/**
	 * if input value is out of range, return null 
	 * @param offset '0' is current value
	 * @return
	 */
	public Node getNode(int offset)
	{
		int index =this.v_data.size() -1 - offset;
		if(index < 0) return null;
		return this.v_data.get(index);
	}
	
	public void display()
	{
		Logging.debug("---------[ONLY Debug]----------");
		for(Node node : this.v_data)
		{
			if(node.getToken() instanceof TokenTag)
			{
				//TagNode tNode = (TagNode)node;
				TokenTag tkTag = (TokenTag)node.getToken();
				Logging.debug("|" + tkTag.getTagName() + "\t|");
			}
			else
			{
				Logging.debug("<"+node.getClass().getCanonicalName()+">");
			}
		}
		Logging.debug("---------[/ONLY Debug]----------");
	}
}