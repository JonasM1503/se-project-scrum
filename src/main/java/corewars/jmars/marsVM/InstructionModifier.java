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

public class InstructionModifier {

    protected byte modifier;

    // valid modifiers
    final public static byte mF = 0;
    final public static byte mA = 1;
    final public static byte mB = 2;
    final public static byte mAB = 3;
    final public static byte mBA = 4;
    final public static byte mX = 5;
    final public static byte mI = 6;
    final public static byte MODIFIERS = 7;

    public InstructionModifier() {
        modifier = mF;
    }

    public byte get() {
        return modifier;
    }

    public void set(byte mod) throws InstructionException {
        if (mod < 0 || mod >= MODIFIERS) {
            throw new InstructionException("Bad modifier");
        }
        modifier = mod;
    }

    public void set(InstructionModifier mo) {
        modifier = mo.get();
    }

    public String toString() {
        switch (modifier) {
            case mF:
                return "F";
            case mA:
                return "A";
            case mB:
                return "B";
            case mAB:
                return "AB";
            case mBA:
                return "BA";
            case mX:
                return "X";
            case mI:
                return "I";
            default:
                throw new InstructionException("Unknown modifier");
        }
    }
}
