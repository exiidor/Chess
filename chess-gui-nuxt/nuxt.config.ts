// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
	srcDir: 'src/',
	app: {
		head: {
			htmlAttrs: {
				lang: "de",
			},
			title: "Softwareschreiber-Schach",
		},
	},
	vite: {
		server: {
			allowedHosts: true
		}
	},
	runtimeConfig: {
		public: {
			chessServerAddress: "ws://localhost:3010",
		}
	},
	modules: [
		'@vueuse/nuxt',
	],
	devtools: {
		enabled: true
	},
	imports: {
		dirs: [
			'enums',
		]
	},
	compatibilityDate: '2024-11-01',
	telemetry: false,
})
