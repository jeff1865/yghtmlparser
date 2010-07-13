package me.yglib.htmlparser.datasource.impl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.util.Logging;

import org.ahmadsoft.ropes.Rope;

/**
 * Need to implements the kind of HttpClient
 * 
 * YG HtmlParser Project
 * 
 * @author Young-Gon Kim (gonni21c@gmail.com) 2009. 09. 12
 */
public class ResourceManager {
	
	public static int defaultStringBufferSize = 100000;
	
	public StringBuffer sBuf = null;
	public Rope rope = null;
	
	public ResourceManager()
	{
		this.sBuf = new StringBuffer();
		this.rope = Rope.BUILDER.build(this.sBuf);
	}
	
	private void loadDataFromURL(URL url, int timeout) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		String ct = conn.getContentType(), charset = "euc-kr";
		if (ct.indexOf("charset=") > 0)
			charset = ct
					.substring("charset=".length() + ct.indexOf("charset="));
		
		conn.setConnectTimeout(timeout);
		conn.setInstanceFollowRedirects(true);
		String contentType = conn.getContentType();

		if (contentType != null && contentType.startsWith("text/html")) {
			InputStreamReader isr = new InputStreamReader(
					conn.getInputStream(), charset);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			this.sBuf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				// System.out.println("download>" + line);
				this.sBuf.append(line + "\r\n");
			}

			this.sBuf.trimToSize();
			br.close();
		} else {
			throw new IOException("Invalid URL :" + url);
		}
	}
	
	private void loadRope(URL url, int timeout) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		String ct = conn.getContentType(), charset = "euc-kr";
		if (ct.indexOf("charset=") > 0)
			charset = ct
					.substring("charset=".length() + ct.indexOf("charset="));

		conn.setConnectTimeout(timeout);
		conn.setInstanceFollowRedirects(true);
		String contentType = conn.getContentType();

		if (contentType != null && contentType.startsWith("text/html")) {
			InputStreamReader isr = new InputStreamReader(
					conn.getInputStream(), charset);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				this.rope.append((line + "\r\n").subSequence(0, line.length() + 2));	
			}

			br.close();
		} else {
			throw new IOException("Invalid URL :" + url);
		}
	}
	
	public static PageSource loadStringBufferPage(URI uri, int timeout) throws IOException
	{
		StringBuffer sb = new StringBuffer(defaultStringBufferSize);
		
		HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();

		String ct = conn.getContentType(), charset = "euc-kr";
		if (ct.indexOf("charset=") > 0)
			charset = ct
					.substring("charset=".length() + ct.indexOf("charset="));
		
		conn.setConnectTimeout(timeout);
		conn.setInstanceFollowRedirects(true);
		String contentType = conn.getContentType();

		if (contentType != null && contentType.startsWith("text/html")) {
			
			InputStreamReader isr = new InputStreamReader(
					conn.getInputStream(), charset);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			//this.sBuf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				//System.out.println("L>" + line);
				sb.append(line + "\r\n");
			}

			//this.sBuf.trimToSize();
			br.close();
		} else {
			throw new IOException("Invalid URL :" + uri);
		}
		
		return new PSstringBuffer(sb);
	}
	
	public static PageSource getLoadedPage(String strUrl, int timeout)
	{
		ResourceManager rm = new ResourceManager();
		try {
			rm.loadRope(new URL(strUrl), timeout);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Rope r2 = rm.rope;
		
		System.out.println("length>"+r2.length() + r2.charAt(1));
		for(int i = 0 ; i< r2.length(); i++)
		{
			System.out.print(r2.charAt(i));
		}
		
		return null;
	}
	
	public static PageSource getLoadedPage(File srcFile){
		StringBuffer sBuf = null;
		BufferedReader br = null;
		
		try {
			sBuf = new StringBuffer(defaultStringBufferSize);
			//br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "UTF-8"));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "UTF-8"));
			String strLine = null;
			while((strLine = br.readLine()) != null){
				//strLine = new String(strLine.getBytes("UTF-8"), "EUC-KR");
				//System.out.println("load>" + strLine);
				sBuf.append(strLine);
			}
						
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		PSstringBuffer psb = new PSstringBuffer(sBuf);
		return psb;
	}
	
	
	public static void main(String...v)
	{
		
		/*
		try {
			Logging.print(Logging.DEBUG, "Start loading..");
			PageSource bufPs = ResourceManager.loadStringBufferPage(new URL("http://www.naver.com/").toURI()
					, 3000);
			//PageSource bufPs = ResourceManager.loadStringBufferPage(new File("d:\\naver.html").toURI(), 3000);
			
			//for(int i=0;i<100;i++)
			//long tm = System.currentTimeMillis();
			
			while(bufPs.hasNextChar())
				System.out.print(bufPs.getNextChar());
			
			Logging.print(Logging.DEBUG, "Loading complete..");
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			//System.out.println("TM>" + (System.currentTimeMillis() - tm));
			Logging.print(Logging.DEBUG, "Loading complete..");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//ResourceManager.getLoadedPage("http://www.naver.com/", 3000);
		/*
		File file = new File("d:\\naver.html");
		long tm = System.currentTimeMillis();
		ResourceManager rm = new ResourceManager();
		try {
			rm.loadDataFromURL(new URL("http://www.naver.com/"), 3000);
			//rm.loadDataFromURL(file.toURI().toURL(), 3000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
			
		Rope r = Rope.BUILDER.build(rm.sBuf);
		System.out.println("length>"+r.length());
		for(int i = 0 ; i< r.length(); i++)
		{
			System.out.print(r.charAt(i));
		}
		
		tm = (System.currentTimeMillis()-tm);
		
		
		file = new File("d:\\naver.html");
		long tm2 = System.currentTimeMillis();
		rm = new ResourceManager();
		try {
			rm.loadRope(new URL("http://www.naver.com/"), 3000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Rope r2 = rm.rope;
		
		for(int i = 0 ; i< r2.length(); i++)
		{
			System.out.print(r2.charAt(i));
		}
		
		System.out.println("length>"+r2.length());
		System.out.println("TM>" + tm);
		System.out.println("TM2>" + (System.currentTimeMillis() - tm2));
		*/
	}
}
