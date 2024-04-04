### SC4051-RemoteFileAccessSystem

## Instructions for running the code.

# In Client’s Command Line Interface: 
Locate the /src directory of the SC4051-RemoteFileAccessSystem project.\n
Compile the client with the relevant packages:\n
**javac client/*.java common/*.java marshalling/*.java -d ../out**
Run the client:\n
**java -cp ../out client.ClientMain
**
# In Server’s Command Line Interface:
Locate the /src directory of the SC4051-RemoteFileAccessSystem project.
Compile the server with the relevant packages: 
**javac server/*.java common/*.java marshalling/*.java strategy/*.java -d ../out**
Run the server:
**java -cp ../out server.ServerMain**
