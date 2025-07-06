<script setup lang="ts">
	const lastReceivedPacket = ref("")
	const connected = ref(false)
	const username = ref("")
	const password = ref("")
	const loggedIn = ref(false)
	const users = ref<User[]>([])
	const inGame = ref(false)
	const showGameConfiguration = ref(false)
	const color = ref<PieceColor>(PieceColor.White)
	const pieces = ref<ChessPiece[]>([])
	const moves = ref<Move[]>([])
	const wsClient = useWebSocket(useRuntimeConfig().public.chessServerAddress, {
		immediate: false,
		onConnected: () => {
			connected.value = true
		},
		onDisconnected: () => {
			connected.value = false
			loggedIn.value = false
			inGame.value = false
		},
		onError: (error) => {
			alert("WebSocket error: " + error)
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
			type: PacketType.LoginC2S,
			data: {
				username: username.value,
				password: await sha256(password.value),
				clientVersion: "1.0.0",
			}
		}))
		password.value = ""
	}

	function newGame(requestedOpponent?: User) {
		showGameConfiguration.value = true
	}

	function leaveGame() {
		wsClient.send(JSON.stringify({
			type: PacketType.LeaveGameC2S,
			data: {
				reason: null
			}
		}))
		inGame.value = false
	}

	function requestMovesForPiece(piece: ChessPiece) {
		wsClient.send(JSON.stringify({
			type: PacketType.RequestMovesC2S,
			data: {
				x: piece.x,
				y: piece.y
			}
		}))
	}

	function handlePacket(packetString: string) {
		const packet = lastReceivedPacket.value = JSON.parse(packetString)
		const packetType = packet.type as PacketType

		switch (packetType) {
			case PacketType.LoginResultS2C:
				if (packet.data.error) {
					alert("Login failed: " + packet.data.error)
				} else {
					loggedIn.value = true
				}

				break
			case PacketType.UserListS2C:
				users.value = packet.data
				break
			case PacketType.KickS2C:
				alert("You have been kicked from the server by " + packet.data.initiator + " for reason: " + packet.data.reason)
				break
			case PacketType.CreateGameResultS2C:
				if (packet.data.error) {
					alert("Failed to create game: " + packet.data.error)
				}
				break
			case PacketType.InviteS2C:
				const accept = confirm("You have been invited to a game by " + packet.data.initiator + ". Do you want to accept?");
				wsClient.send(JSON.stringify({
					type: PacketType.InviteResponseC2S,
					data: {
						accept: accept,
						gameId: packet.data.gameInfo.id,
					}
				}))
				break
			case PacketType.UserJoinedS2C:
				alert(packet.data.username + " has joined the game.")
				break
			case PacketType.JoinGameS2C:
				break
			case PacketType.BoardS2C:
				pieces.value = packet.data.board.pieces
				inGame.value = true
				break
			case PacketType.UserLeftS2C:
				alert(packet.data.user.username + " has left the game.")
				break
			case PacketType.MovesS2C:
				moves.value = packet.data
				break;
			default:
				alert("Unknown packet type: " + packet.type)
		}
	}
</script>


<template>
	<UApp id="uapp">
		<form v-if="!connected" @submit.prevent="connect()">
			<UButton type="submit">Connect</UButton>
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

		<main v-if="loggedIn">
			<div class="left">
				<UButton v-if="connected" @click="disconnect()">Disconnect</UButton>
				<UButton v-if="loggedIn" @click="newGame()">Start new Game</UButton>
				<UButton v-if="inGame" @click="leaveGame()">Leave Game</UButton>
			</div>
			<div class="middle">
				<div v-if="showGameConfiguration">
					<GameConfiguration
						:color="color"
						:users="users"
						@close="showGameConfiguration = false"
					/>
				</div>
				<div v-if="inGame" class="board-container">
					<ChessBoard
						id="board"
						:ws-client-send-func="wsClient.send"
						:our-color="color"
						:pieces="pieces"
						:is-our-turn="true"
						:moves-for-selected-piece="moves"
						@piece-selected="requestMovesForPiece"
					/>
				</div>

				<!-- <div v-if="connected && (lastReceivedPacket && !isBlank(lastReceivedPacket))">
					Last Received Packet:
					<pre>{{ lastReceivedPacket }}</pre>
				</div> -->
			</div>
			<div class="right">
				<UserList :users="users" @user-clicked="newGame" id="userlist" />
			</div>
		</main>
	</UApp>
</template>


<style scoped>
	main {
		width: 100%;
		height: 100vh;
		flex-direction: row;
		gap: 10px;
		display: flex;
	}

	.left {
		display: flex;
		flex-direction: column;
		gap: 5px;
		padding-left: 5px;
		padding-top: 5px;
	}

	.middle {
		flex: 1;
	}

	.board-container {
		display: block;
		height: 100%;
		width: 100%;
		position: relative;
	}

	#board {
		max-width: 100vw;
		max-height: 85vh;
		height: 100%;
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
	}

	.right {
		width: 200px;
	}

	@media (max-width: 900px) {
		main {
			flex-direction: column;
		}

		.left {
			flex-direction: row;
			justify-content: flex-start;
			align-items: center;
			width: 100%;
			gap: 10px;
			margin-bottom: 10px;
		}

		.middle {
			width: 100vw;
			max-height: 50vh;
		}

		.right {
			display: none;
		}
	}

</style>
