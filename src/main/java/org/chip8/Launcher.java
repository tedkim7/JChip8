package org.chip8;

/** The sole purpose of this class is to act as a workaround for a Maven bug
 *  that prevents this project from compiling into an easy-to-use .jar file.
 * @author Ted Kim
 */

public class Launcher {
    public static void main(String[] args) throws Exception {
        Chip8.main(args);
    }
}
