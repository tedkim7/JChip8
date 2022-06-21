# JChip8: A Java CHIP-8 Emulator

This is an emulator for the CHIP-8 interpreted language. CHIP-8 was originally developed for the COSMAC VIP and Telmac 1800 microcomputers of the 1970s. You can read more about CHIP-8 [here](https://en.wikipedia.org/wiki/CHIP-8).

## About This Project
Throughout this project, I familiarized myself with bitwise operations, (CHIP-8) assembly language, and the design and function of a CPU (registers, stacks, fetch-decode-execute cycle, etc.). Computer architecture has been something that has always intrigued me, and this project was a great first opportunity for me to explore the basics of the subject. This was also my first time using the JavaFX library to handle the graphical side of my program, and I found it to be very convenient to use.

## Features
* Passes all tests
* Runs most games and programs
* Implements keyboard functionality via JavaFX

## To-Do
* Sound

## Keyboard Mappings
The keyboard of a CHIP-8 system looked something like this: 

![Source: https://internet-janitor.itch.io/octo/devlog/110279/adaptive-touch-input](https://img.itch.zone/aW1nLzI2ODk1NzIuanBlZw==/original/xZ5LTR.jpeg)
In order to preserve the keypad nature of the keyboard, I stylized my keyboard like this:
|  |  |  |  |
|--|--|--|--|
| 1 | 2 | 3 | 4 |
| Q | W | E | R |
| A | S | D | F |
| Z | X | X | C |


## Gallery
![Test cases passed](https://i.imgur.com/SDy2DAi.png "Passed test cases")![Breakout](https://i.imgur.com/vkyEJe5.png "Playing Breakout")
![Space Invaders](https://i.imgur.com/COvzDOg.png "Space Invaders title screen")

## Installing
After cloning, run

    mvn clean package

Put your ROMs in the `roms` folder.

Run the emulator with

    java -jar target/chip8-1.0.jar [rom name, with extension]
## References
[Cowgod's CHIP-8 reference document](http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#2.1) - an essential document that covers every aspect of the CHIP-8

[r/emudev](www.reddit.com/r/emudev) -  Reddit emulation development community that provided a lot of insight in how to approach this project

