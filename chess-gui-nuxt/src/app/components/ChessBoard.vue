<script setup lang="ts">
import BoardSquare from './BoardSquare.vue'

	defineProps({
		pieces: {
			type: Array as PropType<ChessPiece[]>,
			required: true
		}
	})

	const squareByPiece = ref<Map<ChessPiece, Element | ComponentPublicInstance | null>>(new Map())
	const selectedPiece = ref<ChessPiece | null>(null)
	const highlightedPieces = ref<ChessPiece[]>([])

	function onSquareClicked(piece: ChessPiece | null) {
		if (selectedPiece.value) {
			if (piece === selectedPiece.value) {
				selectPiece(null)
				highlightedPieces.value = []
			} else {
				selectPiece(piece)
				highlightedPieces.value = []
			}
		} else {
			selectPiece(piece)
		}
	}

	function selectPiece(piece: ChessPiece | null) {
		let square = squareOf(selectedPiece.value);

		if (square) {
			square.selectionState = SquareSelectionState.None
		}

		square = squareOf(piece)

		if (square) {
			square.selectionState = SquareSelectionState.Selected
		}

		selectedPiece.value = piece
		highlightedPieces.value = [] // Logic to determine which pieces to highlight
	}

	function squareOf(piece: ChessPiece | null): InstanceType<typeof BoardSquare> | null {
		let square: InstanceType<typeof BoardSquare> | null = null;

		if (piece !== null) {
			square = squareByPiece.value.get(piece) as InstanceType<typeof BoardSquare> | null;
		}

		return square;
	}
</script>


<template>
	<div class="chessboard">
		<BoardSquare v-for="(piece, index) in pieces"
			:key="index"
			:piece="piece"
			:white="index % 2 === 0
				? (Math.floor(index / 8) % 2 === 0 ? true : false)
				: (Math.floor(index / 8) % 2 === 0 ? false : true)"
			:ref="(square) => squareByPiece.set(piece, square)"
			@click="onSquareClicked(piece)"
		/>
	</div>
</template>


<style scoped>
	.chessboard {
		display: grid;
		grid-template-columns: repeat(8, 80px);
		grid-template-rows: repeat(8, 80px);
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
