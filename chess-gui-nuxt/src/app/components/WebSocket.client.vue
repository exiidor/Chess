<script setup>
	const wsClient = useWebSocket("ws://localhost:3010")
	const displayBoard = ref(false)
	const board = ref("")

	watch(wsClient.data, (newData) => {
		if (newData.startsWith("new-game")) {
			const result = JSON.parse(newData.substring(8))
			board.value = result.board
			displayBoard.value = true
		}
	})

	function newGame() {
		wsClient.send("new-game")
	}
</script>


<template>
	<form @submit.prevent="newGame">
		<button type="submit">Start new Game</button>
	</form>

	<div v-if="displayBoard">
		<div class="board-container">
			<div class="chessboard">
				<div v-for="(piece, index) in board.flat()"
						:key="index"
						:class="[
							'square',
							index % 2 === 0
									? (Math.floor(index / 8) % 2 === 0 ? 'white' : 'black')
									: (Math.floor(index / 8) % 2 === 0 ? 'black' : 'white')
						]">
					{{ piece }}
				</div>
			</div>
		</div>
	</div>

	JSON:
	<pre>{{ board }}</pre>
</template>


<style scoped>
	.board-container {
		position: relative;
		display: inline-block;
	}

	.chessboard {
		display: grid;
		grid-template-columns: repeat(8, 80px);
		grid-template-rows: repeat(8, 80px);
		border: 2px solid #333;
	}

	.square {
		width: 80px;
		height: 80px;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 60px;
	}

	.white {
		background-color: #f0d9b5;
	}

	.black {
		background-color: #b58863;
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
