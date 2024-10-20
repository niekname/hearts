package org.socratesbe.hearts2

import org.socratesbe.hearts2.Suit.CLUBS
import org.socratesbe.hearts2.Symbol.TWO

class Game private constructor(events: List<Event> = emptyList()) {
    companion object {
        private val OPENING_CARD = TWO of CLUBS
        fun start(players: Players) = Game().also { it.start(players) }
        fun fromEvents(vararg event: Event) = Game(event.asList())
    }

    private val _events = events.toMutableList()
    val events: List<Event> = _events

    private fun start(players: Players) {
        _events += GameStarted(players)
        dealCards()
    }

    private fun dealCards() {
        val deck = Deck()
        val cards = deck.cards.shuffled().chunked(deck.cards.size / players().size)
            .mapIndexed { idx, cards -> PlayerWithCards(players().atIndex(idx), cards.toSet()) }
        _events += CardsDealt(cards[0], cards[1], cards[2], cards[3])
    }

    /* TODO if startRound would be a command from the outside,
        you would not need to pass the card, only the player.
        An extra check in the playCard would be needed that checks
        if the round has started */
    fun playCard(player: Player, card: Card) {
        when {
            roundHasStarted() -> continueRound(player, card)
            else -> startRound(player, card)
        }
    }

    private fun startRound(player: Player, card: Card) {
        validatePlayerHasCard(player, OPENING_CARD)
        validateOpeningCardIsBeingPlayed(card)
        _events += CardPlayed(player, OPENING_CARD)
    }

    private fun continueRound(player: Player, card: Card) {
        validatePlayerHasCard(player, card)
        validatePlayersTurn(player)
        _events += CardPlayed(player, card)
    }

    private fun validatePlayerHasCard(player: Player, card: Card) {
        if (!playerHasCard(player, card))
            throw RuntimeException("${player.name} does not have $card in their hand")
    }

    private fun validateOpeningCardIsBeingPlayed(card: Card) {
        if (card != OPENING_CARD)
            throw RuntimeException("$OPENING_CARD must be the first card played in the round")
    }

    private fun validatePlayersTurn(player: Player) {
        if (playerIsNotAtTurn(player))
            throw RuntimeException("It's not ${player.name}'s turn to play")
    }

    private fun playerIsNotAtTurn(player: Player) =
        player != players().playerAtLeftSideOf(lastPlayer())

    private fun players() =
        _events.filterIsInstance<GameStarted>().first().players

    private fun roundHasStarted() =
        _events.filterIsInstance<CardPlayed>().isNotEmpty()

    private fun playerHasCard(player: Player, card: Card) =
        _events.filterIsInstance<CardsDealt>().first().whoHasCard(card) == player

    private fun lastPlayer() =
        _events.filterIsInstance<CardPlayed>().last().player
}