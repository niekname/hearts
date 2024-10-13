package org.socratesbe.hearts.domain

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

typealias PlayerName = String

data class Player(val name: PlayerName)
data class PlayerWithCards(val player: Player, val cards: List<Card>)
