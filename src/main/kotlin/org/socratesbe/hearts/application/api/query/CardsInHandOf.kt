package org.socratesbe.hearts.application.api.query

import org.socratesbe.hearts.domain.Card
import org.socratesbe.hearts.domain.PlayerName

class CardsInHandOf(val player: PlayerName) : Query<List<Card>>


