# SC4051-RemoteFileAccessSystem

## Instructions to set-up

### In Client’s Command Line Interface: 
1. Locate the /src directory of the SC4051-RemoteFileAccessSystem project.<br />
2. Compile the client with the relevant packages:<br />
**`javac client/*.java common/*.java marshalling/*.java -d ../out`**<br />
3. Run the client:<br />
**`java -cp ../out client.ClientMain`**<br />
Server address: 10.91.137.17

### In Server’s Command Line Interface:
1. Locate the /src directory of the SC4051-RemoteFileAccessSystem project.<br />
2. Compile the server with the relevant packages:<br />
**`javac server/*.java common/*.java marshalling/*.java strategy/*.java -d ../out`**<br />
3. Run the server:<br />
**`java -cp ../out server.ServerMain`**<br />

### To get IP address:
For macOS:<br />
**`ifconfig en0 | grep inet | grep -v inet6 | awk '{print $2}'`**<br />
For Windows:<br />
**`ipconfig`**<br />

### File path example:
macOS:<br />
**`/Users/alex/Desktop/Test.txt`**<br />
Windows:<br />
**`C:\Users\User\Desktop\Test.txt`**<br />

### Testing:
**Single machine testing: Enter 'localhost' or '127.0.0.1' for IP address when prompted on the client CLI.**<br />
- Simply run 2 separate terminal instances, each compiling and running the client and server code separately.<br />
- A 3rd terminal instance can also be run to act as the second client which monitors non-idempotent operations.<br />

**Separate machine testing: Enter the server's IP address when prompted on the client CLI.**<br />
- Tested to also work cross-platform.<br />

### Additional notes:
This project was built and tested with JDK 17.
