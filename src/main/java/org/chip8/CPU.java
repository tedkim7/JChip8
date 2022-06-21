package org.chip8;

import java.util.Random;

/** Implementation of the CHIP-8's CPU.
 * @author Ted Kim
 * */

public class CPU {

    private int[] V; // registers
    private int[] memory; // 4kb memory
    private int index; // index register
    private int pc; // program counter
    private int[] stack; // stack (keeps track of program counters)
    private int pointer; // stack pointer: keep track of where we are in the stack
    private int delayTimer; // delay timer
    private int soundTimer; // sound timer, probably will not implement
    private Display display; // the display
    private Controller controller; // deals with keyboard inputs
    private int opcode; // current opcode

    public CPU(Controller controller, Display display) {
        V = new int[16];
        memory = new int[4096];
        index = 0;
        pc = 0x200;
        stack = new int[16];
        pointer = 0;
        this.display = display;
        this.controller = controller;
        initializeCharacters();
    }

    /** Add characters to memory. The CPU expects to find these characters in memory starting at byte 0x50. */
    public void initializeCharacters() {
        int[] chars = new int[] {
                0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                0x20, 0x60, 0x20, 0x20, 0x70, // 1
                0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        };
        System.arraycopy(chars, 0, memory, 0x50, chars.length);
    }

    /** Loads the ROM into memory, starting at 0x200. */
    public void loadROM(int[] rom) {
        System.arraycopy(rom, 0, memory, 0x200, rom.length);
    }

    /** Performs the fetch-decode-execute operation of this CPU. */
    public void nextOpcode() {
        if (delayTimer > 0) { // if the delay timer is set, decrement it.
            delayTimer -= 1;
        }
        opcode = ((memory[pc++] & 0xFF) << 8) + (memory[pc++] & 0xFF);
        switch (opcode & 0xF000) { // $1nnn, $2nnn, $3xkk, $4xkk, $5xy0, $6xkk, $7xkk, $9xy0, $Annn, $Bnnn, $Cxkk, $Dxyn
            case 0x1000:
                op1nnn();
                return;
            case 0x2000:
                op2nnn();
                return;
            case 0x3000:
                op3xkk();
                return;
            case 0x4000:
                op4xkk();
                return;
            case 0x5000:
                op5xy0();
                return;
            case 0x6000:
                op6xkk();
                return;
            case 0x7000:
                op7xkk();
                return;
            case 0x9000:
                op9xy0();
                return;
            case 0xA000:
                opAnnn();
                return;
            case 0xB000:
                opBnnn();
                return;
            case 0xC000:
                opCxkk();
                return;
            case 0xD000:
                opDxyn();
                return;
        }

        switch (opcode & 0xF00F) { // $8xy0, $8xy1, $8xy2, $8xy3, $8xy4, $8xy5, $8xy6, $8xy7, $8xyE
            case 0x8000:
                op8xy0();
                return;
            case 0x8001:
                op8xy1();
                return;
            case 0x8002:
                op8xy2();
                return;
            case 0x8003:
                op8xy3();
                return;
            case 0x8004:
                op8xy4();
                return;
            case 0x8005:
                op8xy5();
                return;
            case 0x8006:
                op8xy6();
                return;
            case 0x8007:
                op8xy7();
                return;
            case 0x800E:
                op8xyE();
                return;
        }

        switch (opcode) { // $00E0, $00EE
            case 0x00E0:
                op00e0();
                return;
            case 0x00EE:
                op00ee();
                return;
        }

        switch (opcode & 0xF0FF) { // $ExA1, $Ex9E, $Fx07, $Fx0A, $Fx15, $Fx18, $Fx1E, $Fx29, $Fx33, $Fx55, $Fx65
            case 0xE0A1:
                opExA1();
                return;
            case 0xE09E:
                opEx9E();
                return;
            case 0xF007:
                opFx07();
                return;
            case 0xF00A:
                opFx0A();
                return;
            case 0xF015:
                opFx15();
                return;
            case 0xF018:
                opFx18();
                return;
            case 0xF01E:
                opFx1E();
                return;
            case 0xF029:
                opFx29();
                return;
            case 0xF033:
                opFx33();
                return;
            case 0xF055:
                opFx55();
                return;
            case 0xF065:
                opFx65();
                return;
        }
    }

    /** Handling opcodes. */

