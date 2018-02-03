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
import edu.wpi.first.wpilibj.Joystick;
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

	DriveTrain drivetrain;
	// JeVois Variables
	private SerialPort jevois = null;
	private int loopCount;
	private UsbCamera jevoisCam;

	// Private DifferentialDrive drive;
	private Joystick j_leftStick;
	private Joystick j_rightStick;

	private ICameraSettings _currentCameraSettings = new DumbCameraSettings();

	// Gyro kp, the smaller the value the small the corrections get
	double Kp = 0.015;

	//	public void disabledPeriodic() {
	//		checkJeVois();
	//	}

	// Auton, creating string for new auton and has a sendable chooser at the end of it
	final String defaultAuton = "Default Auton";
	final String DriveBaseLine = "Drive BaseLine";
	String autonSelected;
	SendableChooser<String> chooser;

	@Override
	public void robotInit() {
		// Makes classes recongized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();

		// Calling the 0 and 1 port for usb on driverstation computer
		j_leftStick = new Joystick(0);
		j_rightStick = new Joystick(1);

		// Called 4 solenoids in the air class
		air = new Air(0, 1, 2, 3);

		// BEGIN JeVois Code //

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
	}

	public void autonInit() {
		// Zero and initalize values for auton 
		air.initializeAir();

		// Auton Chooser and its SmartDashBoard component
		autonSelected = chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);
	}

	public void autonPeriodic() {
		// If else statement for auton selection
		if (autonSelected == defaultAuton) {
			defaultAuton();
		}

		else if (autonSelected == DriveBaseLine) {
			DriveBaseLine();
		}
	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();
	}

	public void teleopPeriodic() {
		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();

		// Arcade Drive using first joystick
		double forward = -(j_leftStick.getY()); // xbox gampad left X, positive is forward
		double turn = j_leftStick.getX(); // xbox gampad right X, positive means turn right

		// BEGIN NAVX Gyro Code //
		// Creates a boolean for enabling or disabling NavX
		boolean b_EnableGyroNAVX = false;

		// If robot is going forward or back ward with thin certain values, enable NavX drive straight 
		if (turn <= .05 && turn >= -0.05) {
			if (b_EnableGyroNAVX == false) {
				sensors.setFollowAngleNAVX(0);
			}
			b_EnableGyroNAVX = true;
			drivetrain.drive.arcadeDrive(forward, (sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * Kp);
		}
		// If robot is doing anything other than forward or backward turn NavX Drive straight off
		else {
			drivetrain.drive.arcadeDrive(forward, turn);
			b_EnableGyroNAVX = false;
		}

		// Puts values on SmartDashBoard
		// SmartDashboard.putNumber("Gyro", sensors.analogGyro.getAngle());
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());

		// Updating the values put on SmartDashboard
		SmartDashboard.updateValues();
	}

	// Called during periodic, if it sees jevois it tells you how long it took to connect and if it does not connect it tries to reconnect
	public void checkJeVois() {
		if (jevois == null)
			return;

		if (jevois.getBytesReceived() > 0) {
			System.out.println("Waited: " + loopCount + " loops, Rcv'd: " + jevois.readString());
			loopCount = 0;
		}

		if (++loopCount % 150 == 0) {
			System.out.println("checkJeVois() waiting..." + loopCount);
			jevoisCam.setVideoMode(PixelFormat.kYUYV, _currentCameraSettings.getWidth(),
					_currentCameraSettings.getHeight(), _currentCameraSettings.getFps());
			writeJeVois("getpar serout\n");
			writeJeVois("info\n");
		}

		// If else statement for first joystick button RB, switched between high and low gear
		if (j_leftStick.getRawButton(6)) {
			air.s_DSShifter.set(true);
		} else {
			air.s_DSShifter.set(false);
		}

		// If else statement for button A on second joystick
		if (j_rightStick.getRawButton(1)) {
			air.s_sol1.set(true);
		} else {
			air.s_sol1.set(false);
		}

		// If else statement for button B on second joystick
		if (j_rightStick.getRawButton(2)) {
			air.s_sol2.set(true);
		} else {
			air.s_sol2.set(false);
		}

		// If else statement for #6 button on second joystick
		if (j_rightStick.getRawButton(6)) {
			air.s_sol3.set(true);
		} else {
			air.s_sol3.set(false);
		}
	}

	// Writes to console
	public void writeJeVois(String cmd) {
		if (jevois == null)
			return;

		int bytes = jevois.writeString(cmd);
		System.out.println("wrote " + bytes + "/" + cmd.length() + " bytes");
		loopCount = 0;
	}

	// When no Auton is called this one will be run
	public void defaultAuton() {
		if (autonSelected == defaultAuton) {
		}
	}

	// The most basic Auton: Drive forward 10 feet and stop
	public void DriveBaseLine() {
	}

	// Private camera settings code
	private interface ICameraSettings {
		// Any class that "implements" this interface must define these methods.
		// This way we know any camera settings class can getWidth, getHeight, and getFps.
		public int getWidth();
		public int getHeight();
		public int getFps();
	}

	private class ObjectTrackerCameraSettings implements ICameraSettings {
		private int width = 320;
		private int height = 254;
		private int fps = 60;

		public int getWidth() { return width; }
		public int getHeight() { return height; }
		public int getFps() { return fps; }
	}

	private class DumbCameraSettings implements ICameraSettings {
		private int width = 176;
		private int height = 144;
		private int fps = 115;

		public int getWidth() { return width; }
		public int getHeight() { return height; }
		public int getFps() { return fps; }
	}
	// End private camera settings
}
