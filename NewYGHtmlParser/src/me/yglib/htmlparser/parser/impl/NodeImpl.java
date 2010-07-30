package me.yglib.htmlparser.parser.impl;

import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.parser.Node;

public class NodeImpl implements Node{
	
	private Token token = null;
	private Node pNode = null;
	private List<Node> childrenNodes = null;
	
	public NodeImpl(Token tk){
		this.token = tk;
	}
	
	void setParentNode(Node parentNode){
		this.pNode = parentNode;
		((NodeImpl)this.pNode).addChildNode(this);
	}
	
	private void addChildNode(Node node){
		if(this.childrenNodes == null)
			this.childrenNodes = new ArrayList<Node>();
		this.childrenNodes.add(node);
	}
	
	@Override
	public List<Node> getChildren() {
		return this.childrenNodes;
	}

	@Override
	public Node getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getParent() {
		return this.pNode;
	}

	@Override
	public Token getToken() {
		return this.token;
	}
	
	public String toString() {
		return this.token.toString();
	}
}
