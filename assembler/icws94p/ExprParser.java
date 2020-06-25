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

import java.util.Map;

import corewars.jmars.assembler.AssemblerException;

public class ExprParser
{
	// hold label constant mappings
	Map mConstMap;
	
	/**
	 * Create a new expression parser.
	 * @param java.util.Map constMap - Mapping of labels to constants. <em>All keys should be Strings and all values ints</em>
	 */
	public ExprParser(Map constMap)
	{
		mConstMap = constMap;
	}

	/**
	 * set the constant map.
	 * @param java.util.Map constMap - new constant map
	 */
	public void setConstPool(Map constMap)
	{
		mConstMap = constMap;
	}

	/**
	 * evaluate an expression.
	 * @param String expr - expression to evaluate.
	 * @param int instrNumber - current instruction number that the expression appears in.
	 * @return int - value of expression.
	 */
	public int evaluate(String expr, int instrNumber) throws AssemblerException
	{
		// First remove all whitespace from the expression then do the actual parsing.
		char[] exprC = expr.toCharArray();
		int compIndex = 0;
		for (int i=0; i < exprC.length; i++)
		{
			if (!Character.isWhitespace(exprC[i]))
			{
				exprC[compIndex] = exprC[i];
				compIndex++;
			}
		}

		char[] newExprC = new char[compIndex];
		for (int i=0; i < compIndex; i++)
		{
			newExprC[i] = exprC[i];
		}

		expr = new String(newExprC);
	
		return evalParExpr(expr, instrNumber, expr);
	}

	/*
	 * check for parenthetical expressions.
	 */
	int evalParExpr(String expr, int instrNumber, String orgExpr) throws AssemblerException
	{
		if (expr.indexOf('(') != -1)
		{
			int lParenIndex = expr.indexOf('(');
			int rParenIndex = expr.lastIndexOf(')');
			if (rParenIndex < lParenIndex)
				throw new AssemblerException("unmatched left parenthesis in expression \""+ orgExpr +"\" at instruction "+ instrNumber +" "+ expr);

			String subExpr = expr.substring(0, lParenIndex) + evalAddSubExpr(expr.substring(lParenIndex+1, rParenIndex), instrNumber, orgExpr, expr.length());
			if (rParenIndex < expr.length()-1)
			{
				subExpr = subExpr + expr.substring(rParenIndex+1);
			}

			return evalParExpr(subExpr, instrNumber, orgExpr);
		}

		return evalAddSubExpr(expr, instrNumber, orgExpr, expr.length());
	}

