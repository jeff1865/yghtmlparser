package me.yglib.htmlparser.parser.impl;
import java.util.*;

import me.yglib.htmlparser.TokenTag;
import me.yglib.htmlparser.parser.Node;
import me.yglib.htmlparser.util.Logging;

public class CheckStack {
	
	private Vector<Node> v_data;
	private int v_pointer = 0;
	
	public CheckStack()
	{
		this.v_data = new Vector<Node>();
	}
	
	public boolean empty()
	{
		if(this.v_data.size() == 0) return true;
		return false;
	}
	
	public void push(Node node)
	{
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
	
	public Node checkNode2(Node inputTagNode)
	{
		TokenTag inputTkTag = (TokenTag)inputTagNode.getToken();
		
		Node retNode = null;
		int index = 0;
		Node popNode =null; 
		int count = 0;
		while((popNode = this.getNode(index++)) != null)
		{
			if(popNode.getToken() instanceof TokenTag)
			{
				//TagNode tNode = (TagNode)popNode;
				TokenTag popTagToken = (TokenTag)popNode.getToken();
				
				//System.out.println("S:Target="+tNode.getTagName() + ":" + tagNode.getTagName());
				if(popTagToken.getTagName().equalsIgnoreCase(inputTkTag.getTagName()))
				{
					for(int i=0;i<=count;i++)
						retNode =  this.pop();
					// 현재 stack 상태 출력
					//System.out.println("current:" + tagNode);
					//this.display();
					break;
				}
				else
				{	// 현재값과 다른 값이 있는 경우..
					count++;				
					continue;
				}
				
			}
			else
			{
				System.out.println("이상반응..");
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
		Logging.debug("---------");
		for(Node node : this.v_data)
		{
			if(node.getToken() instanceof TokenTag)
			{
				//TagNode tNode = (TagNode)node;
				TokenTag tkTag = (TokenTag)node.getToken();
				//System.out.println("|" + tNode.getTagName() + "\t|");
				Logging.debug("|" + tkTag.getTagName() + "\t|");
			}
			else
			{
				Logging.debug("<"+node.getClass().getCanonicalName()+">");
			}
		}
		Logging.debug("---------");
	}
}