<script lang="ts" setup>
	import type { UseConfirmDialogReturn } from '@vueuse/core'

	const props = defineProps({
		controller: {
			type: Object as PropType<UseConfirmDialogReturn<any, any, any>>,
			required: true
		},
		invite: {
			type: Object as PropType<InviteS2CData>,
			required: true
		},
		user: {
			type: Object as PropType<User>,
			required: true
		}
	})

	const inviteDialogPercentageRemaining = ref(100)

	onMounted(() => {
		const intervalId = setInterval(() => {
			inviteDialogPercentageRemaining.value = Math.max(0, inviteDialogPercentageRemaining.value -= 1)
		}, 100)
		setTimeout(() => {
			clearInterval(intervalId)
			if (props.controller.isRevealed.value) {
				props.controller.cancel()
			}
		}, 10000)
	})
</script>


<template>
	<UModal v-model:open="controller.isRevealed.value"
		title="Game Invite"
		:description="`You have been invited to a game by ${invite.initiator}.
				You are going to be playing as ${invite.gameInfo.whitePlayer.username === user.username ? 'White' : 'Black'}
				and are going to have ${invite.gameInfo.maxSecondsPerMove} seconds per move.
				Do you want to accept?`"
		:ui="{ footer: 'justify-end' }"
		:close="false"
	>
		<template #footer>
			<UProgress v-model="inviteDialogPercentageRemaining" />
			<UButton label="No" color="neutral" variant="outline" @click="controller.cancel" />
			<UButton label="Yes" color="neutral" @click="controller.confirm" />
		</template>
	</UModal>
</template>
