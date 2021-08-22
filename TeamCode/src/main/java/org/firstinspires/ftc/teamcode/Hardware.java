/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.app.Application;
import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is the main reference class for the robot
 * <p>
 * This is where all of our main methods are stored and where we initiate motors, servos, sensors,
 * etc...
 * <p>
 * To use this class:
 *      <pre>
 *          Hardware r = new Hardware();
 *      </pre>
 * */
public class Hardware extends Application {
    //created by team 9161 Overload
    
    /*
     *
     * To add DC motor, use "public DCMotor %var name%;"
     *
     * To add Servo, use "public Servo %var name%;"
     *
     * ====!then refer to initHardware()!====
     *
     * */
    /** Front Left Drive Motor */
    public DcMotorEx frontLeft;
    /** Back Left Drive Motor */
    public DcMotorEx backLeft;
    /** Front Right Drive Motor */
    public DcMotorEx frontRight;
    /** Back Right Drive Motor */
    public DcMotorEx backRight;
    
    /** All of your driving motors */
    public DcMotor[] Drive_Motors;
    /** All of your motors */
    public DcMotor[] All_Motors;
    
    /** Wobble Servo */
    public Servo Wobble;
    /** All the servos */
    public Servo[] Servos;
    
    Telemetry telemetry;
    HardwareMap hwMap;
    
    // ===========Locations===========
    
    /** First square on the red side */
    public final int[] Red_A = {7,-45};
    /** Second square on the red side */
    public final int[] Red_B = {31,-40};
    /** Third square on the red side */
    public final int[] Red_C = {31,-40};
    
    /** First square on the blue side */
    public final int[] Blue_A = {7,58};
    /** Second square on the blue side */
    public final int[] Blue_B = {22,25};
    /** Third square on the blue side */
    public final int[] Blue_C = {7,58};
    
    /** Red side launching position */
    public final int[] Red_Launch  = {13,-39};
    /** Blue side launching position */
    public final int[] Blue_Launch = {13,34};
    
    /** Red side line */
    public final int[] Red_Line  = {25,-39};
    /** Blue side line */
    public final int[] Blue_Line = {25,35};
    
    private final ElapsedTime Timer = new ElapsedTime();
    public ElapsedTime timer = new ElapsedTime();
    
    /** The amount of ticks it takes to go one inch */
    public static final int ticksPerInch=56;
    /** +- the amount of ticks before you are "On  Target" */
    public static final int encoderSafeZone=50;/*a motor must be within this many ticks of its
   target to be considered "on target"*/
    
    /** This is the speed we want the flywheel to spin */
    public final int targetSpeed = 725;
    
    
    // The IMU sensor object
    BNO055IMU imu;
    
    /**
     * Init the robot
     * <p>
     * Runs {@link #initHardware() initHardware()} after finished
     * @param op Pass it the OpMode of the code (use "this")
     */
    public Hardware(OpMode op){
        hwMap = op.hardwareMap;
        telemetry = op.telemetry;
        initHardware();
    }
    
    /**
     * DONT USE THIS!
     * @param hwMap Robots hardware map
     * @param telemetry Telemetry for you robot
     */
    public Hardware(HardwareMap hwMap, Telemetry telemetry){
        this.hwMap = hwMap;
        this.telemetry = telemetry;
        initHardware();
    }
    
    /**
     * DONT USE THIS!
     * @param hwMap Robots hardware map
     */
    public Hardware(HardwareMap hwMap){
        this.hwMap = hwMap;
        initHardware();
    }
    
