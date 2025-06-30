<script setup lang="ts">
	const props = defineProps({
		piece: {
			type: Object as PropType<ChessPiece | null>,
			required: true
		},
		white: {
			type: Boolean,
			required: true
		}
	})
	const emit = defineEmits<{
		(event: 'square-clicked', piece: ChessPiece | null): void
	}>()
	const selectionState = ref(SquareSelectionState.None)

	function onClick() {
		emit('square-clicked', props.piece);
	}

	defineExpose({
		selectionState
	})
</script>


<template>
	<div :class="['square', white ? 'white' : 'black', SquareSelectionState[selectionState]?.toLowerCase()]" @click="onClick()">
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
</style>
