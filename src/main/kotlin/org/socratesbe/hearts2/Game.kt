package org.socratesbe.hearts2

import org.socratesbe.hearts2.Suit.CLUBS
import org.socratesbe.hearts2.Suit.HEARTS
import org.socratesbe.hearts2.Symbol.TWO

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
        if (passingHasHappened())
            throw RuntimeException("Cards have already been passed")

        player1pass.cards.forEach { validatePlayerCanPassCard(player1pass.player, it) }
        player2pass.cards.forEach { validatePlayerCanPassCard(player2pass.player, it) }
        player3pass.cards.forEach { validatePlayerCanPassCard(player3pass.player, it) }
        player4pass.cards.forEach { validatePlayerCanPassCard(player4pass.player, it) }

        when {
            isFirstDeal() -> passToTheLeft(player1pass, player2pass, player3pass, player4pass)
            else -> passToTheRight(player1pass, player2pass, player3pass, player4pass)
        }
    }

    private fun isFirstDeal() =
        _events.filterIsInstance<CardsDealt>().size == 1

    private fun passToTheLeft(
        player1pass: PlayerWithCards,
        player2pass: PlayerWithCards,
        player3pass: PlayerWithCards,
        player4pass: PlayerWithCards
    ) {
        val player1 = CardsPassed.PlayerPassing(
            player1pass.player,
            players().playerAtLeftSideOf(player1pass.player),
            player1pass.cards.elementAt(0),
            player1pass.cards.elementAt(1),
            player1pass.cards.elementAt(2)
        )
        val player2 = CardsPassed.PlayerPassing(
            player2pass.player,
            players().playerAtLeftSideOf(player2pass.player),
            player2pass.cards.elementAt(0),
            player2pass.cards.elementAt(1),
            player2pass.cards.elementAt(2)
        )
        val player3 = CardsPassed.PlayerPassing(
            player3pass.player,
            players().playerAtLeftSideOf(player3pass.player),
            player3pass.cards.elementAt(0),
            player3pass.cards.elementAt(1),
            player3pass.cards.elementAt(2)
        )
        val player4 = CardsPassed.PlayerPassing(
            player4pass.player,
            players().playerAtLeftSideOf(player4pass.player),
            player4pass.cards.elementAt(0),
            player4pass.cards.elementAt(1),
            player4pass.cards.elementAt(2)
        )

        _events += CardsPassed(listOf(player1, player2, player3, player4))
    }

    private fun passToTheRight(
        player1pass: PlayerWithCards,
        player2pass: PlayerWithCards,
        player3pass: PlayerWithCards,
        player4pass: PlayerWithCards
    ) {
        val player1 = CardsPassed.PlayerPassing(
            player1pass.player,
            players().playerAtRightSideOf(player1pass.player),
            player1pass.cards.elementAt(0),
            player1pass.cards.elementAt(1),
            player1pass.cards.elementAt(2)
        )
        val player2 = CardsPassed.PlayerPassing(
            player2pass.player,
            players().playerAtRightSideOf(player2pass.player),
            player2pass.cards.elementAt(0),
            player2pass.cards.elementAt(1),
            player2pass.cards.elementAt(2)
        )
        val player3 = CardsPassed.PlayerPassing(
            player3pass.player,
            players().playerAtRightSideOf(player3pass.player),
            player3pass.cards.elementAt(0),
            player3pass.cards.elementAt(1),
            player3pass.cards.elementAt(2)
        )
        val player4 = CardsPassed.PlayerPassing(
            player4pass.player,
            players().playerAtRightSideOf(player4pass.player),
            player4pass.cards.elementAt(0),
            player4pass.cards.elementAt(1),
            player4pass.cards.elementAt(2)
        )

        _events += CardsPassed(listOf(player1, player2, player3, player4))
    }

    fun playCard(player: Player, card: Card) {
        CardPlayed(player, card)
            .also { checkRules(it) }
            .let { _events += it }

        if (!gameHasEnded() && allCardsPlayed()) dealCards()
    }

    private fun gameHasEnded() = anyPlayerReachedMaxScore()

    private fun anyPlayerReachedMaxScore() =
        tricks()
            .map { it.score() }
            .groupingBy { it.player }
            .fold(0) { acc, elem -> acc + elem.score }
            .any { it.value >= MAX_SCORE }

    private fun allCardsPlayed() = cardsPlayed().size == Deck().cards.size

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
        val lastTrick = tricks().last()
        if (lastTrick.isFinished()) return
        if (cannotFollowSuit(player, lastTrick.leadingSuit())) return
        if (lastTrick.leadingSuit() != card.suit)
            throw RuntimeException("${player.name} must follow leading suit")
    }

    private fun validateHeartsCanBePlayed(cardPlayed: CardPlayed) {
        if (cardPlayed.card.suit != HEARTS) return
        if (playerHasOnlyHearts(cardPlayed.player)) return

        if (heartsHaveBeenBroken()) return
        if (!isFirstTrick() && cannotFollowSuit(cardPlayed.player, tricks().last().leadingSuit())) return

        throw RuntimeException("$HEARTS have not been broken")
    }

    private fun isFirstTrick() = currentHand().tricks().size == 1

    private fun validateCardHasNotYetBeenPlayed(card: Card) {
        if (cardsPlayed().contains(card))
            throw RuntimeException("$card has already been played")
    }

    private fun cannotFollowSuit(player: Player, suit: Suit) =
        currentHand().remainingCardsInHandOf(player).none { it.suit == suit }

    private fun heartsHaveBeenBroken() = cardsPlayed().any { it.suit == HEARTS }

    private fun playerHasOnlyHearts(player: Player) =
        currentHand().remainingCardsInHandOf(player).all { it.suit == HEARTS }

    private fun whoIsAtTurn() = when {
        currentHand().isFirstCardOfHand() -> whoHasCard(OPENING_CARD)
        trickIsOngoing() -> players().playerAtLeftSideOf(lastPlayer())
        else -> playerThatWonLastTrick()
    }

    private fun trickIsOngoing() =
        !tricks().last().isFinished()

    private fun playerThatWonLastTrick() =
        tricks().last { it.isFinished() }.wonBy()

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

    private fun cardsPlayed() =
        currentHand().cardsPlayed()
}