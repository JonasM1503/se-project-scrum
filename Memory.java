/*-
 * Copyright (c) 1998 Brian Haskin jr.
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
 
/* Class for corewar memory */

package corewars.jmars;

public class Memory 
{
	// fields of a memory cell
	public byte opcode;
	public byte modifier;
	public byte aIndir, bIndir; // Immediate, Direct or Indirect
	public byte aTarget, bTarget; // A or B indirection
	public byte aTiming, bTiming; // Pre or Post
	public byte aAction, bAction; // decrement or increment
	public int aValue, bValue;

	// valid opcodes
	final public static byte MOV = 0;
	final public static byte ADD = 1;
	final public static byte SUB = 2;
	final public static byte MUL = 3;
	final public static byte DIV = 4;
	final public static byte MOD = 5;
	final public static byte JMZ = 6;
	final public static byte JMN = 7;
	final public static byte DJN = 8;
	final public static byte CMP = 9;	// two names for same instruction
	final public static byte SEQ = 9;
	final public static byte SNE = 10;
	final public static byte SLT = 11;
	final public static byte SPL = 12;
	final public static byte DAT = 13;
	final public static byte JMP = 14;
	final public static byte NOP = 15;
	final public static byte LDP = 16;
	final public static byte STP = 17;
	
	// valid modifiers
	final public static byte mA = 0;
	final public static byte mB = 1;
	final public static byte mAB = 2;
	final public static byte mBA = 3;
	final public static byte mF = 4;
	final public static byte mX = 5;
	final public static byte mI = 6;

	// valid Indirections
	final public static byte IMMEDIATE = 0;
	final public static byte DIRECT = 1;
	final public static byte INDIRECT = 2;
	
	// valid Targets
	final public static byte A = 0;
	final public static byte B = 1;

	// valid Timings
	final public static byte PRE = 0;
	final public static byte POST = 1;
	
	// valid Actions
	final public static byte NONE = 0;
	final public static byte DECREMENT = 1;
	final public static byte INCREMENT = 2;
		
	public Memory()
	{
		opcode = DAT;
		modifier = mF;
		aIndir = DIRECT;
		bIndir = DIRECT;
		aTarget = B;
		bTarget = B;
		aTiming = PRE;
		bTiming = PRE;
		aAction = NONE;
		bAction = NONE;
		aValue = 0;
		bValue = 0;
	}
	
	public Memory(Memory src)
	{
		copy(src);
	}
	
	public void copy(Memory src)
	{
		opcode = src.opcode;
		modifier = src.modifier;
		aIndir = src.aIndir;
		bIndir = src.bIndir;
		aTarget = src.aTarget;
		bTarget = src.bTarget;
		aTiming = src.aTiming;
		bTiming = src.bTiming;
		aAction = src.aAction;
		bAction = src.bAction;
		aValue = src.aValue;
		bValue = src.bValue;
	}
	
	public boolean equals(Memory comp)
	{
		if ((opcode != comp.opcode) ||
			(modifier != comp.modifier) ||
			(aIndir != comp.aIndir) ||
			(aAction != comp.aAction) ||
			(aTarget != comp.aTarget) ||
			(aTiming != comp.aTiming) ||
			(aValue != comp.aValue) ||
			(bIndir != comp.bIndir) ||
			(bAction != comp.bAction) ||
			(bTarget != comp.bTarget) ||
			(bTiming != comp.bTiming) ||
			(bValue != comp.bValue))
				return false;

		return true;
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof Memory)
			return equals((Memory) obj);
			
