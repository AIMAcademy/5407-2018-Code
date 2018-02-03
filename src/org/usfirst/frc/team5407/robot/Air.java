package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class Air {
	// Create solenoids here and name them
	Solenoid s_DSShifter; 
	Solenoid s_sol1;
	Solenoid s_sol2;
	Solenoid s_sol3;
	
	public Air(int i_sol0, int i_sol1, int i_sol2, int i_sol3) {
		// Calls solenoids name, creates solenoid and gives them a port number
		s_DSShifter = new Solenoid(i_sol0);
		s_sol1 = new Solenoid(i_sol1);
		s_sol2 = new Solenoid(i_sol2);
		s_sol3 = new Solenoid(i_sol3);
		
		// Gives the default values of the solenoids
		initializeAir();
	}

	// Set the default values of the solenoids here
	public void initializeAir() {
		s_DSShifter.set(false);
		s_sol1.set(true);
		s_sol2.set(false);
		s_sol3.set(false);
		
	}
}
