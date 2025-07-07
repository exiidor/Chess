<script setup lang="ts">
	defineProps({
		user: {
			type: Object as PropType<User>,
			required: true
		}
	})

	const emit = defineEmits<{
		(event: 'user-clicked', user: User): void
	}>()

	function onUserClicked(user: User) {
		emit('user-clicked', user);
	}

	function userStatusColor(status: UserStatus): "primary" | "info" | "neutral" | "secondary" | undefined {
		switch (status) {
			case UserStatus.Online:
				return "primary";
			case UserStatus.Playing:
				return "secondary";
			case UserStatus.Spectating:
				return "info";
			case UserStatus.Offline:
				return "neutral";
			default:
				return undefined;
		}
	}

	function getUserIconFromNameIfPossible(name: string): string {
		return "https://github.com/" + name + ".png"
	}

</script>


<template>
	<div class="user-list-entry">
		<UPopover>
			<div class="chip-and-username">
				<UChip inset position="bottom-left" :color="userStatusColor(user.status)">
					<UAvatar :src="getUserIconFromNameIfPossible(user.username)" :alt="user.username" />
				</UChip>
				{{ user.username }}
			</div>

			<template #content>
				<div class="p-4">
					<div class="flex items-center gap-2">
						<UChip inset position="bottom-left" size="3xl" :color="userStatusColor(user.status)">
							<UAvatar size="3xl" :src="getUserIconFromNameIfPossible(user.username)" :alt="user.username" />
						</UChip>
						{{ user.username }}
					</div>
					<br>
					<div>
						Status: {{ user.status }}
					</div>
					<div v-if="user.status === UserStatus.Playing || user.status === UserStatus.Spectating">
						Game ID: {{ user.gameId }}
					</div>
					<div>
						Games Played: {{ user.gamesWon + user.gamesLost + user.gamesDrawn }}
					</div>
					<div>
						Games Won: {{ user.gamesWon }}
					</div>
					<div>
						Games Lost: {{ user.gamesLost }}
					</div>
					<div>
						Games Drawn: {{ user.gamesDrawn }}
					</div>
				</div>
			</template>
		</UPopover>
	</div>
</template>


<style scoped>
	.chip-and-username {
		box-sizing: border-box;
		padding: 4px;
		display: flex;
		gap: calc(var(--spacing) * 2);
		align-items: center;
		border: 1px solid transparent;

		&:hover {
			cursor: pointer;
			border-color: #959595;
			border-radius: 4px;
		}
	}
</style>
