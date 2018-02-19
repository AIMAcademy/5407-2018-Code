/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This program contains FRC team 5407's code for the 2018 competition season 
 */
public class Robot extends IterativeRobot {
	// Create new classes and call them here 
	Sensors sensors;
	Air air;
	Inputs inputs;
	Constants constants;
	Lift lift;
	DriveTrain drivetrain;
	Intake intake;

	// JeVois Variables
	private SerialPort jevois = null;
	private int loopCount;
	private UsbCamera jevoisCam;

	private ICameraSettings _currentCameraSettings;
	

	//	public void disabledPeriodic() {
	//		checkJeVois();
	//	}

	// Autos, creating string for new auton and has a sendable chooser at the end of it
	final String defaultAuton = "Default Auton";
	final String driveBaseLine = "Drive BaseLine"; //Needs Testing
	final String turn90Right = "Turn 90 Right"; //Needs Testing
	String autonSelected;
	SendableChooser<String> Autonchooser;
	
	@Override
	public void robotInit() {
		// Makes classes recognized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();
		inputs = new Inputs(0, 1); 
		constants = new Constants();
		lift = new Lift(0);
		intake = new Intake(1,2);

		// Calls 4 solenoids in the air class
		air = new Air(0, 1, 2, 4);

		// BEGIN JeVois Code //
		// Get default camera settings
		_currentCameraSettings = new CameraSettings();
		
		// Tries to reach camera camera and if not, it prints out a failed 
		// Without this if it did not connect, the whole program would crash
		int tryCount = 0;
		do {
			try {
				System.out.print("Trying to create jevois SerialPort...");
				jevois = new SerialPort(9600, SerialPort.Port.kUSB);
				System.out.println("jevois: " + jevois);
				tryCount = 99;
				System.out.println("success!");
			} catch (Exception e) {
				tryCount += 1;
				System.out.println("failed!");
			}
		} while (tryCount < 3);

		// Creating video stream and setting video mode which is mapped to the object tracker module
		System.out.println("Starting CameraServer");
		if (jevoisCam == null) {
			try {
				jevoisCam = CameraServer.getInstance().startAutomaticCapture();
				jevoisCam.setVideoMode(PixelFormat.kYUYV, _currentCameraSettings.getWidth(),
						_currentCameraSettings.getHeight(), _currentCameraSettings.getFps());
				VideoMode vm = jevoisCam.getVideoMode();
				System.out.println("jevoisCam pixel: " + vm.pixelFormat);
				System.out.println("jevoisCam res: " + vm.width + "x" + vm.height);
				System.out.println("jevoisCam fps: " + vm.fps);
			} catch (Exception e) {
				System.out.println(e.toString());
				System.out.println("no camera connection");
			}

			// Below code done not work on our robot 
			// Keeping here in case of trouble shooting later
			//jevoisCam.setResolution(320, 254);
			//jevoisCam.setPixelFormat(PixelFormat.kYUYV);
			//jevoisCam.setFPS(60);
		}

		if (tryCount == 99) {
			writeJeVois("info\n");
		}
		loopCount = 0;
		// END JeVois Code // 
		
		Autonchooser = new SendableChooser<String>();
		Autonchooser.addDefault("Default Auton", defaultAuton);
		Autonchooser.addObject("Drive to Baseline", driveBaseLine);
		Autonchooser.addObject("Turn 90 Right", turn90Right);
		SmartDashboard.putData("Auton Choices", Autonchooser);
		
	}
	
	public void robotPeriodic() {}
	
	public void disabledInit() {}
	
	public void disabledPeriodic() {
		
		//autonSelected = chooser.getSelected();
		//SmartDashboard.putString("My Selected Auton is ", autonSelected);
	}

	public void autonomousInit() {
		// Zero and initalize values for auton 
		air.initializeAir();
		
		//resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		
		//resets gyro to zero
		sensors.ahrs.reset();
	}

	public void autonomousPeriodic() {		
		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();
		// Gets all needed angles from NavX
		sensors.getPresentAngleNAVX();
		sensors.getFollowAngleNAVX();
		sensors.ahrs.getAngle();
		// Gets auto choosen and displays it on SmartDashboard
		autonSelected = Autonchooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);
		
		// If else statement for auton selection
		if (autonSelected == defaultAuton) {
			defaultAuton();
		}else if (autonSelected == driveBaseLine) {
			driveBaseLine();
		}else if (autonSelected == turn90Right) {
			turn90Right();
		}
		
