<script setup lang="ts">
	defineProps({
		heading: {
			type: String,
			required: true
		},
		users: {
			type: Array as PropType<User[]>,
			required: true
		}
	})
</script>


<template>
	<div class="user-list-wrapper">
		{{ heading }} ({{ users.length }})
		<ul>
			<li v-for="user in users" :key="user.username">
				<UserListEntry :user="user" />
			</li>
		</ul>
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
</style>
