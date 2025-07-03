declare interface ChessPiece {
	type: PieceType
	color: string
	symbol: string
	x: number
	y: number
}

declare interface User {
	username: string
	status: string
}

declare interface Position {
	x: number
	y: number
}

declare interface Move {
	type : MoveType
	sourcePos: Position
	targetPos: Position
}

declare interface NormalMove extends Move {
}

declare interface CaptureMove extends Move {
	captured: ChessPiece
}

declare interface PromotionMove extends Move {
	captured: ChessPiece | null
	replacement: ChessPiece
}

declare interface EnPassantMove extends CaptureMove {
}

declare interface CastlingMove extends Move {
	other: Position
	otherMove: NormalMove
}
