package org.socratesbe.hearts2

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.socratesbe.hearts2.Suit.*
import org.socratesbe.hearts2.Symbol.*

class GameTest {

    @Test
    fun `each player is dealt 13 unique cards on game start`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.start(players)

        assertThat(game.events.first()).isEqualTo(GameStarted(players))
        val cardsDealt = game.events.last() as CardsDealt

        assertThat(cardsDealt.players).isEqualTo(players)
        assertThat(cardsDealt.allCards).containsAll(Deck().cards)

        assertThat(cardsDealt.player1WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player2WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player3WithCards.cards).hasSize(13)
        assertThat(cardsDealt.player4WithCards.cards).hasSize(13)
    }

    @Test
    fun `cards are shuffled`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.start(players)
        val game2 = Game.start(players)

        val game1Cards = game.events.last() as CardsDealt
        val game2Cards = game2.events.last() as CardsDealt

        assertThat(game1Cards.player1WithCards).isNotEqualTo(game2Cards.player1WithCards)
        assertThat(game1Cards.player2WithCards).isNotEqualTo(game2Cards.player2WithCards)
        assertThat(game1Cards.player3WithCards).isNotEqualTo(game2Cards.player3WithCards)
        assertThat(game1Cards.player4WithCards).isNotEqualTo(game2Cards.player4WithCards)
    }

    @Test
    fun `player with TWO of CLUBS starts the round`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards)
        )

        game.playCard(Player("Bob"), TWO of CLUBS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Bob"), TWO of CLUBS))
    }

    @Test
    fun `TWO of CLUBS must be the first card played in the round`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards)
        )

        val throwable = catchThrowable { game.playCard(Player("Bob"), SIX of DIAMONDS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("${TWO of CLUBS} must be the first card played in the round")
    }

    @Test
    fun `player cannot play a card they don't have in their hand`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(Player("Jane"), TWO of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Jane does not have ${TWO of CLUBS} in their hand")
    }

    @Test
    fun `player that is not to the left of the previous player cannot play next`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(Player("Mary"), TEN of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("It's not Mary's turn to play")
    }

    @Test
    fun `player that is to the left of the previous player can play next`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        game.playCard(Player("Jane"), THREE of CLUBS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Jane"), THREE of CLUBS))
    }

    // TODO test player cannot play a previously played card

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