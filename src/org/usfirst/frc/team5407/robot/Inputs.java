package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Inputs {
    public Joystick j_leftStick;
    public Joystick j_rightStick;
    public Joystick j_emJoy;
    
    public double shootSpeed;

    private boolean isCameraButtonPressed;
    private boolean isIntakeButtonPressed;
    private boolean isDualSpeedShifterButtonPressed;
    private boolean isSolenoidFourButtonPressed;
    private boolean isSolenoidTwoButtonPressed;   //x button claw release
    private boolean isSolenoidThreeButtonPressed;
    private boolean isIntakeOutButtonPressed;
    private boolean isSolenoidFiveButtonPressed;
    private boolean isSuperButtonPressed;
    private boolean isemJoyButtonPressed;// end game button
    
    private double throttle;//for drivetrain
    private double turn;//for drivetrain 
    private double winchSpeed;//end game lift winch 
    private double liftSpeed;

    public Inputs(int leftJoystickPort, int rightJoystickPort, int emJoy) {
        j_leftStick = new Joystick(leftJoystickPort);
        j_rightStick = new Joystick(rightJoystickPort);
        j_emJoy = new Joystick(emJoy);
    }
    
    // Public Booleans
    public boolean getIsCameraButtonPressed() { return isCameraButtonPressed; }
    public boolean getIsDualSpeedShifterButtonPressed() { return isDualSpeedShifterButtonPressed; }
    public boolean getIsSolenoidFourButtonPressed() { return isSolenoidFourButtonPressed; }
    public boolean getIsSolenoidTwoButtonPressed() { return isSolenoidTwoButtonPressed; }  // x button claw release
    public boolean getIsSolenoidThreeButtonPressed() { return isSolenoidThreeButtonPressed; }
    public boolean getIsSolenoidFiveButtonPresses() {return isSolenoidFiveButtonPressed; }
    public boolean getIsIntakeButtonPressed() { return isIntakeButtonPressed;  }
    public boolean getIsIntakeOutButtonPressed() { return isIntakeOutButtonPressed; }
    public boolean getIsSuperButtonPressed() {return isSuperButtonPressed;}
    public boolean getIsemJoyButtonPressed() {return isemJoyButtonPressed;}
    
    // Public doubles
    public double getThrottle() {return throttle;}
    public double getTurn() {return turn;}
    public double getWinchSpeed() {return winchSpeed;}
    public double getLiftSpeed() {return liftSpeed;}

    public void ReadValues() {
    	//Driver Controller
		// Private doubles
    	if (j_rightStick.getY() < 0.1 && j_rightStick.getY() > -0.1){
    		throttle = 0.0;
    	}else {
    		throttle = j_rightStick.getY(); // xbox left X, positive is forward
    	}
		
    	if (j_rightStick.getX() < 0.2 && j_rightStick.getX() > -0.2 ){
    		turn = 0.0;
    	}else {
    		turn = j_rightStick.getX(); // xbox right X, positive means turn right
    	}
    	
    	if (j_rightStick.getRawAxis(5) < 0.1 && j_rightStick.getRawAxis(5) > -0.1){
    		winchSpeed = 0.0;
    	}else {
    		winchSpeed = j_rightStick.getRawAxis(5); // xbox left X, positive is forward
    	}

		// Private booleans
        isCameraButtonPressed = j_rightStick.getRawButton(5);
        isDualSpeedShifterButtonPressed = j_rightStick.getRawButton(6);
        isSolenoidFiveButtonPressed = j_rightStick.getRawButton(1);
  
    //Operation Controller       
        // Private doubles
        if(j_leftStick.getRawAxis(1) < 0.2 && j_leftStick.getRawAxis(1) > -0.2){
        	liftSpeed = 0.0;
        }else{
        	liftSpeed = j_leftStick.getRawAxis(1);
        }
        
        // Private booleans
        isSolenoidFourButtonPressed = j_leftStick.getRawButton(6);
        isIntakeButtonPressed = j_leftStick.getRawButton(5);
        isSuperButtonPressed = j_leftStick.getRawAxis(3)>0.1;//super closes intake
        //  isSolenoidThreeButtonPressed = j_rightStick.getRawButton(6);
        isIntakeOutButtonPressed = j_leftStick.getRawAxis(2)>0.1; //moved to drive side
        isSolenoidTwoButtonPressed = j_leftStick.getRawButton(3); //x button claw release
        
        //em joystick 
        isemJoyButtonPressed = j_emJoy.getRawButton(12);//end game switch

        shootSpeed = j_leftStick.getRawAxis(2);

    }
}
