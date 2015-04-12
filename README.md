# UDP
Coursework 5 - A simple client-server application

Multiple Clients connect to a Server via TCP. The first to connect will become the sender, sending UDP packets. The server handler then relay these UDP packets to the UDP broadcast socket where all other clients who are receivers can listen to through the server handler. At the end of transmition or when the client stops sending packets, the Server decides who would be the next client to become the sender to send UDP packs as above and so on.

<b>The Server</b>
 - Server listens to clients via TCP.
 - Accepts connection from Clients.
 - Create new instances of ServerClientHandler passing in itself, the Client's TCP Socket, the Client's uniqueID and the current Client's Role.
 - Adds the new instances of ServerClientHandler to private Server list.
 - Each UDP packet sent by the Client is then sent to the ServerClientHandler and then broadcasted to the UDP port where all receiving clients are listening to throught their ServerClientHandlers.
 - When packets stop coming in, next non-sender ServerClientHandler added to the server list, will become the new sender.

<b>The Client</b>
 - Collects from the args the Audio file to be broadcast.
 - Tries to connect with the Server.
 - When accepted, requests for an unique ID and the role.
 - When requested, opens a new UDP connection to Server.
 - Depending on the role, starts sending or receiving packets via the new UPD connection.
 - If sending, sends the initially given Audio file
 - If receiving, saves the received Audio packets into file.

<b>The ServerClientHandler</b>
 - Initiated by the Server with: 
   - A server instance
   - The Client's TCP socket to setup the remaining info Client will require
   - An unique ID and Role information to reply to the Client at the Client's request.
 - Client requests for the uniqueID and the Role which is then sent via the TCP connection.
 - A new UDP Socket is opened and then requested for the Client to listen to it.
 - If role is sending, while the client has UDP packets to send, relays them over to the Server.
 - If role is receiving, relays Server UDP packets to client.
 - If Client stops sending packets or closes connection, ServerClientHandler sets the next Client to be the sender and either switches role to receiver, or exits if Client closed connection.
