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
package corewars.jmars.frontend;

import java.awt.*;

/**
 * A label displaying the current round and cycle.
 */
public class RoundCycleCounter extends Label implements CycleListener, RoundListener {

    protected int cycle, round;
    private boolean changed;

    /**
     * Creates a new Round/Cycle counter and places it in a container.
     *
     * @param frontend.FrontEndManager man - frontend manager to register, as a
     * round and cycle listener, with.
     * @param java.awt.Container con - container to place in.
     */
    public RoundCycleCounter(FrontEndManager man, Container con) {
        super("Round #0000, Cycle #000000", Label.CENTER);

        man.registerCycleListener(this);
        man.registerRoundListener(this);

        con.add(this);

        cycle = 1;
        round = 1;
        changed = true;

        return;
    }

    /**
     * CycleListener method.
     *
     * @param int c - number of cycles completed.
     */
    public void cycleFinished(int c) {
        cycle = c + 1;
        repaint();
        return;
    }

    /**
     * RoundListener method.
     *
     * @param int r - number of rounds completed.
     */
    public void roundResults(int r) {
        round = r + 2;
        repaint();
        return;
    }

    /**
     * java.awt.Component method overwridden to display round and cycle number.
     *
     * @param java.awt.Graphics g - graphics context to draw to.
     */
    public void paint(Graphics g) {
        setText("Round #" + round + ", Cycle #" + cycle);

        super.paint(g);
        return;
    }
}
