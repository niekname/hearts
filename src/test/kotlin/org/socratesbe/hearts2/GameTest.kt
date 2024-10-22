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
    fun `player with TWO of CLUBS starts the hand`() {
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
    fun `TWO of CLUBS must be the first card played in the hand`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards)
        )

        val throwable = catchThrowable { game.playCard(Player("Bob"), SIX of DIAMONDS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("${TWO of CLUBS} must be the first card played in the hand")
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

    @Test
    fun `player has to follow the leading suit if they can`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(Player("Jane"), TEN of DIAMONDS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Jane must follow leading suit")
    }

    // TODO also test this after a few played cards when it is eventually no longer possible to follow suit
    @Test
    fun `player can play another card if they cannot follow leading suit`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCardsNoClubsJane, joeCardsNoClubsJane, bobCardsNoClubsJane, janeCardsNoClubsJane),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        game.playCard(Player("Jane"), QUEEN of SPADES)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Jane"), QUEEN of SPADES))
    }

    @Test
    fun `hearts cannot be played in the first trick`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCardsNoClubsJane, joeCardsNoClubsJane, bobCardsNoClubsJane, janeCardsNoClubsJane),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(Player("Jane"), THREE of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Cannot play hearts on the first trick")
    }

    @Test
    fun `player can play hearts in first round when player has no other options`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(
                maryCardsOnlyHeartsMary,
                joeCardsOnlyHeartsMary,
                bobCardsOnlyHeartsMary,
                janeCardsOnlyHeartsMary
            ),
            CardPlayed(Player("Bob"), TWO of CLUBS),
            CardPlayed(Player("Jane"), THREE of CLUBS)
        )

        game.playCard(Player("Mary"), TEN of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Mary"), TEN of HEARTS))
    }

    @Test
    fun `the player that won the last trick starts the next trick`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        game.playCard(Player("Jane"), THREE of CLUBS)
        game.playCard(Player("Mary"), TEN of CLUBS)
        game.playCard(Player("Joe"), NINE of CLUBS)

        game.playCard(Player("Mary"), EIGHT of SPADES)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Mary"), EIGHT of SPADES))
    }

    @Test
    fun `player cannot play a previously played card`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            CardsDealt(maryCards, joeCards, bobCards, janeCards),
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        game.playCard(Player("Jane"), THREE of CLUBS)
        game.playCard(Player("Mary"), TEN of CLUBS)
        game.playCard(Player("Joe"), NINE of CLUBS)

        val throwable = catchThrowable { game.playCard(Player("Mary"), TEN of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("${TEN of CLUBS} has already been played")
    }

    // TODO test case for if a player gets dealt only hearts > https://boardgames.stackexchange.com/questions/38220/in-hearts-impossible-to-play-first-hand
    // rules: https://cardgames.io/hearts/#rules

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

        private val maryCardsNoClubsJane = PlayerWithCards(
            Player("Mary"), setOf(
                THREE of CLUBS,
                THREE of DIAMONDS,
                SIX of HEARTS,
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
        )
        private val joeCardsNoClubsJane = PlayerWithCards(
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
                EIGHT of CLUBS,
                TWO of SPADES,
                EIGHT of DIAMONDS,
                FIVE of SPADES
            )
        )
        private val bobCardsNoClubsJane = PlayerWithCards(
            Player("Bob"), setOf(
                SIX of DIAMONDS,
                TWO of CLUBS,
                FIVE of CLUBS,
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
        )
        private val janeCardsNoClubsJane = PlayerWithCards(
            Player("Jane"), setOf(
                EIGHT of SPADES,
                TEN of DIAMONDS,
                NINE of DIAMONDS,
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

        private val maryCardsOnlyHeartsMary = PlayerWithCards(
            Player("Mary"), setOf(
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
        )
        private val joeCardsOnlyHeartsMary = PlayerWithCards(
            Player("Joe"), setOf(
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
        )
        private val bobCardsOnlyHeartsMary = PlayerWithCards(
            Player("Bob"), setOf(
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
        )
        private val janeCardsOnlyHeartsMary = PlayerWithCards(
            Player("Jane"), setOf(
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
        )
    }
}