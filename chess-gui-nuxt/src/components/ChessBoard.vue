<script setup lang="ts">
	import BoardSquare from './BoardSquare.vue'

	const props = defineProps({
		wsSendFunc: {
			type: Function as PropType<(data: string) => void>,
			required: true
		},
		playerColor: {
			type: String as PropType<PieceColor | null>,
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
		movesForSelectedPiece: {
			type: Array as PropType<Move[]>,
			required: true,
			default: () => []
		}
	})

	const emit = defineEmits<{
		(event: 'piece-selected', piece: ChessPiece): void
		(event: 'piece-moved'): void
	}>()

	const squares = ref<InstanceType<typeof BoardSquare>[][]>(Array.from({ length: 8 }, () => Array(8).fill(null)))
	const squareByPiece = ref<Map<ChessPiece, InstanceType<typeof BoardSquare>>>(new Map())
	const selectedPiece = ref<ChessPiece | null>(null)
	const targetSquares = ref<(InstanceType<typeof BoardSquare> | null)[]>([])
	const movesToTargetedSquares = ref<Map<InstanceType<typeof BoardSquare>, Move[]>>(new Map())

	watch(() => props.pieces, (_) => {
		clearTargetedSquares()
		clearSelectedSquare()
	})
	watch(() => props.movesForSelectedPiece, (_) => showPossibleMoves())

	function onSquareClicked(clickedSquare: InstanceType<typeof BoardSquare>, clickedPiece: ChessPiece | null) {
		clearSelectedSquare()

		if (targetSquares.value.includes(clickedSquare) && getSelectedSquare() != clickedSquare) {
			if (props.isOurTurn && selectedPiece.value?.color === props.playerColor) {
				const moves = movesToTargetedSquares.value.get(clickedSquare)!
				let move: Move

				if (moves.length == 1) {
					move = moves[0]!
				} else {
					move = moves.find(move => (move as PromotionMove).replacement.type === PieceType.Queen)!
				}

				props.wsSendFunc(JSON.stringify({
					type: PacketType.MoveC2S,
					data: {
						committedMoveIndex: props.movesForSelectedPiece.indexOf(move),
					}
				}))

				emit('piece-moved')
			}

			clearTargetedSquares()
			return
		}

		clearTargetedSquares()

		if (clickedPiece == null || clickedPiece == selectedPiece.value) {
			selectedPiece.value = null
			return
		}

		clickedSquare.selectionState = SquareState.Selected
		selectedPiece.value = clickedPiece
		fetchMoves(clickedPiece)
	}

	function getSelectedSquare(): InstanceType<typeof BoardSquare> | null {
		return selectedPiece.value
				? squareByPiece.value.get(selectedPiece.value) ?? null
				: null
	}

	function clearSelectedSquare() {
		const selectedSquare = getSelectedSquare()

		if (selectedSquare) {
			selectedSquare.selectionState = SquareState.Default
		}
	}

	function clearTargetedSquares() {
		for (const square of targetSquares.value) {
			square!.selectionState = SquareState.Default
		}

		targetSquares.value = []
		movesToTargetedSquares.value.clear();
	}

	function fetchMoves(piece: ChessPiece) {
		emit('piece-selected', piece)
	}

	function showPossibleMoves() {
		for (const move of props.movesForSelectedPiece) {
			const targetPos = move.targetPos
			const targetSquare = squares.value[targetPos.y]![targetPos.x]!

			if (!movesToTargetedSquares.value.has(targetSquare)) {
				movesToTargetedSquares.value.set(targetSquare, [])
			}

			movesToTargetedSquares.value.get(targetSquare)!.push(move);

			if (move.type === MoveType.Capture
					|| move.type === MoveType.EnPassant
					|| move.type === MoveType.Promotion && (move as PromotionMove).captured) {
				targetSquare.selectionState = SquareState.HighlightedCapture;
			} else if (move.type === MoveType.Promotion) {
				targetSquare.selectionState = SquareState.HighlightedPromotion;
			} else {
				targetSquare.selectionState = SquareState.Highlighted;
			}

			targetSquares.value.push(targetSquare);
		}
	}
</script>


<template>
	<div class="chessboard" :class="isOurTurn ? 'chessboard--our-turn' : 'chessboard--not-our-turn'">
		<BoardSquare v-for="(piece, index) in pieces"
			class="boardsquare"
			:key="index"
			:piece="piece"
			:player-color="props.playerColor"
			:color="index % 2 === 0
				? (Math.floor(index / 8) % 2 === 0 ? PieceColor.White : PieceColor.Black)
				: (Math.floor(index / 8) % 2 === 0 ? PieceColor.Black : PieceColor.White)"
			:ref="(square) => {
				squares[Math.floor(index / 8)]![index % 8] = square as InstanceType<typeof BoardSquare>
				if (piece !== null) {
					squareByPiece.set(piece, square as InstanceType<typeof BoardSquare>)
				}
			}"
			@click="onSquareClicked(squares[Math.floor(index / 8)]![index % 8]!, piece)"
		/>
	</div>
</template>


<style scoped>
	.chessboard {
		display: grid;
  		grid-template-columns: repeat(8, 1fr);
  		aspect-ratio: 1 / 1;
  		/* width: 70vmin; */
  		box-sizing: border-box;
	}

	.chessboard--our-turn {
		border: 0.4em solid #228C22;
		box-shadow: 0 0 10px #228C22, 0 0 20px #228C22;
	}

	.chessboard--not-our-turn {
		border: 0.4em solid #8e1600;
		box-shadow: 0 0 10px #8e1600, 0 0 20px #8e1600;
	}

	.boardsquare {
		width: 100%;
  		height: 100%;
  		aspect-ratio: 1 / 1;
 		box-sizing: border-box;
		padding: 0;
	}

	.rank-label,
	.file-label {
		position: absolute;
		color: #333;
		font-size: 14px;
		font-weight: bold;
		padding: 0;
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
