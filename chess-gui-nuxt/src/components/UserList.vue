<script setup lang="ts">
	const props = defineProps({
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

	function getRelevantStatuses(): (UserStatus.Online | UserStatus.Offline)[] {
		return [UserStatus.Online, UserStatus.Offline];
	}

	function getUsers(userStatus: UserStatus.Online | UserStatus.Offline): User[] {
		switch (userStatus) {
			case UserStatus.Online:
				return props.users.filter(user => user.status === UserStatus.Online
						|| user.status === UserStatus.Playing
						|| user.status === UserStatus.Spectating)
			case UserStatus.Offline:
				return props.users.filter(user => user.status === UserStatus.Offline)
		}
	}
</script>


<template>
	<div class="user-list-wrapper">
		<UserSubList v-for="status in getRelevantStatuses()"
			:heading="status"
			:users="getUsers(status)"
			@user-clicked="onUserClicked"
		/>
	</div>
</template>


<style scoped>
	.user-list-wrapper {
		box-sizing: border-box;
		display: block;
		padding-top: 6px;
		padding-bottom: 4px;
		padding-left: 7px;
		padding-right: 7px;
	}

	ul {
		padding: 0;
	}

	li {
		box-sizing: border-box;
		display: flex;
		padding: 4px;
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
