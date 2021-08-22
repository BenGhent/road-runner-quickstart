package org.firstinspires.ftc.teamcode.Threads.File;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FileManager {
	File directory;
	File file;
	
	DcMotor[] motor = null;
	
	OpMode opMode;
	OP Op;
	
	List<String> buffer = new ArrayList<>();
	
	FileOutputStream fos;
	ElapsedTime time;
	
	/**
	 * Init the file manager
	 * @param Type Weather this is for Autonomous or TeleOp
	 */
	public FileManager(OP Type, OpMode opMode, ElapsedTime time){
		this.opMode = opMode;
		init(Type, time);
	}
	
	/**
	 * Init FileManager
	 * @param Type Weather its TeleOp or Autonomous
	 */
	public void init(OP Type, ElapsedTime time){
		this.Op = Type;
		try {
			directory = new File(AppUtil.ROOT_FOLDER+""+Type+"/");
			if(!directory.exists())
				directory.mkdirs();
			directory = new File("/sdcard/Logs/"+Type+"/"+Type+Objects.requireNonNull(directory.listFiles()).length);
			directory.mkdirs();
			file = new File(directory, "Log.txt");
			fos = new FileOutputStream(file);
			Log.d("File", "Created new " + Type + " file!");
		} catch (IOException e){
			e.printStackTrace();
			Log.wtf("File", "Failed");
		}
		this.time = time;
		if(Op == OP.TELEOP){
			timer.schedule(new calling(time, this), 0, 500);
		}
	}
	
	/**
	 * Point this file writer to the motors on your bot
	 * @param motors The array of all your motors
	 */
	public void initMotors(DcMotor[] motors){
		this.motor = motors;
	}
	
	Timer timer = new Timer();
	
	/**
	 * Write everything in the buffer to the file, this runs periodically
	 */
	public void writeToFile(){
		for(int i=0;i<buffer.size();i++){
			try {
				fos.write(buffer.get(i).getBytes());
				fos.write("\n".getBytes());
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		buffer.clear();
	}
	
	/**
	 * Log all of the data from the gamepads to the buffer
	 * @param time When you are logging this
	 */
	public void writeGamepad(double time){
		// Gamepad 1
		if(opMode.gamepad1.getGamepadId() > 0) {
			// Sticks
			writeFile(Gamepad1.LStick.value(), new float[]{opMode.gamepad1.left_stick_x, opMode.gamepad1.left_stick_y}, time);
			writeFile(Gamepad1.RStick.value(), new float[]{opMode.gamepad1.right_stick_x, opMode.gamepad1.right_stick_y}, time);
			// YBAX
			writeFile(Gamepad1.YBAX.value(), new boolean[]{opMode.gamepad1.y, opMode.gamepad1.b, opMode.gamepad1.a, opMode.gamepad1.x}, time);
			// Triggers
			writeFile(Gamepad1.T.value(), new float[]{opMode.gamepad1.left_trigger, opMode.gamepad1.right_trigger}, time);
			// Bumpers
			writeFile(Gamepad1.B.value(), new boolean[]{opMode.gamepad1.left_bumper, opMode.gamepad1.right_bumper}, time);
			// Dpad
			writeFile(Gamepad1.DP.value(), new boolean[]{opMode.gamepad1.dpad_up, opMode.gamepad1.dpad_right, opMode.gamepad1.dpad_down, opMode.gamepad1.dpad_left},time);
			// Random
			writeFile(Gamepad1.S.value(), new boolean[]{opMode.gamepad1.start, opMode.gamepad1.back, opMode.gamepad1.left_stick_button, opMode.gamepad1.right_stick_button}, time);
		}
		// Gamepad 2
		if(opMode.gamepad2.getGamepadId() > 0){
			// Sticks
			writeFile(Gamepad2.LStick.value(), new float[]{opMode.gamepad2.left_stick_x, opMode.gamepad2.left_stick_y}, time);
			writeFile(Gamepad2.RStick.value(), new float[]{opMode.gamepad2.right_stick_x, opMode.gamepad2.right_stick_y}, time);
			// YBAX
			writeFile(Gamepad2.YBAX.value(), new boolean[]{opMode.gamepad2.y, opMode.gamepad2.b, opMode.gamepad2.a, opMode.gamepad2.x}, time);
			// Triggers
			writeFile(Gamepad2.T.value(), new float[]{opMode.gamepad2.left_trigger, opMode.gamepad2.right_trigger}, time);
			// Bumpers
			writeFile(Gamepad2.B.value(), new boolean[]{opMode.gamepad2.left_bumper, opMode.gamepad2.right_bumper}, time);
			// Dpad
			writeFile(Gamepad2.DP.value(), new boolean[]{opMode.gamepad2.dpad_up, opMode.gamepad2.dpad_right, opMode.gamepad2.dpad_down, opMode.gamepad2.dpad_left}, time);
			// Random
			writeFile(Gamepad2.S.value(), new boolean[]{opMode.gamepad2.start, opMode.gamepad2.back, opMode.gamepad2.left_stick_button, opMode.gamepad2.right_stick_button}, time);
		}
	}
	
	/**
	 * White data from the motors to the buffer
	 * @param time When you are logging this
	 */
	public void writeMotors(double time){
		if(motor != null){
			for(DcMotor dcMotor : motor){
				writeFile(String.valueOf(opMode.hardwareMap.getNamesOf(dcMotor)), new double[]{dcMotor.getPower(), dcMotor.getTargetPosition(), dcMotor.getCurrentPosition()}, time);
			}
		}
	}
	
	// The max amount of char in a Break line
	int maxLength = 100;
	int charLength;
	
	/**
	 * This method is made to add a bar across the file that outlines different things that happen
	 * @param name The label of the bar
	 */
	public void Break(String name){
		String thing = "";
		char[] Name = name.toCharArray();
		charLength = maxLength - Name.length;
		for(int i = 0; i<charLength; i++){
			if(i==((int) charLength/2)){
				thing+=name;
			}
			thing+="=";
		}
		Log.d("Thing", thing);
		buffer.add(thing);
	}
	
	/**
	 * Close the file manager
	 * <p>
	 * You won't be able to save anything else to the file
	 */
	public void close(){
		try {
			Break("End of File");
			timer.cancel();
			writeToFile();
			fos.close();
			timer.cancel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Decrease how fast you write to the file, meant for when the robot is not idle
	 */
	public void getFast(){
		timer.cancel();
		timer.schedule(new calling(time, this), 0, 50);
	}
	
	/**
	 * Write to the file buffer
	 * @param Name The name of the things that you are logging
	 * @param LogThing The thing you want to log
	 * @param time When you logged this thing
	 */
	public void writeFile(String Name, Object LogThing, double time){
		buffer.add(Name+"/"+(int)time+":"+LogThing);
	}
	
	public enum OP {
		TELEOP,
		AUTO
	}
}

class calling extends TimerTask{
	
	FileManager f;
	ElapsedTime time;
	public calling(ElapsedTime time, FileManager f){
		this.time = time;
		this.f = f;
	}
	
	public void run(){
		double t = time.milliseconds();
		f.writeGamepad(t);
		f.writeMotors(t);
		f.writeToFile();
	}
}

enum Gamepad1{
	LStick("Gamepad1-LStick"),
	RStick("Gamepad1-RStick"),
	YBAX("Gamepad1-YBAX"),
	T("Gamepad1-T"),
	B("Gamepad1-B"),
	DP("Gamepad1-DP"),
	S("Gamepad1-S");
	
	private String description = null;
	
	private Gamepad1(String desc){
		this.description = desc;
	}
	
	public String value() {
		return this.description;
	}
}

enum Gamepad2{
	LStick("Gamepad2-LStick"),
	RStick("Gamepad2-RStick"),
	YBAX("Gamepad2-YBAX"),
	T("Gamepad2-T"),
	B("Gamepad2-B"),
	DP("Gamepad2-DP"),
	S("Gamepad2-S");
	
	private String description = null;
	
	private Gamepad2(String desc){
		this.description = desc;
	}
	
	public String value() {
		return this.description;
	}
}

