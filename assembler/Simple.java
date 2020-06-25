/*-
 * Copyright (c) 1998, 2000 Brian Haskin jr.
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
 
package corewars.jmars.assembler;

/**
 * A simple assembler that reads in ICWS'94-draft loader files.
 */

import java.io.*;
import java.util.LinkedList;

import corewars.jmars.Memory;

public class Simple implements Assembler
{
	protected BufferedReader in;
	protected StreamTokenizer tok;
	protected LinkedList instructions;
	protected Memory cell;
	protected int start;
	
	// meta values
	protected String name;
	protected String author;
	
	public Simple()
	{
		// pass
	}
	
	public void parseWarrior(InputStream is) throws IOException, AssemblerException
	{
		in = new BufferedReader(new InputStreamReader(is));
		tok = new StreamTokenizer(in);
		tok.lowerCaseMode(true);
		tok.ordinaryChar('/');
		tok.eolIsSignificant(true);
		tok.parseNumbers();
		tok.ordinaryChar('.');
		tok.ordinaryChar(',');
		
		start = 0;
		name = null;
		author = null;
		
		instructions = new LinkedList();
		
		assemble();
	}

	public void addConstant(String label, String expansion)
	{
		return;
	}
	
	public Memory[] getWarrior()
	{
		Memory[] tmp = new Memory[0];
		tmp = (Memory[])instructions.toArray(tmp);
		return tmp;
	}
	
	public int getStart()
	{
		return start;
	}
	
	public String getName()
	{
		if (name != null)
			return name;
			
		return "";
	}
	
	public String getAuthor()
	{
		if (author != null)
			return author;
			
		return "";
	}
	
	public int length()
	{
		return instructions.size();
	}
	
	void assemble() throws IOException, AssemblerException
	{
		begin:
		while(tok.nextToken() != StreamTokenizer.TT_EOF)
		{
			if(tok.ttype == ';')
				pComment();
			else if (tok.ttype == StreamTokenizer.TT_WORD && tok.sval.equals("org"))
			{
					if(tok.nextToken() != tok.TT_NUMBER)
						throw new AssemblerException("Expected a number");

					start = (int) tok.nval;
					
					tok.nextToken();
					
					if (tok.ttype == ';')
						pComment();
					
			} else if (tok.ttype == StreamTokenizer.TT_WORD)
			{
				cell = new Memory();
				
				if (tok.sval.equals("mov"))
					cell.opcode = Memory.MOV;
				else if (tok.sval.equals("add"))
					cell.opcode = Memory.ADD;
				else if (tok.sval.equals("sub"))
					cell.opcode = Memory.SUB;
				else if (tok.sval.equals("mul"))
					cell.opcode = Memory.MUL;
				else if (tok.sval.equals("div"))
					cell.opcode = Memory.DIV;
				else if (tok.sval.equals("mod"))
					cell.opcode = Memory.MOD;
				else if (tok.sval.equals("jmz"))
					cell.opcode = Memory.JMZ;
				else if (tok.sval.equals("jmn"))
					cell.opcode = Memory.JMN;
				else if (tok.sval.equals("djn"))
					cell.opcode = Memory.DJN;
				else if (tok.sval.equals("cmp"))
					cell.opcode = Memory.CMP;
				else if (tok.sval.equals("seq"))
					cell.opcode = Memory.SEQ;
				else if (tok.sval.equals("slt"))
					cell.opcode = Memory.SLT;
				else if (tok.sval.equals("spl"))
					cell.opcode = Memory.SPL;
				else if (tok.sval.equals("dat"))
					cell.opcode = Memory.DAT;
				else if (tok.sval.equals("jmp"))
					cell.opcode = Memory.JMP;
				else if (tok.sval.equals("sne"))
					cell.opcode = Memory.SNE;
				else if (tok.sval.equals("nop"))
					cell.opcode = Memory.NOP;
				else if (tok.sval.equals("ldp"))
					cell.opcode = Memory.LDP;
				else if (tok.sval.equals("stp"))
					cell.opcode = Memory.STP;
				else if (tok.sval.equals("end"))
				{
					if (tok.nextToken() == tok.TT_NUMBER)
						start = (int) tok.nval;
						
					return;
				}
				else 
					throw new AssemblerException();

				pModifier();
				
				instructions.add(cell);

				if (tok.ttype == ';')
					pComment();
			} 
				
			if (tok.ttype != tok.TT_EOL)
				throw new AssemblerException();
		}

		return;
	}
	
