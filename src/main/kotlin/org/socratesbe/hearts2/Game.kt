package org.socratesbe.hearts2

import org.socratesbe.hearts2.Suit.CLUBS
import org.socratesbe.hearts2.Suit.HEARTS
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

    fun playCard(player: Player, card: Card) =
        CardPlayed(player, card)
            .also { checkRules(it) }
            .let { _events += it }

    private fun checkRules(cardPlayed: CardPlayed) {
        validatePlayerHasCard(cardPlayed)
        validatePlayersTurn(cardPlayed.player)

        if (handHasNotStarted())
            validateOpeningCardIsBeingPlayed(cardPlayed.card)
        else {
            validateLeadingSuitIsBeingFollowed(cardPlayed.player, cardPlayed.card)
            validateHeartsCanBePlayed(cardPlayed)
            validateCardHasNotYetBeenPlayed(cardPlayed.card)
        }
    }

    private fun validatePlayerHasCard(cardPlayed: CardPlayed) {
        if (!playerHasCard(cardPlayed.player, cardPlayed.card))
            throw RuntimeException("${cardPlayed.player.name} does not have ${cardPlayed.card} in their hand")
    }

    private fun validateOpeningCardIsBeingPlayed(card: Card) {
        if (card != OPENING_CARD)
            throw RuntimeException("$OPENING_CARD must be the first card played in the hand")
    }

    private fun validatePlayersTurn(player: Player) {
        if (player != whoIsAtTurn())
            throw RuntimeException("It's not ${player.name}'s turn to play")
    }

    private fun validateLeadingSuitIsBeingFollowed(player: Player, card: Card) {
        val lastTrick = tricks().last()
        if (lastTrick.isFinished()) return
        if (cannotFollowSuit(player, lastTrick.leadingSuit())) return
        if (lastTrick.leadingSuit() != card.suit)
            throw RuntimeException("${player.name} must follow leading suit")
    }

    private fun cannotFollowSuit(player: Player, suit: Suit) =
        cardsOfPlayer(player).none { it.suit == suit }

    private fun cardsOfPlayer(player: Player) =
        cardsDealt().cardsForPlayer(player)

    private fun validateHeartsCanBePlayed(cardPlayed: CardPlayed) {
        if (playerHasOnlyHearts(cardPlayed.player)) return
        if (cardPlayed.card.suit == HEARTS)
            throw RuntimeException("Cannot play hearts on the first trick")
    }

    private fun playerHasOnlyHearts(player: Player) =
        cardsOfPlayer(player).all { it.suit == HEARTS }

    private fun validateCardHasNotYetBeenPlayed(card: Card) {
        if (cardsPlayed().contains(card))
            throw RuntimeException("$card has already been played")
    }

    private fun whoIsAtTurn() = when {
        handHasNotStarted() -> cardsDealt().whoHasCard(OPENING_CARD)
        trickIsOngoing() -> players().playerAtLeftSideOf(lastPlayer())
        else -> playerThatWonLastTrick()
    }

    private fun trickIsOngoing() =
        !tricks().last().isFinished()

    private fun playerThatWonLastTrick() =
        tricks().last { it.isFinished() }.wonBy()

    private fun players() =
        _events.filterIsInstance<GameStarted>().first().players

    private fun cardsDealt() =
        _events.filterIsInstance<CardsDealt>().first()

    private fun handHasNotStarted() =
        _events.filterIsInstance<CardPlayed>().isEmpty()

    private fun playerHasCard(player: Player, card: Card) =
        cardsDealt().whoHasCard(card) == player

    private fun lastPlayer() =
        _events.filterIsInstance<CardPlayed>().last().player

    private fun tricks() =
        _events.filterIsInstance<CardPlayed>()
            .chunked(4) // TODO magic number
            .map(::Trick)

    private fun cardsPlayed() =
        _events.filterIsInstance<CardPlayed>().map { it.card }
}