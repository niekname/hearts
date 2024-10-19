package org.socratesbe.hearts2

import org.socratesbe.hearts2.Suit.CLUBS
import org.socratesbe.hearts2.Symbol.TWO

class Game private constructor(events: List<Event> = emptyList()) {
    private val OPENING_CARD = TWO of CLUBS

    private val _events = events.toMutableList()
    val events: List<Event> = _events

    private fun start(players: Players) {
        _events += GameStarted(players)
        dealCards()
    }

    private fun dealCards() {
        val deck = Deck()
        val cards = deck.cards.chunked(deck.cards.size / players().size)
            .mapIndexed { idx, cards -> PlayerWithCards(players().atIndex(idx), cards.toSet()) }
        _events += CardsDealt(cards[0], cards[1], cards[2], cards[3])
    }

    private fun players() = _events.filterIsInstance<GameStarted>().first().players


    fun playCard(player: Player, card: Card) {
        if (card != OPENING_CARD)
            throw RuntimeException("$OPENING_CARD must be the first card played in the round")
        if (!playerHasCard(player, card))
            throw RuntimeException("${player.name} does not have $card in their hand")

        _events += CardPlayed(player, card)
    }

    private fun playerHasCard(player: Player, card: Card): Boolean {
        val playerThatHasTheCard = _events.filterIsInstance<CardsDealt>().first().whoHasCard(card)
        return player == playerThatHasTheCard
    }

    companion object {
        fun start(players: Players) = Game().also { it.start(players) }
        fun fromEvents(vararg event: Event) = Game(event.asList())
    }
}