<script setup lang="ts">
	defineProps({
		users: {
			type: Array as PropType<User[]>,
			required: true
		}
	})

	const emit = defineEmits<{
		(event: 'user-clicked', user: User): void
	}>()

	function onUserClicked(user: User) {
		emit('user-clicked', user);
	}

	function userStatusColor(status: string): string {
		switch (status.toLowerCase()) {
			case 'online':
			case 'spectating':
				return 'green';
			case 'playing':
				return 'blue';
			default:
				return 'grey';
		}
	}
</script>


<template>
	<ul>
		<li v-for="user in users" :key="user.username" @click="onUserClicked(user)">
			<span :user="user" :style="{color: userStatusColor(user.status.toLowerCase())}">
				{{ user.username }}
			</span>
		</li>
	</ul>
</template>


<style scoped>
	ul {
		padding: 0;
	}

	li {
		display: inline-block;
		box-sizing: border-box;
		border: 2px solid #000;
		margin: 0 5px;
		background-color: lightgrey;
		padding-top: 4px;
		padding-bottom: 4px;
		padding-left: 7px;
		padding-right: 7px;

		&:hover {
			border-color: red;
			cursor: pointer;
		}
	}
</style>
