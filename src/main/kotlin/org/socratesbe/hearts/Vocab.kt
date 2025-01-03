package org.socratesbe.hearts

import org.socratesbe.hearts.Suit.HEARTS
import org.socratesbe.hearts.Suit.SPADES
import org.socratesbe.hearts.Symbol.QUEEN

typealias PlayerName = String

data class Players(val player1: Player, val player2: Player, val player3: Player, val player4: Player) {
    val size = 4
    fun atIndex(idx: Int) =
        when (idx) {
            0 -> player1
            1 -> player2
            2 -> player3
            3 -> player4
            else -> error("impossible")
        }

    fun playerAtLeftSideOf(player: Player) =
        when (player) {
            player1 -> player2
            player2 -> player3
            player3 -> player4
            player4 -> player1
            else -> error("impossible")
        }

    fun playerAtRightSideOf(player: Player) =
        when (player) {
            player1 -> player4
            player2 -> player1
            player3 -> player2
            player4 -> player3
            else -> error("impossible")
        }

    fun playerAcross(player: Player) =
        when (player) {
            player1 -> player3
            player2 -> player4
            player3 -> player1
            player4 -> player2
            else -> error("impossible")
        }

    fun asList() = listOf(player1, player2, player3, player4)
}

data class Player(val name: PlayerName)

data class PlayerWithCards(val player: Player, val cards: Set<Card>)

class Hand(events: List<Event>) {
    private val cardsDealt = events.first() as CardsDealt
    private val cardsPassed = events.filterIsInstance<CardsPassed>().firstOrNull()
    private val cardsPlayed = events.filterIsInstance<CardPlayed>()

    fun isFirstCardOfHand() =
        cardsPlayed.isEmpty()

    fun remainingCardsInHandOf(player: Player) =
        cardsDealt.cardsForPlayer(player) +
                cardsPassedTo(player) -
                cardsPassedBy(player) -
                cardsPlayedBy(player)

    fun isFirstTrick() = tricks().size == 1

    fun heartsHaveBeenBroken() = cardsPlayed.any { it.card.suit == HEARTS }

    fun cardHasBeenPlayed(card: Card) = cardsPlayed.any { it.card == card }

    fun allCardsPlayed() = cardsPlayed.size == Deck().cards.size

    private fun tricks() =
        cardsPlayed.chunked(4) // TODO magic number

    private fun cardsPlayedBy(player: Player) =
        cardsPlayed
            .filter { it.player == player }
            .map { it.card }
            .toSet()

    private fun cardsPassedBy(player: Player) =
        cardsPassed?.byPlayer(player)?.cards() ?: emptySet()

    private fun cardsPassedTo(player: Player) =
        cardsPassed?.toPlayer(player)?.cards() ?: emptySet()
}

// TODO add specific types for finished / unfinished trick?
data class Trick(val cardsPlayed: List<CardPlayed>) {
    fun isFinished() = cardsPlayed.size == 4 // TODO this is the amount of players
    fun isOngoing() = !isFinished()
    fun wonBy() = highestRankingCardInLeadingSuit().player
    private fun highestRankingCardInLeadingSuit() =
        cardsPlayed
            .filter { it.card.suit == leadingSuit() }
            .maxBy { it.card.symbol }

    fun leadingSuit() = cardsPlayed.first().card.suit

    fun score() =
        PlayerScore(wonBy(), cardsPlayed.map { it.card }.sumOf { it.penaltyPoints })
}

data class PlayerScore(val player: Player, val score: Int)

data class Deck(
    val cards: List<Card> =
        Suit.entries.flatMap { suit ->
            Symbol.entries.map { Card(suit, it) }
        }
)

data class Card(val suit: Suit, val symbol: Symbol) {
    val penaltyPoints: Int
        get() {
            return when {
                this == QUEEN of SPADES -> 13
                suit == HEARTS -> 1
                else -> 0
            }
        }

    override fun toString() = "$symbol$suit"
}

enum class Suit(private val value: String) {
    HEARTS("♥️"), DIAMONDS("♦️"), CLUBS("♣️"), SPADES("♠️");

    override fun toString() = this.value
}

// TODO add explicit ordinal?
enum class Symbol(private val value: String) {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");

    override fun toString() = this.value
}

infix fun Symbol.of(suit: Suit) = Card(suit, this)

infix fun Player.played(card: Card) = CardPlayed(this, card)
