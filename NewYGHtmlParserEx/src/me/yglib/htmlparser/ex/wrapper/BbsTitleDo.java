package me.yglib.htmlparser.ex.wrapper;

public class BbsTitleDo {
	private String title;
	private String dateTime;
	private String author;
	private int replyCount;
	private String anchor_url;
	
	public BbsTitleDo() {}
	
	public BbsTitleDo(String title, String dateTime, String author, int replyCount) {
		this.title = title;
		this.dateTime = dateTime;
		this.author = author;
		this.replyCount = replyCount;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	
	
}
