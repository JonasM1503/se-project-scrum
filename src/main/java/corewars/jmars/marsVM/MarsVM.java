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
 * This class implements the virtual machine (MARS) for corewars
 */
package corewars.jmars.marsVM;

import corewars.jmars.frontend.StepReport;
import corewars.jmars.WarriorObj;
import corewars.jmars.Memory;

public class MarsVM implements VM {

    private StepReport report;

    public Memory core[];
    protected WarriorRT currentW;		// current warrior
    int numWarriors;
    protected int maxProc;
    protected int coreSize;

    private Memory instrCopy;
    private Memory instrA;
//	private Memory instrB;

    public MarsVM(int coreS, int maxP) {
        maxProc = maxP;
        coreSize = coreS;

        core = new Memory[coreSize];

        for (int i = 0; i < core.length; i++) {
            core[i] = new Memory();
        }

        report = new StepReport();

        instrCopy = new Memory();
        instrA = new Memory();
//		instrB = new Memory();

    }

    public void reset() {
        currentW = null;
        numWarriors = 0;

        for (int i = 0; i < core.length; i++) {
            core[i] = new Memory();
        }
    }

    /**
     * Load a warrior into the core.
     *
     * @param WarriorObj warrior - warrior to load
     * @param int startPosition - first memory location occupied by warrior
     * @returns boolean - false if couldn't load warrior
     */
    public boolean loadWarrior(WarriorObj warrior, int startPosition) {
        WarriorRT newWarrior;
        Memory wMemory[] = warrior.getMemory(coreSize);
        if ((startPosition + wMemory.length - 1) > coreSize) {
            return false;	// check that warrior fits in memory
        }
        numWarriors++;

        for (int i = 0; i < wMemory.length; i++) {
            core[startPosition + i].copy(wMemory[i]);
        }

        warrior.normalizePSpace(coreSize);
        newWarrior = new WarriorRT(warrior, startPosition + warrior.getOffset(), warrior.getPSpace());

        if (currentW == null) {
            currentW = newWarrior;
        } else {
            newWarrior.Insert(currentW, currentW.getNextWarrior());
        }

        return true;
    }

    /**
     * get the warrior object of the current warrior
     *
     * @returns marsVM.WarriorObj - current warrior's object
     */
    public WarriorObj getCurrentWarrior() {
        return currentW.warrior();
    }

