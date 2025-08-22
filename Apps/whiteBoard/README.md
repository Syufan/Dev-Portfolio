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
		•Every user sees updates to the whiteboard as they happen
	2.	Role-Based Access:
		•Manager can control session-level actions (create, open, save, close)
		•Manager must approve new user join requests
		•Manager can “kick out” any user if needed
	3.	Drawing & Editing:
		•Lines, circles, rectangles, ovals, text insertion, eraser tool
		•16 color options for creativity and clarity
	4.	Chat System:
		•Users can communicate with each other in real time via text messages
	5.	File Operations:
		•Manager can save the current whiteboard state to a file and load from an existing file

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






### Further Improvement

Currently, the Canvas class listens to user actions (mouse events) and directly calls server.broadcastCanvas(...).
This design creates a tight coupling between the UI layer (Canvas) and the networking layer (Server).

Problems with the current design
	1.	Strong coupling – The Canvas is supposed to be a drawing component, but it also knows how to communicate with the server.
	2.	Low flexibility – If the networking implementation changes (e.g., switching from RMI to WebSocket), the Canvas code must be modified.
	3.	Harder testing – It is difficult to test the drawing logic independently, because it depends on a remote server object.

Proposed Improvement

Introduce an event handler interface to decouple drawing logic from server communication.
	•	Define an interface (e.g., DrawEventHandler) that handles outgoing drawing events.
	•	Let Canvas generate Draw messages and pass them to the handler instead of calling the server directly.
	•	Let the Client class implement the handler and forward the messages to the server.

Benefits
	•	Single responsibility – Canvas only cares about drawing and UI events.
	•	Better abstraction – Networking and server logic stay in the Client and Server classes.
	•	Easier maintenance – Switching protocols (RMI → WebSocket) requires no changes to Canvas.
	•	Improved testability – The drawing component can be tested with a mock handler without any server dependency.