    /** 00E0: CLS */
    public void op00e0() {
        display.clear();
    }

    /** 00EE: RET */
    public void op00ee() {
        pointer -= 1; // go to last instruction
        pc = stack[pointer];
    }

    /** 1nnn: JP addr */
    public void op1nnn() {
        pc = (opcode & 0x0FFF); // get rid of the '1' in front of the opcode to get just the address.
    }

    /** 2nnn: CALL addr */
    public void op2nnn() {
        stack[pointer] = pc;
        pointer += 1;
        pc = (opcode & 0x0FFF); // get rid of the '2' in front of the opcode to get just the address.
    }

    /** 3xkk: SE Vx, byte */
    public void op3xkk() {
        int x = (opcode & 0x0F00) >> 8;
        int kk = opcode & 0xFF;
        if ((V[x] & 0xFF) == kk) {
            pc += 2; // skips next two hex codes that correspond to an opcode.
        }
    }

    /** 4xkk: SNE Vx, byte */
    public void op4xkk() {
        int x = (opcode & 0x0F00) >> 8;
        int kk = opcode & 0xFF;
        if ((V[x] & 0xFF) != kk) {
            pc += 2; // skips next two hex codes that correspond to an opcode.
        }
    }

    /** 5xy0: SE Vx, Vy */
    public void op5xy0() {
        int x = (opcode & 0x0F00) >> 8;
        int y = (opcode & 0x00F0) >> 4;
        if ((V[x] & 0xFF) == (V[y] & 0xFF)) {
            pc += 2;
        }
    }

    /** 6xkk: LD Vx, byte */
    public void op6xkk() {
        int x = (opcode & 0xF00) >> 8;
        int kk = opcode & 0xFF;
        V[x] = kk;
    }

    /** 7xkk: ADD Vx, byte */
    public void op7xkk() {
        int x = (opcode & 0xF00) >> 8;
        int kk = opcode & 0xFF;
        V[x] += kk;
    }