    /**
     * The heart of the VM it executes one instruction in core each time it is
     * called
     *
     * @returns marsVM.StepReport - report of what happened this instuction.
     */
    public StepReport step() {
        int tempAddr;		// temporary address for use in mode evaluation
        int addrA = 0;			// A's address
        int addrAAValue = 0;	// address A's A Value
        int addrABValue = 0;	// address B's B Value
        int addrB = 0;			// address B
        int addrBAValue = 0;	// address B's A Value
        int addrBBValue = 0;	// address B's B Value

        report.reset();

        // get instruction pointer
        int IP = currentW.getProc();
        report.warrior = currentW.warrior();
        report.numProc = currentW.numProc();

        // Get a copy of the current instruction
        instrCopy.copy(core[IP]);

        // evaluate A operand
        tempAddr = (instrCopy.aValue + IP) % coreSize; // temporary address stuffed with the direct value to evaluate actions and help with indirect mode
        // do Pre timed actions
        if (instrCopy.aTiming == Memory.PRE && instrCopy.aAction != Memory.NONE) {
            if (instrCopy.aAction == Memory.DECREMENT) {
                if (instrCopy.aTarget == Memory.A) {
                    if (--core[tempAddr].aValue < 0) {
                        core[tempAddr].aValue = coreSize - 1;
                    }
                } else {
                    if (--core[tempAddr].bValue < 0) {
                        core[tempAddr].bValue = coreSize - 1;
                    }
                }
                report.decrement(tempAddr);
            } else {
                if (instrCopy.aTarget == Memory.A) {
                    core[tempAddr].aValue = (++core[tempAddr].aValue) % coreSize;
                } else {
                    core[tempAddr].bValue = (++core[tempAddr].bValue) % coreSize;
                }
                report.increment(tempAddr);
            }
        }

        // evaluate indirection
        switch (instrCopy.aIndir) {
            case Memory.IMMEDIATE:
                addrA = IP;
                instrA.copy(core[IP]);
                addrAAValue = instrCopy.aValue;
                addrABValue = instrCopy.bValue;
                break;

            case Memory.DIRECT:
                addrA = (IP + instrCopy.aValue) % coreSize;
                instrA.copy(core[(IP + instrCopy.aValue) % coreSize]);
                addrAAValue = core[addrA].aValue;
                addrABValue = core[addrA].bValue;
                break;

            case Memory.INDIRECT:
                if (instrCopy.aTarget == instrCopy.A) {
                    addrA = (core[tempAddr].aValue + tempAddr) % coreSize;
                } else {
                    addrA = (core[tempAddr].bValue + tempAddr) % coreSize;
                }

                instrA.copy(core[addrA]);
                addrAAValue = core[addrA].aValue;
                addrABValue = core[addrA].bValue;

                report.increment(tempAddr);
                break;
        }

        // do Post actions
        if (instrCopy.aTiming == Memory.POST && instrCopy.aAction != Memory.NONE) {
            if (instrCopy.aAction == Memory.DECREMENT) {
                if (instrCopy.aTarget == Memory.A) {
                    if (--core[tempAddr].aValue < 0) {
                        core[tempAddr].aValue = coreSize - 1;
                    }
                } else {
                    if (--core[tempAddr].bValue < 0) {
                        core[tempAddr].bValue = coreSize - 1;
                    }
                }
                report.decrement(tempAddr);
            } else {
                if (instrCopy.aTarget == Memory.A) {
                    core[tempAddr].aValue = (++core[tempAddr].aValue) % coreSize;
                } else {
                    core[tempAddr].bValue = (++core[tempAddr].bValue) % coreSize;
                }
                report.increment(tempAddr);
            }
        }

        // evaluate B operand
        tempAddr = (instrCopy.bValue + IP) % coreSize; // temporary address stuffed with the direct value to evaluate actions and help with indirect mode
        // do Pre timed actions
        if (instrCopy.bTiming == Memory.PRE && instrCopy.bAction != Memory.NONE) {
            if (instrCopy.bAction == Memory.DECREMENT) {
                if (instrCopy.bTarget == Memory.A) {
                    if (--core[tempAddr].aValue < 0) {
                        core[tempAddr].aValue = coreSize - 1;
                    }
                } else {
                    if (--core[tempAddr].bValue < 0) {
                        core[tempAddr].bValue = coreSize - 1;
                    }
                }
                report.decrement(tempAddr);
            } else {
                if (instrCopy.bTarget == Memory.A) {
                    core[tempAddr].aValue = (++core[tempAddr].aValue) % coreSize;
                } else {
                    core[tempAddr].bValue = (++core[tempAddr].bValue) % coreSize;
                }
                report.increment(tempAddr);
            }
        }

        // evaluate indirection
        switch (instrCopy.bIndir) {
            case Memory.IMMEDIATE:
                addrB = IP;
//				instrB.copy(core[IP]);
                addrBAValue = instrCopy.aValue;
                addrBBValue = instrCopy.bValue;
                break;

            case Memory.DIRECT:
                addrB = (IP + instrCopy.bValue) % coreSize;
//				instrB.copy(core[(IP + instrCopy.bValue) % coreSize]);
                addrBAValue = core[addrB].aValue;
                addrBBValue = core[addrB].bValue;
                break;

            case Memory.INDIRECT:
                if (instrCopy.bTarget == Memory.A) {
                    addrB = (core[tempAddr].aValue + tempAddr) % coreSize;
                } else {
                    addrB = (core[tempAddr].bValue + tempAddr) % coreSize;
                }

//				instrB.copy(core[addrB]);
                addrBAValue = core[addrB].aValue;
                addrBBValue = core[addrB].bValue;

                report.increment(tempAddr);

                break;
        }

        // do Post actions
        if (instrCopy.bTiming == Memory.POST && instrCopy.bAction != Memory.NONE) {
            if (instrCopy.bAction == Memory.DECREMENT) {
                if (instrCopy.bTarget == Memory.A) {
                    if (--core[tempAddr].aValue < 0) {
                        core[tempAddr].aValue = coreSize - 1;
                    }
                } else {
                    if (--core[tempAddr].bValue < 0) {
                        core[tempAddr].bValue = coreSize - 1;
                    }
                }
                report.decrement(tempAddr);
            } else {
                if (instrCopy.bTarget == Memory.A) {
                    core[tempAddr].aValue = (++core[tempAddr].aValue) % coreSize;
                } else {
                    core[tempAddr].bValue = (++core[tempAddr].bValue) % coreSize;
                }
                report.increment(tempAddr);
            }
        }

        // execute instruction
        report.execAddr = IP;
        switch (instrCopy.opcode) {
            case Memory.DAT:
                killProc(report);
                return report;

            case Memory.MOV:
                switch (instrCopy.modifier) {
                    case Memory.mI:
                        core[addrB].copy(instrA);
                        break;

                    case Memory.mA:
                        core[addrB].aValue = addrAAValue;
                        break;

                    case Memory.mF:
                        core[addrB].aValue = addrAAValue;
                    // fallthrough for rest
                    case Memory.mB:
                        core[addrB].bValue = addrABValue;
                        break;

                    case Memory.mAB:
                        core[addrB].bValue = addrAAValue;
                        break;

                    case Memory.mX:
                        core[addrB].bValue = addrAAValue;
                    // fallthrough for rest
                    case Memory.mBA:
                        core[addrB].aValue = addrABValue;
                        break;
                }
                report.read(addrA);
                report.write(addrB);
                break;

            case Memory.ADD:
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        core[addrB].aValue = (addrAAValue + addrBAValue) % coreSize;
                        break;

                    case Memory.mI:
                    case Memory.mF:
                        core[addrB].aValue = (addrAAValue + addrBAValue) % coreSize;
                    // fallthrough for rest
                    case Memory.mB:
                        core[addrB].bValue = (addrABValue + addrBBValue) % coreSize;
                        break;

                    case Memory.mAB:
                        core[addrB].bValue = (addrAAValue + addrBBValue) % coreSize;
                        break;

                    case Memory.mX:
                        core[addrB].bValue = (addrAAValue + addrBBValue) % coreSize;
                    // fallthrough for rest
                    case Memory.mBA:
                        core[addrB].aValue = (addrABValue + addrBAValue) % coreSize;
                        break;
                }
                report.read(addrA);
                report.write(addrB);
                break;

            case Memory.SUB:
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        if ((core[addrB].aValue = addrBAValue - addrAAValue) < 0) {
                            core[addrB].aValue += coreSize;
                        }
                        break;

                    case Memory.mI:
                    case Memory.mF:
                        if ((core[addrB].aValue = addrBAValue - addrAAValue) < 0) {
                            core[addrB].aValue += coreSize;
                        }
                    // fallthrough for rest
                    case Memory.mB:
                        if ((core[addrB].bValue = addrBBValue - addrABValue) < 0) {
                            core[addrB].bValue += coreSize;
                        }
                        break;

                    case Memory.mAB:
                        if ((core[addrB].bValue = addrBBValue - addrAAValue) < 0) {
                            core[addrB].bValue += coreSize;
                        }
                        break;

                    case Memory.mX:
                        if ((core[addrB].bValue = addrBBValue - addrAAValue) < 0) {
                            core[addrB].aValue += coreSize;
                        }
                    // falthrough for rest
                    case Memory.mBA:
                        if ((core[addrB].aValue = addrBAValue - addrABValue) < 0) {
                            core[addrB].aValue += coreSize;
                        }
                        break;
                }
                report.read(addrA);
                report.write(addrB);
                break;

