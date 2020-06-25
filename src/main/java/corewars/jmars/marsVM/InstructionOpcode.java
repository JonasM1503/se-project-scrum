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

public class InstructionOpcode {

    protected byte opcode;

    // valid opcodes
    final public static byte DAT = 0;
    final public static byte MOV = 1;
    final public static byte ADD = 2;
    final public static byte SUB = 3;
    final public static byte MUL = 4;
    final public static byte DIV = 5;
    final public static byte MOD = 6;
    final public static byte JMZ = 7;
    final public static byte JMN = 8;
    final public static byte DJN = 9;
    final public static byte SEQ = 10;
    final public static byte SNE = 11;
    final public static byte SLT = 12;
    final public static byte SPL = 13;
    final public static byte JMP = 14;
    final public static byte NOP = 15;
    final public static byte LDP = 16;
    final public static byte STP = 17;
    final public static byte OPCODES = 18;

    public InstructionOpcode() {
        opcode = DAT;
    }

    public InstructionOpcode(byte op) throws InstructionException {
        set(op);
    }

    public byte get() {
        return opcode;
    }

    public void set(byte op) throws InstructionException {
        if (op < 0 || op >= OPCODES) {
            throw new InstructionException("Bad opcode");
        }
        opcode = op;
    }

    public void set(InstructionOpcode op) {
        opcode = op.get();
    }

    public String toString() {
        switch (opcode) {
            case DAT:
                return "DAT";
            case MOV:
                return "MOV";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case DIV:
                return "DIV";
            case MOD:
                return "MOD";
            case JMZ:
                return "JMZ";
            case JMN:
                return "JMN";
            case DJN:
                return "DJN";
            case SEQ:
                return "SEQ";
            case SNE:
                return "SNE";
            case SLT:
                return "SLT";
            case SPL:
                return "SPL";
            case JMP:
                return "JMP";
            case NOP:
                return "NOP";
            case LDP:
                return "LDP";
            case STP:
                return "STP";
            default:
                throw new InstructionException("Unknown opcode");
        }
    }

}