		//Puts values on SmartDashboard in Auto
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.updateValues();
		
	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();
	}

	public void teleopPeriodic() {
		inputs.ReadValues();

		//put all buttons here
		air.s_DSShifter.set(inputs.getIsDualSpeedShifterButtonPressed());
		air.s_sol4.set(inputs.getIsSolenoidFourButtonPressed());
		air.s_sol2.set(inputs.getIsSolenoidTwoButtonPressed());
		air.s_sol1.set(inputs.getIsSolenoidThreeButtonPressed());
		
		if(inputs.getIsIntakeButtonPressed() == true) {
			intakeIn();
		}else if (inputs.getIsIntakeOutButtonPressed() == true) {
			intakeOut();
		}else {
			intakeStop();
		}
		
		// Lift postion needs testing!!
		if(inputs.getisScaleLiftButtonPressed() == true) {
				scaleLiftPosition();
		} else if(inputs.getisPortalLiftButtonPressed() == true) {
				portalLiftPosition();
		} else if(inputs.getisDefaultLiftButtonPressed() == true) {
				defaultLiftPosition();
		}else	{
			lift.mot_liftDart.set(-inputs.j_rightStick.getY());
		}
		
		boolean setCameraToTrackObjects = inputs.getIsCameraButtonPressed();
		if (setCameraToTrackObjects && _currentCameraSettings.getIsUsingDefaultSettings()) {
			_currentCameraSettings.setObjectTrackerSettings();
			setJeVoisVideoMode();
		} else if (!setCameraToTrackObjects && !_currentCameraSettings.getIsUsingDefaultSettings()) {
			_currentCameraSettings.setDefaultSettings();
			setJeVoisVideoMode();
		}

		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();

		// BEGIN NAVX Gyro Code //
		// Creates a boolean for enabling or disabling NavX
		boolean b_EnableGyroNAVX = false;

		// If robot is going forward or back ward with thin certain values, enable NavX drive straight 
		if (inputs.getTurn() <= .05 && inputs.getTurn() >= -0.05) {
			if (b_EnableGyroNAVX == false) {
				sensors.setFollowAngleNAVX(0);
			}
			b_EnableGyroNAVX = true;
			drivetrain.drive.arcadeDrive(inputs.getThrottle(), (sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * constants.GyroKp);
		}
		// If robot is doing anything other than forward or backward turn NavX Drive straight off
		else {
			drivetrain.drive.arcadeDrive(inputs.getThrottle(), inputs.getTurn());
			b_EnableGyroNAVX = false;
		}

		//Puts values on SmartDashBoard
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.putNumber("Lift Pot", sensors.analogLiftPot.get());

		// Updating the values put on SmartDashboard
		SmartDashboard.updateValues();
	}

	// Called during periodic, if it sees jevois it tells you how long it took to connect and if it does not connect it tries to reconnect
	public void setJeVoisVideoMode() {
		jevoisCam.setVideoMode(PixelFormat.kYUYV, _currentCameraSettings.getWidth(),
				_currentCameraSettings.getHeight(), _currentCameraSettings.getFps());
	}

	// Writes to console
	public void writeJeVois(String cmd) {
		if (jevois == null)
			return;

		int bytes = jevois.writeString(cmd);
		System.out.println("wrote " + bytes + "/" + cmd.length() + " bytes");
		loopCount = 0;
	}
	
	// Private camera settings code
	private interface ICameraSettings {
		// Any class that "implements" this interface must define these methods.
		// This way we know any camera settings class can getWidth, getHeight, and getFps, etc.
		public int getWidth();
		public int getHeight();
		public int getFps();
		public boolean getIsUsingDefaultSettings();
		public void setDefaultSettings();
		public void setObjectTrackerSettings();
	}

	public class CameraSettings implements ICameraSettings {
		private int width;
		private int height;
		private int fps;
		private boolean isUsingDefaultSettings;

		public CameraSettings() {
			setDefaultSettings();
		}

		public int getWidth() { return width; }
		public int getHeight() { return height; }
		public int getFps() { return fps;}
		public boolean getIsUsingDefaultSettings() { return isUsingDefaultSettings; }

		public void setDefaultSettings() {
			width = 176;
			height = 144;
			fps = 60;

			isUsingDefaultSettings = true;
		}
		public void setObjectTrackerSettings() {
			width = 320;
			height = 254;
			fps = 60;

			isUsingDefaultSettings = false;
		}
	}
	// End private camera settings
	
	// Lift Position methods
	//may need to add an else statement
	//To go up make it negative
	public void scaleLiftPosition() {
		if(sensors.analogLiftPot.get() > constants.scaleLiftPot) {
			lift.mot_liftDart.set(-0.75);
		}else if (sensors.analogLiftPot.get() == constants.scaleLiftPot) {
			lift.mot_liftDart.set(0.0);
		}
	}	

	public void portalLiftPosition() {
		if(sensors.analogLiftPot.get() > constants.portalLiftPot) {
			lift.mot_liftDart.set(-0.50* constants.LiftKp);
		}else if (sensors.analogLiftPot.get() < constants.portalLiftPot) {
			lift.mot_liftDart.set(0.50* constants.LiftKp);
		}else if (sensors.analogLiftPot.get() < 10 && constants.portalLiftPot> 10 ){
			lift.mot_liftDart.set(0.0);
		}
	}
	
	public void defaultLiftPosition() {
		if(sensors.analogLiftPot.get() < constants.defaultLiftPot) {
			lift.mot_liftDart.set(0.75);
		}else if (sensors.analogLiftPot.get() == constants.defaultLiftPot) {
			lift.mot_liftDart.set(0.0);
		}
	}

	public void intakeIn() {
		intake.mot_leftSideIntake.set(0.8);
		intake.mot_rightSideIntake.set(-0.8);
	}
	
	public void intakeOut() {
		intake.mot_leftSideIntake.set(-0.8);
		intake.mot_rightSideIntake.set(0.8);
	}
	
	public void intakeStop() {
		intake.mot_leftSideIntake.set(0.0);
		intake.mot_rightSideIntake.set(0.0);
	}
	
	// When no Auton is called this one will be run, we just sit there
	public void defaultAuton() {
		if (autonSelected == defaultAuton) {}
	}

	// The most basic Auton: Drive forward 10 feet and stop, needs testing and tuning!!!!!
	public void driveBaseLine() {
		if (drivetrain.getLeftQuadPosition() < 60 && drivetrain.getRightQuadPosition() < 60) {
			drivetrain.drive.arcadeDrive(0.50, 0);
		}else {
			drivetrain.drive.arcadeDrive(0, 0);
		}
	}

	//deff needs testing because I have little idea of what it will do!!!
	public void turn90Right() {
		if(sensors.ahrs.getAngle() < 90) {
			drivetrain.drive.arcadeDrive(0, 0.5);
		}else {
			drivetrain.drive.arcadeDrive(0, 0);
		}
	}

}
