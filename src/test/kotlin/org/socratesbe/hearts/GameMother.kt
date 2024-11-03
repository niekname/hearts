package org.socratesbe.hearts

import org.socratesbe.hearts.CardsPassed.PlayerPassing
import org.socratesbe.hearts.Suit.*
import org.socratesbe.hearts.Symbol.*

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

    val cardsPassedHand1 = CardsPassed(
        listOf(
            PlayerPassing(MARY, JOE, QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS),
            PlayerPassing(JOE, BOB, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
            PlayerPassing(BOB, JANE, THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS),
            PlayerPassing(JANE, MARY, EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS),
        )
    )

    val cardsPassedHand2 = CardsPassed(
        listOf(
            PlayerPassing(from = MARY, to = JANE, QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS),
            PlayerPassing(from = JOE, to = MARY, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
            PlayerPassing(from = BOB, to = JOE, THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS),
            PlayerPassing(from = JANE, to = BOB, EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS),
        )
    )

    val firstTrickOfFirstHand = listOf(
        BOB played (TWO of CLUBS),
        JANE played (THREE of CLUBS),
        MARY played (TEN of CLUBS),
        JOE played (QUEEN of CLUBS),
    )

    val firstHand = listOf(
        *firstTrickOfFirstHand.toTypedArray(),

        JOE played (NINE of CLUBS),
        BOB played (FIVE of CLUBS),
        JANE played (EIGHT of CLUBS),
        MARY played (ACE of CLUBS),

        MARY played (EIGHT of SPADES),
        JOE played (SEVEN of SPADES),
        BOB played (SIX of SPADES),
        JANE played (KING of SPADES),

        JANE played (TEN of DIAMONDS),
        MARY played (THREE of DIAMONDS),
        JOE played (EIGHT of DIAMONDS),
        BOB played (SIX of DIAMONDS),

        JANE played (NINE of DIAMONDS),
        MARY played (JACK of DIAMONDS),
        JOE played (FOUR of CLUBS),
        BOB played (QUEEN of DIAMONDS),

        BOB played (FOUR of DIAMONDS),
        JANE played (KING of DIAMONDS),
        MARY played (FIVE of DIAMONDS),
        JOE played (QUEEN of SPADES),

        JANE played (JACK of SPADES),
        MARY played (TEN of SPADES),
        JOE played (TWO of SPADES),
        BOB played (FOUR of SPADES),

        JANE played (THREE of SPADES),
        MARY played (NINE of SPADES),
        JOE played (FIVE of SPADES),
        BOB played (ACE of SPADES),

        BOB played (SEVEN of CLUBS),
        JANE played (KING of CLUBS),
        MARY played (ACE of DIAMONDS),
        JOE played (TWO of HEARTS),

        JANE played (THREE of HEARTS),
        MARY played (SIX of HEARTS),
        JOE played (EIGHT of HEARTS),
        BOB played (FOUR of HEARTS),

        JOE played (SEVEN of HEARTS),
        BOB played (NINE of HEARTS),
        JANE played (KING of HEARTS),
        MARY played (TEN of HEARTS),

        JANE played (SIX of CLUBS),
        MARY played (SEVEN of DIAMONDS),
        JOE played (QUEEN of HEARTS),
        BOB played (JACK of CLUBS),

        BOB played (TWO of DIAMONDS),
        JANE played (JACK of HEARTS),
        MARY played (ACE of HEARTS),
        JOE played (FIVE of HEARTS)
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

    val firstTrickOfFirstHand = listOf(
        BOB played (TWO of CLUBS),
        JANE played (KING of DIAMONDS),
        MARY played (TEN of CLUBS),
        JOE played (QUEEN of CLUBS),
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