Design:

Hub opens a ServerSocket and listens to a port.
Client connects to Hub through socket.
Each connection is saved as a map in Hub.

CommandProcessor: Read command string from socket, parse string to Message, save into MessageQueue
HubMessageProcessor: Ingest Message object in MessageQueue in Hub and put strings into corresponding clients' sockets
ClientMessageProcessor:Pull string message from socket for client and print it out
CommandProcessor: Receive commands from socket, parse it into a Message object and pass it to Hub


================================================================================================

It is recommended to Open the project in IDE like Eclipse.

To run the program:
1, Run Java application: Hub
2, Run Java application: Client
You may run Client as many times as you want.

For each client, you may type the following commands:
Who am I?
Who is here?
relay 2,3 body: my message




