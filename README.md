# UDP
Coursework 5 - A simple client-server application


Multiple Clients connect to a Server via TCP. The first to connect will become the sender to send UDP packets to it. The server then relay these packets to all other clients who would be come receivers. At the end of transmition or when the client stops sending packets, the next client in line will become the sender to send UDP packs to all other clients and so on.
