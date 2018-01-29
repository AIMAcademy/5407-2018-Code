package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class Air {
	
	Solenoid s_DSShifter; 
	Solenoid s_sol1;
	Solenoid s_sol2;
	
	public Air(int i_sol0, int i_sol1, int i_sol2){
		
		s_DSShifter = new Solenoid(i_sol0);
		s_sol1 = new Solenoid(i_sol1);
		s_sol2 = new Solenoid(i_sol2);
		
		
		initializeAir();
		
	}
	
	public void initializeAir(){
		s_DSShifter.set(false);
		s_sol1.set(true);
		s_sol2.set(false);
		
	}

}
