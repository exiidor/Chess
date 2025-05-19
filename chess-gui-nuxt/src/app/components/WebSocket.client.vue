<script setup lang="ts">
	const lastReceivedPacket = ref(null)
	const connected = ref(false)
	const username = ref("")
	const password = ref("")
	const loggedIn = ref(false)
	const userList = ref<User[]>([])
	const displayBoard = ref(false)
	const board = ref(Array.from({ length: 8 }, () => Array(8).fill(null)))
	const wsClient = useWebSocket("ws://localhost:3010", {
		immediate: false,
		onConnected: () => {
			connected.value = true
		},
		onDisconnected: () => {
			connected.value = false
			loggedIn.value = false
			displayBoard.value = false
		},
		onError: (error) => {
			alert("WebSocket error:" + error)
		},
		onMessage: (_, event) => {
			handlePacket(event.data)
		}
	})

	interface User {
		username: string
		status: string
	}

	function connect() {
		wsClient.open()
	}

	function disconnect() {
		wsClient.close()
	}

	async function login() {
		wsClient.send(JSON.stringify({
			type: "LoginC2S",
			data: {
				username: username.value,
				password: await sha256(password.value),
				clientVersion: "1.0.0",
			}
		}))
	}

	function newGame() {
		// TODO
	}

	function handlePacket(packetString: string) {
		let packet = lastReceivedPacket.value = JSON.parse(packetString)

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
				alert("Unknown packet type: " + packet.type)
		}
	}

	async function sha256(password: string): Promise<string> {
		const encoder = new TextEncoder()
		const data = encoder.encode(password)
		const hashBuffer = await crypto.subtle.digest("SHA-256", data)
		const hashArray = Array.from(new Uint8Array(hashBuffer))
		const hashHex = hashArray.map(b => b.toString(16).padStart(2, "0")).join("")
		return hashHex
	}
</script>


<template>
	<form v-if="!connected" @submit.prevent="connect()">
		<button type="submit">Connect</button>
	</form>

	<form v-if="connected" @submit.prevent="disconnect()">
		<button type="submit">Disconnect</button>
	</form>

	<form v-if="connected && !loggedIn" @submit.prevent="login()">
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

	<form v-if="loggedIn" @submit.prevent="newGame()">
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

	<br>

	<div v-if="connected && lastReceivedPacket">
		Last Received Packet:
		<pre>{{ lastReceivedPacket }}</pre>
	</div>
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
