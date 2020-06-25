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

/**
 * This is the interface for all jMARS virtual machines
 */
 
package corewars.jmars.marsVM;

import corewars.jmars.WarriorObj;
import corewars.jmars.frontend.StepReport;

public interface VM
{
	/** 
	 * Load a warrior into core at the specified position.
	 * @param WarriorObj warrior - warrior to load.
	 * @param int startPosition - Position to start loading warrior at.
	 * @returns boolean - true if warrior succesfully loaded.
	 */
	public boolean loadWarrior(WarriorObj warrior, int startPosition);
	
	/**
	 * step the vm forward one instruction.
	 * @returns StepReport - Report on actions that occured in this step.
	 */
	public StepReport step();
	
	/**
	 * Reset the VM to it's initial state. All warriors are unloaded and core is cleared.
	 */
	public void reset();
}
