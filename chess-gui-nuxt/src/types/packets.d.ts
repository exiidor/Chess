declare interface Packet {
	type: PacketType
	data: any
}

declare interface InviteS2C extends Packet {
	type: PacketType.InviteS2C
	data: InviteS2CData
}

declare interface InviteS2CData {
	initiator: string
	gameInfo: {
		id: string
		whitePlayer: User
		blackPlayer: User
		maxSecondsPerMove: number
	},
	maxSecondsPerMove: number
}
