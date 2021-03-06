package ygsoft.htmlviewer.contents.filter.logic;

import java.util.ArrayList;
import java.util.List;

public class SentenceAnalyzer {
	
	public static List<String> getSeparatedSentence(String paragraph){
		ArrayList<String> lstSent = new ArrayList<String>();
		String[] split = paragraph.split("\n|\\D\\.\\D|\r");
		for(String s : split){
			s = s.trim();
			if(s.length() > 1){
				lstSent.add(s);
			}
		}
		return lstSent;
	}
	
	
	public static void main(String ... v){
		//..2010-12-04 17:36
		//List<String> ss = getSeparatedSentence("이것은 무엇인가\n알파온은 모던한 감각의 볼륨 있는 디자인을 바탕으로 2.5ℓ L4 엔진과 서스펜션의 조화로 고연비는 물론 중저속 시 뛰어난 가속 성능을 발휘한다... 가격은 3210만원~3690만원이다.");
		List<String> ss = getSeparatedSentence("2010-12-04 17:36");
		for(String str : ss){
			System.out.println("--->" + str);
		}
	}
}
