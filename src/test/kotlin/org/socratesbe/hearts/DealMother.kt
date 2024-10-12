package org.socratesbe.hearts

import org.socratesbe.hearts.vocabulary.Suit.CLUBS
import org.socratesbe.hearts.vocabulary.Suit.DIAMONDS
import org.socratesbe.hearts.vocabulary.Suit.HEARTS
import org.socratesbe.hearts.vocabulary.Suit.SPADES
import org.socratesbe.hearts.vocabulary.Symbol.ACE
import org.socratesbe.hearts.vocabulary.Symbol.EIGHT
import org.socratesbe.hearts.vocabulary.Symbol.FIVE
import org.socratesbe.hearts.vocabulary.Symbol.FOUR
import org.socratesbe.hearts.vocabulary.Symbol.JACK
import org.socratesbe.hearts.vocabulary.Symbol.KING
import org.socratesbe.hearts.vocabulary.Symbol.NINE
import org.socratesbe.hearts.vocabulary.Symbol.QUEEN
import org.socratesbe.hearts.vocabulary.Symbol.SEVEN
import org.socratesbe.hearts.vocabulary.Symbol.SIX
import org.socratesbe.hearts.vocabulary.Symbol.TEN
import org.socratesbe.hearts.vocabulary.Symbol.THREE
import org.socratesbe.hearts.vocabulary.Symbol.TWO
import org.socratesbe.hearts.vocabulary.PlayerName
import org.socratesbe.hearts.vocabulary.of

object DealMother {
    fun dealFixedCards(player: PlayerName) = when (player) {
        "Mary" -> listOf(
            EIGHT of SPADES,
            THREE of DIAMONDS,
            SIX of HEARTS,
            TEN of HEARTS,
            JACK of DIAMONDS,
            TEN of CLUBS,
            TEN of SPADES,
            FIVE of DIAMONDS,
            ACE of DIAMONDS,
            ACE of CLUBS,
            SEVEN of DIAMONDS,
            NINE of SPADES,
            ACE of HEARTS
        )

        "Joe" -> listOf(
            QUEEN of CLUBS,
            TWO of HEARTS,
            EIGHT of HEARTS,
            SEVEN of HEARTS,
            NINE of CLUBS,
            QUEEN of HEARTS,
            FOUR of CLUBS,
            SEVEN of SPADES,
            FIVE of HEARTS,
            QUEEN of SPADES,
            TWO of SPADES,
            EIGHT of DIAMONDS,
            FIVE of SPADES
        )

        "Bob" -> listOf(
            SIX of DIAMONDS,
            TWO of CLUBS,
            FIVE of CLUBS,
            QUEEN of DIAMONDS,
            SIX of SPADES,
            FOUR of DIAMONDS,
            FOUR of SPADES,
            FOUR of HEARTS,
            ACE of SPADES,
            NINE of HEARTS,
            SEVEN of CLUBS,
            JACK of CLUBS,
            TWO of DIAMONDS
        )

        "Jane" -> listOf(
            THREE of CLUBS,
            TEN of DIAMONDS,
            NINE of DIAMONDS,
            KING of DIAMONDS,
            KING of SPADES,
            THREE of HEARTS,
            JACK of SPADES,
            EIGHT of CLUBS,
            THREE of SPADES,
            KING of CLUBS,
            KING of HEARTS,
            SIX of CLUBS,
            JACK of HEARTS
        )

        else -> error("Unknown player $player")
    }

    fun dealCardsSoJoeHas2ofClubs(player: PlayerName) = when (player) {
        "Mary" -> listOf(
            EIGHT of SPADES,
            THREE of DIAMONDS,
            SIX of HEARTS,
            TEN of HEARTS,
            JACK of DIAMONDS,
            TEN of CLUBS,
            TEN of SPADES,
            FIVE of DIAMONDS,
            ACE of DIAMONDS,
            ACE of CLUBS,
            SEVEN of DIAMONDS,
            NINE of SPADES,
            ACE of HEARTS
        )

        "Bob" -> listOf(
            QUEEN of CLUBS,
            TWO of HEARTS,
            EIGHT of HEARTS,
            SEVEN of HEARTS,
            NINE of CLUBS,
            QUEEN of HEARTS,
            FOUR of CLUBS,
            SEVEN of SPADES,
            FIVE of HEARTS,
            QUEEN of SPADES,
            TWO of SPADES,
            EIGHT of DIAMONDS,
            FIVE of SPADES
        )

        "Joe" -> listOf(
            SIX of DIAMONDS,
            TWO of CLUBS,
            FIVE of CLUBS,
            QUEEN of DIAMONDS,
            SIX of SPADES,
            FOUR of DIAMONDS,
            FOUR of SPADES,
            FOUR of HEARTS,
            ACE of SPADES,
            NINE of HEARTS,
            SEVEN of CLUBS,
            JACK of CLUBS,
            TWO of DIAMONDS
        )

        "Jane" -> listOf(
            THREE of CLUBS,
            TEN of DIAMONDS,
            NINE of DIAMONDS,
            KING of DIAMONDS,
            KING of SPADES,
            THREE of HEARTS,
            JACK of SPADES,
            EIGHT of CLUBS,
            THREE of SPADES,
            KING of CLUBS,
            KING of HEARTS,
            SIX of CLUBS,
            JACK of HEARTS
        )

        else -> error("Unknown player $player")
    }

