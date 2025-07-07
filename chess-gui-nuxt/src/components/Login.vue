<script lang="ts" setup>
	import zod from 'zod';

	const props = defineProps({
		wsSendFunc: {
			type: Function as PropType<(data: string) => void>,
			required: true
		}
	})

	const emit = defineEmits<{
		(e: 'attempted-login', username: string): void
	}>()

	const schema = zod.object({
		username: zod.string().nonempty('Username is required'),
		password: zod.string().nonempty('Password is required')
	})

	type Schema = zod.output<typeof schema>

	const state = reactive<Partial<Schema>>({
		username: undefined,
		password: undefined
	})

	async function login() {
		props.wsSendFunc(JSON.stringify({
			type: PacketType.LoginC2S,
			data: {
				username: state.username,
				password: await sha256(state.password!),
				clientVersion: "1.0.0",
			}
		}))
		state.password = undefined
		emit('attempted-login', state.username!)
	}
</script>


<template>
	<UForm :schema="schema" :state="state" class="space-y-4" @submit="login">
		<UFormField label="Username" name="username">
			<UInput v-model="state.username" />
		</UFormField>
		<UFormField label="Password" name="password" :password="true">
			<UInput v-model="state.password" type="password" />
		</UFormField>
		<UButton type="submit">Login</UButton>
	</UForm>
</template>
