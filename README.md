# SC4051-RemoteFileAccessSystem

## Instructions for running the code.

### In Client’s Command Line Interface: 
1. Locate the /src directory of the SC4051-RemoteFileAccessSystem project.<br />
2. Compile the client with the relevant packages:<br />
**javac client/*.java common/*.java marshalling/*.java -d ../out**<br />
3. Run the client:<br />
**java -cp ../out client.ClientMain**<br />

### In Server’s Command Line Interface:
1. Locate the /src directory of the SC4051-RemoteFileAccessSystem project.<br />
2. Compile the server with the relevant packages:<br />
**javac server/*.java common/*.java marshalling/*.java strategy/*.java -d ../out**<br />
3.Run the server:<br />
**java -cp ../out server.ServerMain**
