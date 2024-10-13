package org.socratesbe.hearts.application.api.command

import org.socratesbe.hearts.domain.Card
import org.socratesbe.hearts.domain.PlayerName

class PassCards(val cards: Set<Card>, val passedBy: PlayerName) : Command<PassCardsResponse>

sealed interface PassCardsResponse
data object PassedCards : PassCardsResponse
data class CouldNotPassCards(val reason: String) : PassCardsResponse
