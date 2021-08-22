package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Threads.File.FileManager;

public class RConf {
	
	// These are your drive motors
	DcMotor frontLeft, frontRight, backLeft, backRight;
	DcMotor[] driveMotors;
	DcMotor[] allMotors;
	
	// This is where you will add any motors you need for your specific comp
	//DcMotorEx ;
	//DcMotorEx[] compMotors = {};
	
	// This is where you setup any servos you are using
	//Servo ;
	Servo[] compServo = {};
	
	// Init the IMU
	BNO055IMU imu;
	
	//============================
	//       DO NOT EDIT
	//============================
	
	OpMode opMode;
	Telemetry telemetry;
	HardwareMap hwMap;
	
	FileManager fileManager = null;
	
	ElapsedTime time = new ElapsedTime();
	
	public RConf(OpMode opMode){
		this.opMode = opMode;
		this.telemetry = opMode.telemetry;
		this.hwMap = opMode.hardwareMap;
		initHardware();
	}
	
	public void initHardware(){
		Log.d("HardwareInit", "Hardware Init Started");
		frontLeft  = hwMap.dcMotor.get(Conf.Motors.FRONTLEFT.getName());
		frontRight = hwMap.dcMotor.get(Conf.Motors.FRONTRIGHT.getName());
		backLeft   = hwMap.dcMotor.get(Conf.Motors.BACKLEFT.getName());
		backRight  = hwMap.dcMotor.get(Conf.Motors.BACKRIGHT.getName());
		
		frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
		backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
		
		driveMotors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};
		allMotors   = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};
		
		Log.d("HardwareInit", "Hardware Init Finished");
	}
	
	public void initAuto(){
		setDriveMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		for(Servo servo : compServo) servo.setPosition(0);
		waiter(500);
	}
	
	/**
	 * Set all drive motors to the same mode
	 * @param mode {@link DcMotor.RunMode RUNMODE} for information about the runmodes
	 */
	public void setDriveMotorMode(DcMotor.RunMode mode) {
		for(DcMotor dcMotor : driveMotors) dcMotor.setMode(mode);
	}
	
	ElapsedTime Timer = new ElapsedTime();
	
	/**
	 * Wait for a specified amount of time
	 * @param time in milliseconds
	 */
	public void waiter(int time) {
		Timer.reset();
		while (true) if (!(Timer.milliseconds() < time)) break;
	}
	
	public class filer {
		public filer(FileManager.OP type){
			fileManager = new FileManager(type, opMode, time);
		}
		
		public void Break(String Label){
			fileManager.Break(Label);
		}
		
		public filer addData(String Name, Object message){
			fileManager.writeFile(Name, message, time.milliseconds());
			return this;
		}
		
		public filer commit(){
			fileManager.writeToFile();
			return this;
		}
	}
	
}