	/*
	 * evaluate binary addition and subtraction.
	 */
	int evalAddSubExpr(String expr, int instrNumber, String orgExpr, int checkLength) throws AssemblerException
	{
		String inExpr = expr.substring(0, checkLength);
		if (inExpr.indexOf('-') != -1 || inExpr.indexOf('+') != -1)
		{
			int subIndex = inExpr.lastIndexOf('-');
			int addIndex = inExpr.lastIndexOf('+');
			if (subIndex > addIndex)
			{
				if (subIndex == expr.length()-1)
				{
					throw new AssemblerException("Expected data after - in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				else if (subIndex == 0)
				{
					return evalDivMulModExpr(expr, instrNumber, orgExpr);
				}
				
				// check for unary -
				if (isOperator(expr.charAt(subIndex-1)))
				{
					return evalAddSubExpr(expr, instrNumber, orgExpr, subIndex);
				}
				
				String preExpr = expr.substring(0, subIndex);
				String postExpr = expr.substring(subIndex+1);
				
				return evalAddSubExpr(preExpr, instrNumber, orgExpr, preExpr.length()) - evalDivMulModExpr(postExpr, instrNumber, orgExpr);
			}
			else
			{
				if (addIndex == expr.length()-1)
				{
					throw new AssemblerException("Expected data after + in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				else if (addIndex == 0)
				{
					return evalDivMulModExpr(expr, instrNumber, orgExpr);
				}
				
				// check for unary +
				if (isOperator(expr.charAt(addIndex-1)))
				{
					return evalAddSubExpr(expr, instrNumber, orgExpr, addIndex);
				}
				
				String preExpr = expr.substring(0, addIndex);
				String postExpr = expr.substring(addIndex+1);
				
				return evalAddSubExpr(preExpr, instrNumber, orgExpr, preExpr.length()) + evalDivMulModExpr(postExpr, instrNumber, orgExpr);
			}
		}

		return evalDivMulModExpr(expr, instrNumber, orgExpr);
	}

	/*
	 * checks to see if a char is an operator
	 * used to distinguish binary or unary addition and subtraction
	 */
	boolean isOperator(char c)
	{
		switch (c)
		{
			case '+':
			case '-':
			case '*':
			case '/':
			case '%':
			case '(':
				return true;

			default:
				return false;
		}
	}

	/*
	 * evaluate division, multiplication and modulo
	 */
	int evalDivMulModExpr(String expr, int instrNumber, String orgExpr) throws AssemblerException
	{
		if (expr.indexOf('/') != -1 || expr.indexOf('*') != -1 || expr.indexOf('%') != -1)
		{
			int divIndex = expr.lastIndexOf('/');
			int mulIndex = expr.lastIndexOf('*');
			int modIndex = expr.lastIndexOf('%');
			if (divIndex > mulIndex && divIndex > modIndex)
			{
				if (divIndex == expr.length()-1)
				{
					throw new AssemblerException("Expected data after / in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				else if (divIndex == 0)
				{
					throw new AssemblerException("Expected data before / in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
			
				return evalDivMulModExpr(expr.substring(0, divIndex), instrNumber, orgExpr) / evalUnaryExpr(expr.substring(divIndex+1), instrNumber, orgExpr);
			}
			else if (mulIndex > modIndex)
			{
				if (mulIndex == expr.length()-1)
				{
					throw new AssemblerException("Expected data after * in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				else if (mulIndex == 0)
				{
					throw new AssemblerException("Expected data before * in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				
				return evalDivMulModExpr(expr.substring(0, mulIndex), instrNumber, orgExpr) * evalUnaryExpr(expr.substring(mulIndex+1), instrNumber, orgExpr);
			}
			else
			{
				if (modIndex == expr.length()-1)
				{
					throw new AssemblerException("Expected data after % in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				else if (mulIndex == 0)
				{
					throw new AssemblerException("Expected data before % in expression \""+ orgExpr +"\" at instruction "+instrNumber +".");
				}
				
				return evalDivMulModExpr(expr.substring(0, modIndex), instrNumber, orgExpr) % evalUnaryExpr(expr.substring(modIndex+1), instrNumber, orgExpr);
			}
		}

		return evalUnaryExpr(expr, instrNumber, orgExpr);
	}

	/*
	 * evaluate all unary operators (+, -)
	 * FIXME: still needs !
	 */
	int evalUnaryExpr(String expr, int instrNumber, String orgExpr) throws AssemblerException
	{
		switch (expr.charAt(0))
		{
			case '-':
				return 0 - evalTerm(expr.substring(1), instrNumber, orgExpr);

			case '+':
				return evalTerm(expr.substring(1), instrNumber, orgExpr);

			default:
				return evalTerm(expr, instrNumber, orgExpr);
		}
	}

	/*
	 * evaluate a term
	 * this is either a number, defined label, or the special label CURLINE
	 */
	int evalTerm(String term, int instrNumber, String orgExpr) throws AssemblerException
	{
		int value;
		// if it starts with a digit it must be a number.
		if (Character.isDigit(term.charAt(0)))
		{
			value = Integer.parseInt(term);
		}
		else	// otherwise it must be a label
		{
			if (mConstMap.containsKey(term))
			{
				value = ((Integer)mConstMap.get(term)).intValue() - instrNumber;
			}
			else if (term == "CURLINE")
			{
				value = instrNumber-1;
			}
			else
			{
				throw new AssemblerException("Undefined label \""+ term +"\" in expression \""+ orgExpr +"\" at instruction "+ instrNumber);
			}
		}

		return value;
	}
}