	void pComment() throws IOException
	{
		// this function is in place to get meta data
		if (tok.nextToken() == tok.TT_WORD)
		{ 
			if (tok.sval.equals("name"))
			{
				name = in.readLine();
			} else if (tok.sval.equals("author"))
			{
				author = in.readLine();
			} else
			{
				in.readLine();
			}
		} else
		{
			in.readLine();
		}
			
		tok.ttype = tok.TT_EOL;
			
		return;
	}
	
	void pModifier() throws IOException, AssemblerException
	{
		if (tok.nextToken() != '.')
		{
			// Assign correct default modifier
			switch(cell.opcode)
			{
				case Memory.DAT:
					cell.modifier = Memory.mF;
					pAOperand(false);
					return;
				
				case Memory.JMP:
				case Memory.JMZ:
				case Memory.JMN:
				case Memory.DJN:
				case Memory.SPL:
					cell.modifier = Memory.mB;
					pAOperand(false);
					return;
					
				default:
					// Need more information before the correct modifier can be determined.
					pAOperand(true);
					return;
			}
		}
		else if (tok.nextToken() == tok.TT_WORD)
		{
			if (tok.sval.equals("a"))
				cell.modifier = Memory.mA;
			else if (tok.sval.equals("b"))
				cell.modifier = Memory.mB;
			else if (tok.sval.equals("ab"))
				cell.modifier = Memory.mAB;
			else if (tok.sval.equals("ba"))
				cell.modifier = Memory.mBA;
			else if (tok.sval.equals("f"))
				cell.modifier = Memory.mF;
			else if (tok.sval.equals("x"))
				cell.modifier = Memory.mX;
			else if (tok.sval.equals("i"))
				cell.modifier = Memory.mI;
			else 
				throw new AssemblerException();

			tok.nextToken();

			pAOperand(false);
			return;
		} else 
			throw new AssemblerException();

	}
	
	void pAOperand(boolean setModifier) throws IOException, AssemblerException
	{
		switch (tok.ttype)
		{
			case StreamTokenizer.TT_NUMBER:
				pAValue(setModifier);
				return;
				
			case '#':
				cell.aIndir = Memory.IMMEDIATE;
				cell.aTiming = Memory.PRE;
				cell.aAction = Memory.NONE;
				cell.aTarget = Memory.B;
				// If the modifier hasn't already been set we now have enough information to set it.
				if (setModifier)
				{
					cell.modifier = Memory.mAB;
					setModifier = false;
				}
				break;
				
			case '$':
				cell.aIndir = Memory.DIRECT;
				cell.aTiming = Memory.POST;
				cell.aAction = Memory.NONE;
				cell.aTarget = Memory.B;
				break;
				
			case '@':
				cell.aIndir = Memory.INDIRECT;
				cell.aTiming = Memory.POST;
				cell.aAction = Memory.NONE;
				cell.aTarget = Memory.B;
				break;
				
			case '<':
				cell.aIndir = Memory.INDIRECT;
				cell.aTiming = Memory.PRE;
				cell.aAction = Memory.DECREMENT;
				cell.aTarget = Memory.B;
				break;
				
			case '>':
				cell.aIndir = Memory.INDIRECT;
				cell.aTiming = Memory.POST;
				cell.aAction = Memory.INCREMENT;
				cell.aTarget = Memory.B;
				break;
				
			case '*':
				cell.aIndir = Memory.INDIRECT;
				cell.aTiming = Memory.POST;
				cell.aAction = Memory.NONE;
				cell.aTarget = Memory.A;
				break;
				
			case '{':
				cell.aIndir = Memory.INDIRECT;
				cell.aTiming = Memory.PRE;
				cell.aAction = Memory.DECREMENT;
				cell.aTarget = Memory.A;
				break;
				
			case '}':
				cell.aIndir = Memory.INDIRECT;
				cell.aTiming = Memory.POST;
				cell.aAction = Memory.INCREMENT;
				cell.aTarget = Memory.A;
				break;
				
			default:
				throw new AssemblerException();
				
		}
		
		tok.nextToken();

		pAValue(setModifier);
		return;
	}

