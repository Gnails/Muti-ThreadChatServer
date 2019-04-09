#**This is a chat server program which can provide service for mutiple users.**#
## **Here is the description about several most important class:**
***++==client.PoolClientUser:==++ This is a chat client .
++==Dispatcher.DispatcherController:== ++This a Dispatcher for processing the request from client.It can allocate port ,and return it back to client,make the client start a new connection to this port;
==++pool.InPoolAdmin:++==A pool which message can be taken from;
==++pool.OutPoolAdmin:++==A Pool which mananges the data to be sent;
++==util.Sender:==++A util for sending;
util.listener: A tool for receiving;***

