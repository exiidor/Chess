# Chess Server Protocol
The chess server uses JSON transmitted over WebSockets for communication. The general structure of these JSON strings is as follows:
```jsonc
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
- `LoginC2S`: [example](./examples/packets/LoginC2S.jsonc), [schema](./schemas/packets/LoginC2S.jsonc)
- `LoginResultS2C`: [example](./examples/packets/LoginResultS2C.jsonc), [schema](./schemas/packets/LoginResultS2C.jsonc)
- `UserListS2C`: [example](./examples/packets/UserListS2C.jsonc), [schema](./schemas/packets/UserListS2C.jsonc)


## Kicking clients
When the server kicks a client, it may optionally send a `KickS2C` packet immediately before closing the connection, which
contains the reason why the client was kicked (so the client can display it to the user). This is not required though -
the server may simply terminate the connection. After the connection is closed, the server should send the new user list
to the other connected clients.

**Packets:**
- `KickS2C`: [example](./examples/packets/KickS2C.jsonc), [schema](./schemas/packets/KickS2C.jsonc)


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
- `CreateGameC2S`: [example](./examples/packets/CreateGameC2S.jsonc), [schema](./schemas/packets/CreateGameC2S.jsonc)
- `CreateGameResultS2C`: [example](./examples/packets/CreateGameResultS2C.jsonc), [schema](./schemas/packets/CreateGameResultS2C.jsonc)
- `InviteS2C`: [example](./examples/packets/InviteS2C.jsonc), [schema](./schemas/packets/InviteS2C.jsonc)
- `InviteResponseC2S`: [example](./examples/packets/InviteResponseC2S.jsonc), [schema](./schemas/packets/InviteResponseC2S.jsonc)
- `JoinGameS2C`: [example](./examples/packets/JoinGameS2C.jsonc), [schema](./schemas/packets/JoinGameS2C.jsonc)
- `PlayerJoinedS2C`: [example](./examples/packets/PlayerJoinedS2C.jsonc), [schema](./schemas/packets/PlayerJoinedS2C.jsonc)
- `StartGameS2C`: [example](./examples/packets/StartGameS2C.jsonc), [schema](./schemas/packets/StartGameS2C.jsonc)


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
- `LeaveGameC2S`: [example](./examples/packets/LeaveGameC2S.jsonc), [schema](./schemas/packets/LeaveGameC2S.jsonc)
- `PlayerLeftS2C`: [example](./examples/packets/PlayerLeftS2C.jsonc), [schema](./schemas/packets/PlayerLeftS2C.jsonc)


## Committing a move
```
Client A        Server     Client B
|                 |               |
| RequestMovesC2S |               |
| >>>>>>>>>>>>>>> |               |
|                 |               |
|     MovesS2C    |               |
| <<<<<<<<<<<<<<< |               |
|                 |               |
|     MoveC2S     |               |
| >>>>>>>>>>>>>>> |               |
|                 |               |
|  MoveResultS2C  |    MoveS2C    |
| <<<<<<<<<<<<<<< | >>>>>>>>>>>>> |
```
1. The client sends a `RequestMovesC2S` packet to the server, requesting the list of possible moves for a piece.
2. The server sends a `SendMovesS2C` packet back to the client, containing the list of possible moves for the piece.
3. The client sends a `MoveC2S` packet to the server, containing the move to be made.
4. The server validates the move and sends a `MoveResultS2C` packet back to the client, containing the result of the move, or an error message if the move was invalid.
5. The server sends a `MoveS2C` packet to all clients in the game, notifying them of the move that was made.

**Packets:**
- `RequestMovesC2S`: [example](./examples/packets/RequestMovesC2S.jsonc), [schema](./schemas/packets/RequestMovesC2S.jsonc)
- `MovesS2C`: [example](./examples/packets/MovesS2C.jsonc), [schema](./schemas/packets/MovesS2C.jsonc)
- `MoveC2S`: [example](./examples/packets/MoveC2S.jsonc), [schema](./schemas/packets/MoveC2S.jsonc)
- `MoveResultS2C`: [example](./examples/packets/MoveResultS2C.jsonc), [schema](./schemas/packets/MoveResultS2C.jsonc)
- `MoveS2C`: [example](./examples/packets/MoveS2C.jsonc), [schema](./schemas/packets/MoveS2C.jsonc)