    fun maryHasNoClubs(player: PlayerName) = when (player) {
        "Mary" -> listOf(
            TEN of HEARTS,
            ACE of HEARTS,
            TWO of HEARTS,
            EIGHT of HEARTS,
            SEVEN of HEARTS,
            QUEEN of HEARTS,
            FIVE of HEARTS,
            FOUR of HEARTS,
            NINE of HEARTS,
            THREE of HEARTS,
            KING of HEARTS,
            JACK of HEARTS,
            TWO of SPADES,
        )

        "Joe" -> listOf(
            QUEEN of CLUBS,
            NINE of CLUBS,
            FOUR of CLUBS,
            SEVEN of SPADES,
            QUEEN of SPADES,
            TEN of CLUBS,
            EIGHT of DIAMONDS,
            FIVE of SPADES,
            EIGHT of SPADES,
            THREE of DIAMONDS,
            JACK of DIAMONDS,
            TEN of SPADES,
            SIX of HEARTS,
        )

        "Bob" -> listOf(
            SIX of DIAMONDS,
            TWO of CLUBS,
            FIVE of CLUBS,
            QUEEN of DIAMONDS,
            SIX of SPADES,
            FOUR of DIAMONDS,
            FOUR of SPADES,
            ACE of SPADES,
            SEVEN of CLUBS,
            JACK of CLUBS,
            TWO of DIAMONDS,
            FIVE of DIAMONDS,
            ACE of DIAMONDS
        )

        "Jane" -> listOf(
            THREE of CLUBS,
            TEN of DIAMONDS,
            NINE of DIAMONDS,
            KING of DIAMONDS,
            KING of SPADES,
            JACK of SPADES,
            EIGHT of CLUBS,
            THREE of SPADES,
            KING of CLUBS,
            SIX of CLUBS,
            ACE of CLUBS,
            SEVEN of DIAMONDS,
            NINE of SPADES
        )

        else -> error("Unknown player $player")
    }

    fun maryHasOnlyHearts(player: PlayerName) = when (player) {
        "Mary" -> listOf(
            SIX of HEARTS,
            TEN of HEARTS,
            ACE of HEARTS,
            TWO of HEARTS,
            EIGHT of HEARTS,
            SEVEN of HEARTS,
            QUEEN of HEARTS,
            FIVE of HEARTS,
            FOUR of HEARTS,
            NINE of HEARTS,
            THREE of HEARTS,
            KING of HEARTS,
            JACK of HEARTS
        )

        "Joe" -> listOf(
            QUEEN of CLUBS,
            NINE of CLUBS,
            FOUR of CLUBS,
            SEVEN of SPADES,
            QUEEN of SPADES,
            TWO of SPADES,
            EIGHT of DIAMONDS,
            FIVE of SPADES,
            EIGHT of SPADES,
            THREE of DIAMONDS,
            JACK of DIAMONDS,
            TEN of CLUBS,
            TEN of SPADES
        )

        "Bob" -> listOf(
            SIX of DIAMONDS,
            TWO of CLUBS,
            FIVE of CLUBS,
            QUEEN of DIAMONDS,
            SIX of SPADES,
            FOUR of DIAMONDS,
            FOUR of SPADES,
            ACE of SPADES,
            SEVEN of CLUBS,
            JACK of CLUBS,
            TWO of DIAMONDS,
            FIVE of DIAMONDS,
            ACE of DIAMONDS
        )

        "Jane" -> listOf(
            THREE of CLUBS,
            TEN of DIAMONDS,
            NINE of DIAMONDS,
            KING of DIAMONDS,
            KING of SPADES,
            JACK of SPADES,
            EIGHT of CLUBS,
            THREE of SPADES,
            KING of CLUBS,
            SIX of CLUBS,
            ACE of CLUBS,
            SEVEN of DIAMONDS,
            NINE of SPADES
        )

        else -> error("Unknown player $player")
    }

    fun maryForcedToPlayHeartsOnSecondRound(player: PlayerName) = when (player) {
        "Mary" -> listOf(
            TEN of HEARTS,
            ACE of HEARTS,
            TWO of HEARTS,
            EIGHT of HEARTS,
            SEVEN of HEARTS,
            QUEEN of HEARTS,
            FIVE of HEARTS,
            FOUR of HEARTS,
            NINE of HEARTS,
            THREE of HEARTS,
            KING of HEARTS,
            JACK of HEARTS,
            TEN of CLUBS
        )

        "Joe" -> listOf(
            QUEEN of CLUBS,
            NINE of CLUBS,
            FOUR of CLUBS,
            SEVEN of SPADES,
            QUEEN of SPADES,
            TWO of SPADES,
            EIGHT of DIAMONDS,
            FIVE of SPADES,
            EIGHT of SPADES,
            THREE of DIAMONDS,
            JACK of DIAMONDS,
            TEN of SPADES,
            SIX of HEARTS,
        )

        "Bob" -> listOf(
            SIX of DIAMONDS,
            TWO of CLUBS,
            FIVE of CLUBS,
            QUEEN of DIAMONDS,
            SIX of SPADES,
            FOUR of DIAMONDS,
            FOUR of SPADES,
            ACE of SPADES,
            SEVEN of CLUBS,
            JACK of CLUBS,
            TWO of DIAMONDS,
            FIVE of DIAMONDS,
            ACE of DIAMONDS
        )

        "Jane" -> listOf(
            THREE of CLUBS,
            TEN of DIAMONDS,
            NINE of DIAMONDS,
            KING of DIAMONDS,
            KING of SPADES,
            JACK of SPADES,
            EIGHT of CLUBS,
            THREE of SPADES,
            KING of CLUBS,
            SIX of CLUBS,
            ACE of CLUBS,
            SEVEN of DIAMONDS,
            NINE of SPADES
        )

        else -> error("Unknown player $player")
    }
}