		return false;
	}
	
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
		switch (opcode)
		{
			case MOV:
				str.append("MOV");
				break;
				
			case ADD:
				str.append("ADD");
				break;
				
			case SUB:
				str.append("SUB");
				break;
				
			case MUL:
				str.append("MUL");
				break;
				
			case DIV:
				str.append("DIV");
				break;
				
			case MOD:
				str.append("MOD");
				break;
				
			case JMZ:
				str.append("JMZ");
				break;
				
			case JMN:
				str.append("JMN");
				break;
				
			case DJN:
				str.append("DJN");
				break;
				
			case SEQ:
				str.append("SEQ");
				break;
				
			case SNE:
				str.append("SNE");
				break;
				
			case SLT:
				str.append("SLT");
				break;
				
			case SPL:
				str.append("SPL");
				break;
				
			case DAT:
				str.append("DAT");
				break;
				
			case JMP:
				str.append("JMP");
				break;
				
			case NOP:
				str.append("NOP");
				break;
				
			case LDP:
				str.append("LDP");
				break;
				
			case STP:
				str.append("STP");
				break;
		}
		
		switch (modifier)
		{
			case mA:
				str.append(".A  ");
				break;
				
			case mB:
				str.append(".B  ");
				break;
				
			case mAB:
				str.append(".AB ");
				break;
				
			case mBA:
				str.append(".BA ");
				break;
				
			case mF:
				str.append(".F  ");
				break;
				
			case mX:
				str.append(".X  ");
				break;
				
			case mI:
				str.append(".I  ");
				break;
		}
		
		if ((aIndir == INDIRECT) && (aTiming == PRE) && (aAction == DECREMENT))
		{
			switch (aTarget)
			{
				case A:
					str.append("{ ");
					break;
					
				case B:
					str.append("< ");
			}
		} else if ((aIndir == INDIRECT) && (aTiming == POST) && (aAction == INCREMENT))
		{
			switch (aTarget)
			{
				case A:
					str.append("} ");
					break;
					
				case B:
					str.append("> ");
			}
		} else 
		{
			switch (aIndir)
			{
				case IMMEDIATE:
					str.append("#");
					break;
				
				case DIRECT:
					str.append("$");
					break;
				
				case INDIRECT:
					if (aAction==NONE)
					{
						switch (aTarget)
						{
							case A:
								str.append("*");
								break;
							
							case B:
								str.append("@");
								break;
						}
					} else
					{
						str.append("@");
					}
					break;
			}
		
			if (aAction != NONE)
			{
				switch (aTiming)
				{
					case PRE:
						str.append("<");
						break;
				
					case POST:
						str.append(">");
						break;
				}
			
				switch (aAction)
				{
					case DECREMENT:
						str.append("-");
						break;
					
					case INCREMENT:
						str.append("+");
						break;
				}
			
				switch (aTarget)
				{
					case A:
						str.append("A");
						break;
				
					case B:
						str.append("B");
						break;
				}
			} else
			{
				str.append(" ");
			}
		}

		int i = 6 - Integer.toString(aValue).length();
		for (;i > 0; i--)
		{
			str.append(" ");
		}
		
		str.append(" " + aValue + ", ");
		
		if ((bIndir == INDIRECT) && (bTiming == PRE) && (bAction == DECREMENT))
		{
			switch (bTarget)
			{
				case A:
					str.append("{ ");
					break;
					
				case B:
					str.append("< ");
			}
		} else if ((bIndir == INDIRECT) && (bTiming == POST) && (bAction == INCREMENT))
		{
			switch (bTarget)
			{
				case A:
					str.append("} ");
					break;
					
				case B:
					str.append("> ");
			}
		} else 
		{
			switch (bIndir)
			{
				case IMMEDIATE:
					str.append("#");
					break;
				
				case DIRECT:
					str.append("$");
					break;
				
				case INDIRECT:
					if (aAction==NONE)
					{
						switch (aTarget)
						{
							case A:
								str.append("*");
								break;
							
							case B:
								str.append("@");
								break;
						}
					} else
					{
						str.append("@");
					}
					break;
			}
		
			if (bAction != NONE)
			{
				switch (bTiming)
				{
					case PRE:
						str.append("<");
						break;
					
					case POST:
						str.append(">");
						break;
				}
			
				switch (bAction)
				{
					case DECREMENT:
						str.append("-");
						break;
				
					case INCREMENT:
						str.append("+");
						break;
				}
			
				switch (bTarget)
				{
					case A:
						str.append("A");
						break;
				
					case B:
						str.append("B");
						break;
				}
			} else
			{
				str.append(" ");
			}
		}
		
		i = 6 - Integer.toString(bValue).length();
		for (;i > 0; i--)
		{
			str.append(" ");
		}
		
		str.append(" " + bValue);
		
		return str.toString();
	}
	
};

