<script setup lang="ts">
	import BoardSquare from './BoardSquare.vue'

	const props = defineProps({
		wsClientSendFunc: {
			type: Function as PropType<(data: string) => void>,
			required: true
		},
		pieces: {
			type: Array as PropType<(ChessPiece | null)[]>,
			required: true
		},
		isOurTurn: {
			type: Boolean,
			required: true
		},
		ourColor: {
			type: String as PropType<Color>,
			required: true
		},
		movesForSelectedPiece: {
			type: Array as PropType<Move[]>,
			required: true,
			default: () => []
		}
	})

	const emit = defineEmits<{
		(event: 'piece-selected', piece: ChessPiece): void
	}>()

	const squares = ref<InstanceType<typeof BoardSquare>[][]>(Array.from({ length: 8 }, () => Array(8).fill(null)))
	const squareByPiece = ref<Map<ChessPiece, InstanceType<typeof BoardSquare> | null>>(new Map())
	const selectedPiece = ref<ChessPiece | null>(null)
	const targetSquares = ref<(InstanceType<typeof BoardSquare> | null)[]>([])
	const movesToTargetedPieces = ref<Map<ChessPiece, Move[]>>(new Map())

	function onSquareClicked(piece: ChessPiece | null) {
		const selectedSquare = squareOf(selectedPiece.value)!
		const clickedSquare = squareOf(piece)

		if (selectedSquare) {
			selectedSquare.selectionState = SquareState.Default
		}

		if (targetSquares.value.includes(selectedSquare) && selectedPiece.value != piece) {
			if (props.isOurTurn) {
				const moves = movesToTargetedPieces.value.get(piece!)!
				let move: Move

				if (moves.length == 1) {
					move = moves[0]!
				} else {
					move = moves.find(move => (move as PromotionMove).replacement.type === PieceType.Queen)!
				}

				props.wsClientSendFunc(JSON.stringify({
					type: PacketType.MoveC2S,
					data: {
						move: {
							from: {
								x: selectedPiece.value!.x,
								y: selectedPiece.value!.y
							},
							to: {
								x: move.targetPos.x,
								y: move.targetPos.y
							},
						}
					}
				}))
			}

			clearTargetedPieces()
			return
		}

		clearTargetedPieces()

		if (piece == null || piece == selectedPiece.value) {
			selectedPiece.value = null
			return
		}

		squareOf(piece)!.selectionState = SquareState.Selected
		selectedPiece.value = piece
		fetchMoves(piece)
	}

	function squareOf(piece: ChessPiece | null): InstanceType<typeof BoardSquare> | null {
		let square: InstanceType<typeof BoardSquare> | null = null

		if (piece !== null) {
			square = squareByPiece.value.get(piece) as InstanceType<typeof BoardSquare> | null
		}

		return square
	}

	watch(() => props.movesForSelectedPiece, (_) => {
		showPossibleMoves(selectedPiece.value!)
	})

	function showPossibleMoves(piece: ChessPiece) {
		for (const move of props.movesForSelectedPiece) {
			const targetPos = move.targetPos
			const targetSquare = squares.value[targetPos.y]![targetPos.x]!

			if (!movesToTargetedPieces.value.has(piece)) {
				movesToTargetedPieces.value.set(piece, [])
			}

			movesToTargetedPieces.value.get(piece)!.push(move);

			if (move.type === MoveType.Capture || move.type === MoveType.EnPassant && (move as EnPassantMove).captured !== null) {
				targetSquare.selectionState = SquareState.HighlightedCapture;
			} else if (move.type === MoveType.Promotion) {
				targetSquare.selectionState = SquareState.HighlightedPromotion;
			} else {
				targetSquare.selectionState = SquareState.Highlighted;
			}

			targetSquares.value.push(targetSquare);
		}
	}

	function fetchMoves(piece: ChessPiece) {
		emit('piece-selected', piece)
	}

	function clearTargetedPieces() {
		for (const square of targetSquares.value) {
			square!.selectionState = SquareState.Default
		}

		targetSquares.value = []
		movesToTargetedPieces.value.clear();
	}
</script>


<template>
	<div class="chessboard">
		<BoardSquare v-for="(piece, index) in pieces"
			:key="index"
			:piece="piece"
			:color="index % 2 === 0
				? (Math.floor(index / 8) % 2 === 0 ? Color.White : Color.Black)
				: (Math.floor(index / 8) % 2 === 0 ? Color.Black : Color.White)"
			:ref="(square) => {
				squares[Math.floor(index / 8)]![index % 8] = square as InstanceType<typeof BoardSquare>
				if (piece !== null) {
					squareByPiece.set(piece, square as InstanceType<typeof BoardSquare>)
				}
			}"
			@click="onSquareClicked(piece)"
		/>
	</div>
</template>


<style scoped>
	.chessboard {
		display: grid;
		grid-template-columns: repeat(8, 11.5vw);
		grid-template-rows: repeat(8, 11.5vw);
		border: 2px solid #333;
	}

	.rank-label, .file-label {
		position: absolute;
		color: #333;
		font-size: 14px;
		font-weight: bold;
		pointer-events: none;
	}

	.rank-label {
		left: -20px;
		width: 20px;
		text-align: right;
		height: 80px;
		line-height: 80px;
	}

	.file-label {
		top: 480px;
		width: 80px;
		text-align: center;
		height: 20px;
	}
</style>
