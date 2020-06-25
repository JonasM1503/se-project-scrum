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
 * A pMARS style core display
 */
public class CoreDisplay extends Canvas implements StepListener, CycleListener {

    /* core background color */
    protected Color background;

    /* size of core */
    protected int coreSize;

    protected int width;
    protected int height;

    protected Graphics buffer;

    protected int[] x, y, style;
    protected Color[] fgColor;
    protected int bufferIndex;

    /**
     * Create a new core display for a specified core size and width.
     *
     * @param FrontEndManager man - Object managing the front end components.
     * @param Container con - Display container for this component.
     * @param int coreS - Size of core to be displayed.
     * @param int w - desired width of display.
     */
    public CoreDisplay(FrontEndManager man, Container con, int coreS, int w) {
        coreSize = coreS;
        width = w;
        height = (coreSize / width) + 1;

        x = new int[2000];
        y = new int[2000];
        style = new int[2000];
        fgColor = new Color[2000];
        bufferIndex = 0;

        background = Color.black;

        man.registerStepListener(this);
        man.registerCycleListener(this);
        con.add(this);
        buffer = getGraphics();
    }

    /**
     * Update display with info from a round.
     *
     * @param marsVM.StatReport report - info from round
     */
    public synchronized void stepProcess(StepReport report) {
        int i, j;
        int[] addr = null;
        Color tmpColor;

        // Grow the buffer if needed
        if (x.length - 20 < bufferIndex) {
            int[] newX = new int[x.length * 2];
            int[] newY = new int[x.length * 2];
            int[] newStyle = new int[x.length * 2];
            Color[] newColor = new Color[x.length * 2];

            for (i = 0; i <= bufferIndex; i++) {
                newX[i] = x[i];
                newY[i] = y[i];
                newStyle[i] = style[i];
                newColor[i] = fgColor[i];
            }

            x = newX;
            y = newY;
            style = newStyle;
            fgColor = newColor;

//			System.out.println("Increased buffer size "+ x.length);
        }

        tmpColor = report.warrior.getColor();

        for (i = 0; i != 4; i++) {
            switch (i) {
                case 0:
                    addr = report.addrRead();
                    break;

                case 1:
                    addr = report.addrWrite();
                    break;

                case 2:
                    addr = report.addrDec();
                    break;

                case 3:
                    addr = report.addrInc();
                    break;

                default:
                    System.out.println("ERROR: we reached the default case in CoreDisplay.stepProccess().");
                    break;
            }

            for (j = 0; j < addr.length; j++) {
                x[bufferIndex] = (addr[j] % width);
                y[bufferIndex] = (addr[j] / width);
                style[bufferIndex] = i;
                fgColor[bufferIndex] = tmpColor;
                bufferIndex++;
            }
        }

        if ((i = report.execAddr) != -1) {
            x[bufferIndex] = (i % width);
            y[bufferIndex] = (i / width);
            style[bufferIndex] = 4;

            if (report.pDeath) {
                fgColor[bufferIndex] = report.warrior.getDColor();
            } else {
                fgColor[bufferIndex] = tmpColor;
            }
        }
    }

    /**
     * CycleListener method.
     *
     * @param int c - number of cycles completed.
     */
    public void cycleFinished(int c) {
        repaint();
        return;
    }

    protected synchronized void updateBuffer() {
        int i;

        for (i = 0; i < bufferIndex; i++) {
            buffer.setColor(fgColor[i]);
            switch (style[i]) {
                case 0:
                    buffer.fillRect(x[i] * 10, y[i] * 10, 8, 8);
                    break;

                case 1:
                    buffer.fillRect(x[i] * 10, y[i] * 10 , 8, 8);
                    break;

                case 2:
                case 3:
                    buffer.fillRect(x[i] * 10, y[i] * 10, 8, 8);
                    break;
 
                case 4:
                    buffer.fillRect(x[i] * 10, y[i] * 10, 8, 8);
                    break;
            }
        }

        bufferIndex = 0;
    }

    /**
     * clear the display
     */
    public void clear() {
        buffer.setColor(background);
        buffer.fillRect(0, 0, width * 10 +1, height * 10 +1);
    }

    /**
     * Overide update to avoid clearing the component.
     *
     * @param java.awt.Graphics g - graphics context to pass to paint method.
     */
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * paint the display on the screen
     *
     * @param java.awt.Graphics screen - Graphics context to paint to
     */
    public void paint(Graphics g) {
        updateBuffer();        
        return;
    }

    /**
     * Get the maximum size for the display
     *
     * @return java.awt.Dimension - maximum size
     */
    public Dimension getMaximumSize() {
        return new Dimension(width * 10 + 1, height * 10 + 1);
    }

    /**
     * Get the preffered size for the display
     *
     * @return java.awt.Dimension - preferred size
     */
    public Dimension getPreferredSize() {
        return new Dimension(width * 10 + 1, height * 10 + 1);
    }

    /**
     * Get the minimum size for the display
     *
     * @return java.awt.Dimension - minimum size
     */
    public Dimension getMinimumSize() {
        return new Dimension(width * 10 + 1, height * 10 + 1);
    }

    /**
     * Get X alignment the display wants in the layout. Asks for a center
     * alignment.
     *
     * @return float - X alignment (0.5)
     */
    public float getAlignmentX() {
        return 0.5F;
    }

    /**
     * Get Y alignment the display wants in the layout. Asks for a center
     * alignment.
     *
     * @return float - Y alignment (0.5)
     */
    public float getAlignmentY() {
        return 0.5F;
    }
}
