declare interface GameInfo {
	id: string
	whitePlayer: User | null
	blackPlayer: User | null
	maxSecondsPerMove: number
	spectatingEnabled: boolean
}

declare interface ChessPiece {
	type: PieceType
	color: string
	symbol: string
	x: number
	y: number
}

declare interface User {
	username: string
	status: UserStatus
	gameId: string | null
	gamesWon: number
	gamesLost: number
	gamesDrawn: number
}

declare interface Position {
	x: number
	y: number
}
