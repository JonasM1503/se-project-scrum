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

/* 
 * This class implements the warrior runtime object for corewars
 * it basically takes care of all the proccess management for MarsVM
 */
 
package corewars.jmars.marsVM;

import corewars.jmars.WarriorObj;

import java.util.Vector;
 
public class WarriorRT
{
	protected WarriorObj warrior;
	protected int pspace[];
	protected int[] pQueue;
	protected int pQFirst;
	protected int pQLast;
	protected int	numProc;		// Current number of processes
	protected WarriorRT prev;	// previous warrior in the execute queue
	protected WarriorRT next;	// next warrior in the execute queue
	
	public WarriorRT(WarriorObj war, int firstInst, int[] p)
	{
		warrior = war;
		
		pQueue = new int[10000];
		pQueue[0] = firstInst;
		pQFirst = 0;
		pQLast = 1;
		numProc = 1;

		pspace = p;

		prev = this;
		next = this;
	}
	
	public void Insert(WarriorRT prevWarr, WarriorRT nextWarr)
	{
		prev = prevWarr;
		next = nextWarr;
		
		prevWarr.next = this;
		nextWarr.prev = this;
		
		return;
	}
	
	public void Remove()
	{
		prev.next = next;
		next.prev = prev;
		
		prev = this;
		next = this;
		
		return;
	}
	
	public WarriorObj warrior()
	{
		return warrior;
	}
	
	public void addProc(int inst)
	{
		int l = pQueue.length;
		
		pQueue[pQLast] = inst;
		
		pQLast = (pQLast + 1) % l;

		if (pQLast == pQFirst)
		{
			int[] nQ = new int[(int) (l * 1.3)];
			
			for (int i=0; i < l; i++)
			{
				nQ[i] = pQueue[(pQFirst + i) % l];
			}
			
			pQueue = nQ;
			pQFirst = 0;
			pQLast = l;
		}
		
		numProc++;
		
		return;
	}
	
	public int getProc()
	{
		int i = pQueue[pQFirst];
		pQFirst++;
		pQFirst %= pQueue.length;
		numProc--;
		
		return i;
	}
	
	
	public WarriorRT getPrevWarrior()
	{
		return prev;
	}
	
	
	public WarriorRT getNextWarrior()
	{
		return next;
	}
	
	
	public int numProc()
	{
		return numProc;
	}


	public void setPCell(int addr, int value)
	{
		pspace[addr%pspace.length] = value;
		
		return;
	}
	
	public int getPCell(int addr)
	{
		return pspace[addr%pspace.length];
	}
	
	public int[] getPSpace()
	{
		return pspace;
	}
}
		
	
