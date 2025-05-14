<script setup lang="ts">
	const wsClient = useWebSocket("ws://localhost:3010")
	const board = ref("")

	watch(wsClient.data, (newData) => {
		if (newData.startsWith("new-game")) {
			board.value = newData.substring(8)
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

	<pre>{{ board }}</pre>
</template>
