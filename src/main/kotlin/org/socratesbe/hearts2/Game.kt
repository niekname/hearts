package org.socratesbe.hearts2


class Game(private val players: Players) {
    var events: List<Event> = emptyList()

    init {
        events += GameStarted
        dealCards()
    }

    private fun dealCards() {
        val deck = Deck()
        val cards = deck.cards.chunked(deck.cards.size / players.size)
            .mapIndexed { idx, cards -> PlayerWithCards(players.atIndex(idx), cards.toSet()) }
        events += CardsDealt(cards[0], cards[1], cards[2], cards[3])
    }

    fun playCard(player: Player, card: Card) {
        throw RuntimeException("It's not ${player.name}'s turn to play")
    }

    companion object {
        fun start(players: Players) = Game(players)
    }
}