<script setup lang="ts">
	const props = defineProps({
		piece: {
			type: Object as PropType<ChessPiece | null>,
			required: false,
			default: null
		},
		color: {
			type: String as PropType<Color>,
			required: true
		}
	})
	const emit = defineEmits<{
		(event: 'square-clicked', piece: ChessPiece | null): void
	}>()
	const selectionState = ref(SquareState.Default)

	function onClick() {
		emit('square-clicked', props.piece);
	}

	defineExpose({
		selectionState
	})
</script>


<template>
	<div :class="['square', color, SquareState[selectionState]?.toLowerCase()]" @click="onClick()">
		{{ piece?.symbol }}
	</div>
</template>


<style scoped>
	.square {
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 60px;
		user-select: none;
	}

	.white {
		background-color: #f0d9b5;
	}

	.black {
		background-color: #b58863;
	}

	.selected {
		background-color: #6da4edaa;
	}

	.highlighted {
		background-color: #6da4ed55;
	}

	.highlightedcapture {
		background-color: #ff6d6daa;
	}

	.highlightedpromotion {
		background-color: #ffd66daa;
	}
</style>
