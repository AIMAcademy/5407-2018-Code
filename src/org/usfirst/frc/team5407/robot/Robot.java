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
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	// Create new classes and call them here 
	Sensors sensors;
	Air air;
	Inputs inputs;
	Constants constants;

	DriveTrain drivetrain;
	// JeVois Variables
	private SerialPort jevois = null;
	private int loopCount;
	private UsbCamera jevoisCam;

	private ICameraSettings _currentCameraSettings;

	//	public void disabledPeriodic() {
	//		checkJeVois();
	//	}

	// Auton, creating string for new auton and has a sendable chooser at the end of it
	final String defaultAuton = "Default Auton";
	final String driveBaseLine = "Drive BaseLine"; //Needs Testing
	final String turn90Right = "Turn 90 Right"; //Needs Testing
	String autonSelected;
	SendableChooser<String> chooser;

	@Override
	public void robotInit() {
		// Makes classes recongized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();
		inputs = new Inputs(0, 1);

		// Called 4 solenoids in the air class
		air = new Air(0, 1, 2, 3);
		constants = new Constants();

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
		
		chooser = new SendableChooser<String>();
		chooser.addDefault("Default Auton", defaultAuton);
		chooser.addObject("Drive to Baseline", driveBaseLine);
		chooser.addObject("Turn 90 Right", turn90Right);
		SmartDashboard.putData("Auton Choices", chooser);
		
		//drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		//drivetrain.frontRightDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);

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
		
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		
		sensors.ahrs.reset();
	}

	public void autonomousPeriodic() {
		
		//drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		//drivetrain.frontRightDriveMotor.setSelectedSensorPosition(constants.encoderpos, 0, 10);
		
		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();
		
		sensors.getPresentAngleNAVX();
		sensors.getFollowAngleNAVX();
		sensors.ahrs.getAngle();
		
		autonSelected = chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);
		
		// If else statement for auton selection
		if (autonSelected == defaultAuton) {
			defaultAuton();
		}else if (autonSelected == driveBaseLine) {
			driveBaseLine();
		}else if (autonSelected == turn90Right) {
			turn90Right();
		}
		
		
		//SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.updateValues();
		
	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();
		
	//	drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(constants.sensorpos, 0, 10);
	//	drivetrain.frontRightDriveMotor.setSelectedSensorPosition(constants.sensorpos, 0, 10);
	}

	public void teleopPeriodic() {
		inputs.ReadValues();

		air.s_DSShifter.set(inputs.isDualSpeedShifterButtonPressed);
		air.s_sol1.set(inputs.isSolenoidOneButtonPressed);
		air.s_sol2.set(inputs.isSolenoidTwoButtonPressed);
		air.s_sol3.set(inputs.isSolenoidThreeButtonPressed);

		boolean setCameraToTrackObjects = inputs.isCameraButtonPressed;
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

		// Arcade Drive using first joystick
		double forward = -inputs.j_leftStick.getY(); // xbox gampad left X, positive is forward
		double turn = inputs.j_leftStick.getX(); // xbox gampad right X, positive means turn right

		// BEGIN NAVX Gyro Code //
		// Creates a boolean for enabling or disabling NavX
		boolean b_EnableGyroNAVX = false;

		// If robot is going forward or back ward with thin certain values, enable NavX drive straight 
		if (turn <= .05 && turn >= -0.05) {
			if (b_EnableGyroNAVX == false) {
				sensors.setFollowAngleNAVX(0);
			}
			b_EnableGyroNAVX = true;
			drivetrain.drive.arcadeDrive(forward, (sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * constants.Kp);
		}
		// If robot is doing anything other than forward or backward turn NavX Drive straight off
		else {
			drivetrain.drive.arcadeDrive(forward, turn);
			b_EnableGyroNAVX = false;
		}

		// Puts values on SmartDashBoard
		// SmartDashboard.putNumber("Gyro", sensors.analogGyro.getAngle());
		//SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		// Auton Chooser and its SmartDashBoard component

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

	// Private camera settings code
	private interface ICameraSettings {
		// Any class that "implements" this interface must define these methods.
		// This way we know any camera settings class can getWidth, getHeight, and getFps.
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
			this.width = 176;
			this.height = 144;
			this.fps = 60;

			this.isUsingDefaultSettings = true;
		}
		public void setObjectTrackerSettings() {
			this.width = 320;
			this.height = 254;
			this.fps = 60;

			this.isUsingDefaultSettings = false;
		}
	}
	// End private camera settings
}
