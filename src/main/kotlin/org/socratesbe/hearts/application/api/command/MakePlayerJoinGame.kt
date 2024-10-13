package org.socratesbe.hearts.application.api.command

import org.socratesbe.hearts.domain.PlayerName

data class MakePlayerJoinGame(val player: PlayerName) : Command<PlayerJoinResponse>

sealed interface PlayerJoinResponse
data class PlayerCouldNotJoin(val player: PlayerName, val reason: String) : PlayerJoinResponse
data object PlayerJoined : PlayerJoinResponse
