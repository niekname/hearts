package org.socratesbe.hearts

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.socratesbe.hearts.CardsPassed.PlayerPassing
import org.socratesbe.hearts.Suit.*
import org.socratesbe.hearts.Symbol.*

class GameTest {

    @Test
    fun `each player is dealt 13 unique cards on game start`() {
        val game = Game.start(players)

        assertThat(game.events.first()).isEqualTo(GameStarted(players))
        val cardsDealt = game.events.last() as CardsDealt
        assertThatCardsAreDealt(cardsDealt)
    }

    @Test
    fun `cards are shuffled`() {
        val game = Game.start(players)
        val game2 = Game.start(players)

        val game1Cards = game.events.last() as CardsDealt
        val game2Cards = game2.events.last() as CardsDealt

        assertThat(game1Cards.player1WithCards).isNotEqualTo(game2Cards.player1WithCards)
        assertThat(game1Cards.player2WithCards).isNotEqualTo(game2Cards.player2WithCards)
        assertThat(game1Cards.player3WithCards).isNotEqualTo(game2Cards.player3WithCards)
        assertThat(game1Cards.player4WithCards).isNotEqualTo(game2Cards.player4WithCards)
    }

    // TODO parameterize?
    @Test
    fun `players cannot pass cards they don't have`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt
        )

        val throwable = catchThrowable {
            game.passCards(
                PlayerWithCards(MARY, setOf(QUEEN of HEARTS, TWO of HEARTS, EIGHT of HEARTS)),
                PlayerWithCards(JOE, setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS)),
                PlayerWithCards(BOB, setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS)),
                PlayerWithCards(JANE, setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)),
            )
        }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Mary does not have ${QUEEN of HEARTS}")
    }

    @Test
    fun `should pass cards to the left on first deal`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt
        )

        game.passCards(
            PlayerWithCards(MARY, setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS)),
            PlayerWithCards(JOE, setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS)),
            PlayerWithCards(BOB, setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS)),
            PlayerWithCards(JANE, setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)),
        )

        assertThat(game.events.filterIsInstance<CardsPassed>().first()).isEqualTo(
            CardsPassed(
                listOf(
                    PlayerPassing(from = MARY, to = JOE, QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS),
                    PlayerPassing(from = JOE, to = BOB, SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS),
                    PlayerPassing(from = BOB, to = JANE, THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS),
                    PlayerPassing(from = JANE, to = MARY, EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS),
                )
            )
        )
    }

    // TODO players should pass exactly 3 cards

    @Test
    fun `cannot pass twice`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft
        )

        val throwable = catchThrowable {
            game.passCards(
                PlayerWithCards(MARY, setOf(TEN of HEARTS, JACK of DIAMONDS, TEN of CLUBS)),
                PlayerWithCards(JOE, setOf(SEVEN of HEARTS, NINE of CLUBS, QUEEN of HEARTS)),
                PlayerWithCards(BOB, setOf(QUEEN of DIAMONDS, SIX of SPADES, FOUR of DIAMONDS)),
                PlayerWithCards(JANE, setOf(KING of DIAMONDS, KING of SPADES, THREE of HEARTS))
            )
        }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Cards have already been passed")
    }

    @Test
    fun `cannot play a card before passing has finished`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt
        )

        val throwable = catchThrowable { game.playCard(BOB, TWO of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Cannot play cards before passing has finished")
    }

    @Test
    fun `TWO of CLUBS must be the first card played in the hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft
        )

        val throwable = catchThrowable { game.playCard(BOB, SIX of DIAMONDS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("${TWO of CLUBS} must be the first card played in the hand")
    }

    @Test
    fun `player with TWO of CLUBS starts the hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft
        )

        game.playCard(BOB, TWO of CLUBS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(BOB played (TWO of CLUBS))
    }

    // TODO parameterize
    @Test
    fun `player cannot play a card they don't have in their hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            BOB played (TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(JANE, ACE of SPADES) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Jane does not have ${ACE of SPADES}")
    }

    // TODO parameterize
    @Test
    fun `player that is not to the left of the previous player cannot play next`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            BOB played (TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(MARY, TEN of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("It's not Mary's turn to play")
    }

    @Test
    fun `player that is to the left of the previous player can play next`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            BOB played (TWO of CLUBS)
        )

        game.playCard(JANE, THREE of CLUBS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(JANE played (THREE of CLUBS))
    }

    @Test
    fun `player has to follow the leading suit if they can`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            BOB played (TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(JANE, TEN of DIAMONDS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Jane must follow leading suit")
    }

    // TODO also test this after a few played cards when it is eventually no longer possible to follow suit
    @Test
    fun `player can play another card if they cannot follow leading suit`() {
        val game = Game.fromEvents(
            GameStarted(players),
            JaneHasNoClubs.cardsDealt,
            JaneHasNoClubs.cardsPassed,
            BOB played (TWO of CLUBS)
        )

        game.playCard(JANE, QUEEN of SPADES)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(JANE played (QUEEN of SPADES))
    }

    @Test
    fun `hearts cannot be played in the first trick`() {
        val game = Game.fromEvents(
            GameStarted(players),
            JaneHasNoClubs.cardsDealt,
            JaneHasNoClubs.cardsPassed,
            BOB played (TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(JANE, THREE of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("$HEARTS have not been broken")
    }

    @Test
    fun `player can play hearts in first trick when player has no other options`() {
        val game = Game.fromEvents(
            GameStarted(players),
            MaryHasOnlyHearts.cardsDealt,
            MaryHasOnlyHearts.cardsPassed,
            BOB played (TWO of CLUBS),
            JANE played (THREE of CLUBS)
        )

        game.playCard(MARY, TEN of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(MARY played (TEN of HEARTS))
    }

    @Test
    fun `player that did not win the previous trick cannot lead the new trick`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstTrickOfFirstHand.toTypedArray()
        )

        val throwable = catchThrowable { game.playCard(MARY, ACE of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("It's not Mary's turn to play")
    }

    @Test
    fun `the player that won the last trick starts the next trick`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstTrickOfFirstHand.toTypedArray()
        )

        game.playCard(JOE, NINE of CLUBS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(JOE played (NINE of CLUBS))
    }

    @Test
    fun `player can play hearts from the second trick onward if they cannot follow leading suit`() {
        val game = Game.fromEvents(
            GameStarted(players),
            JaneHasNoClubs.cardsDealt,
            JaneHasNoClubs.cardsPassed,
            *JaneHasNoClubs.firstTrickOfFirstHand.toTypedArray(),
            JOE played (NINE of CLUBS),
            BOB played (SEVEN of CLUBS),
        )

        game.playCard(JANE, KING of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(JANE played (KING of HEARTS))
    }

    @Test
    fun `player cannot play a previously played card`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstTrickOfFirstHand.toTypedArray()
        )

        val throwable = catchThrowable { game.playCard(MARY, TEN of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("${TEN of CLUBS} has already been played")
    }

    @Test
    fun `player cannot play hearts when hearts have not been broken`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstTrickOfFirstHand.toTypedArray()
        )

        val throwable = catchThrowable { game.playCard(JOE, FIVE of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("$HEARTS have not been broken")
    }

    @Test
    fun `player can open with hearts when hearts haven't been played and player has no other options`() {
        val game = Game.fromEvents(
            GameStarted(players),
            MaryForcedToPlayHeartsOnSecondTrick.cardsDealt,
            MaryForcedToPlayHeartsOnSecondTrick.cardsPassed,
            BOB played (TWO of CLUBS),
            JANE played (THREE of CLUBS),
            MARY played (TEN of CLUBS),
            JOE played (NINE of CLUBS),
        )

        game.playCard(MARY, TEN of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(MARY played (TEN of HEARTS))
    }

    @Test
    fun `player can open with hearts when hearts have been played`() {
        val game = Game.fromEvents(
            GameStarted(players),
            MaryForcedToPlayHeartsOnSecondTrick.cardsDealt,
            MaryForcedToPlayHeartsOnSecondTrick.cardsPassed,
            BOB played (TWO of CLUBS),
            JANE played (ACE of CLUBS),
            MARY played (TEN of CLUBS),
            JOE played (FOUR of CLUBS),

            JANE played (THREE of CLUBS),
            MARY played (TEN of HEARTS),
            JOE played (NINE of CLUBS),
            BOB played (FIVE of CLUBS)
        )

        game.playCard(JOE, SIX of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(JOE played (SIX of HEARTS))
    }

    @Test
    fun `cards are dealt a second time when all cards from first deal have been played`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.filterNot { it == DefaultGame.firstHand.last() }.toTypedArray()
        )

        game.playCard(JOE, FIVE of HEARTS)

        val cardsDealt = game.events.filterIsInstance<CardsDealt>()
        assertThat(cardsDealt).hasSize(2)
        assertThatCardsAreDealt(cardsDealt.last())
    }

    @Test
    fun `2nd hand - players cannot pass cards they don't have`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt
        )

        val throwable = catchThrowable {
            game.passCards(
                PlayerWithCards(MARY, setOf(QUEEN of HEARTS, TWO of HEARTS, EIGHT of HEARTS)),
                PlayerWithCards(JOE, setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS)),
                PlayerWithCards(BOB, setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS)),
                PlayerWithCards(JANE, setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)),
            )
        }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Mary does not have ${QUEEN of HEARTS}")
    }

    @Test
    fun `should pass cards to the right on second hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt
        )

        game.passCards(
            PlayerWithCards(MARY, setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS)),
            PlayerWithCards(JOE, setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS)),
            PlayerWithCards(BOB, setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS)),
            PlayerWithCards(JANE, setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)),
        )

        assertThat(game.events.filterIsInstance<CardsPassed>()).containsExactly(
            DefaultGame.cardsPassedToTheLeft,
            DefaultGame.cardsPassedToTheRight
        )
    }

    @Test
    fun `2nd hand - hearts cannot be played in the first trick`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            JaneHasNoClubs.cardsDealt,
            JaneHasNoClubs.cardsPassed,
            BOB played (TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(JANE, THREE of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("$HEARTS have not been broken")
    }

    @Test
    fun `2nd hand - player can play hearts in first trick when player has no other options`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            MaryHasOnlyHearts.cardsDealt,
            MaryHasOnlyHearts.cardsPassed,
            BOB played (TWO of CLUBS),
            JANE played (THREE of CLUBS)
        )

        game.playCard(MARY, TEN of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(MARY played (TEN of HEARTS))
    }

    @Test
    fun `should pass cards across on third hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheRight,
            *DefaultGame.secondHand.toTypedArray(),
            DefaultGame.cardsDealt
        )

        game.passCards(
            PlayerWithCards(MARY, setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS)),
            PlayerWithCards(JOE, setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS)),
            PlayerWithCards(BOB, setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS)),
            PlayerWithCards(JANE, setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)),
        )

        assertThat(game.events.filterIsInstance<CardsPassed>()).containsExactly(
            DefaultGame.cardsPassedToTheLeft,
            DefaultGame.cardsPassedToTheRight,
            DefaultGame.cardsPassedAcross,
        )
    }

    @Test
    fun `should not pass cards on fourth hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheRight,
            *DefaultGame.secondHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft, // TODO
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt
        )

        val throwable = catchThrowable {
            game.passCards(
                PlayerWithCards(MARY, setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS)),
                PlayerWithCards(JOE, setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS)),
                PlayerWithCards(BOB, setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS)),
                PlayerWithCards(JANE, setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)),
            )
        }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("No cards must be passed in this hand")
    }

    @Test
    fun `play first card in fourth hand`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheRight,
            *DefaultGame.secondHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft, // TODO
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt
        )

        game.playCard(JOE, TWO of CLUBS)

        assertThat(game.events.last()).isEqualTo(JOE played (TWO of CLUBS))
    }

    @Test
    fun `play a full second round`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheRight
        )

        assertThatNoException().isThrownBy {
            DefaultGame.secondHand.forEach {
                game.playCard(it.player, it.card)
            }
        }

        assertThat(game.events).containsAll(DefaultGame.secondHand)
    }

    @Test
    fun `game ends when a score of 100 or higher is reached`() {
        val game = Game.fromEvents(
            GameStarted(players),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.toTypedArray(),
            DefaultGame.cardsDealt,
            DefaultGame.cardsPassedToTheLeft,
            *DefaultGame.firstHand.filterNot { it == DefaultGame.firstHand.last() }.toTypedArray()
        )

        game.playCard(JOE, FIVE of HEARTS)

        assertThat(game.events.last())
            .isEqualTo(JOE played (FIVE of HEARTS))

        // TODO add proper exceptions on passCards / playCard that game has come to an end
    }

    // TODO add full game test

    private fun assertThatCardsAreDealt(cardsDealt: CardsDealt) {
        assertThat(cardsDealt.players).isEqualTo(players)
        assertThat(cardsDealt.allCards).containsAll(Deck().cards)

        assertThat(cardsDealt.player1WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player2WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player3WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player4WithCards.cards).hasSize(13)
    }
}