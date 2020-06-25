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
 * reads in the file specified on the command line and
 * produces a listing.
 */
package corewars.jmars;

import java.io.*;
import java.awt.Color;

import corewars.jmars.assembler.*;

public class AsTest {

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("usage: AsTest filename");
            return;
        }

        try {
            FileInputStream file = new FileInputStream(args[0]);
            Memory warrior[];

            try {
                Assembler parser = new corewars.jmars.assembler.icws94p.ICWS94p();
                //Assembler parser = new Simple();

                parser.parseWarrior(file);

                System.out.println(";name " + parser.getName());
                System.out.println(";author " + parser.getAuthor() + "\n");
                System.out.println("ORG	" + parser.getStart());
                warrior = parser.getWarrior();

                for (int i = 0; i < warrior.length; i++) {
                    System.out.println(warrior[i]);
                }

            } catch (AssemblerException ae) {
                System.out.println("Error parsing warrior file.");
                System.out.print(ae.toString());
                return;
            } catch (IOException ioe) {
                System.out.print(ioe.toString());
                return;
            }

        } catch (FileNotFoundException e) {
            System.out.println("file could not be opened");
            return;
        }

        return;
    }
};
