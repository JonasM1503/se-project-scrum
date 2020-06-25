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
package corewars.jmars.assembler.icws94p;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;

import corewars.jmars.Memory;
import corewars.jmars.assembler.Assembler;
import corewars.jmars.assembler.AssemblerException;

public class ICWS94p implements Assembler {
    // The actual compiled warrior.

    protected Memory[] warrior;

    // Index of starting instruction.
    protected int startIP;

    // Name of warrior.
    protected String name;

    // Author of warrior.
    protected String author;

    // Strategy of warrior.
    protected List strategy;

    // Mapping of labels used by the assembler
    private Map labelMap;

    // Mapping of predefined equates
    private SortedMap constMap;

    public ICWS94p() {
        constMap = new TreeMap(new LengthCompare());
    }

    public void parseWarrior(InputStream file) throws IOException, AssemblerException {
        BufferedReader in = new BufferedReader(new InputStreamReader(file));

        List warriorText = new ArrayList();
        String line;
        while ((line = in.readLine()) != null) {
            warriorText.add(line);
        }

        warrior = null;
        startIP = 0;
        name = null;
        author = null;
        strategy = new LinkedList();
        labelMap = new HashMap();

//		System.out.println("Parsing Comments and EQUs");
        warriorText = parseCommentsAndEQUs(warriorText);

//		System.out.println("Parsing Instructions");
        warrior = parseInstructions(warriorText);
    }

    public void addConstant(String label, String expansion) {
        constMap.put(label, expansion);
        return;
    }

    /**
     * Get the instructions that make up this warrior.<br>
     * Note: the instructions are not normalized for a specific core size.
     *
     * @returns corewars.jmars.marsVM.Memory - Array of instructions.
     */
    public Memory[] getWarrior() {
        return warrior;
    }

    /**
     * Get the starting instruction for the warrior.
     *
     * @returns int - first instruction to be executed.
     */
    public int getStart() {
        return startIP;
    }

    /**
     * Name of the warrior.
     *
     * @returns String - name of warrior. NULL if not set.
     */
    public String getName() {
        return name;
    }

    /**
     * Author of the warrior.
     *
     * @returns String - author of warrior. NULL if not set.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Warrior length.
     *
     * @returns int - length of warrior.
     */
    public int length() {
        return warrior.length;
    }

    // comparator to sort strings from largest to smallest.
    private class LengthCompare implements Comparator {

        public int compare(Object a, Object b) throws ClassCastException {
            int diff = 0 - (((String) a).length() - ((String) b).length());
            if (diff == 0) {
                return ((String) a).compareTo((String) b);
            }

            return diff;
        }

