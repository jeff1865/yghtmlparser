package ygsoft.htmlviewer.contents.filter;

import java.util.ArrayList;
import java.util.List;

import ygsoft.htmlviewer.contents.filter.logic.SentenceAnalyzer;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.parser.Node;

public class NodeGroup {
	
	private ArrayList<Node> lstNodes = null;
	private ArrayList<String> lstGrp = null; 
	
	public NodeGroup(){
		this.lstNodes = new ArrayList<Node>();
	}

	public void addNode(Node node){
		this.lstNodes.add(node);
	}
	
	public List<Node> getNodes(){
		return this.lstNodes;
	}
	
	public void setGroup0(String grpID){
		if(this.lstGrp == null){
			this.lstGrp = new ArrayList<String>();
		}
		this.lstGrp.add(grpID);
	}
	
	public void setLogicGroup(String grpID_rulePath){
		if(this.lstGrp == null){
			this.lstGrp = new ArrayList<String>();
		}
		
		for(String strGrp : this.lstGrp){
			if(SimpleFilter.getDepth(strGrp) == SimpleFilter.getDepth(grpID_rulePath)){
				// is path same logic?
				if(SimpleFilter.isSameLogic(strGrp, grpID_rulePath)){
					return;
				}
			}
		}
		this.lstGrp.add(grpID_rulePath);
	}
	
	public List<String> getGroups(){
		return this.lstGrp;
	}
	
	//TODO ���ڼ� ����
	public int getCharCount(){
		int sum = -1;
		
		for(Node node : this.lstNodes){
			Token token = node.getToken();//count text
			if(token instanceof TokenText){
				TokenText tt = (TokenText)token;
				sum += tt.getValueText().length();
			}
		}
		
		return sum;
	}
	
	//TODO ����� ����
	public int getSentCount(){
		int sum = 0;
		
		for(Node node : this.lstNodes){
			Token token = node.getToken();//count text
			if(token instanceof TokenText){
				TokenText tt = (TokenText)token;
				if(tt.getValueText() != null)
					sum += SentenceAnalyzer.getSeparatedSentence(tt.getValueText()).size();
			}
		}
		
		return sum;
	}
	
	public String toString(){
		String strRet = "";
		if(this.lstGrp != null){
			strRet = "Grp:" + this.lstGrp.size() + ":";
			for(String strGrp : this.lstGrp){
				strRet += strGrp + ",";
			}
			strRet += "\n";
		}
		
		strRet += "==> CharCount :" + this.getCharCount() + ", SentCount :" 
			+ this.getSentCount() + "\n";
		
		for(Node node : this.lstNodes){
			strRet += node.getToken().toString();
		}
		return strRet;
	}
}