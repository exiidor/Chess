<script setup lang="ts">
	const lastReceivedPacket = ref("")
	const connected = ref(false)
	const username = ref("")
	const password = ref("")
	const loggedIn = ref(false)
	const users = ref<User[]>([])
	const displayBoard = ref(false)
	const pieces = ref<ChessPiece[]>([])
	const wsClient = useWebSocket(useRuntimeConfig().public.chessServerAddress, {
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
		password.value = ""
	}

	function newGame(requestedOpponent?: User) {
		wsClient.send(JSON.stringify({
			type: "CreateGameC2S",
			data: {
				cpuOpponent: true,
				requestedOpponent: requestedOpponent ? requestedOpponent.username : prompt("Enter the username of the opponent:"),
				ownColor: "white",
				maxSecondsPerMove: 30,
			}
		}))
	}

	function handlePacket(packetString: string) {
		let packet = lastReceivedPacket.value = JSON.parse(packetString)

		switch (packet.type) {
			case "LoginResultS2C":
				if (packet.data.error) {
					alert("Login failed: " + packet.data.error)
				} else {
					loggedIn.value = true
					displayBoard.value = false
				}

				break
			case "UserListS2C":
				users.value = packet.data
				break
			case "KickS2C":
				alert("You have been kicked from the server by " + packet.data.initiator + " for reason: " + packet.data.reason)
				break
			case "CreateGameResultS2C":
				if (packet.data.error) {
					alert("Failed to create game: " + packet.data.error)
				} else {
					alert("Game created successfully with ID: " + packet.data.gameId)
				}
				break
			case "InviteS2C":
				const accept = confirm("You have been invited to a game by " + packet.data.initiator + ". Do you want to accept?");
				wsClient.send(JSON.stringify({
					type: "InviteResponseC2S",
					data: {
						accept: accept,
						gameId: packet.data.gameInfo.id,
					}
				}))
				break
			case "UserJoinedS2C":
				alert(packet.data.username + " has joined the game.")
				break
			case "JoinGameS2C":
				alert("You have joined the game with ID: " + packet.data.gameId)
				break
			case "BoardS2C":
				pieces.value = packet.data.board.pieces
				displayBoard.value = true
				break;
			default:
				alert("Unknown packet type: " + packet.type)
		}
	}
</script>


<template>
	<div class="buttons">
		<form v-if="!connected" @submit.prevent="connect()">
			<button type="submit">Connect</button>
		</form>

		<form v-if="connected" @submit.prevent="disconnect()">
			<button type="submit">Disconnect</button>
		</form>

		<form v-if="loggedIn" @submit.prevent="newGame()">
			<button type="submit">Start new Game</button>
		</form>
	</div>

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

	<hr>

	<UserList v-if="loggedIn" :users="users" @user-clicked="newGame" />

	<hr v-if="loggedIn">

	<div v-if="displayBoard" class="board-container">
		<ChessBoard :pieces="pieces" />
	</div>

	<br>

	<div v-if="connected && (lastReceivedPacket && !isBlank(lastReceivedPacket))">
		Last Received Packet:
		<pre>{{ lastReceivedPacket }}</pre>
	</div>
</template>


<style scoped>
	.board-container {
		position: relative;
		display: inline-block;
	}

	.buttons {
		display: block;
		box-sizing: border-box;
	}

	.buttons > * {
		display: block;
		box-sizing: border-box;
		margin: 0 10px;
		margin-left: 0px;
		cursor: pointer;
	}
</style>
