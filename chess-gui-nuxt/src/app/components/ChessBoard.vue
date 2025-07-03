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
		username: {
			type: String,
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
	const squareByPiece = ref<Map<ChessPiece, InstanceType<typeof BoardSquare>>>(new Map())
	const selectedPiece = ref<ChessPiece | null>(null)
	const targetSquares = ref<(InstanceType<typeof BoardSquare> | null)[]>([])
	const movesToTargetedSquares = ref<Map<InstanceType<typeof BoardSquare>, Move[]>>(new Map())

	function onSquareClicked(clickedSquare: InstanceType<typeof BoardSquare>, clickedPiece: ChessPiece | null) {
		const selectedSquare = selectedPiece.value ? squareOf(selectedPiece.value) : null;

		if (selectedSquare) {
			selectedSquare.selectionState = SquareState.Default
		}

		if (targetSquares.value.includes(clickedSquare) && selectedSquare != clickedSquare) {
			if (props.isOurTurn) {
				console.log("movesToTargetedSquares:", movesToTargetedSquares.value)
				const moves = movesToTargetedSquares.value.get(clickedSquare)!
				let move: Move

				if (moves.length == 1) {
					move = moves[0]!
				} else {
					move = moves.find(move => (move as PromotionMove).replacement.type === PieceType.Queen)!
				}

				props.wsClientSendFunc(JSON.stringify({
					type: PacketType.MoveC2S,
					data: move
				}))
			}

			clearTargetedPieces()
			return
		}

		clearTargetedPieces()

		if (clickedPiece == null || clickedPiece == selectedPiece.value) {
			selectedPiece.value = null
			return
		}

		clickedSquare.selectionState = SquareState.Selected
		selectedPiece.value = clickedPiece
		fetchMoves(clickedPiece)
	}

	function squareOf(piece: ChessPiece): InstanceType<typeof BoardSquare> {
		return squareByPiece.value.get(piece) as InstanceType<typeof BoardSquare>
	}

	watch(() => props.movesForSelectedPiece, (_) => showPossibleMoves())

	function showPossibleMoves() {
		for (const move of props.movesForSelectedPiece) {
			const targetPos = move.targetPos
			const targetSquare = squares.value[targetPos.y]![targetPos.x]!

			if (!movesToTargetedSquares.value.has(targetSquare)) {
				movesToTargetedSquares.value.set(targetSquare, [])
			}

			movesToTargetedSquares.value.get(targetSquare)!.push(move);

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
		movesToTargetedSquares.value.clear();
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
			@click="onSquareClicked(squares[Math.floor(index / 8)]![index % 8]!, piece)"
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
