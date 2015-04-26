# UDP


Coursework 5 - A simple client-server application

Multiple Clients connect to a Server via TCP. The first to connect will become the sender, sending UDP packets. The server handler then relay these UDP packets to the UDP broadcast socket where all other clients who are receivers can listen to through the server handler. At the end of transmition or when the client stops sending packets, the Server decides who would be the next client to become the sender to send UDP packs as above and so on.

<b>The Server</b>
 - Server listens to clients via TCP.
 - Accepts connection from Clients.
 - Adds new instances of ServerClientHandler to the list and fires a new Thread to run each it time.
 - The waiting for new Clients to connect, has a timeout, after which will check if any shutdown was issued.
 - When a shutdown was issued, it will exit the endless loop, close the socket, and happily exit the application.

<b>The ServerClientHandler</b>
 - Initiated by the Server with server instance, Client's TCP socket, an unique ID and Role information.
 - Client and Handler will handshake on the above information which is passed between the two via TCP. 
 - After initial setup, the runnable starts, and the Handler fires a Listener which will listen for any future TCP messages coming though.
 - The only Role the endless loop is interested in checking, is the Client's role Sender, which means the handler is to receive packets from the Client and relay these to all other Clients via their handler instantiations.
 - Whenever an empty packet is received, the Client either sent all data or went away, and a new sends is picked up.
 - When the role shutdown is requested, the endless loop is exited and the handlers is finalised.

<b>The Client</b>
 - Requests user to insert two paths: one for sending and another for receiving: ClientImpl /mypath/send /mypath/receive 
 - All files insider the send directory will be selected to be sent, and all files received via UDP will be stored in /mypath/receive
 - After all initialisations were made in setting role, id, sockets, ports, etc.., the run starts by also firing a Listener Thread to capture any further TCP messages, and free the Client to do other tasks.
 - When the CLient is on the sender role, send the next file to send.
 - When finished, other Client will be picked up to send or the server will get back to this one to send another file if the other Clients have no files to send.
 - When end of file is reached, will send a TCP message to say file was transferred.
 - When there is not more files to be sent, will send a TCP message saying so.
 - If a shutdown was issued, the endless look wil break and Client will finalise.