    /**
     * Use this method to init the hardware on your robot
     * This is ment to run at the beginning of the code or in the init()
     */
    public void initHardware() {
        /*
         *
         * To init a DC motor, use %var name% = hwMap.dcMotor.get("%name of motor%");
         * To set the direction of DC motor, use %var name%.setDirection(DcMotorSimple.Direction.REVERSE);
         *                                                                                       FORWARDS
         *
         * L = Left
         * R = Right
         * F = Front
         * B = Back
         * M = Motor
         *
         * I = intake
         * Lau = Launcher
         *
         *
         *  ___        _____
         * |   |       |   |
         * |FLM|       |FRM|
         * |   ‾‾‾‾‾‾‾‾    |
         * |   ________    |
         * |BLM|       |BRM|
         * |   |       |   |
         * ‾‾‾‾‾       ‾‾‾‾‾
         *
         *
         * To map servo, use %var name% = hwMap.servo.get("%name of servo%");
         *
         * */
        Log.d("Hardware", "Starting");
        //Wheels
        frontLeft = (DcMotorEx) hwMap.dcMotor.get("FLM");  //Front left motor
        frontRight = (DcMotorEx) hwMap.dcMotor.get("FRM"); //Front right motor
        backLeft = (DcMotorEx) hwMap.dcMotor.get("BLM");   //Back left motor
        backRight = (DcMotorEx) hwMap.dcMotor.get("BRM");  //Back right motor
        
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        Drive_Motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};
        All_Motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        Servos = new Servo[]{null};
        Log.d("Hardware", "Finished!");
        
    }
    
    /**
     * This method is used to get the robot ready to run during autonomous
     * <p>
     *     Inits servos and the IMU
     */
    public void initAutonomous(){
        setDriveMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        for(Servo servo : Servos) servo.setPosition(0);
        //initIMU();
        waiter(500);
    }
    
    public void initIMU(){
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        
        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
    }
    
    /*=======================================
     *
     * =============Do not edit===============
     *
     * =======================================*/
    
    /**
     * Set all drive motors to the same mode
     * @param mode {@link DcMotor.RunMode RUNMODE} for information about the runmodes
     */
    public void setDriveMotorMode(DcMotor.RunMode mode) {
        for(DcMotor dcMotor : Drive_Motors) dcMotor.setMode(mode);
    }
    /**
     * Move the robot forward a specified distance in inches
     * @param distance how far you want to move
     */
    public void setMotorEncoderForward(int distance){
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(distance);
        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(distance);
    }
    /**
     * Move the robot backward a specified distance in inches
     * @param distance how far you want to move
     */
    public void setMotorEncoderBackward(int distance) {
        frontLeft.setTargetPosition(-distance);
        frontRight.setTargetPosition(-distance);
        backLeft.setTargetPosition(-distance);
        backRight.setTargetPosition(-distance);
    }
    /**
     * Move the robot left a specified distance in inches
     * @param distance how far you want to move
     */
    public void setMotorEncoderLeft(int distance) {
        frontLeft.setTargetPosition(-distance);
        frontRight.setTargetPosition(distance);
        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(-distance);
    }
    /**
     * Move the robot right a specified distance in inches
     * @param distance how far you want to move
     */
    public void setMotorEncoderRight(int distance) {
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(-distance);
        backLeft.setTargetPosition(-distance);
        backRight.setTargetPosition(distance);
    }
    /**
     * Rotate the robot Clockwise a specified distance in inches
     * @param distance how far you want to move
     */
    public void setMotorEncoderClockwise(int distance) {
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(-distance);
        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(-distance);
    }
    /**
     * Rotate the robot CounterClockwise a specified distance in inches
     * @param distance how far you want to move
     */
    public void setMotorEncoderCounterwise(int distance) {
        frontLeft.setTargetPosition(-distance);
        frontRight.setTargetPosition(distance);
        backLeft.setTargetPosition(-distance);
        backRight.setTargetPosition(distance);
    }
    /**
     * Set all Drive_Motors to the same power to move forward
     * @param power double from 0 to 1
     * @see #Drive_Motors for all your drive motors
     */
    public void setToForward(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }
    /**
     * Set all Drive_Motors to the same power to move backward
     * @param power double from 0 to 1
     * @see #Drive_Motors for all your drive motors
     */
    public void setToBackward(double power) {
        frontLeft.setPower(-1 * power);
        frontRight.setPower(-1 * power);
        backLeft.setPower(-1 * power);
        backRight.setPower(-1 * power);
    }
    /**
     * Set all Drive_Motors to the same power to rotate Counterclockwise
     * @param power double from 0 to 1
     * @see #Drive_Motors for all your drive motors
     */
    public void setToCounterwise(double power) {
        frontLeft.setPower(-1 * power);
        frontRight.setPower(1 * power);
        backLeft.setPower(-1 * power);
        backRight.setPower(1 * power);
    }
    /**
     * Set all Drive_Motors to the same power to rotate Clockwise
     * @param power double from 0 to 1
     * @see #Drive_Motors for all your drive motors
     */
    public void setToClockwise(double power) {
        frontLeft.setPower(1 * power);
        frontRight.setPower(-1 * power);
        backLeft.setPower(1 * power);
        backRight.setPower(-1 * power);
    }
    /**
     * Set all Drive_Motors to the same power to move right
     * @param power double from 0 to 1
     * @see #Drive_Motors for all your drive motors
     */
    public void setToRight(double power) {
        frontLeft.setPower(1 * power);
        frontRight.setPower(-1 * power);
        backLeft.setPower(-1 * power);
        backRight.setPower(1 * power);
    }
    /**
     * Set all Drive_Motors to the same power to move left
     * @param power double from 0.0 to 1.0
     * @see #Drive_Motors for all your drive motors
     */
    public void setToLeft(double power) {
        frontLeft.setPower(-1 * power);
        frontRight.setPower(1 * power);
        backLeft.setPower(1 * power);
        backRight.setPower(-1 * power);
    }
    /** Stop moving the robot */
    public void setToStill() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    /**
     * Move forward a specified distance using the encoders
     * @param power double from 0.0 to 1.0
     * @param distance in inches
     */
    public void driveForwardEncoder(double power, int distance) {
        
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderForward(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());
            
            telemetry.addData("frontLeft distanceFrom: ", frontLeft.getCurrentPosition());
            telemetry.addData("frontRight distanceFrom: ",frontRight.getCurrentPosition());
            telemetry.addData("backLeft distanceFrom: ",backLeft.getCurrentPosition());
            telemetry.addData("backRight distanceFrom: ",backRight.getCurrentPosition());
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    /**
     * Move backward a specified distance using the encoders
     * @param power double from 0.0 to 1.0
     * @param distance in inches
     */
    public void driveBackwardEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderBackward(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());
            
            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    /**
     * Move left a specified distance using the encoders
     * @param power double from 0.0 to 1.0
     * @param distance in inches
     */
    public void driveLeftEncoder(double power, int distance) {
        
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderLeft(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());
            
            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    /**
     * Move right a specified distance using the encoders
     * @param power double from 0.0 to 1.0
     * @param distance in inches
     */
    public void driveRightEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderRight(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());
            
            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    /**
     * Rotate Clockwise a specified distance using the encoders
     * @param power double from 0.0 to 1.0
     * @param distance in inches
     */
    public void turnClockwiseEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderClockwise(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());
            
            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    /**
     * Rotate Counterclockwise a specified distance using the encoders
     * @param power double from 0 to 1
     * @param distance in inches
     */
    public void turnCounterwiseEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderCounterwise(distance * ticksPerInch + frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        setToForward(power);
        do {
            frontLDist = Math.abs(frontLeft.getTargetPosition() - frontLeft.getCurrentPosition());
            frontRDist = Math.abs(frontRight.getTargetPosition() - frontRight.getCurrentPosition());
            backLDist = Math.abs(backLeft.getTargetPosition() - backLeft.getCurrentPosition());
            backRDist = Math.abs(backRight.getTargetPosition() - backRight.getCurrentPosition());
            
            telemetry.addData("frontLeft distanceFrom: ", frontLDist);
            telemetry.addData("frontRight distanceFrom: ", frontRDist);
            telemetry.addData("backLeft distanceFrom: ", backLDist);
            telemetry.addData("backRight distanceFrom: ", backRDist);
            telemetry.update();
        } while (
                frontLDist > encoderSafeZone &&
                        frontRDist > encoderSafeZone &&
                        backLDist > encoderSafeZone &&
                        backRDist > encoderSafeZone
        );
        setToStill();
    }
    /**
     * Set the behavior of the motors once you stop applying power
     * @param behavior FLOAT, BREAK
     */
    public void setDriveMotorZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for(DcMotor dcMotor : Drive_Motors) dcMotor.setZeroPowerBehavior(behavior);
    }
    /**
     * Same as {@link #setToForward(double)} but meant for use while using encoders
     * @param power Double from 0.0 to 1.0
     */
    public void setDriveMotorPower(double power){
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }
    /**
     * This method is made to rotate the robot to a specific angle
     * @param speed The seed you wish to turn
     * @param degree The angle you wish to end up with
     */
    public void rotateDeg(double speed, int degree){
        // while we are not at the degree
        //      rotate right if degree > 0
        //      rotate left if degree < 0
    }
    /**
     * Wait for a specified amount of time
     * @param time in milliseconds
     */
    public void waiter(int time) {
        Timer.reset();
        while (true) if (!(Timer.milliseconds() < time)) break;
    }
    
    /*========================================
      =========  Development code  ===========
      ========= DON'T USE INT COMP ===========
     =========================================*/
	
	/**
	 * Move at the desired angle
	 * @param speed 0.0 to 1.0
	 * @param degree -179 to 180
	 */
	public void setToDiagonal(double speed, int degree){
		double deg = Math.toRadians(degree);
		
		//equations taking the polar coordinates and turning them into motor powers
		double vx = speed * Math.cos(deg + (Math.PI / 4)); // determine the velocity in the Y-axis
		double vy = speed * Math.sin(deg + (Math.PI / 4)); // determine the velocity in the X-axis
		
		frontLeft.setPower(vx);
		frontRight.setPower(vy);
		backLeft.setPower(vy);
		backRight.setPower(vx);
	}
    
	// This is the main test of the code, HELP: Comment out for comp if not working
	
	/**
	 * Move the robot in a sin wave around an object
	 * @param speed 0.0 to 1.0
	 * @param distance distance in meters
	 * @param around how far around the object you want to move (in meters)
	 */
	public void moveSinTest(double speed, double distance, double around){
		double Gx = (Math.PI*around*Math.cos((Math.PI*timer.milliseconds())/distance))/distance;
		double angle = Math.atan(Gx);
		
		//equations taking the polar coordinates and turning them into motor powers
		double vx = speed * Math.cos(angle + (Math.PI / 4)); // determine the velocity in the Y-axis
		double vy = speed * Math.sin(angle + (Math.PI / 4)); // determine the velocity in the X-axis
		
		frontLeft.setPower(vx);
		frontRight.setPower(vy);
		backLeft.setPower(vy);
		backRight.setPower(vx);
	}
	
	/*
	This is a test section, please ignore
	 */
    
    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
//            InputStream is = getApplicationContext().getAssets().open("Field.json");
            
            InputStream is = hwMap.appContext.getAssets().open(fileName);
            
            int size = is.available();
            
            byte[] buffer = new byte[size];
            
            is.read(buffer);
            
            is.close();
            
            json = new String(buffer, "UTF-8");
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
//		Log.d("Test", json + "\n Test");
//		Log.d("Test", "Running");
    }
    
    /*=======================================
     *
     *============End do not edit============
     *
     *=======================================*/
    
    /*
     * __________________
     * |                 |
     * | _______________ |
     * | |      92     | |
     * | |_____________| |
     * | |_____________| |
     * | |             | |
     * | |      67     | |
     * | |             | |
     * | |_____________| |
     * | |_____________| |
     * | |             | |
     * | |      42     | |
     * | |             | |
     * | |_____________| |
     * |                 |
     * |                 |
     * |_________________|
     *
     * ______
     * | 1  |
     * |____|______
     *       | 2  |
     * ______|____|
     * | 3  |
     * |____|
     *
     */
    
}