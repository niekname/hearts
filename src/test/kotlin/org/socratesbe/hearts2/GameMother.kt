package org.socratesbe.hearts2

import org.socratesbe.hearts2.CardsPassed.PlayerPassing
import org.socratesbe.hearts2.Suit.*
import org.socratesbe.hearts2.Symbol.*

val MARY = Player("Mary")
val JOE = Player("Joe")
val BOB = Player("Bob")
val JANE = Player("Jane")
val players = Players(MARY, JOE, BOB, JANE)

object DefaultGame {
    val cardsDealt = CardsDealt(
        player1WithCards = PlayerWithCards(
            MARY, setOf(
                QUEEN of CLUBS,
                TWO of HEARTS,
                EIGHT of HEARTS,
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
        ), player2WithCards = PlayerWithCards(
            JOE, setOf(
                SIX of DIAMONDS,
                TWO of CLUBS,
                FIVE of CLUBS,
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
        ), player3WithCards = PlayerWithCards(
            BOB, setOf(
                THREE of CLUBS,
                TEN of DIAMONDS,
                NINE of DIAMONDS,
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
        ), player4WithCards = PlayerWithCards(
            JANE, setOf(
                EIGHT of SPADES,
                THREE of DIAMONDS,
                SIX of HEARTS,
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
        )
    )

    val cardsPassed = CardsPassed(
        listOf(
            PlayerPassing(MARY, JOE, QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS),
            PlayerPassing(JOE, BOB, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
            PlayerPassing(BOB, JANE, THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS),
            PlayerPassing(JANE, MARY, EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS),
        )
    )

    val firstTrickOfFirstHand = listOf(
        CardPlayed(BOB, TWO of CLUBS),
        CardPlayed(JANE, THREE of CLUBS),
        CardPlayed(MARY, TEN of CLUBS),
        CardPlayed(JOE, QUEEN of CLUBS),
    )

    val firstHand = listOf(
        *firstTrickOfFirstHand.toTypedArray(),

        CardPlayed(JOE, NINE of CLUBS),
        CardPlayed(BOB, FIVE of CLUBS),
        CardPlayed(JANE, EIGHT of CLUBS),
        CardPlayed(MARY, ACE of CLUBS),

        CardPlayed(MARY, EIGHT of SPADES),
        CardPlayed(JOE, SEVEN of SPADES),
        CardPlayed(BOB, SIX of SPADES),
        CardPlayed(JANE, KING of SPADES),

        CardPlayed(JANE, TEN of DIAMONDS),
        CardPlayed(MARY, THREE of DIAMONDS),
        CardPlayed(JOE, EIGHT of DIAMONDS),
        CardPlayed(BOB, SIX of DIAMONDS),

        CardPlayed(JANE, NINE of DIAMONDS),
        CardPlayed(MARY, JACK of DIAMONDS),
        CardPlayed(JOE, FOUR of CLUBS),
        CardPlayed(BOB, QUEEN of DIAMONDS),

        CardPlayed(BOB, FOUR of DIAMONDS),
        CardPlayed(JANE, KING of DIAMONDS),
        CardPlayed(MARY, FIVE of DIAMONDS),
        CardPlayed(JOE, QUEEN of SPADES),

        CardPlayed(JANE, JACK of SPADES),
        CardPlayed(MARY, TEN of SPADES),
        CardPlayed(JOE, TWO of SPADES),
        CardPlayed(BOB, FOUR of SPADES),

        CardPlayed(JANE, THREE of SPADES),
        CardPlayed(MARY, NINE of SPADES),
        CardPlayed(JOE, FIVE of SPADES),
        CardPlayed(BOB, ACE of SPADES),

        CardPlayed(BOB, SEVEN of CLUBS),
        CardPlayed(JANE, KING of CLUBS),
        CardPlayed(MARY, ACE of DIAMONDS),
        CardPlayed(JOE, TWO of HEARTS),

        CardPlayed(JANE, THREE of HEARTS),
        CardPlayed(MARY, SIX of HEARTS),
        CardPlayed(JOE, EIGHT of HEARTS),
        CardPlayed(BOB, FOUR of HEARTS),

        CardPlayed(JOE, SEVEN of HEARTS),
        CardPlayed(BOB, NINE of HEARTS),
        CardPlayed(JANE, KING of HEARTS),
        CardPlayed(MARY, TEN of HEARTS),

        CardPlayed(JANE, SIX of CLUBS),
        CardPlayed(MARY, SEVEN of DIAMONDS),
        CardPlayed(JOE, QUEEN of HEARTS),
        CardPlayed(BOB, JACK of CLUBS),

        CardPlayed(BOB, TWO of DIAMONDS),
        CardPlayed(JANE, JACK of HEARTS),
        CardPlayed(MARY, ACE of HEARTS),
        CardPlayed(JOE, FIVE of HEARTS)
    )
}

object JaneHasNoClubs {
    val cardsDealt = CardsDealt(
        player1WithCards = PlayerWithCards(
            MARY, setOf(
                QUEEN of CLUBS,
                TWO of HEARTS,
                EIGHT of HEARTS,
                TEN of HEARTS,
                TEN of CLUBS,
                TEN of SPADES,
                SIX of CLUBS,
                FIVE of DIAMONDS,
                ACE of DIAMONDS,
                ACE of CLUBS,
                SEVEN of DIAMONDS,
                NINE of SPADES,
                ACE of HEARTS
            )
        ),
        player2WithCards = PlayerWithCards(
            JOE, setOf(
                SIX of DIAMONDS,
                TWO of CLUBS,
                FIVE of CLUBS,
                SEVEN of HEARTS,
                NINE of CLUBS,
                QUEEN of HEARTS,
                FOUR of CLUBS,
                SEVEN of SPADES,
                FIVE of HEARTS,
                EIGHT of CLUBS,
                TWO of SPADES,
                EIGHT of DIAMONDS,
                FIVE of SPADES
            )
        ),
        player3WithCards = PlayerWithCards(
            BOB, setOf(
                EIGHT of SPADES,
                TEN of DIAMONDS,
                NINE of DIAMONDS,
                QUEEN of DIAMONDS,
                SIX of SPADES,
                FOUR of DIAMONDS,
                FOUR of SPADES,
                FOUR of HEARTS,
                KING of CLUBS,
                NINE of HEARTS,
                SEVEN of CLUBS,
                JACK of CLUBS,
                TWO of DIAMONDS
            )
        ),
        player4WithCards = PlayerWithCards(
            JANE, setOf(
                THREE of CLUBS,
                THREE of DIAMONDS,
                SIX of HEARTS,
                KING of DIAMONDS,
                KING of SPADES,
                THREE of HEARTS,
                JACK of SPADES,
                QUEEN of SPADES,
                THREE of SPADES,
                ACE of SPADES,
                KING of HEARTS,
                JACK of DIAMONDS,
                JACK of HEARTS
            )
        )
    )

    val cardsPassed = CardsPassed(
        listOf(
            PlayerPassing(MARY, JOE, QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS),
            PlayerPassing(JOE, BOB, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
            PlayerPassing(BOB, JANE, EIGHT of SPADES, TEN of DIAMONDS, NINE of DIAMONDS),
            PlayerPassing(JANE, MARY, THREE of CLUBS, THREE of DIAMONDS, SIX of HEARTS),
        )
    )
}

object MaryHasOnlyHearts {
    val cardsDealt = CardsDealt(
        player1WithCards = PlayerWithCards(
            MARY, setOf(
                QUEEN of CLUBS,
                NINE of CLUBS,
                FOUR of CLUBS,
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
        ),
        player2WithCards = PlayerWithCards(
            JOE, setOf(
                SIX of DIAMONDS,
                TWO of CLUBS,
                FIVE of CLUBS,
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
        ),
        player3WithCards = PlayerWithCards(
            BOB, setOf(
                THREE of CLUBS,
                TEN of DIAMONDS,
                NINE of DIAMONDS,
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
        ),
        player4WithCards = PlayerWithCards(
            JANE, setOf(
                SIX of HEARTS,
                TEN of HEARTS,
                ACE of HEARTS,
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
        )
    )

    val cardsPassed = CardsPassed(
        listOf(
            PlayerPassing(MARY, JOE, QUEEN of CLUBS, NINE of CLUBS, FOUR of CLUBS),
            PlayerPassing(JOE, BOB, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
            PlayerPassing(BOB, JANE, THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS),
            PlayerPassing(JANE, MARY, SIX of HEARTS, TEN of HEARTS, ACE of HEARTS),
        )
    )
}

object MaryForcedToPlayHeartsOnSecondTrick {
    val cardsDealt = CardsDealt(
        player1WithCards = PlayerWithCards(
            MARY, setOf(
                QUEEN of CLUBS,
                NINE of CLUBS,
                FOUR of CLUBS,
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
        ),
        player2WithCards = PlayerWithCards(
            JOE, setOf(
                SIX of DIAMONDS,
                TWO of CLUBS,
                FIVE of CLUBS,
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
        ),
        player3WithCards = PlayerWithCards(
            BOB, setOf(
                THREE of CLUBS,
                TEN of DIAMONDS,
                NINE of DIAMONDS,
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
        ),
        player4WithCards = PlayerWithCards(
            JANE, setOf(
                TEN of HEARTS,
                ACE of HEARTS,
                TWO of HEARTS,
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
        )
    )

    val cardsPassed = CardsPassed(
        listOf(
            PlayerPassing(MARY, JOE, QUEEN of CLUBS, NINE of CLUBS, FOUR of CLUBS),
            PlayerPassing(JOE, BOB, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
            PlayerPassing(BOB, JANE, THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS),
            PlayerPassing(JANE, MARY, TEN of HEARTS, ACE of HEARTS, TWO of HEARTS),
        )
    )
}