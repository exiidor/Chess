<script lang="ts" setup>
	const props = defineProps({
		users: {
			type: Array as PropType<User[]>,
			required: true
		}
	})

	const colorOptions = ref<PieceColor[]>([
		PieceColor.White,
		PieceColor.Black
	])
	const color = ref<PieceColor>(PieceColor.White)
	const cpuEnemy = ref(false)
	const requestedOpponent = ref<User | null>(null)
</script>

<template>
	Own color:
	<URadioGroup v-model="color" :items="colorOptions" />

	<br>
	<UCheckbox v-model="cpuEnemy" label="Against Computer" />

	<br>
	<URadioGroup
		v-if="!cpuEnemy"
		v-model="requestedOpponent"
		:items="props.users"
	/>
</template>

<style scoped>

</style>
