# Dev-Portfolio
A professional portfolio highlighting my expertise in AI, NLP, full-stack development, and application building. Includes projects built with Python, React, Flask, and TensorFlow.

# Apps
### WhiteBoard
WhiteBoard is a distributed shared whiteboard system that allows multiple users to collaboratively draw, edit, and communicate in real time over a network. The application supports two main user roles: a manager and normal users.

### Introduction

This project builds a distributed shared whiteboard system that allows multiple users to collaborate and edit content together on a shared canvas over a network. The system has two main user roles:

Manager:
	Has all the drawing capabilities of normal users.
	Can create new whiteboard sessions, open existing whiteboard files, save the current state, and close the application for everyone.
	Approves new user join requests and can forcibly disconnect (“kick out”) any user from the shared session.
Normal Users:
	Can perform various drawing operations (lines, circles, ovals, rectangles).
	Can type text anywhere on the canvas.
	Choose from 16 different colors.
	Use an eraser tool to modify existing drawings.
	Chat with other connected users in a text-based chat window.
	The system leverages Java RMI (Remote Method Invocation) for real-time collaboration and drawing synchronization. It uses a Concurrent HashMap to handle concurrent access and manage the shared whiteboard state, ensuring that multiple clients can work together seamlessly.

The system leverages Java RMI (Remote Method Invocation) for real-time collaboration and drawing synchronization. It uses a Concurrent HashMap to handle concurrent access and manage the shared whiteboard state, ensuring that multiple clients can work together seamlessly.

### Features
	1.	Real-Time Collaboration:
		•	Every user sees updates to the whiteboard as they happen
	2.	Role-Based Access:
		•	Manager can control session-level actions (create, open, save, close)
		•	Manager must approve new user join requests
		•	Manager can “kick out” any user if needed
	3.	Drawing & Editing:
		•	Lines, circles, rectangles, ovals, text insertion, eraser tool
		•	16 color options for creativity and clarity
	4.	Chat System:
		•	Users can communicate with each other in real time via text messages
	5.	File Operations:
		•	Manager can save the current whiteboard state to a file and load from an existing file

### System Architecture
	1.	RMI Communication
		•	The server hosts RMI objects that manage the shared state of the whiteboard
		•	The clients invoke methods on these remote objects to perform drawing actions, request to join, send chat messages, etc
		•	Java RMI handles the low-level network communication, allowing real-time updates to propagate to all connected clients
	2.	Concurrency
		•	A Concurrent HashMap is used to store and manage the shared whiteboard data, ensuring thread-safe operations when multiple users draw or edit simultaneously
	3.	Manager vs. Normal User
		•	Manager is effectively the “admin” of the session: can open, save, or close the entire session
		•	Normal users have standard drawing and chat capabilities but require manager approval to join
### How to Start
	1.	Start the Server
 		java -jar WhiteBoardServer.jar <port number>
   		Example:   java -jar WhiteBoardServer.jar 1234
	2.	Start the Client
 		java -jar WhiteBoardClient.jar <host address> <port number>
		Example:   java -jar WhiteBoardClient.jar localhost 1234
    		Replace localhost with the server’s IP or hostname if running remotely
      		You will be prompted to enter a username. If the manager is already running, you can request to join
 ### Folder Structure
	 whiteBoard/
	├── bin/
	│   ├── client/
	│   ├── remote/
	│   └── server/
	├── src/
	├── WhiteBoardClient.jar
	├── WhiteBoardServer.jar
	└── README.md

# FullStack_Projects
### WebLife-Diabetes

WebLife-Diabetes is a full-stack web application designed to help doctors and patients monitor and manage diabetes more effectively. Doctors can track patient records, provide guidance, and patients can log their daily health data. The goal is to simplify communication and improve the overall management of diabetes care.

### Overview

