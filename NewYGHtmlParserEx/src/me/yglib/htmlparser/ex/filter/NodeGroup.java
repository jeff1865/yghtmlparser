package me.yglib.htmlparser.ex.filter;

import java.util.ArrayList;
import java.util.List;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.TokenText;
import me.yglib.htmlparser.parser.Node;

public class NodeGroup {
	
	private ArrayList<Node> lstNodes = null;
	private boolean cleaned = false;
	private ArrayList<String> lstGrp = null; 
	
	public boolean isCleaned() {
		return cleaned;
	}

	public void setCleaned(boolean cleaned) {
		this.cleaned = cleaned;
	}

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
	
	//TODO 문자수 세기
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
	
	//TODO 문장수 세기
	public int getSentCount(){
		int sum = 0;
		
		for(Node node : this.lstNodes){
			Token token = node.getToken();//count text
			if(token instanceof TokenText){
				TokenText tt = (TokenText)token;
				if(tt.getValueText() != null && tt.getValueText().contains(" "))
					sum += SentenceAnalyzer.getSeparatedSentence(tt.getValueText()).size();
			}
		}
		
		return sum;
	}
	
	public int getCountContainValidCont(){
		int sum = 0;
		
		for(Node node : this.lstNodes){
			Token token = node.getToken();//count text
			if(token instanceof TokenText){
				TokenText tt = (TokenText)token;
				if(tt.getValueText().contains(" "))
					sum ++;
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
			strRet += node.getToken().getIndex() + "__" + node.getToken().toString() + "\n";
		}
		return strRet;
	}
}
