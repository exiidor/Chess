# Softwareschreiber Chess
This project consists of a basic [chess engine](./chess-engine/) (consumable as a library), a [chess server](./chess-server/) and a web-based [frontend](./chess-gui-nuxt/). You can play against other users or against a bot, which uses a custom evaluation function under the hood that is around 1000 Elo.

## Tech Stack
- [Java 21](https://dev.java/) for the chess engine and server with user management, login system and multiplayer game management
- [Nuxt 3](https://nuxt.com/) for the user-facing frontend
- Custom [websocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)- and [JSON](https://en.wikipedia.org/wiki/JSON)-based [protocol](./chess-server/docs/protocol.md) for data interchange between frontend and backend
- [Microsoft Azure](https://azure.microsoft.com/) for hosting. Other providers work too, it's just what our deployment guide targets.


## Features
You can play chess against a bot around a 1000 Elo rating or choose to play against another user.

### Chess Engine
- Utilizes the [Minmax](https://en.wikipedia.org/wiki/Minimax) algorithm with alpha-beta pruning for move generation, the depth of the search is proportional to the count of pieces on the board.
- Custom evaluation function, that calculates the value of a position based on material, mobility and if there is a checkmate. Here is a simplified version:
  ```java
  public int evaluate() {
  	return absoluteMaterialEvaluation()
  			+ mobilityEvaluation()
  			+ relativeMobilityEvaluation()
  			+ enemyInCheckMate();
  }
  ```
  This is a very basic approach to chess evaluation.
- Basic move generation and validation
- Game logic to handle turns, check/checkmate detection, and game state management

### Chess Server
- User management with login and session handling functionality
- Game management handling creating, joining, and playing games
- Custom [communication protocol](./chess-server/docs/protocol.md)

### Frontend
- Login page
- Game lobby with active games and players
- Game creation, customization, and joining
- Interactive chessboard
- Game history
- Player information
- Responsive design for mobile and desktop
