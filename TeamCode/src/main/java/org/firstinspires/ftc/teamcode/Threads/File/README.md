# File Manager
## What?
This class is meant to take information you want to be saved long term and write it to a file. This has two main objectives it has to accomplish:
1. Get the information it need
    1.1. Either from this class or from outside classes
2. Write the information to a file

## How?
When you init this class you tell it the ElapsedTime we want to use. This is the global time that we share between all files and keeps everything in time. 

To initiate this class:
```java
FileManager f = new FileManager("TeleOp", this); // Or "Auto"

// Then inside the start
f.initMotors(%Motor array list%);
f.StartTeleOp(%ElapsedTime%);
```

In this class we have two methods that automatically write to the buffer. 
#### Start the file manager
The file manager class is a thread which allows us to get information without being reliant on any external factors. To begin we need to create the file:
1.Figure out the directory we need to look at
2.Find how many files are in the directory
3.Create the file with the name with TeleOp or Auto along with the number it is in the list of files

To do this we use the built in Android file system. We create/find the directory first where "Type" is telling it weather it is TeleOp or Auto:
```java
directory = new File("/sdcard/Logs/"+Type+"/");
if(!directory.exists())
	directory.mkdirs();
```

Now we point the Android file system towards it and create the file:
```java
file = new File(directory, Type+ Objects.requireNonNull(directory.listFiles()).length+".txt");
fos = new FileOutputStream(file);
```
#### Methods writing to buffer
The buffer is an easy way to fix an issue that when we wrote the the file directly without a buffer the information would be mixed together example:
> Gamepad1-R/7542: false,EncoderLoc/7547: -0.059780984212385535,1.1956196842477107false,false,false

Gamepad1-R is separate information from EncoderLoc.

The way that we write to the buffer is with the writeFile method, the buffer itself is a ```List<String> buffer = new ArrayList<>();```
```java
	public void writeFile(String Name, Object LogThing, double time){
		buffer.add(Name+"/"+(int)time+":"+LogThing);
	}
```
#### What are we writing
The two things that we are writing using this class is the info from the gamepads and the motors. To do this we are utilizing the `writeFile()` method shown above. For the gamepads we write out each section of the gamepad, the left joystick, right joystick, ABYX, etc... How we're doing this is by making array lists of the specific items.
```java
writeFile("Gamepad1-LStick", new float[]{op.gamepad1.left_stick_x, op.gamepad1.left_stick_y}, time);
```
We pass it the name of what we are logging and all of the information in an array format we want to pass.

The way we write to the file is very important, it always needs to be in the same order. I have chosen an order of the name of the object, then when we wanted to save it, then finally the thing we actually want to save.
#### Why?
Why have we made a file manager? We decided to make a file manager because it would made the debug process a lot simpler and faster. We are also able to create graphs of the data we receive, this allows us to better show how the match went and quickly see any errors that my have accrued during the match. Another interesting thing that this allows us to do is show the judges in simpler terms what the robot is doing throughout the game.

#### How do we view the information?
Once we log all of the information