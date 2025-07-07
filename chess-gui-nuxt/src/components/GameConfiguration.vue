<script lang="ts" setup>
	const props = defineProps({
		user: {
			type: Object as PropType<User | null | undefined>,
			required: true
		},
		users: {
			type: Array as PropType<User[]>,
			required: true
		},
		inGameFunc: {
			// Can't pass a ref as it won't be updated once this component is no longer rendered.
			type: Function as PropType<() => boolean>,
			required: true
		},
		wsSendFunc: {
			type: Function as PropType<(data: string) => void>,
			required: true
		}
	})

	const colorOptions = ref<PieceColor[]>([
		PieceColor.White,
		PieceColor.Black
	])
	const color = ref<PieceColor>(PieceColor.White)
	const cpuEnemy = ref(true)
	const requestedOpponent = ref<string | null>(null)
	const maxSecondsPerMove = ref(30)
	const spectatingEnabled = ref(true)
	const waiting = ref(false)
	const availableOpponents = computed(() => {
		return props.users
				.filter(user => user.username !== props.user!.username)
				.filter(user => user.status === UserStatus.Online)
	});

	function startGame() {
		waiting.value = true

		props.wsSendFunc(JSON.stringify({
			type: PacketType.CreateGameC2S,
			data: {
				cpuOpponent: cpuEnemy.value,
				requestedOpponent: requestedOpponent.value,
				ownColor: color.value,
				maxSecondsPerMove: maxSecondsPerMove.value,
			}
		}))

		if (!cpuEnemy.value) {
			setTimeout(() => {
				waiting.value = false
				if (!props.inGameFunc()) {
					useToast().add({
						title: 'Invitation Timeout',
						description: requestedOpponent.value
							+ " hasn't accepted the invitation. Try again or choose another opponent.",
						color: 'warning'
					})
				}
			}, 12000)
		}
	}
</script>

<template>
	<div class="flex flex-col gap-6 p-4">
		<h1 class="text-2xl font-bold">New Game</h1>
		<div>
			<div class="group-header">
				Own color:
			</div>
			<URadioGroup v-model="color" :items="colorOptions" :disabled="waiting" />
		</div>

		<div>
			<UCheckbox v-model="cpuEnemy" @change="() => requestedOpponent = null" label="Play against Computer" :disabled="waiting" />
		</div>

		<div v-if="!cpuEnemy">
			<div class="group-header">
				Opponent:
			</div>
			<URadioGroup
				v-if ="availableOpponents.length > 0"
				v-model="requestedOpponent"
				:items="availableOpponents
					.map(user => ({
						label: user.username,
						value: user.username,
					}))"
				:disabled="waiting"
			/>
			<span v-else>No users available!</span>
		</div>

		<div>
			<div class="group-header">
				Maximum seconds per move: {{ maxSecondsPerMove }}s
			</div>
			<USlider
				name="Max seconds per move"
				v-model="maxSecondsPerMove"
				:default-value="30"
				:min="5"
				:max="120"
				:step="5"
				:disabled="waiting"
			/>
		</div>

		<div>
			<UCheckbox v-model="spectatingEnabled" label="Allow other users to spectate your game" :disabled="waiting" />
		</div>

		<div class="self-start flex flex-row gap-2 mt-4">
			<UButton
				label="Start Game"
				:loading="waiting"
				:disabled="waiting || !(cpuEnemy || requestedOpponent)"
				@click="startGame()"
			/>
			<UButton
				label="Cancel"
				color="neutral"
				variant="outline"
				:disabled="waiting"
				@click="$emit('close')"
			/>
		</div>
	</div>
</template>

<style scoped>
	.group-header {
		margin-bottom: 0.5rem;
	}
</style>
