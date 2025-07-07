<script setup lang="ts">
	const props = defineProps({
		piece: {
			type: Object as PropType<ChessPiece | null>,
			required: false,
			default: null
		},
		color: {
			type: String as PropType<PieceColor>,
			required: true
		},
		playerColor: {
			type: String as PropType<PieceColor | null>,
			required: true
		}
	})
	const emit = defineEmits<{
		(event: 'square-clicked', piece: ChessPiece | null): void
	}>()
	const selectionState = ref(SquareState.Default)

	function onClick() {
		if (props.playerColor !== null) {
			emit('square-clicked', props.piece);
		}
	}

	defineExpose({
		selectionState
	})
</script>


<template>
	<div :class="['square', color, SquareState[selectionState]?.toLowerCase()]"
		 :style="playerColor === PieceColor.Black ? 'transform: rotate(0deg)' : 'transform: rotate(180deg)'"
		 @click="onClick()">
		{{ piece?.symbol }}
	</div>
</template>


<style scoped>
	.square {
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 7vmin; /* Not over 7 here! Otherwise the squares don't share the same aspect ratio */
		user-select: none;
		font-family: 'ChessFont', sans-serif;
	}

	.dark .square {
		color: #d9d9d9;
	}

	.dark .white {
		background-color: #655a5a;
	}

	.light .white {
		background-color: #f0d9b5;
	}

	.dark .black {
		background-color: #1f1c1c;
	}

	.light .black {
		background-color: #b58863;
	}

	.dark .selected {
		background-color: #6da4edaa;
	}

	.light .selected {
		background-color: #6da4edaa;
	}

	.dark .highlighted {
		background-color: #6da4ed55;
	}

	.light .highlighted {
		background-color: #6da4ed55;
	}

	.dark .highlightedcapture {
		background-color: #ff6d6daa;
	}

	.light .highlightedcapture {
		background-color: #ff6d6daa;
	}

	.dark .highlightedpromotion {
		background-color: #ffd66daa;
	}

	.light .highlightedpromotion {
		background-color: #ffd66daa;
	}
</style>
