package org.firstinspires.ftc.teamcode;

public class Conf {
	
	/**
	 * This will contain all the locations of places of interest on the field
	 */
	enum Loc {
		T(new int[]{93, 50}),
		Y(new int[]{60, 20});
		
		private int[] I;
		
		private Loc(int[] ints) {
			this.I = ints;
		}
		
		public int[] location() {
			return this.I;
		}
	}
	
	/**
	 * This will contain all motors in a central place for ease of config init
	 */
	enum Motors {
		FRONTRIGHT("FLM"),
		FRONTLEFT("FLM"),
		BACKLEFT("BLM"),
		BACKRIGHT("BRM");
		
		private String mto = null;
		
		private Motors(String mot) {
			this.mto = mot;
		}
		
		public String getName() {
			return mto;
		}
		
	}
	
	/**
	 * This will contain all the servos in a central place for ease of config
	 */
	enum Servos {
		// Nothing yet
		MAIN("Thing");
		
		
		private String sre = null;
		
		private Servos(String ser) {
			this.sre = ser;
		}
		
		public String getName() {
			return sre;
		}
	}
}
