package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Inputs {
    public Joystick j_leftStick;
    public Joystick j_rightStick;

    public boolean isCameraButtonPressed;
    public boolean isDualSpeedShifterButtonPressed;
    public boolean isSolenoidOneButtonPressed;
    public boolean isSolenoidTwoButtonPressed;
    public boolean isSolenoidThreeButtonPressed;

    public Inputs(int leftJoystickPort, int rightJoystickPort) {
        j_leftStick = new Joystick(leftJoystickPort);
        j_rightStick = new Joystick(rightJoystickPort);
    }

    public void ReadValues() {
        isCameraButtonPressed = j_leftStick.getRawButton(5);
        isDualSpeedShifterButtonPressed = j_leftStick.getRawButton(6);
        isSolenoidOneButtonPressed = j_rightStick.getRawButton(1);
        isSolenoidTwoButtonPressed = j_rightStick.getRawButton(2);
        isSolenoidThreeButtonPressed = j_rightStick.getRawButton(6);
    }
}
