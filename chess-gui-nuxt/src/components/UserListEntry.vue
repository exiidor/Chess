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
		console.log(status);
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
		<UChip inset :color="userStatusColor(user.status)">
			<UAvatar :src="getUserIconFromNameIfPossible(user.username)" :alt="user.username" />
		</UChip>
		<div class="username">{{ user.username }}</div>
	</div>
</template>


<style scoped>
	.user-list-entry {
		box-sizing: border-box;
		display: flex;
		border: 1px solid transparent;

		&:hover {
			cursor: pointer;
			border-color: #959595;
			border-radius: 4px;
		}
	}

	.username {
		padding-top: auto;
		padding-bottom: auto;
	}
</style>
