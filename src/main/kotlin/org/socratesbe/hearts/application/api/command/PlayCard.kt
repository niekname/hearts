package org.socratesbe.hearts.application.api.command

import org.socratesbe.hearts.domain.Card
import org.socratesbe.hearts.domain.PlayerName

data class PlayCard(val card: Card, val playedBy: PlayerName): Command<PlayCardResponse>

sealed interface PlayCardResponse
data object PlayedCard : PlayCardResponse
data class CouldNotPlayCard(val reason: String): PlayCardResponse
