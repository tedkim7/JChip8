package org.chip8;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Display unit of the CHIP-8 emulator.
 * @author Ted Kim
 */

public class Display {

    private int[][] displayGrid;
    private int scale;
    private GraphicsContext gc;

    public Display(GraphicsContext gc) {
        scale = 15;
        displayGrid = new int[64][32]; // dimensions of screen. 0: off (black), 1: on (white)
        this.gc = gc;
    }

    /** Clears the screen. */
    public void clear() {
        int rowMax = displayGrid.length;
        int colMax = displayGrid[0].length;
        for (int row = 0; row < rowMax; row++) {
            for (int col = 0; col < colMax; col++) {
                displayGrid[row][col] = 0;
            }
        }
        render();
    }

    /** Renders the screen based on the current displayGrid. */
    public void render() {
        int rowMax = displayGrid.length;
        int colMax = displayGrid[0].length;
        for (int row = 0; row < rowMax; row++) {
            for (int col = 0; col < colMax; col++) {
                if (displayGrid[row][col] == 0) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(row * scale, col * scale, scale, scale);
            }
        }
    }

    /** Returns the pixel at row, col. */
    public int getPixel(int row, int col) {
        return displayGrid[row][col];
    }

    /** Updates the pixel at row, col.
     *  On CHIP-8, the pixel is set like an XOR operation: black ^ white = white, white ^ white = black
     * */
    public void setPixel(int row, int col, int pixel) {
        displayGrid[row][col] ^= pixel;
    }

}