        public boolean equals(Object a, Object b) {
            if (compare(a, b) == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    protected List parseCommentsAndEQUs(List warriorText) throws AssemblerException {
        // Order the EQUates from longest label to shortest so that substrings don't get expanded
        SortedMap EQUMap = new TreeMap(new LengthCompare());
        EQUMap.putAll(constMap);

        for (int lineNumber = 0; lineNumber < warriorText.size(); lineNumber++) {
            String line = (String) warriorText.get(lineNumber);
            int cStart;
            if ((cStart = line.indexOf(';')) != -1) {
                if (line.toLowerCase().startsWith("name ", cStart + 1)) {
                    name = line.substring(cStart + 5).trim();
                } else if (line.toLowerCase().startsWith("author ", cStart + 1)) {
                    author = line.substring(cStart + 7).trim();
                } else if (line.toLowerCase().startsWith("strategy ", cStart + 1)) {
                    strategy.add(line.substring(cStart + 9).trim());
                }

                line = line.substring(0, cStart);
                warriorText.set(lineNumber, line);
            }

            int EQUStart;
            if ((EQUStart = line.toLowerCase().indexOf("equ")) != -1) {
                String newLabel = line.substring(0, EQUStart).trim();
                String newExpansion = line.substring(EQUStart + 3).trim();

                for (int charIndex = 0; charIndex < newLabel.length(); charIndex++) {
                    if (newLabel.charAt(charIndex) == ' ' || newLabel.charAt(charIndex) == '\t') {
                        throw new AssemblerException("Illegal label for EQU statement at line " + lineNumber);
                    }
                }

//				System.out.println("Expanding EQUate "+ newLabel);
                // Expand the current EQUate with any past expansions.
                Iterator labels = EQUMap.keySet().iterator();
                while (labels.hasNext()) {
                    String label = (String) labels.next();
                    String expansion = (String) EQUMap.get(label);

                    int lIndex = 0;
                    while ((lIndex = newExpansion.indexOf(label, lIndex)) != -1) {
                        newExpansion = newExpansion.substring(0, lIndex) + expansion + newExpansion.substring(lIndex + label.length());
//						System.out.println(label+", "+ newExpansion);
                    }
                }

                // If there is a recursive definition it will now show up.
                if (newExpansion.indexOf(newLabel) != -1) {
                    throw new AssemblerException("Recursive EQU declaration detected at line " + lineNumber);
                }

//				System.out.println("Expanding old equates with "+ newLabel);
                // Expand any old EQUates that had a forward reference to this one.
                labels = EQUMap.keySet().iterator();
                while (labels.hasNext()) {
                    String label = (String) labels.next();
                    String expansion = (String) EQUMap.get(label);

                    int lIndex = 0;
                    while ((lIndex = expansion.indexOf(newLabel, lIndex)) != -1) {
                        expansion = expansion.substring(0, lIndex) + newExpansion + expansion.substring(lIndex + newLabel.length());
//						System.out.println(newLabel+", "+ label+", "+ expansion);
                    }

                    EQUMap.put(label, expansion);
                }

                EQUMap.put(newLabel, newExpansion);

                warriorText.set(lineNumber, "");
            }
        }

//		System.out.println("Expanding equates in all lines");
        Iterator labels = EQUMap.keySet().iterator();
        while (labels.hasNext()) {
            String label = (String) labels.next();
            String expansion = (String) EQUMap.get(label);

            for (int expLine = 0; expLine < warriorText.size(); expLine++) {
                int lIndex = 0;
                while ((lIndex = ((String) warriorText.get(expLine)).indexOf(label, lIndex)) != -1) {
                    String newLine = ((String) warriorText.get(expLine)).substring(0, lIndex) + expansion + ((String) warriorText.get(expLine)).substring(lIndex + label.length());
                    warriorText.set(expLine, newLine);
                    lIndex = lIndex + expansion.length();
                }
            }
        }

        return warriorText;
    }

    private class AsmMemory extends Memory {

        String aExpr;
        String bExpr;
    }

    protected Memory[] parseInstructions(List warriorText) throws IOException, AssemblerException {

        List instructions = new LinkedList();

        StringBuffer warriorString = new StringBuffer();
        for (int i = 0; i < warriorText.size(); i++) {
            warriorString.append(warriorText.get(i) + "\n");
        }

        Lexer tok = new Lexer(warriorText);

        String startExpr = null;
        int startInstr = 0;
        int instrNumber = 1;
        while (tok.ttype != tok.TT_EOF && tok.nextToken() != tok.TT_EOF) {
            boolean instrParsed = false;
            while (tok.ttype == tok.TT_WORD && !(instrParsed = pOpcode(tok, instructions))) {
                if (tok.word.equalsIgnoreCase("org")) {
                    if (tok.nextToken() != tok.TT_WORD) {
                        throw new AssemblerException("Expected a value for org");
                    }

                    startExpr = tok.getExpression();

                    tok.nextToken();
                    break;
                } else if (tok.word.equalsIgnoreCase("end")) {
                    if (tok.nextToken() == tok.TT_WORD) {
                        startExpr = tok.getExpression();
                    }

                    // Force parsing to stop
                    tok.ttype = tok.TT_EOF;
                    break;
                } // it must be a label
                else {
                    labelMap.put(tok.word, new Integer(instrNumber));
//					System.out.println(tok.word +", "+ instrNumber);
                    tok.nextToken();
                }
            }

            if (instrParsed) {
                instrNumber++;
            }

            if (tok.ttype != tok.TT_EOL && tok.ttype != tok.TT_EOF) {
                throw new AssemblerException("Illegal data at instruction " + instrNumber + ". Starting with \"" + tok.toString() + "\"");
            }

        }

        ExprParser eParse = new ExprParser(constMap);

        if (startExpr != null) {
            startIP = eParse.evaluate(startExpr, 1);
        }

        instrNumber = 0;
        Iterator iter = instructions.iterator();
        while (iter.hasNext()) {
            AsmMemory cell = (AsmMemory) iter.next();
            instrNumber++;
            if (cell.aExpr != null) {
                cell.aValue = eParse.evaluate(cell.aExpr, instrNumber);
            }

            if (cell.bExpr != null) {
                cell.bValue = eParse.evaluate(cell.bExpr, instrNumber);
            }

//			System.out.println(cell.toString() + ", \""+ cell.aExpr +"\" \""+ cell.bExpr +"\"");
        }

        Object[] obj = instructions.toArray();

        Memory[] mem = new Memory[obj.length];

        for (int i = 0; i < obj.length; i++) {
            mem[i] = (Memory) obj[i];
        }

        return mem;
    }

    private boolean pOpcode(Lexer tok, List instructions) throws IOException, AssemblerException {
        if (tok.ttype == tok.TT_WORD) {
            AsmMemory cell = new AsmMemory();

            if (tok.word.equalsIgnoreCase("mov")) {
                cell.opcode = Memory.MOV;
            } else if (tok.word.equalsIgnoreCase("add")) {
                cell.opcode = Memory.ADD;
            } else if (tok.word.equalsIgnoreCase("sub")) {
                cell.opcode = Memory.SUB;
            } else if (tok.word.equalsIgnoreCase("mul")) {
                cell.opcode = Memory.MUL;
            } else if (tok.word.equalsIgnoreCase("div")) {
                cell.opcode = Memory.DIV;
            } else if (tok.word.equalsIgnoreCase("mod")) {
                cell.opcode = Memory.MOD;
            } else if (tok.word.equalsIgnoreCase("jmz")) {
                cell.opcode = Memory.JMZ;
            } else if (tok.word.equalsIgnoreCase("jmn")) {
                cell.opcode = Memory.JMN;
            } else if (tok.word.equalsIgnoreCase("djn")) {
                cell.opcode = Memory.DJN;
            } else if (tok.word.equalsIgnoreCase("cmp")) {
                cell.opcode = Memory.CMP;
            } else if (tok.word.equalsIgnoreCase("seq")) {
                cell.opcode = Memory.SEQ;
            } else if (tok.word.equalsIgnoreCase("slt")) {
                cell.opcode = Memory.SLT;
            } else if (tok.word.equalsIgnoreCase("spl")) {
                cell.opcode = Memory.SPL;
            } else if (tok.word.equalsIgnoreCase("dat")) {
                cell.opcode = Memory.DAT;
            } else if (tok.word.equalsIgnoreCase("jmp")) {
                cell.opcode = Memory.JMP;
            } else if (tok.word.equalsIgnoreCase("sne")) {
                cell.opcode = Memory.SNE;
            } else if (tok.word.equalsIgnoreCase("nop")) {
                cell.opcode = Memory.NOP;
            } else if (tok.word.equalsIgnoreCase("ldp")) {
                cell.opcode = Memory.LDP;
            } else if (tok.word.equalsIgnoreCase("stp")) {
                cell.opcode = Memory.STP;
            } else {
                return false;
            }

            pModifier(tok, cell);

            instructions.add(cell);
            return true;
        }

        return false;
    }

    void pModifier(Lexer tok, AsmMemory cell) throws IOException, AssemblerException {
        if (tok.nextToken() != '.') {
            // Assign correct default modifier
            switch (cell.opcode) {
                case Memory.DAT:
                    cell.modifier = Memory.mF;
                    pAOperand(tok, cell, false);
                    return;

                case Memory.JMP:
                case Memory.JMZ:
                case Memory.JMN:
                case Memory.DJN:
                case Memory.SPL:
                    cell.modifier = Memory.mB;
                    pAOperand(tok, cell, false);
                    return;

                default:
                    // Need more information before the correct modifier can be determined.
                    pAOperand(tok, cell, true);
                    return;
            }
        } else if (tok.nextToken() == tok.TT_WORD) {
            if (tok.word.equalsIgnoreCase("a")) {
                cell.modifier = Memory.mA;
            } else if (tok.word.equalsIgnoreCase("b")) {
                cell.modifier = Memory.mB;
            } else if (tok.word.equalsIgnoreCase("ab")) {
                cell.modifier = Memory.mAB;
            } else if (tok.word.equalsIgnoreCase("ba")) {
                cell.modifier = Memory.mBA;
            } else if (tok.word.equalsIgnoreCase("f")) {
                cell.modifier = Memory.mF;
            } else if (tok.word.equalsIgnoreCase("x")) {
                cell.modifier = Memory.mX;
            } else if (tok.word.equalsIgnoreCase("i")) {
                cell.modifier = Memory.mI;
            } else {
                throw new AssemblerException("Illegal modifier: " + tok.word);
            }

            tok.nextToken();

            pAOperand(tok, cell, false);
            return;
        } else {
            throw new AssemblerException();
        }

    }

    void pAOperand(Lexer tok, AsmMemory cell, boolean setModifier) throws IOException, AssemblerException {
        switch (tok.ttype) {
            case Lexer.TT_WORD:
                pAValue(tok, cell, setModifier);
                return;

            case '#':
                cell.aIndir = Memory.IMMEDIATE;
                cell.aTiming = Memory.PRE;
                cell.aAction = Memory.NONE;
                cell.aTarget = Memory.B;
                // If the modifier hasn't already been set we now have enough information to set it.
                if (setModifier) {
                    cell.modifier = Memory.mAB;
                    setModifier = false;
                }
                break;

            case '$':
                cell.aIndir = Memory.DIRECT;
                cell.aTiming = Memory.POST;
                cell.aAction = Memory.NONE;
                cell.aTarget = Memory.B;
                break;

            case '@':
                cell.aIndir = Memory.INDIRECT;
                cell.aTiming = Memory.POST;
                cell.aAction = Memory.NONE;
                cell.aTarget = Memory.B;
                break;

            case '<':
                cell.aIndir = Memory.INDIRECT;
                cell.aTiming = Memory.PRE;
                cell.aAction = Memory.DECREMENT;
                cell.aTarget = Memory.B;
                break;

            case '>':
                cell.aIndir = Memory.INDIRECT;
                cell.aTiming = Memory.POST;
                cell.aAction = Memory.INCREMENT;
                cell.aTarget = Memory.B;
                break;

            case '*':
                cell.aIndir = Memory.INDIRECT;
                cell.aTiming = Memory.POST;
                cell.aAction = Memory.NONE;
                cell.aTarget = Memory.A;
                break;

            case '{':
                cell.aIndir = Memory.INDIRECT;
                cell.aTiming = Memory.PRE;
                cell.aAction = Memory.DECREMENT;
                cell.aTarget = Memory.A;
                break;

            case '}':
                cell.aIndir = Memory.INDIRECT;
                cell.aTiming = Memory.POST;
                cell.aAction = Memory.INCREMENT;
                cell.aTarget = Memory.A;
                break;

            default:
                throw new AssemblerException();

        }

        tok.nextToken();

        pAValue(tok, cell, setModifier);
        return;
    }

    void pAValue(Lexer tok, AsmMemory cell, boolean setModifier) throws IOException, AssemblerException {
        if (tok.ttype != tok.TT_WORD) {
            throw new AssemblerException("pAValue");
        }

        cell.aExpr = tok.getExpression();

        if (tok.nextToken() != ',') {
            if (cell.opcode == Memory.DAT) {
                cell.aIndir = Memory.IMMEDIATE;
                cell.aTiming = Memory.PRE;
                cell.aAction = Memory.NONE;
                cell.aTarget = Memory.B;
                // If the modifier hasn't already been set we now have enough information to set it.
                if (setModifier) {
                    cell.modifier = Memory.mAB;
                    setModifier = false;
                }
                cell.aValue = 0;

                cell.bExpr = cell.aExpr;
                cell.aExpr = null;
            } else {
                cell.bIndir = Memory.IMMEDIATE;
                cell.bTiming = Memory.PRE;
                cell.bAction = Memory.NONE;
                cell.bTarget = Memory.B;

                if (setModifier) {
                    switch (cell.opcode) {
                        case Memory.MOV:
                        case Memory.CMP:
                        case Memory.ADD:
                        case Memory.SUB:
                        case Memory.MUL:
                        case Memory.DIV:
                        case Memory.MOD:
                            cell.modifier = Memory.mB;
                            setModifier = false;
                            break;

                        default:
                            break;
                    }
                }

                cell.bValue = 0;
            }

            return;
        }

        tok.nextToken();

        pBOperand(tok, cell, setModifier);
        return;
    }

    void pBOperand(Lexer tok, AsmMemory cell, boolean setModifier) throws IOException, AssemblerException {
        switch (tok.ttype) {
            case Lexer.TT_WORD:
                if (setModifier) {
                    switch (cell.opcode) {
                        case Memory.MOV:
                        case Memory.CMP:
                            cell.modifier = Memory.mI;
                            break;

                        case Memory.ADD:
                        case Memory.SUB:
                        case Memory.MUL:
                        case Memory.DIV:
                        case Memory.MOD:
                            cell.modifier = Memory.mF;
                            break;

                        case Memory.SLT:
                            cell.modifier = Memory.mB;
                            break;

                        default:
                            throw new AssemblerException("Internal assembler error. Modifier not set");
                    }
                }
                pBValue(tok, cell);
                return;

            case '#':
                cell.bIndir = Memory.IMMEDIATE;
                cell.bTiming = Memory.PRE;
                cell.bAction = Memory.NONE;
                cell.bTarget = Memory.B;

                if (setModifier) {
                    switch (cell.opcode) {
                        case Memory.MOV:
                        case Memory.CMP:
                        case Memory.ADD:
                        case Memory.SUB:
                        case Memory.MUL:
                        case Memory.DIV:
                        case Memory.MOD:
                            cell.modifier = Memory.mB;
                            setModifier = false;
                            break;

                        default:
                            break;
                    }
                }
                break;

            case '$':
                cell.bIndir = Memory.DIRECT;
                cell.bTiming = Memory.POST;
                cell.bAction = Memory.NONE;
                cell.bTarget = Memory.B;
                break;

            case '@':
                cell.bIndir = Memory.INDIRECT;
                cell.bTiming = Memory.POST;
                cell.bAction = Memory.NONE;
                cell.bTarget = Memory.B;
                break;

            case '<':
                cell.bIndir = Memory.INDIRECT;
                cell.bTiming = Memory.PRE;
                cell.bAction = Memory.DECREMENT;
                cell.bTarget = Memory.B;
                break;

            case '>':
                cell.bIndir = Memory.INDIRECT;
                cell.bTiming = Memory.POST;
                cell.bAction = Memory.INCREMENT;
                cell.bTarget = Memory.B;
                break;

            case '*':
                cell.bIndir = Memory.INDIRECT;
                cell.bTiming = Memory.POST;
                cell.bAction = Memory.NONE;
                cell.bTarget = Memory.A;
                break;

            case '{':
                cell.bIndir = Memory.INDIRECT;
                cell.bTiming = Memory.PRE;
                cell.bAction = Memory.DECREMENT;
                cell.bTarget = Memory.A;
                break;

            case '}':
                cell.bIndir = Memory.INDIRECT;
                cell.bTiming = Memory.POST;
                cell.bAction = Memory.INCREMENT;
                cell.bTarget = Memory.A;
                break;

            default:
                throw new AssemblerException();

        }

        // Set any modifiers that didn't get set earlier
        if (setModifier) {
            switch (cell.opcode) {
                case Memory.MOV:
                case Memory.CMP:
                    cell.modifier = Memory.mI;
                    break;

                case Memory.ADD:
                case Memory.SUB:
                case Memory.MUL:
                case Memory.DIV:
                case Memory.MOD:
                    cell.modifier = Memory.mF;
                    break;

                case Memory.SLT:
                case Memory.JMP:
                case Memory.JMZ:
                case Memory.JMN:
                case Memory.DJN:
                case Memory.SPL:
                    cell.modifier = Memory.mB;
                    break;

                default:
                    throw new AssemblerException("Internal assembler error. Modifier not set.");
            }
        }

        tok.nextToken();

        pBValue(tok, cell);
        return;
    }

    void pBValue(Lexer tok, AsmMemory cell) throws IOException, AssemblerException {
        if (tok.ttype != tok.TT_WORD) {
            throw new AssemblerException("pBValue");
        }

        cell.bExpr = tok.getExpression();

        tok.nextToken();

        return;
    }
}
