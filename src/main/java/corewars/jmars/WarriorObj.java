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
 * Holds compiled warrior and all warrior stats.
 */
package corewars.jmars;

import corewars.jmars.assembler.Assembler;

import java.awt.Color;
import java.io.Reader;

public class WarriorObj {

    public Memory wInst[];
    public int wOffset;
    protected int[] pSpace;

    protected String name;
    protected String author;

    public Color myColor;
    public Color dColor;
    public int numProc;
    public boolean Alive;

    public WarriorObj(Memory[] warrior, int start, Color c, Color d) {
        myColor = c;
        dColor = d;

        wInst = warrior;
        wOffset = start;
    }

    public Memory[] getMemory(int coreSize) {
        Memory[] wNormal = new Memory[wInst.length];

        for (int i = 0; i < wInst.length; i++) {
            wNormal[i] = new Memory(wInst[i]);

            while (wNormal[i].aValue < 0) {
                wNormal[i].aValue += coreSize;
            }

            wNormal[i].aValue %= coreSize;

            while (wNormal[i].bValue < 0) {
                wNormal[i].bValue += coreSize;
            }

            wNormal[i].bValue %= coreSize;
        }

        return wNormal;
    }

    public Color getColor() {
        return myColor;
    }

    public Color getDColor() {
        return dColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getOffset() {
        return wOffset;
    }

    public void initPSpace(int length) {
        pSpace = new int[length];

        return;
    }

    public int[] getPSpace() {
        return pSpace;
    }

    public void normalizePSpace(int coreSize) {
        for (int i = 0; i < pSpace.length; i++) {
            while (pSpace[i] < 0) {
                pSpace[i] += coreSize;
            }

            pSpace[i] %= coreSize;
        }

        return;
    }

    public void setPSpace(int[] p) {
        pSpace = p;
        return;
    }

    public int getPCell(int index) {
        if (pSpace == null || index < 0 || index >= pSpace.length) {
            return 0;
        }

        return pSpace[index];
    }

    public boolean setPCell(int index, int value) {
        if (index < 0 || index >= pSpace.length) {
            return false;
        }

        pSpace[index] = value;

        return true;
    }
}