    /** 8xy0: LD Vx, Vy */
    public void op8xy0() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        V[x] = V[y];
    }

    /** 8xy1: OR Vx, Vy */
    public void op8xy1() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        V[x] |= V[y];
    }

    /** 8xy2: AND Vx, Vy */
    public void op8xy2() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        V[x] &= V[y];
    }

    /** 8xy3: XOR Vx, Vy */
    public void op8xy3() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        V[x] ^= V[y];
    }

    /** 8xy4: ADD Vx, Vy */
    public void op8xy4() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        int newX = (V[x] & 0xFF) + (V[y] & 0xFF);
        if (newX > 255) {
            V[0xF] = 1;
        } else {
            V[0xF] = 0;
        }
        V[x] = newX & 0xFF; // chop off potential overflow (ie. there would be a '1' in the 8th bit place)
    }

    /** 8xy5: SUB Vx, Vy */
    public void op8xy5() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        if ((V[x] & 0xFF) > (V[y] & 0xFF)) {
            V[0xF] = 1;
        } else {
            V[0xF] = 0;
        }
        V[x] = (V[x] & 0xFF) - (V[y] & 0xFF); // V[x] -= V[y]
    }

    /** 8xy6: SHR Vx */
    public void op8xy6() {
        int x = (opcode & 0xF00) >> 8;
        V[0xF] = V[x] & 0x1;
        V[x] >>= 1;
    }

    /** 8xy7: SUBN Vx, Vy */
    public void op8xy7() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        if ((V[y] & 0xFF) > (V[x] & 0xFF)) {
            V[0xF] = 1;
        } else {
            V[0xF] = 0;
        }
        V[x] = (byte) (V[y] - V[x]);
    }

    /** 8xyE: SHL Vx {, Vy} */
    public void op8xyE() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        V[0xF] = (V[x] & 0x80) >> 7;
        V[x] <<= 1;
    }

    /** 9xy0: SNE Vx, Vy */
    public void op9xy0() {
        int x = (opcode & 0x0F00) >> 8;
        int y = (opcode & 0x00F0) >> 4;
        if ((V[x] & 0xFF) != (V[y] & 0xFF)) {
            pc += 2;
        }
    }

    /** Annn: LD I, addr */
    public void opAnnn() {
        index = (opcode & 0xFFF);
    }

    /** Bnnn: JP V0, addr */
    public void opBnnn() {
        pc = ((V[0] & 0xFF) + (opcode & 0x0FFF));
    }

    /** Cxkk: RND Vx, byte */
    public void opCxkk() {
        Random rand = new Random();
        byte[] randByte = new byte[1];
        byte x = (byte) ((opcode & 0xF00) >> 8);
        byte kk = (byte) (opcode & 0xFF);
        rand.nextBytes(randByte);
        V[x] = randByte[0] & kk;
    }

    /** Dxyn: DRW Vx, Vy, nibble */
    public void opDxyn() {
        int x = (opcode & 0xF00) >> 8;
        int y = (opcode & 0xF0) >> 4;
        int lines = opcode & 0xF;
        int xCoord = (V[x] & 0xFF) % 64;
        int yCoord = (V[y] & 0xFF) % 32;

        for (int row = 0; row < lines; row++) {

            int spriteRow = (memory[index + row] & 0xFF);

            for (int col = 0; col < 8; col++) {
                int spritePixel = spriteRow & (0x80 >> col);
                int yOffset = (yCoord + row) % 32 + ((yCoord + row) / 32); // handle "overflow"
                int xOffset = (xCoord + col) % 64;
                int displayPixel = display.getPixel(xOffset, yOffset);

                if (spritePixel != 0) { // sprite pixel is on

                    if (displayPixel != 0) { // display is on --> pixel gets erased
                        V[0xF] = 1;
                    } else { // display is off --> pixel is not erased
                        V[0xF] = 0;
                    }

                    display.setPixel(xOffset, yOffset, 1);
                }
            }
        }
        display.render();
    }

    /** Ex9E: SKP Vx */
    public void opEx9E() {
        int x = (opcode & 0xF00) >> 8;
        int key = (V[x] & 0xFF);
        if (controller.getKey(key)) { // true --> key is pressed
            pc += 2;
        }
    }

    /** ExA1: SKNP Vx */
    public void opExA1() {
        int x = (opcode & 0x0F00) >> 8;
        int key = (V[x] & 0xFF);
        if (!controller.getKey(key)) { // true --> key is pressed
            pc += 2;
        }
    }

    /** Fx07: LD Vx, DT */
    public void opFx07() {
        int x = (opcode & 0x0F00) >> 8;
        V[x] = delayTimer;
    }

    /** Fx0A: LD Vx, K */
    public void opFx0A() {
        int x = (opcode & 0x0F00) >> 8;
        boolean keyPressed = false;

        while (!keyPressed) {
            for (int i = 0; i < 16; i++) { // check all keys (indexes 0 - 15)
                if (controller.getKey(i)) { // if key pressed --> update Vx
                    V[x] = i;
                    keyPressed = true;
                    break;
                }
            }
        }
    }

    /** Fx15: LD DT, Vx */
    public void opFx15() {
        int x = (opcode & 0xF00) >> 8;
        delayTimer = (V[x] & 0xFF);
    }

    /** Fx18: LD ST, Vx */
    public void opFx18() {
        int x = (opcode & 0xF00) >> 8;
        soundTimer = (V[x] & 0xFF);
    }

    /** Fx1E: ADD I, Vx */
    public void opFx1E() {
        int x = (opcode & 0x0F00) >> 8;
        index += (V[x] & 0xFF);
    }

    /** Fx29: LD F, Vx */
    public void opFx29() {
        int x = (opcode & 0x0F00) >> 8;
        index = 0x50 + ((V[x] & 0xFF) * 5); // fonts start at 0x50 in memory, each font occupies 5 bytes
    }

    /** Fx33: LD B, Vx */
    public void opFx33() {
        int x = (opcode & 0x0F00) >> 8;
        memory[index] = (V[x] & 0xFF) / 100; // hundreds digit of V[x]
        memory[index + 1] = ((V[x] & 0xFF) % 100) / 10; // tens digit of V[x]
        memory[index + 2] = ((V[x] & 0xFF) % 100) % 10; // ones digit of V[x]
    }

    /** Fx55: LD [I], Vx */
    public void opFx55() {
        int x = (opcode & 0x0F00) >> 8;
        for (int i = 0; i <= x; i++) {
            memory[index + i] = V[i];
        }
    }

    /** Fx65: LD Vx, [I] */
    public void opFx65() {
        int x = (opcode & 0x0F00) >> 8;
        for (int i = 0; i <= x; i++) {
            V[i] = memory[index + i];
        }
    }
}
