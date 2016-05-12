# sQuire by Knight Writers
CS383 Project by the Knight Writers group.

##Distribution Instructions
Both the server and client can downloaded and executed as runnable jar.
The code is also open source and accessible for anyone who wants to set
up their own server to run and access using the sQuire client.

Client Project: sQuire kw/GUI/sQuire/src/sq/app/

Server Project: sQuire kw/Source/Server/SquireServer/src/squire/

1. Obtain Raspberry Pi
2. Install Raspbian OS (Java included)
3. Install MySQL
4. Download sQuire source code from github.com/voxelv/sQuire kw
5. Run sql files to create database structure:
a) 01 Create Users.sql
b) 02 Create Messages.sql
c) 03 Create Projects.sql
6. Modify function setProperties() in DBConnector.java to match MySQL
configuration
7. Modify the connection properties between server and client
a) Port number (default 9898)
i. Server: Change this in Server.java line 27
ii. Client: Change this in MainApp.java line 45
b) Server URL (or IP address) (default is our server)
i. Client: Change this in MainApp.java line 45
8. Modify stormpath connection
a) Create an account at Stormpath.com
b) Create a new application in stormpath
c) Go to stormpath home page
d) Click “Manage API Keys”
e) Click “Create API Key”
f) Copy the ‘id’ and ‘secret’ values to server AccountManager.java
lines 55 & 56
9. Compile the Server into a runnable JAR.
a) use makeJar.sh in Source/Server/SquireServer/ or an IDE.
10. Run server in background on your server (linux: “java -jar Source/Server/SquireServer/Server.jar
&”)
11. If you are planning on using this over the internet, will need to port
forward the port you configured the server to listen to (default 9898)
to your server
12. Compile client runnable JAR using IDE
13. Run sQuireClient.jar

##Quick Start Guide
1. Registration: 
Once the steps from the distribution section have been completed,
create an account via the register tab in the login pane.
2. User Activities: 
Once you are logged in, you can create projects, edit code, and even
edit other users’ projects. To gain access to another user’s project,
simply obtain the password to their project, select Edit→Project Access
in the drop down menu, then enter the password.
When editing a project, the user can exit their current project to
return to the list of projects available to them.
3. Project Interaction: 
Once you have a project open you can add folders and files, edit file
contents, and run the project locally.
4. Chat: 
The chat window is in the bottom right corner of the client application.
By default, all users are logged into global chat. To join a chat
room, simply type “\join (chatroomname)”. If this chat room doesn’t
exist, this will automatically create a chat room with that name. To
send a message to a joined chat room, simply select the chat room
in the dropdown list to the left of the chat text area, then type and
send your message.

## Complete Documentation
* [Master Doc] (https://www.overleaf.com/4388669wxqmrp)

##OneNote
* [OneNote] (https://onedrive.live.com/view.aspx?resid=273538595B398734!133&ithint=onenote%2c&app=OneNote&authkey=!ABbDfXT81mMyGkQ)

## Trello Backlog
* New: 02-19-2016
* [Trello Backlog] (https://trello.com/invite/knightwriterscs383/f97390a46c8bb03a20cc124a75af9ae5)

## Class Diagrams
* put diagrams and descriptions here
* [Class Diagrams](https://www.overleaf.com/4361110npqmqd)

## Functional/Non-Functional Requirements
* [Our Requirements Doc](https://www.overleaf.com/4286506kvbrwb#/12707218/)
* [Aggregate Requirements Doc](https://www.overleaf.com/4306618jzdznq)

## Use Cases
## Homework 1's links:
* Team 1: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team1.pdf)
* Team 2: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team2.pdf)
* Team 3: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team3.pdf)
* Team 4: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team4.pdf)

## Homework 2's links:
Team A: [Overleaf] (https://www.overleaf.com/4236744jrrqpg#/12532352/)
Team C: [Overleaf] (https://www.overleaf.com/4236690nydzfb#/12532109/)

