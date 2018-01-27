/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	private DifferentialDrive _drive;
	private Joystick m_leftStick;
	
	// JeVois Variables
	private SerialPort jevois = null;
	private int loopCount;
	private UsbCamera jevoisCam;
	
	/* talons for arcade drive */
	WPI_TalonSRX _frontLeftMotor = new WPI_TalonSRX(11); 		/* device IDs here (1 of 2) */
	WPI_TalonSRX _frontRightMotor = new WPI_TalonSRX(15);
	
	WPI_TalonSRX _backLeftSlave = new WPI_TalonSRX(12);
	WPI_TalonSRX _backRightSlave = new WPI_TalonSRX(16);

//	public void disabledPeriodic() {
//		checkJeVois();
//	}

	@Override
	public void robotInit() {
		_backLeftSlave.follow(_frontLeftMotor);
		_backRightSlave.follow(_frontRightMotor);
		
		_drive = new DifferentialDrive(_frontLeftMotor, _frontRightMotor);
		m_leftStick = new Joystick(0);
		
    	// BEGIN JeVois Code //
    	int tryCount = 0;
		do {
			try {
				System.out.print("Trying to create jevois SerialPort...");
				jevois = new SerialPort(9600, SerialPort.Port.kUSB);
				tryCount = 99;
				System.out.println("success!");
			} catch (Exception e) {
				tryCount += 1;
				System.out.println("failed!");
			}
		} while (tryCount < 3);
		
		System.out.println("Starting CameraServer");
		if (jevoisCam == null) {
			jevoisCam = CameraServer.getInstance().startAutomaticCapture();
			jevoisCam.setVideoMode(PixelFormat.kYUYV,320,254,60);
			//jevoisCam.setResolution(320, 254);
			//jevoisCam.setPixelFormat(PixelFormat.kYUYV);
			//jevoisCam.setFPS(60);
			VideoMode vm = jevoisCam.getVideoMode();
			System.out.println("jevoisCam pixel: " + vm.pixelFormat);
			System.out.println("jevoisCam res: " + vm.width + "x" + vm.height);
			System.out.println("jevoisCam fps: " + vm.fps);
		}
		
		if (tryCount == 99) {
			writeJeVois("info\n");
		}
		loopCount = 0;
    	// END JeVois Code // 
		
	}
	
	public void checkJeVois() {
		if (jevois == null) return;
		if (jevois.getBytesReceived() > 0) {
			System.out.println("Waited: " + loopCount + " loops, Rcv'd: " + jevois.readString());
			loopCount = 0;
		}
		if (++loopCount % 150 == 0) {
			System.out.println("checkJeVois() waiting..." + loopCount);
			jevoisCam.setVideoMode(PixelFormat.kYUYV,320,254,60);
			writeJeVois("getpar serout\n");
			writeJeVois("info\n");
		}
	}
		
	public void writeJeVois(String cmd) {
		if (jevois == null) return;
		int bytes = jevois.writeString(cmd);
		System.out.println("wrote " +  bytes + "/" + cmd.length() + " bytes");	
		loopCount = 0;
	}

	@Override
	public void teleopPeriodic() {
		// Arcade Drive
    	double forward = -m_leftStick.getY(); // logitech gampad left X, positive is forward
    	double turn = m_leftStick.getX(); // logitech gampad right X, positive means turn right
    	_drive.arcadeDrive(forward, turn);
    	
		checkJeVois();
	}
}
