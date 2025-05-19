<script setup>
	const wsClient = useWebSocket("ws://localhost:3010")
	const lastReceivedPacket = ref(null)
	const loggedIn = ref(false)
	const userList = ref([])
	const displayBoard = ref(false)
	const board = ref("")

	watch(wsClient.data, (newData) => {
		let packet = lastReceivedPacket.value = JSON.parse(newData)

		switch (packet.type) {
			case "LoginResultS2C":
				if (packet.data.success) {
					loggedIn.value = true
					displayBoard.value = false
				} else {
					alert("Login failed: " + packet.data.error)
				}

				break
			case "UserListS2C":
				userList.value = packet.data
				break
			default:
				alert("Unknown packet type: ", packet.type)
		}
	})

	function login(username, password) {
		wsClient.send(JSON.stringify({
			type: 'LoginC2S',
			data: {
				username,
				password,
				clientVersion: '1.0.0',
			}
		}))
	}

	function newGame() {
		// TODO
	}
</script>


<template>
	<form v-if="!loggedIn" @submit.prevent="login(username, password)">
		<label>
			Username:
			<input v-model="username" required />
		</label>
		<label>
			Password:
			<input type="password" v-model="password" required />
		</label>
		<button type="submit">Login</button>
	</form>

	<ul v-if="loggedIn">
		<li v-for="user in userList" :key="user.username">
			{{ user.username }} ({{ user.status }})
		</li>
	</ul>

	<form v-if="loggedIn" @submit.prevent="newGame">
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
	<pre>{{ lastReceivedPacket }}</pre>
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
