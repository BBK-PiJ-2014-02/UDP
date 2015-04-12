# UDP
Coursework 5 - A simple client-server application


Multiple Clients connect to a Server via TCP. The first to connect will become the sender to send UDP packets to it. The server then relay these packets to all other clients who would be receivers. At the end of transmition or when the client stops sending packets, the next client in list will become the sender to send UDP packs to all other clients and so on.

<b>The Server</b>
 - Server listens to clients via TCP.
 - Accepts connection from Clients.
 - Create new instances of ServerClientHandler passing in the Server's UDP Socket, the Client's TCP Socket, the Client's uniqueID and the current Client's Role.
 - Adds the instances of ServerClientHandler to Server list.
 - Each UDP packet received is then sent to all ServerClientHandler where client is the receiver.
 - When packets stop coming in, the next non-sender ServerClientHandler will become the sender.

<b>The Client</b>
 - Collects from the args the Audio file to be broadcast.
 - Tries to connect with the Server.
 - When accepted, requests for an unique ID and the role.
 - When requested, opens a new UDP connection to Server.
 - Depending on the role, starts sending or receiving packets via the new UPD connection.
 - If sending, sends the initially given Audio file
 - If receiving, saves the received Audion file into file.

<b>The ServerClientHandler</b>
 - Initiated by the Server with: 
   - New Server UDP socket - to relay any UDP packets to be broadcasted
   - The Client's TCP socket to setup the remaining info Client will require
   - An unique ID and Role information to reply to the Client at the Client's request.
 - Client requests for the uniqueID and the Role which is then sent via the TCP connection.
 - When done, a new UDP Socket is opened and then requested to Client listen to it to.
 - If role is sending, while the client has UDP packets to send, relays them over to the Server.
 - If role is receiving, relays Server UDP packets to client.
 - If Client stops sending packets or closes connection, Server gets informed.
