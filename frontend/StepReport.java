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

/**
 * report for changes made during one step
 */

package corewars.jmars.frontend;

import corewars.jmars.WarriorObj;

public class StepReport
{
	public WarriorObj warrior;
	
	protected int readAddr[];
	protected int numRead;
	protected int indirReadAddr[];
	protected int numIndirRead;
	protected int writeAddr[];
	protected int numWrite;
	protected int decAddr[];
	protected int numDec;
	protected int incAddr[];
	protected int numInc;

	public int execAddr;
	public int numProc;
	public boolean pDeath;
	public boolean wDeath;
	

	protected final static int MAX_READS = 4;
	protected final static int MAX_WRITES = 4;
	protected final static int MAX_INDIR_READS = 4;
	protected final static int MAX_DECS = 5;
	protected final static int MAX_INCS = 5;

	/**
	 * Creates a StepReport object.
	 */
	public StepReport()
	{
		readAddr = new int[MAX_READS];
		numRead = 0;
		indirReadAddr = new int[MAX_INDIR_READS];
		numIndirRead = 0;
		writeAddr = new int[MAX_WRITES];
		numWrite = 0;
		decAddr = new int[MAX_DECS];
		numDec = 0;
		incAddr = new int[MAX_INCS];
		numInc = 0;
		execAddr = -1;
		pDeath = false;
		wDeath = false;
		
		return;
	}
	
	/**
	 * Resets the report to it's initial state.
	 */
	public void reset()
	{
		warrior = null;
		numRead = 0;
		numIndirRead = 0;
		numWrite = 0;
		numDec = 0;
		numInc = 0;
		execAddr = -1;
		numProc = -1;
		pDeath = false;
		wDeath = false;
		
		return;
	}
	
	/**
	 * Set the Warrior object
	 * @param marsVM.WarriorObj warr - Warrior object of currently executing warrior.
	 */
/*	public void warrior(WarriorObj warr)
	{
		warrior = warr;
		
		return;
	} */
	
	/**
	 * Set a location read from.
	 * @param int addr - address of location read
	 */
	public void read(int addr)
	{
		readAddr[numRead] = addr;
		numRead++;
		
		return;
	}
	
	/**
	 * Set a location read from indirection
	 * @param int addr - address of location read
	 */
	public void indirRead(int addr)
	{
		indirReadAddr[numIndirRead] = addr;
		numIndirRead++;
		
		return;
	}
	
	/**
	 * Set a location that was written to
	 * @param int addr - address written to
	 */
	public void write(int addr)
	{
		writeAddr[numWrite] = addr;
		numWrite++;
		
		return;
	}
	
	/**
	 * Set a location that was decremented
	 * @param int addr - address that was decremented
	 */
	public void decrement(int addr)
	{
		decAddr[numDec] = addr;
		numDec++;
		
		return;
	}
		
	/**
	 * Set a location that was incremented
	 * @param int addr - address that was incremented
	 */
	public void increment(int addr)
	{
		incAddr[numInc] = addr;
		numInc++;
		
		return;
	}
	
	/**
	 * Set the location that was executed
	 * @param int addr - address that was executed
	 */
/*	public void execute(int addr)
	{
		execAddr = addr;
		
		return;
	} */
	
	/**
	 * set the number of processes in the current warrior.
	 * @param int numP - number of processes
	 */
/*	public void numProc(int numP)
	{
		numProc = numP;
		
		return;
	}
*/	
	/**
	 * called if a process dies
	 */
/*	public void pDie()
	{
		pDie = true;
	
		return;
	}
*/	
	/**
	 * called if warrior dies
	 */
/*	public void wDie()
	{
		wDeath = true;
		
		return;
	}
*/	
	/**
	 * Get the warrior object of the currently executing warrior
	 * @return marsVM.WarriorObj
	 */
/*	public WarriorObj warrior()
	{
		return warrior;
	}
*/	
	/**
	 * Get the addresses read
	 * @return int[] - array of addresses
	 */
	public int[] addrRead()
	{
		int value[] = new int[numRead];
		
		for (int i=0; i<numRead; i++)
			value[i] = readAddr[i];
		
		return value;
	}
	
	/**
	 * Get the addresses read through indirection
	 * @return int[] - array of addresses
	 */
	public int[] addrIndirRead()
	{
		int value[] = new int[numIndirRead];
		
		for (int i=0; i<numIndirRead; i++)
			value[i] = indirReadAddr[i];
			
		return value;
	}
	
	/**
	 * Get the addresses written to
	 * @return int[] - array of addresses
	 */
	public int[] addrWrite()
	{
		int value[] = new int[numWrite];
		
		for (int i=0; i<numWrite; i++)
			value[i] = writeAddr[i];
		
		return value;
	}
	
	/**
	 * Get the addresses decremented
	 * @return int[] - array of addresses
	 */
	public int[] addrDec()
	{
		int	value[] = new int[numDec];
		
		for (int i=0; i<numDec; i++)
			value[i] = decAddr[i];
		
		return value;
	}
	
	/**
	 * Get the addresses incremented
	 * @return int[] - array of addresses
	 */
	public int[] addrInc()
	{
		int	value[] = new int[numInc];
		
		for (int i=0; i<numInc; i++)
			value[i] = incAddr[i];
		
		return value;
	}
	
	/**
	 * Get the address executed
	 * @return int - address
	 */
/*	public int addrExec()
	{
		return execAddr;
	}
/*	
	/**
	 * Get the number of processes
	 * @return int - number of processes
	 */
/*	public int numProc()
	{
		return numProc;
	}
*/	
	/**
	 * Check to see if current process died
	 * @return boolean - true if process died
	 */
/*	public boolean pDeath()
	{
		return pDie;
	}
*/	
	/**
	 * Check to see if warrior died
	 * @return boolean - true if warrior died
	 */
/*	public boolean wDeath()
	{
		return wDeath;
	} */
}
