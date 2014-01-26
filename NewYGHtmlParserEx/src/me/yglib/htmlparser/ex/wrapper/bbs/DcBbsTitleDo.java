package me.yglib.htmlparser.ex.wrapper.bbs;

import me.yglib.htmlparser.ex.wrapper.BbsTitleDo;

public class DcBbsTitleDo extends BbsTitleDo {
	
	public DcBbsTitleDo() {
		super();
	}
	
	public DcBbsTitleDo(String title, String dateTime, String author,
			int replyCount) {
		super(title, dateTime, author, replyCount);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("title:");
		sb.append(this.getTitle());
		sb.append("\t");
		sb.append("count:");
		sb.append(this.getCount()); 
		sb.append("\t");
		sb.append("author:");
		sb.append(this.getAuthor());
		sb.append("\t");
		sb.append("datetime:");
		sb.append(this.getDateTime());
		
		return sb.toString();
	}
}
