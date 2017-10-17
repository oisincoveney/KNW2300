package RobotGUIPackage;

import rxtxrobot.ArduinoUno;
import rxtxrobot.RXTXRobot;

import java.util.Arrays;

public class Robot {

    private static RXTXRobot robot;
    private static final String ROBOT_PORT = "COM5";
    private static final int SERVO_180_PIN = 11;
    private static final int PING_PIN = 8;
    private static final int TEMP_PIN = 1;
    private static final int WIND_PIN = 2;
    private static final int BUMP_SENSOR = 0;
    private static final int MOTOR1_PIN = 6;
    private static final int MOTOR2_PIN = 5;

    // Number of milliseconds for the robot to travel 1 meter.
    private static final int ROTATION_MS = 100;

    Robot() {
        robot = new ArduinoUno();
        robot.setPort(ROBOT_PORT);
        robot.connect();
        robot.attachMotor(RXTXRobot.MOTOR1, MOTOR1_PIN);
        robot.attachMotor(RXTXRobot.MOTOR2, MOTOR2_PIN);
        robot.attachServo(RXTXRobot.SERVO1, SERVO_180_PIN);
        refresh();
    }

    private int getThermistorReading() {
        int sum = 0;
        int readingCount = 10;
        for (int i = 0; i < readingCount; i++) {
            robot.refreshAnalogPins();
            sum += robot.getAnalogPin(TEMP_PIN).getValue();
        }
        return sum / readingCount;
    }

    void motors() {
        robot.runMotor(RXTXRobot.MOTOR1, -165, RXTXRobot.MOTOR2,
                200, 0);

        int b = bump();
        while (b > 100)
            b = bump();


        robot.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2,
                0, 0);
    }

    void motors(int meters) {
        float meterTime = 7600 / 3;
        robot.runMotor(RXTXRobot.MOTOR1, -165, RXTXRobot.MOTOR2,
                200, (int) (meterTime * meters));
    }

    private int bump() {
        refresh();
        return robot.getAnalogPin(BUMP_SENSOR).getValue();
    }

    private void refresh() {
        robot.refreshAnalogPins();
        robot.refreshDigitalPins();
    }

    float ping() {
        int[] reads = new int[10];

        for(int i = 0; i < 10; i++)
            reads[i] = robot.getPing(PING_PIN);

        Arrays.sort(reads);
        System.out.println(Arrays.toString(reads));
        return (reads[2]);
    }

    void servo(int angle) {
        if (angle > 180 || angle < 0)
            throw new IllegalArgumentException("Angle is " + angle +
                    "but should be within [0, 180]");
        robot.moveServo(RXTXRobot.SERVO1, angle);
        robot.sleep(3000);
        robot.moveServo(RXTXRobot.SERVO1, 0);
    }

    int temperature() {
        return getThermistorReading();
    }

    int conductivity() {
        return 0;
    }

    int anemometer() {
        refresh();
        int sum = 0;
        int readingCount = 10;
        for (int i = 0; i < readingCount; i++) {
            robot.refreshAnalogPins();
            sum += robot.getAnalogPin(WIND_PIN).getValue();
        }
        return sum / readingCount;
    }

    public void finalize() {
        robot.close();
    }
}
