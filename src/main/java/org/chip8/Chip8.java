package org.chip8;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** Handles setting up the CHIP-8 system (CPU, controller, display, ROM data).
 *  @author Ted Kim
 */

public class Chip8 extends Application {

    static CPU cpu;
    static Controller controller;
    static Display display;
    static byte[] data;
    static int[] romData;

    private GraphicsContext gc;

    /** Sets up the graphical interface, initializes controller, display, and CPU, and starts
     *  the main loop.
     */
    @Override
    public void start(Stage stage) {

        Group root = new Group();
        stage.setTitle("CHIP-8 Emulator");
        stage.setResizable(false);
        Canvas canvas = new Canvas(64 * 15, 32 * 15);
        gc = canvas.getGraphicsContext2D();

        controller = new Controller();
        display = new Display(gc);
        cpu = new CPU(controller, display);

        cpu.loadROM(romData);

        display.clear();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);

        EventHandler<KeyEvent> keyPressed = new EventHandler<>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                controller.setKey(keyEvent.getText().toUpperCase(), true);
            }
        };

        EventHandler<KeyEvent> keyReleased = new EventHandler<>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                controller.setKey(keyEvent.getText().toUpperCase(), false);
            }
        };

        scene.setOnKeyPressed(keyPressed);
        scene.setOnKeyReleased(keyReleased);

        stage.setScene(scene);
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3), e -> cpu.nextOpcode()));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public static void main(String[] args) throws Exception {

        File rom = new File("roms/" + args[0]);

        data = new byte[(int) rom.length()];
        romData = new int[data.length];

        try {
            FileInputStream scan = new FileInputStream(rom);
            scan.read(data, 0, data.length);
            for (int i = 0; i < data.length; i++) {
                romData[i] = data[i] & 0xFF;
            }
        } catch (IOException e) {
            throw new Exception("File " + rom.getName() + " does not exist.");
        }
        launch();
    }

}
