package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Inputs {
    public Joystick j_leftStick;
    public Joystick j_rightStick;

    private boolean isCameraButtonPressed;
    private boolean isIntakeButtonPressed;
    private boolean isDualSpeedShifterButtonPressed;
    private boolean isSolenoidOneButtonPressed;
    private boolean isSolenoidTwoButtonPressed;
    private boolean isSolenoidThreeButtonPressed;
    private boolean isIntakeOutButtonPressed;
    private boolean isScaleLiftButtonPressed;
    private boolean isPortalLiftButtonPressed;
    private boolean isDefaultLiftButtonPressed;
    
    private double throttle;
    private double turn;


    public Inputs(int leftJoystickPort, int rightJoystickPort) {
        j_leftStick = new Joystick(leftJoystickPort);
        j_rightStick = new Joystick(rightJoystickPort);
    }
    
    // Public Booleans
    public boolean getIsCameraButtonPressed() { return isCameraButtonPressed; }
    public boolean getIsDualSpeedShifterButtonPressed() { return isDualSpeedShifterButtonPressed; }
    public boolean getIsSolenoidOneButtonPressed() { return isSolenoidOneButtonPressed; }
    public boolean getIsSolenoidTwoButtonPressed() { return isSolenoidTwoButtonPressed; }
    public boolean getIsSolenoidThreeButtonPressed() { return isSolenoidThreeButtonPressed; }
    public boolean getIsIntakeButtonPressed() { return isIntakeButtonPressed;  }
    public boolean getIsIntakeOutButtonPressed() { return isIntakeOutButtonPressed; }
    public boolean getisScaleLiftButtonPressed() { return isScaleLiftButtonPressed;}
    public boolean getisPortalLiftButtonPressed() { return isPortalLiftButtonPressed;}
    public boolean getisDefaultLiftButtonPressed() { return isDefaultLiftButtonPressed;}
    
    // Public doubles
    public double getThrottle() { return throttle;}
    public double getTurn() { return turn;}

    public void ReadValues() {
    	//Driver Controller
		// Private doubles
		throttle = -j_leftStick.getY(); // xbox left X, positive is forward
		turn = j_leftStick.getX(); // xbox right X, positive means turn right

		// Private booleans
        isCameraButtonPressed = j_leftStick.getRawButton(5);
        isDualSpeedShifterButtonPressed = j_leftStick.getRawButton(6);
        isIntakeButtonPressed = j_leftStick.getRawButton(4); //moved to drive side
  
    //Operation Controller       
        // Private booleans
        isSolenoidOneButtonPressed = j_rightStick.getRawButton(6);
        isIntakeOutButtonPressed = j_rightStick.getRawButton(5);
        isScaleLiftButtonPressed = j_rightStick.getRawButton(4);
        isPortalLiftButtonPressed = j_rightStick.getRawButton(2);
        isDefaultLiftButtonPressed = j_rightStick.getRawButton(1);
        //  isSolenoidTwoButtonPressed = j_rightStick.getRawButton(2);
        //  isSolenoidThreeButtonPressed = j_rightStick.getRawButton(6);
        

    }
}
