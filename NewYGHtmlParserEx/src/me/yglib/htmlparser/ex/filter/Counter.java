package me.yglib.htmlparser.ex.filter;

import java.util.Enumeration;
import java.util.Hashtable;


public class Counter<T> {
	
	private Hashtable<T, Integer> htMain = null;
	
	public Counter(){
		this.htMain = new Hashtable<T, Integer>();
	}
	
	public void addKey(T idCode){
		if(this.htMain.containsKey(idCode)){
			int iTemp = this.htMain.get(idCode);
			this.htMain.put(idCode, new Integer(++iTemp));
		} else {
			this.htMain.put(idCode, 1);
		}
	}
	
	public int getCountVal(T t){
		if(this.htMain.containsKey(t)){
			return this.htMain.get(t);
		}
		return -1;
	}
	
	public void printResult(){
		Enumeration<T> keys = this.htMain.keys();
		
		T strKey = null;
		while(keys.hasMoreElements()){
			int cnt = this.htMain.get(strKey = keys.nextElement());
			System.out.println(cnt + ": " + strKey);
		}
		
	}
}