            case Memory.MUL:
                switch (instrCopy.modifier) {
                    // the cast prevents overflow, i hope ;)
                    case Memory.mA:
                        core[addrB].aValue = (int) ((long) addrBAValue * addrAAValue % coreSize);
                        break;

                    case Memory.mI:
                    case Memory.mF:
                        core[addrB].aValue = (int) ((long) addrBAValue * addrAAValue % coreSize);
                    // fallthrough for rest
                    case Memory.mB:
                        core[addrB].bValue = (int) ((long) addrBBValue * addrABValue % coreSize);
                        break;

                    case Memory.mAB:
                        core[addrB].bValue = (int) ((long) addrBBValue * addrAAValue % coreSize);
                        break;

                    case Memory.mX:
                        core[addrB].bValue = (int) ((long) addrBBValue * addrAAValue % coreSize);
                    // fallthrough for rest
                    case Memory.mBA:
                        core[addrB].aValue = (int) ((long) addrBAValue * addrABValue % coreSize);
                        break;
                }
                report.read(addrA);
                report.write(addrB);
                break;

            case Memory.DIV:
                report.read(addrA);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        if (addrAAValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].aValue = addrBAValue / addrAAValue;
                        break;

                    case Memory.mB:
                        if (addrABValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].bValue = addrBBValue / addrABValue;
                        break;

