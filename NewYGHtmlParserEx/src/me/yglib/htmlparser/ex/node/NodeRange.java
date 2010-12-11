package me.yglib.htmlparser.ex.node;

import me.yglib.htmlparser.parser.Node;

public class NodeRange{
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