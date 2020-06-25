/*-
 * Copyright (c) Brian Haskin jr.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package corewars.jmars.assembler.icws94p;

import java.util.List;

import corewars.jmars.assembler.AssemblerException;

public class Lexer
{
	protected List warriorText;

	protected int lineNumber;
	protected int charIndex;
	protected int tokenStart;

	public String word;
	public int ttype;

	static public final int TT_EOF = -1;
	static public final int TT_EOL = TT_EOF-1;
	static public final int TT_WORD = TT_EOL-1;
	static public final int TT_NOTHING = TT_WORD-1;
	
	public Lexer(List text)
	{
		warriorText = text;
		lineNumber = 0;
		charIndex = 0;
		tokenStart = 0;
		ttype = TT_NOTHING;
	}

	public int nextToken()
	{
		if (warriorText.size() == lineNumber)
		{
			ttype = TT_EOF;
			word = null;
			return ttype;
		}
		
		String curLine = ((String)warriorText.get(lineNumber));
		while (charIndex < curLine.length() && (curLine.charAt(charIndex) == ' ' || curLine.charAt(charIndex) == '\t'))
		{
			charIndex++;
		}

		if (charIndex >= curLine.length())
		{
			ttype = TT_EOL;
			word = null;
			tokenStart = charIndex;
			lineNumber++;
			charIndex = 0;
		}
		else
		{
			switch (curLine.charAt(charIndex))
			{
				case '.':
					ttype = '.';
					break;
				case ',':
					ttype = ',';
					break;
				case '#':
					ttype = '#';
					break;
				case '$':
					ttype = '$';
					break;
				case '@':
					ttype = '@';
					break;
				case '<':
					ttype = '<';
					break;
				case '>':
					ttype = '>';
					break;
				case '*':
					ttype = '*';
					break;
				case '{':
					ttype = '{';
					break;
				case '}':
					ttype = '}';
					break;
				default:
					int endWord = charIndex+1;
					while (endWord < curLine.length() && curLine.charAt(endWord) != '.' && curLine.charAt(endWord) != ',' && curLine.charAt(endWord) != ' ' && curLine.charAt(endWord) != '\t')
					{
						endWord++;
					}
					
					ttype = TT_WORD;
					word = curLine.substring(charIndex, endWord);
					tokenStart = charIndex;
					charIndex = endWord;
					break;
			}

			if (ttype != TT_WORD)
			{
				word = null;
				tokenStart = charIndex;
				charIndex++;
			}
		}
		
		return ttype;
	}

	public String getExpression() throws AssemblerException
	{
		if (warriorText.size() <= lineNumber)
		{
			throw new AssemblerException("Lexer.getExpression() called past end of warrior text");
		}
		
		String curLine = (String)warriorText.get(lineNumber);
		int endExpr = charIndex;
		while (endExpr < curLine.length() && curLine.charAt(endExpr) != ',')
		{
			endExpr++;
		}

		String expr = curLine.substring(tokenStart, endExpr);
		charIndex = endExpr;

		return expr;
	}

	public String toString()
	{
		if (warriorText.size() <= lineNumber || charIndex >= ((String)warriorText.get(lineNumber)).length())
		{
			return null;
		}

		return ((String)warriorText.get(lineNumber)).substring(charIndex);
	}
}