                    case Memory.mAB:
                        if (addrAAValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].bValue = addrBBValue / addrAAValue;
                        break;

                    case Memory.mBA:
                        if (addrABValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].aValue = addrBAValue / addrABValue;
                        break;

                    case Memory.mI:
                    case Memory.mF:
                        if (addrAAValue != 0) {
                            core[addrB].aValue = addrBAValue / addrAAValue;
                            if (addrABValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue / addrABValue;
                            break;
                        } else {
                            if (addrABValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue / addrABValue;
                            report.write(addrB);
                            killProc(report);
                            return report;
                        }

                    case Memory.mX:
                        if (addrABValue != 0) {
                            core[addrB].aValue = addrBAValue / addrABValue;
                            if (addrAAValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue / addrAAValue;
                            break;
                        } else {
                            if (addrAAValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue / addrAAValue;
                            report.write(addrB);
                            killProc(report);
                            return report;
                        }
                }
                report.write(addrB);
                break;

            case Memory.MOD:
                report.read(addrA);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        if (addrAAValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].aValue = addrBAValue % addrAAValue;
                        break;

                    case Memory.mB:
                        if (addrABValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].bValue = addrBBValue % addrABValue;
                        break;

                    case Memory.mAB:
                        if (addrAAValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].bValue = addrBBValue % addrAAValue;
                        break;

                    case Memory.mBA:
                        if (addrABValue == 0) {
                            killProc(report);
                            return report;
                        }
                        core[addrB].aValue = addrBAValue % addrABValue;
                        break;

                    case Memory.mI:
                    case Memory.mF:
                        if (addrAAValue != 0) {
                            core[addrB].aValue = addrBAValue % addrAAValue;
                            if (addrABValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue % addrABValue;
                            break;
                        } else {
                            if (addrABValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue % addrABValue;
                            report.write(addrB);
                            killProc(report);
                            return report;
                        }

                    case Memory.mX:
                        if (addrABValue != 0) {
                            core[addrB].aValue = addrBAValue % addrABValue;
                            if (addrAAValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue % addrAAValue;
                            break;
                        } else {
                            if (addrAAValue == 0) {
                                killProc(report);
                                return report;
                            }
                            core[addrB].bValue = addrBBValue % addrAAValue;
                            report.write(addrB);
                            killProc(report);
                            return report;
                        }
                }
                report.write(addrB);
                break;

            case Memory.JMZ:
                report.read(addrB);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                    case Memory.mBA:
                        if (addrBAValue != 0) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mF:
                    case Memory.mX:
                    case Memory.mI:
                        if (addrBAValue != 0) {
                            break;
                        }
                    // fallthrough
                    case Memory.mB:
                    case Memory.mAB:
                        if (addrBBValue != 0) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;
                }
                break;

            case Memory.JMN:
                report.read(addrB);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                    case Memory.mBA:
                        if (addrBAValue == 0) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mF:
                    case Memory.mX:
                    case Memory.mI:
                        if ((addrBAValue == 0) && (addrBBValue == 0)) {
                            break;
                        }
                        currentW.addProc(addrA);
                        break;

                    case Memory.mB:
                    case Memory.mAB:
                        if (addrBBValue == 0) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;
                }
                break;

            case Memory.DJN:
                report.decrement(addrB);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                    case Memory.mBA:
                        if (--core[addrB].aValue < 0) {
                            core[addrB].aValue = coreSize - 1;
                        }
                        if (addrBAValue == 1) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mB:
                    case Memory.mAB:
                        if (--core[addrB].bValue < 0) {
                            core[addrB].bValue = coreSize - 1;
                        }
                        if (addrBBValue == 1) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mF:
                    case Memory.mI:
                    case Memory.mX:
                        if (--core[addrB].bValue < 0) {
                            core[addrB].bValue = coreSize - 1;
                        }
                        if (--core[addrB].aValue < 0) {
                            core[addrB].aValue = coreSize - 1;
                        }
                        if ((addrBAValue == 1) && (addrBBValue == 1)) {
                            break;
                        }
                        currentW.addProc(addrA);
                        currentW = currentW.getNextWarrior();
                        return report;
                }
                break;

            case Memory.SEQ:
                report.read(addrA);
                report.read(addrB);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        if (addrBAValue != addrAAValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mI:
                        if (!core[addrB].equals(core[addrA])) {
                            break;
                        }
                    // fallthrough for rest
                    case Memory.mF:
                        if (addrBAValue != addrAAValue) {
                            break;
                        }
                    // fallthrough for rest
                    case Memory.mB:
                        if (addrBBValue != addrABValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mAB:
                        if (addrBBValue != addrAAValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mX:
                        if (addrBBValue != addrAAValue) {
                            break;
                        }
                    // fallthrough for rest
                    case Memory.mBA:
                        if (addrBAValue != addrABValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;
                }
                break;

            case Memory.SNE:
                report.read(addrA);
                report.read(addrB);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        if (addrBAValue == addrAAValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mI:
                        if (core[addrB].equals(core[addrA])) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mF:
                        if ((addrBAValue == addrAAValue)
                                && (addrBBValue == addrABValue)) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mB:
                        if (addrBBValue == addrABValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mAB:
                        if (addrBBValue == addrAAValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mX:
                        if ((addrBBValue == addrAAValue)
                                && (addrBAValue == addrABValue)) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mBA:
                        if (addrBAValue == addrABValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;
                }
                break;

            case Memory.SLT:
                report.read(addrA);
                report.read(addrB);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        if (addrBAValue <= addrAAValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mF:
                    case Memory.mI:
                        if (addrBAValue <= addrAAValue) {
                            break;
                        }
                    // fallthrough for rest
                    case Memory.mB:
                        if (addrBBValue <= addrABValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mAB:
                        if (addrBBValue <= addrAAValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;

                    case Memory.mX:
                        if (addrBBValue <= addrAAValue) {
                            break;
                        }
                    // fallthrough for rest
                    case Memory.mBA:
                        if (addrBAValue <= addrABValue) {
                            break;
                        }
                        currentW.addProc((IP + 2) % coreSize);
                        currentW = currentW.getNextWarrior();
                        return report;
                }
                break;

            case Memory.JMP:
                currentW.addProc(addrA);
                currentW = currentW.getNextWarrior();
                return report;

            case Memory.SPL:
                currentW.addProc((IP + 1) % coreSize);
                if (currentW.numProc() >= maxProc) {
                    currentW = currentW.getNextWarrior();
                    return report;
                }
                currentW.addProc(addrA);
                report.numProc = currentW.numProc();
                currentW = currentW.getNextWarrior();
                return report;

            case Memory.NOP:
                break;

            case Memory.LDP:
                report.read(addrA);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        core[addrB].aValue = currentW.getPCell(addrAAValue);
                        break;

                    case Memory.mF:
                    case Memory.mX:
                    case Memory.mI:
                    case Memory.mB:
                        core[addrB].bValue = currentW.getPCell(addrABValue);
                        break;

                    case Memory.mAB:
                        core[addrB].bValue = currentW.getPCell(addrAAValue);
                        break;

                    case Memory.mBA:
                        core[addrB].aValue = currentW.getPCell(addrABValue);
                        break;
                }
                report.write(addrB);
                break;

            case Memory.STP:
                report.read(addrA);
                switch (instrCopy.modifier) {
                    case Memory.mA:
                        currentW.setPCell(addrBAValue, addrAAValue);
                        break;

                    case Memory.mF:
                    case Memory.mX:
                    case Memory.mI:
                    case Memory.mB:
                        currentW.setPCell(addrBBValue, addrABValue);
                        break;

                    case Memory.mAB:
                        currentW.setPCell(addrBBValue, addrAAValue);
                        break;

                    case Memory.mBA:
                        currentW.setPCell(addrBAValue, addrABValue);
                        break;
                }
                break;

            default:
                return report;

        }

        // point the IP to the next instruction
        currentW.addProc((IP + 1) % coreSize);

        currentW = currentW.getNextWarrior();

        return report;
    }

    protected void killProc(StepReport report) {
        // delete the current process
        report.pDeath = true;
        report.numProc = currentW.numProc();
        if (currentW.numProc() > 0) {
            currentW = currentW.getNextWarrior();
            return;
        }

        // else if that was the last process in that warrior kill it
        report.wDeath = true;
        numWarriors--;
        currentW.setPCell(0, numWarriors);
        currentW = currentW.getNextWarrior();
        currentW.getPrevWarrior().Remove();
        return;
    }
}
