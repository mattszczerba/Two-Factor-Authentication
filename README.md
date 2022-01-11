# Two-Factor-Authentication 
Two Factor Authentication project for CS492 class written in Java (Eclipse)

### Server:
* Waits for client to talk to on localhost.
* Accesses stored UserData file, which for my program acts as a small database 
* Reads incoming client data such as username/password/token
* Generates server-side authentication token based off of seed matching user in UserData file
* Compares incoming data with UserData file as well as server-side authentication token
* Sends back authentication status to client

### Client:
- Talks to server on localhost
- Inputs user data of username, token, and password from text boxes
- Password converted to a one-way SHA-256 hash to send, rather than plaintext
- Login button action sends username/token/password to server to evaluate authentication
- Text box receives and displays server authentication status messages
- Clear button to clear contents

### Token Generator:
* Uses TOTP algorithm, which is also found server-side for comparison
* Time-based and only creates new usable token for every 30 seconds. 
* Hard coded with specific seed, similar to physical RSA Keyfobs. Only program and server would have. User doesn’t need to know seed, only token. 
