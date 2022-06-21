package org.chip8;

import java.util.Map;

import static java.util.Map.entry;

/** The controller of the CHIP-8, handles user input.
 * @author Ted Kim
 * */

public class Controller {

    private boolean[] keys; // true --> pressed, false --> not pressed
    private Map<String, Integer> keyMapping; // is our Map key a string?

    public Controller() {
        keys = new boolean[16];
        keyMapping = Map.ofEntries(
                entry("1", 0), // 1
                entry("2", 1), // 2
                entry("3", 2), // 3
                entry("4", 3), // C
                entry("Q", 4), // 4
                entry("W", 5), // 5
                entry("E", 6), // 6
                entry("R", 7), // D
                entry("A", 8), // 7
                entry("S", 9), // 8
                entry("D", 10), // 9
                entry("F", 11), // E
                entry("Z", 12), // A
                entry("X", 13), // 0
                entry("C", 14), // B
                entry("V", 15) // F
        );
    }

    /** Sets the KEY to be the value of VALUE. */
    public void setKey(String key, boolean value) {
        try {
            keys[keyMapping.get(key)] = value;
        } catch (NullPointerException n) { // ignore bad input
        }
    }

    /** Gets the value of the indexed key. */
    public boolean getKey(int index) {
        return keys[index];
    }

}
