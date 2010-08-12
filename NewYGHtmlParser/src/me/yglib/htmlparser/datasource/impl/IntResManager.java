// =============================================================================
//   YG Html Parser (Rapid Java Html Parser Project)
//   Copyright 2010 Young-Gon Kim (gonni21c@gmail.com)
//   http://ygonni.blogspot.com
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// =============================================================================

package me.yglib.htmlparser.datasource.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import me.yglib.htmlparser.datasource.PageSource;
import me.yglib.htmlparser.util.Logging;

public class IntResManager {

	private static int defaultStringBufferSize = 4096;
	private static String defaultCharSet = "utf-8";

	public IntResManager() {
		;
	}

	public static PageSource loadStringBufferPage(URI uri, int timeout) {
		PageSource retPs = null;

		StringBuffer sb = new StringBuffer(defaultStringBufferSize);
		HttpURLConnection conn = null;

		try {
			conn = (HttpURLConnection) uri.toURL().openConnection();
			conn.setConnectTimeout(timeout);
			conn.setInstanceFollowRedirects(true);
			String ct = conn.getContentType(), charset = "utf-8";
			String contentType = conn.getContentType();

			if (!(contentType != null && contentType.startsWith("text/html")))
				throw new IOException("Invalid URL (NOT text data) :" + uri);

			if (ct.indexOf("charset=") > 0) // HTTP header contains CHARSET
											// attribute
			{
				Logging.print(Logging.DEBUG, ">>> Charset Detected !!");
				charset = ct.substring("charset=".length()
						+ ct.indexOf("charset="));

				InputStreamReader isr = new InputStreamReader(conn
						.getInputStream(), charset);
				BufferedReader br = new BufferedReader(isr);
				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line + "\r\n"); // convert LocalString
				}

				br.close();

				retPs = new PSstringBuffer(sb);
			} else { // In case charInfo doesn't exist
				InputStream is = conn.getInputStream();
				byte[] data = getDataFromStream(is);

				String strConv = null;
				if (isKoreanHtmlData(data)) {
					strConv = new String(data, "euc-kr");
				} else {
					strConv = new String(data, defaultCharSet);
				}
				
				retPs = new PSstringBuffer(new StringBuffer(strConv));
			}
		} catch (IOException e) {
			Logging.print(Logging.ERROR, "Error in getting data..");
		} finally {
			if (conn != null)
				conn.disconnect();
			Logging.print(Logging.DEBUG, "Http Connection was closed ..");
		}

		return retPs;
	}
	
	private static boolean isKoreanHtmlData(byte[] rawData) {
		String entry = "<html";
		String strData = new String(rawData);
		
		// 1. check meta tag
		//TODO temp code : need to change to get DATA by parse
		if(strData.indexOf("charset=euc-kr") > 0 || strData.indexOf("EUC-KR") >0 ){
			System.out.println("KOR DATA DETECTED =======================================");
			return true;
		}
		try{
		int startInx = strData.indexOf(entry);
		int endInx = strData.indexOf(">", startInx);
		Logging.print(Logging.DEBUG, "Filtered >"
				+ strData.substring(startInx, endInx));
		strData = strData.substring(startInx + entry.length(), endInx);

		if (strData.contains("\"ko\"") || strData.contains("\"KO\""))
			return true;
		}catch(Exception e){
			Logging.print(Logging.ERROR, e.getMessage());
			return false;
		}
		return false;
	}

	private static byte[] getDataFromStream(InputStream is) throws IOException {
		ArrayList<Byte> alst = new ArrayList<Byte>();

		int c = 0;
		byte[] buf = new byte[4096];

		while ((c = is.read(buf)) > 0) {
			for (int i = 0; i < c; i++) {
				alst.add(buf[i]); // add data
			}
		}

		byte[] bRet = new byte[alst.size()];
		int i = 0;
		for (byte b : alst) {
			bRet[i++] = b;
		}

		return bRet;
	}

	public static void main(String... v) {
		try {
			//http://www.bobaedream.co.kr/board/bulletin/view.php?code=nnews&No=84117&Answer=9&rtn=/board/bulletin/list.php%3Fcode%3Dnnews%26or_gu%3D10%26or_se%3Ddesc%26s_select%3DSubject%26s_key%3D%26s_cate%3D%26s_selday%3D%26maker_no%3D%26model_no%3D%26level_no%3D%26page%3D1
//http://clien.career.co.kr/cs2/bbs/board.php?bo_table=park 
			PageSource ps = IntResManager.loadStringBufferPage(new URL("http://www.bobaedream.co.kr").toURI(), 3000);
			 
			 while(ps.hasNextChar()){
				 System.out.print(ps.getNextChar());
			 }
			 
//			HttpURLConnection conn = (HttpURLConnection) new URL(
//					"http://www.chosun.com").openConnection();
//			InputStream is = conn.getInputStream();
//
//			byte[] dfs = getDataFromStream(is);
//			for (int i = 0; i < dfs.length; i++) {
//				System.out.print("" + (char) dfs[i]);
//			}
//
//			if (isKoreanHtmlData(dfs)) {
//				System.out.println(">>> This is Korean Page !!");
//				String strFix = new String(dfs, "euc-kr");
//				System.out
//						.println("----------------------------------------------\n"
//								+ strFix);
//			} else {
//				System.out.println(">>> This isn't Korean Page !!");
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