In many healthcare systems, diabetes management can be fragmented and cumbersome. WebLife-Diabetes provides a centralized platform where patients can log their blood sugar levels, exercise routines, insulin usage, and weight. Meanwhile, clinicians (doctors, nurses, or health coaches) can view each patient’s daily records, offer feedback, and track patient progress in real time.

### Key Goals:
	1.	Enable easy monitoring of daily diabetic metrics
	2.	Provide a communication channel between patients and doctors
	3.	Help doctors analyze trends and adjust treatment or lifestyle advice accordingly

### Key Features
	•	Patient Dashboard
	•	Log daily metrics: blood sugar, exercise, insulin, and weight
	•	View personalized tasks or goals set by the clinician
	•	Clinician Dashboard
	•	View and manage multiple patients’ data in one place
	•	Provide comments, set tasks, and monitor patients’ compliance
	•	Identify unrecorded or missing data quickly
	•	Authentication & Session Management
	•	Secure login for both patients and clinicians using Passport.js
	•	Session-based authentication to keep track of user roles
	•	Responsive UI
	•	Accessible on different devices for both doctors and patients

### Tech Stack
	•	Front-End: HTML, CSS, Handlebars (templating)
	•	Back-End: Node.js, Express
	•	Database: MongoDB (Atlas) + Mongoose
	•	Authentication: Passport.js, Express-Session
	•	Deployment: Azure App Service
	•	Other:
	•	dotenv for environment variables
	•	nodemon for local development

### Project Structure
    WebLife-Diabetes
    ├── app.js                  # Main Express application
    ├── package.json
    ├── package-lock.json
    ├── .env                    # Local environment variables (not pushed to Azure)
    ├── controllers/            # Controller logic for patient/clinician features
    ├── models/                 # Mongoose schemas (Patients, Records, etc.)
    ├── routes/                 # Express route definitions
    ├── views/                  # Handlebars templates
    ├── public/                 # Static files (CSS, JS, images)
    ├── passport.js             # Passport configuration
    └── README.md               # Project README

### Setup & Installation
    1.	Run npm install to install dependencies
    2.	Create a .env file with your MongoDB connection string
    3.	Run npm start to start the server
    4.	Visit http://localhost:3000 to view the application

### Usage
	•	Local Development
	•	After starting the server, visit http://localhost:3000.
	•	Use a test patient account to log daily metrics.
	•	Use a test clinician account to view patient data, add comments, etc.
	•	Azure Deployment
	•	The application is deployed to Azure App Service.
	•	Environment variables (like MONGO_URL) are set in Azure Configuration (not .env).
	•	Access the live site at: https://weblife-diabetes.azurewebsites.net

### Account for clinician and patient:
    clinician1: 
        account: chris@gmail.com
        password: 12345678
        Name: Chris Cao
        Include patient: patient1 (Pat Smith), patient3 (Aman EM),
                         patient4 (Leonardo DiCaprio), patient5 (tori rich), 
                         patient6 (Nicole Kidman), patient10 (Isabella)

    clinician2: 
        account: makoto@gmail.com
        password: Qwert123
        Name: Makoto Zhang
        Include patient: patient2 (Apple Xin), patient7 (Charlotto),
                         patient9 (Olivia Liam), patient8 (Sophia Armstor)

    patient1: 
            account:  pat@gmail.com
            password: Pat12345
    patient2:
            account:  apple@gmail.com
            password:  1234Apple
    patient3:
            account:  aman@gmail.com
            password:  12345678A
    patient4:
            account:  leon@gmail.com 
            password:  87654321Ll
    patient5:
            account:  h1@gmail.com 
            password:  Hh1234567
    patient6:
            account:  nicole@gmail.com
            password:  12345678Nn
    patient7:
            account:  Charlotte@gmail.com
            password:  12345678Cc
    patient8:
            account:  Sophia@gmail.com
            password:  12345678So
    patient9:
            account: Olivia@gmail.com
            password:  Oli12345678
    patient10:
            account: Isabella@gmail.com
            password:  12345678Ls
