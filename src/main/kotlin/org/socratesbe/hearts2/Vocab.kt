package org.socratesbe.hearts2

typealias PlayerName = String

data class Players(val player1: Player, val player2: Player, val player3: Player, val player4: Player) {
    val size = 4
    fun atIndex(idx: Int) =
        when (idx) {
            0 -> player1
            1 -> player2
            2 -> player3
            3 -> player4
            else -> throw RuntimeException()
        }

    fun playerAtLeftSideOf(lastPlayer: Player) =
        when (lastPlayer) {
            player1 -> player2
            player2 -> player3
            player3 -> player4
            player4 -> player1
            else -> throw RuntimeException()
        }
}

data class Player(val name: PlayerName)

data class PlayerWithCards(val player: Player, val cards: Set<Card>) {
    fun hasCard(card: Card) = cards.contains(card)
}

// TODO add specific types for finished / unfinished trick?
data class Trick(val cardsPlayed: List<CardPlayed>) {
    fun isFinished() = cardsPlayed.size == 4 // TODO this is the amount of players
    fun wonBy() = highestRankingCardInLeadingSuit().player
    private fun highestRankingCardInLeadingSuit() =
        cardsPlayed
            .filter { it.card.suit == leadingSuit() }
            .maxBy { it.card.symbol }

    private fun leadingSuit() = cardsPlayed.first().card.suit
}

data class Deck(
    val cards: List<Card> =
        Suit.entries.flatMap { suit ->
            Symbol.entries.map { Card(suit, it) }
        }
)

data class Card(val suit: Suit, val symbol: Symbol) {
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

infix fun Symbol.of(suit: Suit): Card = Card(suit, this)
