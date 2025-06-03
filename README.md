# Softwareschreiber Chess

## Tech-Stack
- `Java` Backend for the chess engine
- `Nuxt` for the frontend
- Deployment using `TODO INSERT DEPLOYMENT`
- UI/UX using basic HTML/CSS/JS with Tailwind CSS
- `JSON` for data interchange

## Features
A chess engine that can play chess against a human player, around a 1000 Elo rating.

__Backend:__
- Utilizes the [Minmax](https://en.wikipedia.org/wiki/Minimax) algorithm with [alpha-beta pruning](https://en.wikipedia.org/wiki/Alpha–beta_apruning) for move generation
- Custom evaluation function, that calculates the value of a position based on material, mobility and if there is a checkmate.

__Frontend:__
- A login page for the user to enter a username and password
- An interactive chessboard, allowing drag and drop, as well as click and move.
- A disconnect button to end the game

## Future Improvements
- Chat functionality
- Online multiplayer
- Different game modes
- Themes
- Different chess engines (evaluation functions)
