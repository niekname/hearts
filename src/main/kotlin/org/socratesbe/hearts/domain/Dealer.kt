package org.socratesbe.hearts.domain

interface Dealer {
    fun dealCardsFor(players: List<Player>): List<PlayerWithCards>
}

class ChunkedDealer(private val deck: Deck = Deck()) : Dealer {
    override fun dealCardsFor(players: List<Player>) =
        deck.cards.chunked(deck.cards.size / players.size)
            .mapIndexed { idx, cards -> PlayerWithCards(players[idx], cards) }
}
