package org.usfirst.frc.team5407.robot;

public class Variables {
	
	//used to set encoder to 0 before auto
	public final int encoderpos = 0;
	
	// Gyro kp, the smaller the value the small the corrections get
	public final double GyroKp = 0.015;
	
	// Lift Pot Values
	// Lowest Lift Position
	public final double defaultLiftPot = 349;
	// Portal Height Lift Position
	public final double portalLiftPot = 300;
	// Scale Lift Position
	public final double scaleLiftPot = 90;
	
	//Auto Turn Kp
	public final double autoTurnKp = 1.00;
}
