package org.socratesbe.hearts.domain

import org.socratesbe.hearts.vocabulary.*
import org.socratesbe.hearts.vocabulary.Suit.CLUBS
import org.socratesbe.hearts.vocabulary.Symbol.TWO

class Game(private val dealer: Dealer) {
    private val events: MutableList<DomainEvent> = mutableListOf()
    private val FIRST_CARD_THAT_NEEDS_TO_BE_PLAYED_IN_ROUND = TWO of CLUBS

    fun playerJoins(name: PlayerName) {
        events += PlayerJoined(Player(name))
    }

    fun start() {
        if (!hasEnoughPlayers()) return
        dealCards()
        events += GameStarted
    }

    fun hasStarted() = events.contains(GameStarted)

    fun cardsInHandOf(playerName: PlayerName): List<Card> =
        events.filterIsInstance<CardsDealt>()
            .first { it.player.name == playerName }
            .cards


    private fun hasEnoughPlayers() = events.filterIsInstance<PlayerJoined>().size == NUMBER_OF_PLAYERS

    private fun dealCards() {
        dealer.dealCardsFor(players())
            .map { CardsDealt(it.player, it.cards) }
            .forEach { events += it }
    }

    private fun players() = events.filterIsInstance<PlayerJoined>().map { it.player }

    fun whoseTurnIsIt() = whoStartsTheRound()

    fun playCard(playedBy: PlayerName, playedCard: Card) {
        validateItsPlayersTurn(playedBy)
        validatePlayerHasCard(playedCard, playedBy)
        validateFirstCardOfRound(playedCard, playedBy)
    }

    private fun validateItsPlayersTurn(playedBy: PlayerName) {
        if (whoseTurnIsIt() != playedBy) throw RuntimeException("It's not ${playedBy}'s turn to play")
    }

    private fun validatePlayerHasCard(
        playedCard: Card,
        playedBy: PlayerName
    ) {
        if (playedCard !in cardsInHandOf(playedBy)) throw RuntimeException("$playedBy does not have $playedCard in their hand")
    }

    private fun validateFirstCardOfRound(
        playedCard: Card,
        playedBy: PlayerName
    ) {
        if (playedCard != FIRST_CARD_THAT_NEEDS_TO_BE_PLAYED_IN_ROUND) throw RuntimeException("$playedBy must play $FIRST_CARD_THAT_NEEDS_TO_BE_PLAYED_IN_ROUND on the first turn")
    }

    private fun whoStartsTheRound() =
        events.filterIsInstance<CardsDealt>()
            .first { FIRST_CARD_THAT_NEEDS_TO_BE_PLAYED_IN_ROUND in it.cards }
            .player.name

    companion object {
        const val NUMBER_OF_PLAYERS = 4
    }
}

data class PlayerJoined(val player: Player) : DomainEvent
data class CardsDealt(val player: Player, val cards: List<Card>) : DomainEvent
data object GameStarted : DomainEvent
interface DomainEvent

interface Dealer {
    fun dealCardsFor(players: List<Player>): List<PlayerWithCards>
}

