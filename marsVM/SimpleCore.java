/*
 * Copyright (c) 2000 Anton Marsden
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
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package corewars.jmars.marsVM;

import corewars.jmars.marsVM.ModularValue;
import corewars.jmars.marsVM.SimpleInstruction;
import corewars.jmars.marsVM.Instruction;
import corewars.jmars.marsVM.Core;

public class SimpleCore implements Core {

    protected Instruction core[];
    protected ModularValue coreSize;

    public SimpleCore(ModularValue coreSize) {
	coreSize = new ModularValue(coreSize);
	core = new Instruction[coreSize.get()];
	reset();
    }

    public void reset() {
	for(int i = 0; i < coreSize.get(); i++)
	    core[i] = new SimpleInstruction();	
    }

    public ModularValue getCoreSize() {
	return new ModularValue(coreSize);
    }

    public Instruction getInstruction(ModularValue pos) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	return new SimpleInstruction(core[pos.get()]);
    }

    public ModularValue getAValue(ModularValue pos) {
	return new ModularValue(getInstruction(pos).aValue());
    }

    public ModularValue getBValue(ModularValue pos) {
	return new ModularValue(getInstruction(pos).bValue());
    }

    public void setInstruction(ModularValue pos, Instruction instr) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	if(coreSize.compare(instr.aValue()) >= 0) throw new CoreException("Invalid A-value");
	if(coreSize.compare(instr.bValue()) >= 0) throw new CoreException("Invalid B-value");
	core[pos.get()].copy(instr);
    }

    public void setAValue(ModularValue pos, ModularValue aval) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	core[pos.get()].aValue().set(aval, coreSize);
    }

    public void setBValue(ModularValue pos, ModularValue bval) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	core[pos.get()].bValue().set(bval, coreSize);
    }

    public void incAValue(ModularValue pos) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	core[pos.get()].aValue().increment(coreSize);
    }

    public void incBValue(ModularValue pos) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	core[pos.get()].bValue().increment(coreSize);
    }

    public void decAValue(ModularValue pos) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	core[pos.get()].aValue().decrement(coreSize);
    }

    public void decBValue(ModularValue pos) {
	if(coreSize.compare(pos) >= 0) throw new CoreException("Invalid core position");
	core[pos.get()].bValue().decrement(coreSize);
    }

}
