<script setup lang="ts">
	const lastReceivedPacket = ref("")
	const connected = ref(false)
	const username = ref("")
	const loggedIn = ref(false)
	const users = ref<User[]>([])
	const user = computed<User | undefined>(() => users.value.find(u => u.username == username.value));
	const inGame = ref(false)
	const showGameConfiguration = ref(false)
	const invite = ref<InviteS2CData | null>(null)
	const acceptInviteDialog = useConfirmDialog(ref(false));
	const color = ref<PieceColor>(PieceColor.White)
	const pieces = ref<ChessPiece[]>([])
	const moves = ref<Move[]>([])
	const toast = useToast()
	const wsClient = useWebSocket(useRuntimeConfig().public.chessServerAddress, {
		immediate: false,
		onConnected: () => {
			connected.value = true
		},
		onDisconnected: () => {
			connected.value = false
			loggedIn.value = false
			showGameConfiguration.value = false
			inGame.value = false
		},
		onError: (error) => {
			toast.add({ title: "WebSocket error", description: String(error), color: "error" })
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

	function onLoginAttempt(inputUsername: string) {
		username.value = inputUsername
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

	async function handlePacket(packetString: string) {
		const packet = lastReceivedPacket.value = JSON.parse(packetString)
		const packetType = packet.type as PacketType

		switch (packetType) {
			case PacketType.LoginResultS2C:
				if (packet.data.error) {
					toast.add({ title: "Login failed", description: packet.data.error, color: "error" })
				} else {
					loggedIn.value = true
				}

				break
			case PacketType.UserListS2C:
				users.value = packet.data
				break
			case PacketType.KickS2C:
				toast.add({
					title: "Kicked from server",
					description: "You have been kicked by " + packet.data.initiator + " for reason: " + packet.data.reason,
					color: "error"
				})
				break
			case PacketType.CreateGameResultS2C:
				if (packet.data.error) {
					toast.add({ title: "Failed to create game", description: packet.data.error, color: "error" })
				}
				break
			case PacketType.InviteS2C:
				invite.value = (packet as InviteS2C).data
				wsClient.send(JSON.stringify({
					type: PacketType.InviteResponseC2S,
					data: {
						gameId: invite.value.gameInfo.id,
						accept: !(await acceptInviteDialog.reveal()).isCanceled,
					}
				}))
				break
			case PacketType.UserJoinedS2C:
				toast.add({
					title: "User Joined",
					description: packet.data.username + " has joined the game as a " + (packet.data.status === UserStatus.Playing ? "player" : "spectator."),
					color: "info"
				})
				break
			case PacketType.JoinGameS2C:
				break
			case PacketType.BoardS2C:
				pieces.value = packet.data.board.pieces
				inGame.value = true
				showGameConfiguration.value = false
				break
			case PacketType.UserLeftS2C:
				toast.add({
					title: "User Left",
					description: packet.data.user.username + " has left the game.",
					color: "info"
				})
				break
			case PacketType.MovesS2C:
				moves.value = packet.data
				break;
			case PacketType.GameEndedS2C:
				toast.add({
					title: "Game Ended",
					description: "The game has ended with a " + packet.data.mateKind.toLowerCase() + " by " + packet.data.winner.username + ".",
					color: "info"
				})
				inGame.value = false
				break;
			default:
				toast.add({ title: "Unknown packet type", description: String(packet.type), color: "warning" })
		}
	}
</script>


<template>
	<UApp id="uapp">
		<div v-if="!connected" class="flex flex-row min-h-screen justify-center items-center">
			<UButton @click="connect()">
				Connect
			</UButton>
		</div>

		<div v-if="connected && !loggedIn" class="flex flex-row min-h-screen justify-center items-center">
			<Login :ws-send-func="wsClient.send" @attempted-login="onLoginAttempt" />
		</div>

		<main v-if="loggedIn">
			<div class="left">
				<UButton v-if="connected" @click="disconnect()">Disconnect</UButton>
				<UButton v-if="loggedIn && !showGameConfiguration" @click="newGame()">Start new Game</UButton>
				<UButton v-if="inGame" @click="leaveGame()">Leave Game</UButton>
				<UModal v-if="connected && (lastReceivedPacket && !isBlank(lastReceivedPacket))" title="Last Received Packet">
					<UButton
						label="Show Last Received Packet"
						color="neutral"
						variant="outline"
					/>

					<template #body>
						<pre>{{ lastReceivedPacket }}</pre>
					</template>
				</UModal>
			</div>
			<div class="middle">
				<div v-if="showGameConfiguration" class="flex justify-center items-center">
					<GameConfiguration
						class="min-w-[300px] max-w-[600px] pt-10"
						:user
						:users
						:in-game-func="() => inGame"
						:ws-send-func="wsClient.send"
						@close="showGameConfiguration = false"
					/>
				</div>
				<div v-if="inGame" class="board-container">
					<ChessBoard
						id="board"
						:ws-send-func="wsClient.send"
						:our-color="color"
						:pieces="pieces"
						:is-our-turn="true"
						:moves-for-selected-piece="moves"
						@piece-selected="requestMovesForPiece"
					/>
				</div>
			</div>
			<div class="right">
				<UserList :users="users" @user-clicked="newGame" id="userlist" />
			</div>
		</main>

		<UContainer class="mt-10" v-if="acceptInviteDialog.isRevealed.value">
			<InviteModal :controller="acceptInviteDialog" :invite="invite!" :user="user!" />
		</UContainer>
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
		overflow-y: auto;
		min-height: 200px;
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
		min-height: 200px;
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
			width: 100%;
			/* max-height: 50vh; */
		}

		.right {
			width: 100%;
			max-height: 30vh;
			overflow-y: auto;
		}
	}

</style>
