package org.socratesbe.hearts

import org.socratesbe.hearts.Suit.CLUBS
import org.socratesbe.hearts.Suit.HEARTS
import org.socratesbe.hearts.Symbol.TWO

class Game private constructor(events: List<Event> = emptyList()) {
    companion object {
        private val OPENING_CARD = TWO of CLUBS
        private const val MAX_SCORE = 100
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
        if (isFourthHand())
            throw RuntimeException("No cards must be passed in this hand")
        // TODO needs validation that this is not 4 times the same player passing the same cards etc
        if (passingHasHappened())
            throw RuntimeException("Cards have already been passed")

        player1pass.cards.forEach { validatePlayerCanPassCard(player1pass.player, it) }
        player2pass.cards.forEach { validatePlayerCanPassCard(player2pass.player, it) }
        player3pass.cards.forEach { validatePlayerCanPassCard(player3pass.player, it) }
        player4pass.cards.forEach { validatePlayerCanPassCard(player4pass.player, it) }

        _events += CardsPassed(
            listOf(player1pass.passCards(), player2pass.passCards(), player3pass.passCards(), player4pass.passCards())
        )
    }

    private fun PlayerWithCards.passCards() =
        CardsPassed.PlayerPassing(
            from = player,
            to = player.shouldPassTo(),
            card1 = cards.elementAt(0),
            card2 = cards.elementAt(1),
            card3 = cards.elementAt(2)
        )

    private fun Player.shouldPassTo() =
        when {
            isFirstHand() -> players().playerAtLeftSideOf(this)
            isSecondHand() -> players().playerAtRightSideOf(this)
            else -> players().playerAcross(this)
        }

    private fun isFirstHand() =
        _events.filterIsInstance<CardsDealt>().size == 1

    private fun isSecondHand() =
        _events.filterIsInstance<CardsDealt>().size == 2

    private fun isFourthHand() =
        _events.filterIsInstance<CardsDealt>().size == 4

    fun playCard(player: Player, card: Card) {
        CardPlayed(player, card)
            .also { checkRules(it) }
            .let { _events += it }

        if (currentHand().allCardsPlayed() && !gameHasEnded())
            dealCards()
    }

    private fun gameHasEnded() = anyPlayerReachedMaxScore()

    private fun anyPlayerReachedMaxScore() =
        tricks()
            .map { it.score() }
            .groupingBy { it.player }
            .fold(0) { acc, elem -> acc + elem.score }
            .any { it.value >= MAX_SCORE }

    private fun checkRules(cardPlayed: CardPlayed) {
        validatePassingHasHappened()
        validateCardHasNotYetBeenPlayed(cardPlayed.card) // TODO validatePlayerHasCard also checks this, so merge?
        validatePlayerHasCard(cardPlayed.player, cardPlayed.card)
        validatePlayersTurn(cardPlayed.player)

        if (currentHand().isFirstCardOfHand())
            validateOpeningCardIsBeingPlayed(cardPlayed.card)
        else {
            validateLeadingSuitIsBeingFollowed(cardPlayed.player, cardPlayed.card)
            validateHeartsCanBePlayed(cardPlayed)
        }
    }

    private fun validatePassingHasHappened() {
        if (!passingHasHappened())
            throw RuntimeException("Cannot play cards before passing has finished")
    }

    private fun validatePlayerCanPassCard(player: Player, card: Card) {
        if (!lastCardsDealt().cardsForPlayer(player).contains(card))
            throw RuntimeException("${player.name} does not have $card")
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
        val lastTrick = lastTrick()
        if (lastTrick.isFinished()) return
        if (cannotFollowSuit(player, lastTrick.leadingSuit())) return
        if (lastTrick.leadingSuit() != card.suit)
            throw RuntimeException("${player.name} must follow leading suit")
    }

    private fun validateHeartsCanBePlayed(cardPlayed: CardPlayed) {
        if (cardPlayed.card.suit != HEARTS) return
        if (playerHasOnlyHearts(cardPlayed.player)) return

        if (currentHand().heartsHaveBeenBroken()) return
        if (!currentHand().isFirstTrick() && cannotFollowSuit(cardPlayed.player, lastTrick().leadingSuit())) return

        throw RuntimeException("$HEARTS have not been broken")
    }

    private fun validateCardHasNotYetBeenPlayed(card: Card) {
        if (currentHand().cardHasBeenPlayed(card))
            throw RuntimeException("$card has already been played")
    }

    private fun cannotFollowSuit(player: Player, suit: Suit) =
        currentHand().remainingCardsInHandOf(player).none { it.suit == suit }

    private fun playerHasOnlyHearts(player: Player) =
        currentHand().remainingCardsInHandOf(player).all { it.suit == HEARTS }

    private fun whoIsAtTurn() = when {
        currentHand().isFirstCardOfHand() -> whoHasCard(OPENING_CARD)
        lastTrick().isOngoing() -> players().playerAtLeftSideOf(lastPlayer())
        else -> lastTrick().wonBy()
    }

    private fun players() =
        _events.filterIsInstance<GameStarted>().first().players

    private fun lastCardsDealt() =
        _events.filterIsInstance<CardsDealt>().last()

    private fun whoHasCard(card: Card) =
        currentCardsOfPlayers().first { it.cards.contains(card) }.player

    private fun currentCardsOfPlayers() =
        players().asList().map { PlayerWithCards(it, currentHand().remainingCardsInHandOf(it)) }

    private fun passingHasHappened() =
        _events.last() !is CardsDealt

    private fun playerHasCard(player: Player, card: Card) =
        whoHasCard(card) == player

    private fun lastPlayer() =
        _events.filterIsInstance<CardPlayed>().last().player

    private fun currentHand(): Hand {
        val lastCardsDealtIndex = _events.indexOfLast { it is CardsDealt }
        val sublist = _events.subList(lastCardsDealtIndex, _events.size)
        return Hand(sublist.first() as CardsDealt, sublist[1] as CardsPassed, sublist.filterIsInstance<CardPlayed>())
    }

    private fun tricks() =
        _events.filterIsInstance<CardPlayed>()
            .chunked(4) // TODO magic number
            .map(::Trick)

    private fun lastTrick() = tricks().last()
}