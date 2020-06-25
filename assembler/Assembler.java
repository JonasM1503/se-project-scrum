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

package corewars.jmars.assembler;

/**
 * Interface for all jmars' assemblers. Parsing is handled in the constructor with AssemblerExceptions throw on errors.
 */
 
import corewars.jmars.Memory;

public interface Assembler
{
	/**
	 * parse an inputStream for a warrior.
	 * @param java.io.InputStream warriorText - stream to parse.
	 * @throws java.io.IOException - on IO exception.
	 * @throws AssemblerException - on parsing error.
	 */
	public void parseWarrior(java.io.InputStream warriorText) throws java.io.IOException, AssemblerException;
	
	/**
	 * Add a predefined EQUate.
	 * @param String label - name of constant.
	 * @param String expansion - replacement of constant.
	 */
	public void addConstant(String label, String expansion);
	
	/**
	 * Get the instructions that make up this warrior.<br>Note: the instructions are not normalized for a specific core size.
	 * @returns corewars.jmars.marsVM.Memory - Array of instructions.
	 */
	public Memory[] getWarrior();
	
	/**
	 * Get the starting instruction for the warrior.
	 * @returns int - first instruction to be executed.
	 */
	public int getStart();

	/** 
	 * Name of the warrior.
	 * @returns String - name of warrior. NULL if not set.
	 */
	public String getName();
	
	/**
	 * Author of the warrior.
	 * @returns String - author of warrior. NULL if not set.
	 */
	public String getAuthor();
	
	/**
	 * Warrior length.
	 * @returns int - length of warrior.
	 */
	public int length();
}
