package org.socratesbe.hearts2

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.socratesbe.hearts2.Suit.*
import org.socratesbe.hearts2.Symbol.*

class GameTest {

    @Test
    fun `should start game`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.start(players)
        assertThat(game.events.first()).isEqualTo(GameStarted)
    }

    @Test
    fun `each player is dealt 13 unique cards on game start`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.start(players)

        val cardsDealt = game.events[1] as CardsDealt

        assertThat(cardsDealt.players).isEqualTo(players)
        assertThat(cardsDealt.allCards).containsAll(Deck().cards)

        assertThat(cardsDealt.player1WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player2WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player3WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player4WithCards.cards).hasSize(13)
    }

    // TODO add test to make sure cards are shuffled

    @Test
    fun `player who is not on turn cannot play a card`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.start(players)

        val throwable = catchThrowable { game.playCard(Player("Joe"), KING of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("It's not Joe's turn to play")
    }

    companion object {
        private val maryCards = PlayerWithCards(
            Player("Mary"), setOf(
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
        )
        private val joeCards = PlayerWithCards(
            Player("Joe"), setOf(
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
        )
        private val bobCards = PlayerWithCards(
            Player("Bob"), setOf(
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
        )
        private val janeCards = PlayerWithCards(
            Player("Jane"), setOf(
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
        )
    }
}