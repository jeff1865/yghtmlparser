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

package me.yglib.htmlparser.lexer.impl;

import me.yglib.htmlparser.*;
import me.yglib.htmlparser.datasource.PageSource;

public class TokenCommentImpl implements TokenComment{
	
	private String strComment;
	private int index = -1;
	
	public TokenCommentImpl(){
		;
	}
	
	public String toString(){
		return "[Comment] " + strComment;
	}
	
	@Override
	public String getCommentText() {
		return this.strComment;
	}
	
	void setCommentText(String str){
		this.strComment = str;
	}

	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	void setIndex(int idx){
		this.index = idx;
	}
	
	@Override
	public int getIndex() {
		return this.index;
	}

	@Override
	public PageSource getPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toHtml() {
		// TODO Auto-generated method stub
		return null;
	}
}
