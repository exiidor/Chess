# Chess Server Protocol
The chess server uses JSON transmitted over WebSockets for communication. The general structure of these JSON strings is as follows:
```json5
{
	"type": "...",
	"data": {
		// packet data
	}
}
```
- `type`: The type of packet being sent. This is a string that identifies the packet type.
- `data`: The data of the packet. This is a JSON object that contains the data for the packet.


## Login
```
Client A       Server      Client B
|                |                |
|    LoginC2S    |                |
| >>>>>>>>>>>>>> |                |
|                |                |
| LoginResultS2C |                |
| <<<<<<<<<<<<<< |                |
|                |                |
|   UserListS2C  |  UserListS2C   |
| <<<<<<<<<<<<<< | >>>>>>>>>>>>>> |
```
1. On connect, the client sends a login packet to the server. This allows the server to test the validity of the client, as well as allowing the client to declare metadata about itself, such as the username.
2. After validating the login packet, the server sends an acknowledgement packet back to the client. If the login was successful, the new user list is transmitted to all connected clients.

The server will ignore any other packets from the client until this entire exchange has been completed, and may kick the client if any other packet is received during this stage.

**Packets:**
- `LoginC2S`: [example](./packets/LoginC2S-example.json5), [schema](./packets/LoginC2S-schema.json5)
- `LoginResultS2C`: [example](./packets/LoginResultS2C-example.json5), [schema](./packets/LoginResultS2C-schema.json5)
- `UserListS2C`: [example](./packets/UserListS2C-example.json5), [schema](./packets/UserListS2C-schema.json5)


## Kicking clients
When the server kicks a client, it may optionally send a `Kick` packet immediately before closing the connection, which
contains the reason why the client was kicked (so the client can display it to the user). This is not required though -
the server may simply terminate the connection. After the connection is closed, the server should send the new user list
to the other connected clients.

**Packets:**
- `KickS2C`: [example](./packets/KickS2C-example.json5), [schema](./packets/KickS2C-schema.json5)


## Creating a game
```
Client A            Server         Client B
|                     |                   |
|    CreateGameC2S    |                   |
| >>>>>>>>>>>>>>>>>>> |                   |
|                     |                   |
| CreateGameResultS2C |                   |
| <<<<<<<<<<<<<<<<<<< |                   |
|                     |                   |
|                     |     InviteS2C     |
|                     | >>>>>>>>>>>>>>>>> |
|                     |                   |
|                     | InviteResponseC2S |
|                     | <<<<<<<<<<<<<<<<  |
|                     |                   |
|   PlayerJoinedS2C   |    JoinGameS2C    |
| <<<<<<<<<<<<<<<<<<< | >>>>>>>>>>>>>>>>> |
|                     |                   |
|     StartGameS2C    |   StartGameS2C    |
| <<<<<<<<<<<<<<<<<<< | >>>>>>>>>>>>>>>>> |
```
1. The client sends a `CreateGameC2S` packet to the server, requesting to create a game with a set of parameters.
2. The server validates the request and sends a `CreateGameResultS2C` packet back to the client, containing the result of the request. On success, the game is created and the client is added to the game, which gets transmitted as part of the result packet.
3. The server sends an `InviteS2C` packet to all invited clients, notifying them of the new game and allowing them to join it.
4. The invited clients respond with an `InviteResponseC2S` packet, indicating whether they accept or decline the invitation.
5. All clients that accepted the invitation are added to the game and get sent a `JoinGameS2C` packet, and all clients already in the game are notified of the new player that joined.
6. Once all required clients have joined, the server sends a `StartGameS2C` packet to all clients in the game, notifying them that the game has started.

**Packets:**
- `CreateGameC2S`: [example](./packets/CreateGameC2S-example.json5), [schema](./packets/CreateGameC2S-schema.json5)
- `CreateGameResultS2C`: [example](./packets/CreateGameResultS2C-example.json5), [schema](./packets/CreateGameResultS2C-schema.json5)
- `InviteS2C`: [example](./packets/InviteS2C-example.json5), [schema](./packets/InviteS2C-schema.json5)
- `InviteResponseC2S`: [example](./packets/InviteResponseC2S-example.json5), [schema](./packets/InviteResponseC2S-schema.json5)
- `JoinGameS2C`: [example](./packets/JoinGameS2C-example.json5), [schema](./packets/JoinGameS2C-schema.json5)
- `PlayerJoinedS2C`: [example](./packets/PlayerJoinedS2C-example.json5), [schema](./packets/PlayerJoinedS2C-schema.json5)
- `StartGameS2C`: [example](./packets/StartGameS2C-example.json5), [schema](./packets/StartGameS2C-schema.json5)


## Leaving a game
```
Client A          Server         Client B
|                   |                   |
|   LeaveGameC2S    |                   |
| >>>>>>>>>>>>>>>>> |                   |
|                   |                   |
|                   |   PlayerLeftS2C   |
|                   | >>>>>>>>>>>>>>>>> |
```
1. The client sends a `LeaveGameC2S` packet to the server, requesting to leave the game.
2. The server removes the client from the game and sends a `PlayerLeftS2C` packet to all other clients in the game, notifying them of the player that left.

**Packets:**
- `LeaveGameC2S`: [example](./packets/LeaveGameC2S-example.json5), [schema](./packets/LeaveGameC2S-schema.json5)
- `PlayerLeftS2C`: [example](./packets/PlayerLeftS2C-example.json5), [schema](./packets/PlayerLeftS2C-schema.json5)


## Committing a move
```
Client A      Server     Client B
|               |               |
|  GetMovesC2S  |               |
| >>>>>>>>>>>>> |               |
|               |               |
|  SendMovesS2C |               |
| <<<<<<<<<<<<< |               |
|               |               |
|    MoveC2S    |               |
| >>>>>>>>>>>>> |               |
|               |               |
| MoveResultS2C |    MoveS2C    |
| <<<<<<<<<<<<< | >>>>>>>>>>>>> |
```
1. The client sends a `GetMovesC2S` packet to the server, requesting the list of possible moves for a piece.
2. The server sends a `SendMovesS2C` packet back to the client, containing the list of possible moves for the piece.
3. The client sends a `MoveC2S` packet to the server, containing the move to be made.
4. The server validates the move and sends a `MoveResultS2C` packet back to the client, containing the result of the move, or an error message if the move was invalid.
5. The server sends a `MoveS2C` packet to all clients in the game, notifying them of the move that was made.

**Packets:**
- `GetMovesC2S`: [example](./packets/GetMovesC2S-example.json5), [schema](./packets/GetMovesC2S-schema.json5)
- `SendMovesS2C`: [example](./packets/SendMovesS2C-example.json5), [schema](./packets/SendMovesS2C-schema.json5)
- `MoveC2S`: [example](./packets/MoveC2S-example.json5), [schema](./packets/MoveC2S-schema.json5)
- `MoveResultS2C`: [example](./packets/MoveResultS2C-example.json5), [schema](./packets/MoveResultS2C-schema.json5)
- `MoveS2C`: [example](./packets/MoveS2C-example.json5), [schema](./packets/MoveS2C-schema.json5)