	void pAValue(boolean setModifier) throws IOException, AssemblerException
	{
		if (tok.ttype != tok.TT_NUMBER)
			throw new AssemblerException();
		
		cell.aValue = (int) tok.nval;
		
		if (tok.nextToken() != ',')
		{
			throw new AssemblerException("No comma after A-value");
		}
		
		tok.nextToken();
		
		pBOperand(setModifier);
		return;
	}
	
	
	void pBOperand(boolean setModifier) throws IOException, AssemblerException
	{
		switch (tok.ttype)
		{
			case StreamTokenizer.TT_NUMBER:
				if (setModifier)
				{
					switch (cell.opcode)
					{
						case Memory.MOV:
						case Memory.CMP:
							cell.modifier = Memory.mI;
							break;
							
						case Memory.ADD:
						case Memory.SUB:
						case Memory.MUL:
						case Memory.DIV:
						case Memory.MOD:
							cell.modifier = Memory.mF;
							break;
							
						case Memory.SLT:
							cell.modifier = Memory.mB;
							break;
							
						default:
							System.out.println("ERROR: Modifier not set in assembler when it should have been.");
							throw new AssemblerException("Internal assembler error.");
					}
				}
				pBValue();
				return;
				
			case '#':
				cell.bIndir = Memory.IMMEDIATE;
				cell.bTiming = Memory.PRE;
				cell.bAction = Memory.NONE;
				cell.bTarget = Memory.B;
				
				if (setModifier)
				{
					switch (cell.opcode)
					{
						case Memory.MOV:
						case Memory.CMP:
						case Memory.ADD:
						case Memory.SUB:
						case Memory.MUL:
						case Memory.DIV:
						case Memory.MOD:
							cell.modifier = Memory.mB;
							setModifier = false;
							break;
							
						default:
							break;
					}
				}
				break;
				
			case '$':
				cell.bIndir = Memory.DIRECT;
				cell.bTiming = Memory.POST;
				cell.bAction = Memory.NONE;
				cell.bTarget = Memory.B;
				break;
				
			case '@':
				cell.bIndir = Memory.INDIRECT;
				cell.bTiming = Memory.POST;
				cell.bAction = Memory.NONE;
				cell.bTarget = Memory.B;
				break;
				
			case '<':
				cell.bIndir = Memory.INDIRECT;
				cell.bTiming = Memory.PRE;
				cell.bAction = Memory.DECREMENT;
				cell.bTarget = Memory.B;
				break;
				
			case '>':
				cell.bIndir = Memory.INDIRECT;
				cell.bTiming = Memory.POST;
				cell.bAction = Memory.INCREMENT;
				cell.bTarget = Memory.B;
				break;
				
			case '*':
				cell.bIndir = Memory.INDIRECT;
				cell.bTiming = Memory.POST;
				cell.bAction = Memory.NONE;
				cell.bTarget = Memory.A;
				break;

			case '{':
				cell.bIndir = Memory.INDIRECT;
				cell.bTiming = Memory.PRE;
				cell.bAction = Memory.DECREMENT;
				cell.bTarget = Memory.A;
				break;
				
			case '}':
				cell.bIndir = Memory.INDIRECT;
				cell.bTiming = Memory.POST;
				cell.bAction = Memory.INCREMENT;
				cell.bTarget = Memory.A;
				break;
				
			default:
				throw new AssemblerException();
				
		}
		
		// Set any modifiers that didn't get set earlier
		if (setModifier)
		{
			switch (cell.opcode)
			{
				case Memory.MOV:
				case Memory.CMP:
					cell.modifier = Memory.mI;
					break;
				
				case Memory.ADD:
				case Memory.SUB:
				case Memory.MUL:
				case Memory.DIV:
				case Memory.MOD:
					cell.modifier = Memory.mF;
					break;
					
				case Memory.SLT:
				case Memory.JMP:
				case Memory.JMZ:
				case Memory.JMN:
				case Memory.DJN:
				case Memory.SPL:
					cell.modifier = Memory.mB;
					break;
					
				default:
					System.out.println("ERROR: Modifier not set in assembler.");
					throw new AssemblerException("Internal assembler error.");
			}
		}
		
		tok.nextToken();

		pBValue();
		return;
	}

	void pBValue() throws IOException, AssemblerException
	{
		if (tok.ttype != tok.TT_NUMBER)
			throw new AssemblerException();
		
		cell.bValue = (int) tok.nval;
		
		tok.nextToken();
		
		return;
	}
	
}
