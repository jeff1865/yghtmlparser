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

package me.yglib.htmlparser.lexer;

import me.yglib.htmlparser.Token;
import me.yglib.htmlparser.datasource.PageSource;

/**
 * 
 * YG HtmlParser Project
 * @author Young-Gon Kim (gonni21c@gmail.com)
 * 2009. 09. 12
 */
public interface Lexer {
	/**
	 * user can add custom token processor
	 * @param tpp instance implementing TokenProcPlugin interface
	 */
	public void addTokepProcPluging(TokenProcPlugin tpp);
	public PageSource getPage();
	public Token getNextToken();
	public boolean hasNextToken();
}
