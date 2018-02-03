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

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	
	// JeVois Variables
	private SerialPort jevois = null;
	private int loopCount;
	private UsbCamera jevoisCam;
	
	private ICameraSettings _currentCameraSettings = new DumbCameraSettings();

//	public void disabledPeriodic() {
//		checkJeVois();
//	}

	@Override
	public void robotInit() {

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
				jevoisCam.setVideoMode(
					PixelFormat.kYUYV,
					_currentCameraSettings.getWidth(),
					_currentCameraSettings.getHeight(),
					_currentCameraSettings.getFps()
				);
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

	// Called during periodic, if it sees jevois it tells you how long it took to connect and if it does not connect it tries to reconnect
	public void checkJeVois() {
		if (jevois == null) return;
		if (jevois.getBytesReceived() > 0) {
			System.out.println("Waited: " + loopCount + " loops, Rcv'd: " + jevois.readString());
			loopCount = 0;
		}
		if (++loopCount % 150 == 0) {
			System.out.println("checkJeVois() waiting..." + loopCount);
			jevoisCam.setVideoMode(
				PixelFormat.kYUYV,
				_currentCameraSettings.getWidth(),
				_currentCameraSettings.getHeight(),
				_currentCameraSettings.getFps()
			);
			writeJeVois("getpar serout\n");
			writeJeVois("info\n");
		}
	}
		// Writes to console
	public void writeJeVois(String cmd) {
		if (jevois == null) return;
		int bytes = jevois.writeString(cmd);
		System.out.println("wrote " +  bytes + "/" + cmd.length() + " bytes");	
		loopCount = 0;
	}

	// Private camera settings code
	private interface ICameraSettings {
		// Any class that "implements" this interface must define these methods.
		// This way we know any camera settings class can getWidth, getHeight, and getFps.
		public int getWidth();
		public int getHeight();
		public int getFps();
	}

	private class ObjectTrackerSettings implements ICameraSettings {
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
	// End private camera settings code
}
