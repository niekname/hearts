package org.socratesbe.hearts.application.api.query

import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.PlayerName

class CardsInHandOf(val player: PlayerName) : Query<List<Card>>


