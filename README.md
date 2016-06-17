SmartDashboardExtensions2016
============

Our custom SmartDashboard extensions for 2016 

#### Extensions:
* [Slider](src/main/java/org/usfirst/frc/team2084/smartdashboard/extensions/Slider.java) - Simple slider, which the SmartDashboard lacks by default.
* [Wheel Controller Display](src/main/java/org/usfirst/frc/team2084/smartdashboard/extensions/WheelControllerDisplay.java) - Display that shows power output, speed and distance for each side of our drive train. Works with our custom drive code.
* [Parameter Command](src/main/java/org/usfirst/frc/team2084/smartdashboard/extensions/ParameterCommand.java) - Modified Command widget that supports parameters that are sent to the robot.
* [Better Compass](src/main/java/org/usfirst/frc/team2084/smartdashboard/extensions/BetterCompass.java) - Compass which works correctly with radians and fixes a few bugs.
* [UDP Stream Viewer](src/main/java/org/usfirst/frc/team2084/smartdashboard/extensions/vision/UDPStreamViewerExtension.java) - Viewer that displays a stream of JPEG images sent over UDP.
* [Remote Vision Extension](src/main/java/org/usfirst/frc/team2084/smartdashboard/extensions/vision/VisionExtension.java) - Extension that was used to view our camera stream and tune our vision algorithm.

#### Our other 2016 repositories:
* [CMonster2016](../../../CMonster2016) - Our 2016 robot code
* [VisionProcessor2016](../../../VisionProcessor2016) - Implementation of our computer vision algorithms
* [VisionServer2016](../../../VisionServer2016) - Launching code for our vision system on our NVIDIA Jetson TK1
* [VisionTest2016](../../../VisionTest2016) - Application for testing and calibrating our vision code
* [WebDashboard2016](../../../WebDashboard2016) - Our web dashboard that makes tuning and debugging easy (uses [pynetworktables2js](../../../../robotpy/pynetworktables2js))