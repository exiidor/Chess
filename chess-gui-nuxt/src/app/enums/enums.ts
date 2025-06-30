export enum PacketType {
	LoginC2S = "LoginC2S",
	LoginResultS2C = "LoginResultS2C",
	UserListS2C = "UserListS2C",
	KickS2C = "KickS2C",
	CreateGameC2S = "CreateGameC2S",
	CreateGameResultS2C = "CreateGameResultS2C",
	InviteS2C = "InviteS2C",
	InviteResponseC2S = "InviteResponseC2S",
	UserJoinedS2C = "UserJoinedS2C",
	JoinGameS2C = "JoinGameS2C",
	BoardS2C = "BoardS2C",
	LeaveGameC2S = "LeaveGameC2S",
	UserLeftS2C = "UserLeftS2C",
}

export enum SquareSelectionState {
	None,
	Selected,
	Highlighted
}
