# Softwareschreiber Chess
## Abstract
This project is a web-based chess game that allows users to play against a bot or another user.
The backend is built in Java using a custom evaluation function, which is around a 1000 Elo rating.

## Tech-Stack
- `Java` Backend for the chess engine and Server with User Management, Login and Game Management
- `Nuxt` for the frontend and universal rendering
- `Microsoft Azure` for deployment
- `HTML/CSS/JS` for basic web development and UI/UX
- `JSON` for data interchange between frontend and backend

## Features
You can play chess against a bot around a 1000 Elo rating or choose to play against another user.

### Backend in JAVA:
#### The chess engine:
- Utilizes the <a href="https://en.wikipedia.org/wiki/Minimax" style="text-decoration: underline; color: grey;">Minmax</a> algorithm with alpha-beta pruning for move generation, the depth of the search is proportional to the count of pieces on the board.
- Custom evaluation function, that calculates the value of a position based on material, mobility and if there is a checkmate. Here is a simplified version of the function:
```java
public int evaluate() {
	return absoluteMaterialEvaluation()
			+ mobilityEvaluation()
			+ relativeMobilityEvaluation()
			+ enemyInCheckMate();
}
```
> This is a very basic approach to chess evaluation.
- Basic move generation and validation.
- Game logic to handle turns, check/checkmate detection, and game state management.

#### Webserver features:
- User management with registration, login, and session handling
- Game management to create, join, and play games
- <b>Custom</b> communication protocol using JSON for data exchange.

### Frontend in NUXT:
- A login page for the user to enter a username and password
- An interactive chessboard
- A disconnect button to end the game

## Planned Improvements
- Player chat functionality
- Themes for the chessboard
