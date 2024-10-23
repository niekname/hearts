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
    val events get() = _events.toList()

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

    fun passCards(
        player1pass: PlayerWithCards,
        player2pass: PlayerWithCards,
        player3pass: PlayerWithCards,
        player4pass: PlayerWithCards
    ) {
        player1pass.cards.forEach {
            validatePlayerHasCard(player1pass.player, it)
        }
        player2pass.cards.forEach {
            validatePlayerHasCard(player2pass.player, it)
        }
        player3pass.cards.forEach {
            validatePlayerHasCard(player3pass.player, it)
        }
        player4pass.cards.forEach {
            validatePlayerHasCard(player4pass.player, it)
        }
        _events += CardsPassed
    }

    fun playCard(player: Player, card: Card) =
        CardPlayed(player, card)
            .also { checkRules(it) }
            .let { _events += it }

    private fun checkRules(cardPlayed: CardPlayed) {
        validatePassingHasHappened()
        validatePlayerHasCard(cardPlayed.player, cardPlayed.card)
        validatePlayersTurn(cardPlayed.player)

        if (handHasNotStarted())
            validateOpeningCardIsBeingPlayed(cardPlayed.card)
        else {
            validateLeadingSuitIsBeingFollowed(cardPlayed.player, cardPlayed.card)
            validateHeartsCanBePlayed(cardPlayed)
            validateCardHasNotYetBeenPlayed(cardPlayed.card)
        }
    }

    private fun validatePassingHasHappened() {
        if (!passingHasHappened())
            throw RuntimeException("Cannot play cards before passing has finished")
    }

    private fun validatePlayerHasCard(player: Player, card: Card) {
        if (!playerHasCard(player, card))
            throw RuntimeException("${player.name} does not have $card")
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

    private fun validateHeartsCanBePlayed(cardPlayed: CardPlayed) {
        if (cardPlayed.card.suit != HEARTS) return
        if (playerHasOnlyHearts(cardPlayed.player)) return
        if (heartsHaveBeenBroken())
            throw RuntimeException("$HEARTS have not been broken")
    }

    private fun validateCardHasNotYetBeenPlayed(card: Card) {
        if (cardsPlayed().contains(card))
            throw RuntimeException("$card has already been played")
    }

    private fun cannotFollowSuit(player: Player, suit: Suit) =
        remainingCardsInHandOf(player).none { it.suit == suit }

    private fun remainingCardsInHandOf(player: Player) =
        cardsDealt().cardsForPlayer(player) - cardsPlayedBy(player)

    private fun heartsHaveBeenBroken() = cardsPlayed().none { it.suit == HEARTS }

    private fun playerHasOnlyHearts(player: Player) =
        remainingCardsInHandOf(player).all { it.suit == HEARTS }

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

    private fun passingHasHappened() =
        _events.filterIsInstance<CardsPassed>().isNotEmpty()

    private fun playerHasCard(player: Player, card: Card) =
        cardsDealt().whoHasCard(card) == player

    private fun lastPlayer() =
        _events.filterIsInstance<CardPlayed>().last().player

    private fun tricks() =
        _events.filterIsInstance<CardPlayed>()
            .chunked(4) // TODO magic number
            .map(::Trick)

    private fun cardsPlayed() =
        _events.filterIsInstance<CardPlayed>().map { it.card }.toSet()

    private fun cardsPlayedBy(player: Player) =
        _events.filterIsInstance<CardPlayed>()
            .filter { it.player == player }
            .map { it.card }
            .toSet()
}