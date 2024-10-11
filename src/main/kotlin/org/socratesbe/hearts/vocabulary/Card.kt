package org.socratesbe.hearts.vocabulary


data class Deck(val cards: List<Card> =
    Suit.entries.flatMap { suit -> Symbol.entries.map { Card(suit, it)} })

data class Card(val suit: Suit, val symbol: Symbol) {
    override fun toString() = "$symbol$suit"
}

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES;

    override fun toString() = when(this) {
        HEARTS -> "♥️"
        DIAMONDS -> "♦️"
        CLUBS -> "♣️"
        SPADES -> "♠️"
    }
}

enum class Symbol {
    TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;

    override fun toString() = when(this) {
        TWO -> "2"
        THREE -> "3"
        FOUR -> "4"
        FIVE -> "5"
        SIX -> "6"
        SEVEN -> "7"
        EIGHT -> "8"
        NINE -> "9"
        TEN -> "10"
        JACK -> "J"
        QUEEN -> "Q"
        KING -> "K"
        ACE -> "A"
    }
}

infix fun Symbol.of(suit: Suit): Card = Card(suit, this)

