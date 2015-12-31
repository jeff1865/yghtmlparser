## Introduction ##

This project is my personal one to research and develop the Java HTML parser.
There are a number of open-source HTML parsers developed by using JAVA. However, Most of those parsers cannot parse some web pages correctly because of an ambiguity of HTML syntax and some of the parsers are too heavy to use.

Developing a HTML parser is definitely differ from XML parser because HTML parser MUST solve and cover the ambiguous syntax by itself. For example, 'br' tag is usually used only opened tag, but in the case of XML, you have to close the tag about opened element. This means that HTML parse tree can be built in various ways.

I have been developing the HTML parser, focusing on the high speed ,light weight to use and exact parsing(building DOM tree) by rule. This parser can be used on the Mobile device which has a low level of H/W power(support) and also used on the some S/W modules like IR engines which need rapid parsing module.

## Feature ##

  * High Speed
  * Low resource(memory) usage
  * High Coverage on the ambiguous syntax
  * Simple, Easy to use
  * High extensibility (support API to extend internal functionality)

## Design(Structure) ##
The following figureis the simple package diagram which doesn't contain implementation packages and some classes seemed not to be important.

<img src='http://yghtmlparser.googlecode.com/files/parseruml.jpg' />

The most important classes :
  * PageSource : get and read effectively data source from remote resource, various resource processor could be used
  * Token : minimal semantic unit, there are also various extended(specialized) tokens like Tag, Text, Script, etc. Token could be extensible
  * Lexer : this interface is base on the 'Iterator' pattern, user can get each token by using that way
  * Node : this interface wrap token into Tree structure called DOM tree

## Using Module ##
NONE, No dependency about other module

## How to use ##
The following source code is the partial HTML code for test, 'test.html'

<img src='http://yghtmlparser.googlecode.com/files/testhtml.jpg' />

Each token can be extracted by Lexer,
the following is recommended code.

<img src='http://yghtmlparser.googlecode.com/files/lexersource.jpg' />

If there is no problem, you can see the following console result.

<img src='http://yghtmlparser.googlecode.com/files/lexer_result.jpg' />

HTML DOM Tree can be made by using the HtmlDomBuilder which composes nodes and a tree.

<img src='http://yghtmlparser.googlecode.com/files/html_dom.jpg' />

DOM Tree viewer page can be created by using HtmlPageGenerator. <br>
The following is a created dom tree view of the previous HTML code.<br>
<br>
<img src='http://yghtmlparser.googlecode.com/files/dom_result.jpg' />

This parser also supports DOD Tree viewer based on the Java SWING as a utility. <br>
The following image is the DOM Tree of 'google.co.kr'. created by this tools.<br>
<br>
<img src='http://yghtmlparser.googlecode.com/files/google.jpg' />

<h2>Developer</h2>
<ul><li>Name : Young-Gon Kim, Korea <br>
</li><li>Email : gonni21c@gmail.com<br><br><br></li></ul>

<h2>Demo</h2>
N/A<br>
<br>
<h2>Reference</h2>
- Blog<br>
<ul><li><a href='http://jakarta.tistory.com'>http://jakarta.tistory.com</a> (Korean)<br>
</li><li><a href='http://ygonni.blogspot.com'>http://ygonni.blogspot.com</a> (English)