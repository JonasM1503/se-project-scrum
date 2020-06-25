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

public class InstructionMode {

    protected byte mode;

    final public static byte DIRECT = 0;
    final public static byte IMMEDIATE = 1;
    final public static byte INDIRECT_B = 2;
    final public static byte INDIRECT_B_PREDEC = 3;
    final public static byte INDIRECT_B_POSTINC = 4;
    final public static byte INDIRECT_A = 5;
    final public static byte INDIRECT_A_PREDEC = 6;
    final public static byte INDIRECT_A_POSTINC = 7;
    final public static byte MODES = 8;

    public InstructionMode() {
        mode = DIRECT;
    }

    public byte get() {
        return mode;
    }

    public void set(byte mo) throws InstructionException {
        if (mo < 0 || mo >= MODES) {
            throw new InstructionException("Bad mode");
        }
        mode = mo;
    }

    public void set(InstructionMode mo) {
        mode = mo.get();
    }

    public String toString() {
        switch (mode) {
            case DIRECT:
                return "$";
            case IMMEDIATE:
                return "#";
            case INDIRECT_B:
                return "@";
            case INDIRECT_B_PREDEC:
                return "<";
            case INDIRECT_B_POSTINC:
                return ">";
            case INDIRECT_A:
                return "*";
            case INDIRECT_A_PREDEC:
                return "{";
            case INDIRECT_A_POSTINC:
                return "}";
            default:
                throw new InstructionException("Unknown mode");
        }
    }
}
