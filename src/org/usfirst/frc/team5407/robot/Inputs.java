package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Inputs {
    // Private DifferentialDrive drive;
    public Joystick j_leftStick;
    public Joystick j_rightStick;
    public boolean isCameraButtonPressed;

    public Inputs(int leftJoystickPort, int rightJoystickPort) {
        j_leftStick = new Joystick(leftJoystickPort);
        j_rightStick = new Joystick(rightJoystickPort);
    }

    public void ReadValues() {
        boolean isLeftStickButtonSixPressed = j_leftStick.getRawButton(6);
        air.s_DSShifter.set(isLeftStickButtonSixPressed);

        isCameraButtonPressed = j_leftStick.getRawButton(5);

        boolean isRightStickButtonOnePressed = j_rightStick.getRawButton(1);
        air.s_sol1.set(isRightStickButtonOnePressed);

        boolean isRightStickButtonTwoPressed = j_rightStick.getRawButton(2);
        air.s_sol2.set(isRightStickButtonTwoPressed);

        boolean isRightStickButtonSixPressed = j_rightStick.getRawButton(6);
        air.s_sol3.set(isRightStickButtonSixPressed);
    }
}
