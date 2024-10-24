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
            defaultCards,
            CardsPassed
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
            defaultCards,
            CardsPassed
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
            defaultCards,
            CardsPassed,
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(Player("Jane"), TWO of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Jane does not have ${TWO of CLUBS}")
    }

    @Test
    fun `player that is not to the left of the previous player cannot play next`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            defaultCards,
            CardsPassed,
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
            defaultCards,
            CardsPassed,
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
            defaultCards,
            CardsPassed,
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
            janeHasNoClubs,
            CardsPassed,
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
            janeHasNoClubs,
            CardsPassed,
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        val throwable = catchThrowable { game.playCard(Player("Jane"), THREE of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("$HEARTS have not been broken")
    }

    @Test
    fun `player can play hearts in first round when player has no other options`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            maryHasOnlyHearts,
            CardsPassed,
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
            defaultCards,
            CardsPassed,
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
            defaultCards,
            CardsPassed,
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

    @Test
    fun `player cannot play hearts when hearts have not been broken`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            defaultCards,
            CardsPassed,
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        game.playCard(Player("Jane"), THREE of CLUBS)
        game.playCard(Player("Mary"), TEN of CLUBS)
        game.playCard(Player("Joe"), NINE of CLUBS)

        val throwable = catchThrowable { game.playCard(Player("Mary"), SIX of HEARTS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("$HEARTS have not been broken")
    }

    @Test
    fun `player can open with hearts when hearts haven't been played and player has no other options`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            maryForcedToPlayHeartsOnSecondRound,
            CardsPassed,
            CardPlayed(Player("Bob"), TWO of CLUBS)
        )

        game.playCard(Player("Jane"), THREE of CLUBS)
        game.playCard(Player("Mary"), TEN of CLUBS)
        game.playCard(Player("Joe"), NINE of CLUBS)

        game.playCard(Player("Mary"), TEN of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Mary"), TEN of HEARTS))
    }

    @Test
    fun `player can open with hearts when hearts have been played`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            maryForcedToPlayHeartsOnSecondRound,
            CardsPassed,
            CardPlayed(Player("Bob"), TWO of CLUBS),
            CardPlayed(Player("Jane"), ACE of CLUBS),
            CardPlayed(Player("Mary"), TEN of CLUBS),
            CardPlayed(Player("Joe"), FOUR of CLUBS),

            CardPlayed(Player("Jane"), THREE of CLUBS),
            CardPlayed(Player("Mary"), TEN of HEARTS),
            CardPlayed(Player("Joe"), NINE of CLUBS),
            CardPlayed(Player("Bob"), FIVE of CLUBS)
        )

        game.playCard(Player("Joe"), SIX of HEARTS)

        val cardPlayed = game.events.last() as CardPlayed
        assertThat(cardPlayed).isEqualTo(CardPlayed(Player("Joe"), SIX of HEARTS))
    }

    @Test
    fun `cannot play a card before passing has finished`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            defaultCards
        )

        val throwable = catchThrowable { game.playCard(Player("Bob"), TWO of CLUBS) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Cannot play cards before passing has finished")
    }

    // TODO parameterize
    @Test
    fun `players cannot pass cards they don't have`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            defaultCards
        )

        val player1pass = PlayerWithCards(Player("Mary"), setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS))
        val player2pass = PlayerWithCards(Player("Joe"), setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS))
        val player3pass = PlayerWithCards(Player("Bob"), setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS))
        val player4pass = PlayerWithCards(Player("Jane"), setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS))
        val throwable = catchThrowable { game.passCards(player1pass, player2pass, player3pass, player4pass) }

        assertThat(throwable)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Mary does not have ${QUEEN of CLUBS}")
    }

    @Test
    fun `should pass cards`() {
        val players = Players(Player("Mary"), Player("Joe"), Player("Bob"), Player("Jane"))
        val game = Game.fromEvents(
            GameStarted(players),
            defaultCards
        )

        val player1pass = PlayerWithCards(Player("Mary"), setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS))
        val player2pass = PlayerWithCards(Player("Joe"), setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS))
        val player3pass = PlayerWithCards(Player("Bob"), setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS))
        val player4pass = PlayerWithCards(Player("Jane"), setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS))
        game.passCards(player1pass, player2pass, player3pass, player4pass)

        assertThat(game.cardsInHandOf(Player("Mary"))).isEqualTo(defaultCards.player1WithCards.cards - player1pass.cards + player4pass.cards)
        assertThat(game.cardsInHandOf(Player("Joe"))).isEqualTo(defaultCards.player2WithCards.cards - player2pass.cards + player3pass.cards)
        assertThat(game.cardsInHandOf(Player("Bob"))).isEqualTo(defaultCards.player3WithCards.cards - player3pass.cards + player4pass.cards)
        assertThat(game.cardsInHandOf(Player("Jane"))).isEqualTo(defaultCards.player4WithCards.cards - player4pass.cards + player1pass.cards)
    }

    // rules: https://cardgames.io/hearts/#rules

    companion object {
        private val defaultCards = CardsDealt(
            player1WithCards = PlayerWithCards(
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
            ), player2WithCards = PlayerWithCards(
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
            ), player3WithCards = PlayerWithCards(
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
            ), player4WithCards = PlayerWithCards(
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
        )

        private val janeHasNoClubs = CardsDealt(
            player1WithCards = PlayerWithCards(
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
            ),
            player2WithCards = PlayerWithCards(
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
            ),
            player3WithCards = PlayerWithCards(
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
            ),
            player4WithCards = PlayerWithCards(
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
        )

        private val maryHasOnlyHearts = CardsDealt(
            player1WithCards = PlayerWithCards(
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
            ),
            player2WithCards = PlayerWithCards(
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
            ),
            player3WithCards = PlayerWithCards(
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
            ),
            player4WithCards = PlayerWithCards(
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
        )

        private val maryForcedToPlayHeartsOnSecondRound = CardsDealt(
            player1WithCards = PlayerWithCards(
                Player("Mary"), setOf(
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
            ),
            player2WithCards = PlayerWithCards(
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
                    TEN of SPADES,
                    SIX of HEARTS,
                )
            ),
            player3WithCards = PlayerWithCards(
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
            ),
            player4WithCards = PlayerWithCards(
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
        )
    }
}