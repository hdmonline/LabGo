# LabGo
A smart inventory management system with automatic check in/out mechanism inspired by Amazon Go.
This repo is the Android app of this system.

## Architecture
The system architecture is shown below.
![system](https://github.com/hdmonline/LabGo/blob/master/image/Smart%20Inventort%20Management%20System.png)

## Check in/out Schematic
The schematic of the system is shown below.
![schematic](https://github.com/hdmonline/LabGo/blob/master/image/schematic.png)
The blue box represents the tool repository with a stack of cabinets in it.
The orange line represents the gate of the repository with cameras on both sides. 
Four RFID readers are placed on both sides of the gate to detect the tools that are being checked in/out.
The prototype of the check in/out system is built with small RFID reader and tags. It also works with the QR codes on the gate.
The cameras are connected to 2 Raspberry Pis and the lock of the gate is implemented by a solenoid controlled by 
[Particle Photon](https://store.particle.io/collections/wifi/products/photon) with a driver board.
The hardware used in this system is shown below.
![schematic](https://github.com/hdmonline/LabGo/blob/master/image/hardware.png)

## Authentication
The authentication of the system is hosted on [Firebase](https://firebase.google.com/) shown below.
![hardware](https://github.com/hdmonline/LabGo/blob/master/image/hardware.png)
### Sign Up
* Picture of BuzzCard (student ID card)
* Recognize GTID by text recognition
* Type in user information
### Sign In
* GTID with password
* Student ID with phone camera

## Database Design
The database design is shown below. It is hosted on AWS RDS.
![database](https://github.com/hdmonline/LabGo/blob/master/image/database.png)

## App
|Login|Signup|Inventory|
|---|---|---|
|![login](https://github.com/hdmonline/LabGo/blob/master/image/login.png)|![signup](https://github.com/hdmonline/LabGo/blob/master/image/signup.jpg)|![inventory](https://github.com/hdmonline/LabGo/blob/master/image/inventory.png)|

|Search Tool|TA Functions|Add Tool (TA)|
|---|---|---|
|![searchtool](https://github.com/hdmonline/LabGo/blob/master/image/searchtool.png)|![ta](https://github.com/hdmonline/LabGo/blob/master/image/ta.png)|![addtool](https://github.com/hdmonline/LabGo/blob/master/image/addtool.png)|
