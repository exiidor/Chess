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
