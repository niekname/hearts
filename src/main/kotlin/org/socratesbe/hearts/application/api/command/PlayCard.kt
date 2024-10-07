package org.socratesbe.hearts.application.api.command

import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.PlayerName

data class PlayCard(val card: Card, val playedBy: PlayerName): Command<PlayCardResponse>

sealed interface PlayCardResponse
data object PlayedCard : PlayCardResponse
data class CouldNotPlayCard(val reason: String): PlayCardResponse
