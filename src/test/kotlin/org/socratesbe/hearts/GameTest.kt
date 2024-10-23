package org.socratesbe.hearts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.Arguments
import org.socratesbe.hearts.DealMother.dealCardsSoJoeHas2ofClubs
import org.socratesbe.hearts.DealMother.dealFixedCards
import org.socratesbe.hearts.DealMother.maryForcedToPlayHeartsOnSecondRound
import org.socratesbe.hearts.DealMother.maryHasNoClubs
import org.socratesbe.hearts.DealMother.maryHasOnlyHearts
import org.socratesbe.hearts.application.api.command.*
import org.socratesbe.hearts.application.api.query.*
import org.socratesbe.hearts.domain.*
import org.socratesbe.hearts.domain.Suit.*
import org.socratesbe.hearts.domain.Symbol.*
import java.util.stream.Stream

class GameTest {

    private var context = Context(Game(DefaultFixedDealer()))

    class DefaultFixedDealer : Dealer {
        override fun dealCardsFor(players: List<Player>): List<PlayerWithCards> {
            return players.map { PlayerWithCards(it, dealFixedCards(it.name)) }
        }
    }

    class JoeStartsFixedDealer : Dealer {
        override fun dealCardsFor(players: List<Player>) =
            players.map { PlayerWithCards(it, dealCardsSoJoeHas2ofClubs(it.name)) }
    }


    @Test
    fun `game can start when exactly four players have joined`() {
        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")

        val result = startGame()

        assertThat(result).isEqualTo(GameHasStarted)
        assertThat(gameHasStarted()).isTrue()
    }

    @Test
    fun `game cannot start when less than four players have joined`() {
        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")

        val result = startGame()

        assertThat(result).isEqualTo(GameHasNotStarted("Not enough players joined"))
        assertThat(gameHasStarted()).isFalse()
    }

    @Test
    fun `no more than four players can join the game`() {
        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")

        val result = joinGame("Sue")

        assertThat(result).isEqualTo(
            PlayerCouldNotJoin(
                player = "Sue",
                reason = "Game already has four players",
            )
        )
    }

    @Test
    fun `each player is dealt 13 unique cards on game start`() {
        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")

        startGame()

        assertThat(cardsInHandOf("Mary").size).isEqualTo(13)
        assertThat(cardsInHandOf("Joe").size).isEqualTo(13)
        assertThat(cardsInHandOf("Bob").size).isEqualTo(13)
        assertThat(cardsInHandOf("Jane").size).isEqualTo(13)

        val uniqueCards = cardsInHandOf("Mary").toSet() + cardsInHandOf("Joe").toSet() +
                cardsInHandOf("Bob").toSet() + cardsInHandOf("Jane").toSet()
        assertThat(uniqueCards.size).isEqualTo(52)
    }

    @Test
    fun `player with 2 of clubs gets the first turn`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")

        startGame()

        assertThat(whoseTurnIsIt()).isEqualTo("Bob")
    }

    @Test
    fun `player with 2 of clubs gets the first turn - Joe`() {
        context = Context(Game(JoeStartsFixedDealer()))

        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")

        startGame()

        assertThat(whoseTurnIsIt()).isEqualTo("Joe")
    }

    @Test
    fun `player who is not on turn cannot play a card`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = playCard("Joe", KING of HEARTS)

        assertThat(result).isEqualTo(CouldNotPlayCard("It's not Joe's turn to play"))
    }

    @Test
    fun `player cannot play a card they don't have in their hand`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = playCard("Bob", KING of HEARTS)

        assertThat(result).isEqualTo(CouldNotPlayCard("Bob does not have K♥️ in their hand"))
    }

    @Test
    fun `first player cannot play card different than two of clubs on first turn`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = playCard("Bob", SIX of DIAMONDS)

        assertThat(result).isEqualTo(CouldNotPlayCard("Bob must play 2♣️ on the first turn"))
    }

    @Test
    fun `player that is not to the left of the previous player cannot play next`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playCard("Bob", TWO of CLUBS)

        val result = playCard("Mary", TWO of HEARTS)

        assertThat(result).isEqualTo(CouldNotPlayCard("It's not Mary's turn to play"))
    }

    @Test
    fun `player that is to the left of the previous player can play next`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playCard("Bob", TWO of CLUBS)

        val result = playCard("Jane", THREE of CLUBS)

        assertThat(result).isEqualTo(PlayedCard)
    }

    @Disabled
    @Test
    fun `the player that won the last trick starts the next trick`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playRound {
            assertThat(playCard("Bob", TWO of CLUBS)).isEqualTo(PlayedCard)
            assertThat(playCard("Jane", THREE of CLUBS)).isEqualTo(PlayedCard)
            assertThat(playCard("Mary", TEN of CLUBS)).isEqualTo(PlayedCard)
            assertThat(playCard("Joe", NINE of CLUBS)).isEqualTo(PlayedCard)
        }

        val result = playCard("Mary", EIGHT of SPADES)

        assertThat(result).isEqualTo(PlayedCard)
    }

    @Disabled
    @Test
    fun `player has to follow suit if they can`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playCard("Bob", TWO of CLUBS)

        val result = playCard("Jane", TEN of DIAMONDS)

        assertThat(result).isEqualTo(CouldNotPlayCard("Jane must follow suit"))
    }

    @Disabled
    @Test
    fun `player cannot play hearts in first round when player has other options`() {
        onDeal(::maryHasNoClubs)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playCard("Bob", TWO of CLUBS)
        playCard("Jane", THREE of CLUBS)

        val result = playCard("Mary", TEN of HEARTS)

        assertThat(result).isEqualTo(CouldNotPlayCard("Mary cannot play ♥️ on the first trick"))
    }

    @Disabled
    @Test
    fun `player can play hearts in first round when player has no other options`() {
        onDeal(::maryHasOnlyHearts)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playCard("Bob", TWO of CLUBS)
        playCard("Jane", THREE of CLUBS)

        val result = playCard("Mary", TEN of HEARTS)

        assertThat(result).isEqualTo(PlayedCard)
    }

    @Disabled
    @Test
    fun `player cannot open with hearts when hearts haven't been played and player has other options`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playRound {
            playCard("Bob", TWO of CLUBS)
            playCard("Jane", THREE of CLUBS)
            playCard("Mary", TEN of CLUBS)
            playCard("Joe", NINE of CLUBS)
        }

        val result = playCard("Mary", SIX of HEARTS)

        assertThat(result).isEqualTo(CouldNotPlayCard("Mary cannot open with ♥️ until first ♥️ has been played"))
    }

    @Disabled
    @Test
    fun `player can open with hearts when hearts haven't been played and player has no other options`() {
        onDeal(::maryForcedToPlayHeartsOnSecondRound)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playRound {
            playCard("Bob", TWO of CLUBS)
            playCard("Jane", THREE of CLUBS)
            playCard("Mary", TEN of CLUBS)
            playCard("Joe", NINE of CLUBS)
        }

        val result = playCard("Mary", TEN of HEARTS)

        assertThat(result).isEqualTo(PlayedCard)
    }

    @Disabled
    @Test
    fun `player can open with hearts when hearts have been played`() {
        onDeal(::maryForcedToPlayHeartsOnSecondRound)
        setPassingRuleTo(NoPassing)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playRound {
            playCard("Bob", TWO of CLUBS)
            playCard("Jane", ACE of CLUBS)
            playCard("Mary", TEN of CLUBS)
            playCard("Joe", FOUR of CLUBS)
        }
        playRound {
            playCard("Jane", THREE of CLUBS)
            playCard("Mary", TEN of HEARTS)
            playCard("Joe", NINE of CLUBS)
            playCard("Bob", FIVE of CLUBS)
        }

        val result = playCard("Joe", SIX of HEARTS)

        assertThat(result).isEqualTo(PlayedCard)
    }

    @Disabled
    @Test
    // HERE
    fun `player cannot play a card before passing has finished`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(AlwaysPassLeft)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = playCard("Bob", TWO of CLUBS)

        assertThat(result).isEqualTo(CouldNotPlayCard("It's not time to play cards yet"))
    }

    @Disabled
    @Test
    fun `player cannot pass cards they don't have`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(AlwaysPassLeft)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of SPADES))

        assertThat(result).isEqualTo(CouldNotPassCards("Bob does not have 5♠️"))
    }

    @Disabled
    @Test
    fun `player cannot pass more than 3 cards`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(AlwaysPassLeft)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS, QUEEN of DIAMONDS))

        assertThat(result).isEqualTo(CouldNotPassCards("Bob needs to pass exactly three cards"))
    }

    @Disabled
    @Test
    fun `player cannot pass less than 3 cards`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(AlwaysPassLeft)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        val result = passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS))

        assertThat(result).isEqualTo(CouldNotPassCards("Bob needs to pass exactly three cards"))
    }

    @Disabled
    @Test
    fun `player cannot pass twice during same deal`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(AlwaysPassLeft)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS))
        val result = passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, QUEEN of DIAMONDS))

        assertThat(result).isEqualTo(CouldNotPassCards("Bob already passed cards during this deal"))
    }

    @Disabled
    @Test
    fun `cards are not received until everyone has passed cards`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(AlwaysPassLeft)

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        passCards("Mary", setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS))
        passCards("Joe", setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS))
        passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS))

        assertThat(cardsInHandOf("Joe")).hasSize(10)

        passCards("Jane", setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS))

        assertThat(cardsInHandOf("Joe"))
            .hasSize(13)
            .contains(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)
    }

    @Disabled
    @Test
    fun `cannot pass cards when passing hasn't begun yet`() {
        joinGame("Mary")
        joinGame("Joe")

        val result = passCards("Mary", setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS))

        assertThat(result).isEqualTo(CouldNotPassCards("Now is not the time to be passing cards"))
    }

    @Disabled
    @Test
    fun `cards are dealt a second time when all cards from first deal have been played`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)
        val cardPlaysInFirstDeal = readCardPlaysFromResource("/fixed_card_plays_no_passing.txt")

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        playCards(cardPlaysInFirstDeal)

        val result = playCard("Bob", TWO of CLUBS)

        assertThat(result).isEqualTo(PlayedCard)
    }

    @Disabled
    @Test
    fun `cards are passed to the right on second deal when four-way passing is enabled`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(FourWayPassing)
        val cardPlaysInFirstDeal = readCardPlaysFromResource("/fixed_card_plays_four_way_passing.txt")

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()
        passCards("Mary", setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS))
        passCards("Joe", setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS))
        passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS))
        passCards("Jane", setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS))

        playCards(cardPlaysInFirstDeal)
        passCards("Mary", setOf(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS))
        passCards("Joe", setOf(QUEEN of CLUBS, TWO of HEARTS, EIGHT of HEARTS))
        passCards("Bob", setOf(SIX of DIAMONDS, TWO of CLUBS, FIVE of CLUBS))
        passCards("Jane", setOf(THREE of CLUBS, TEN of DIAMONDS, NINE of DIAMONDS))

        assertThat(cardsInHandOf("Jane")).contains(EIGHT of SPADES, THREE of DIAMONDS, SIX of HEARTS)
    }

    @Disabled
    @Test
    fun `scores are calculated at the end of each deal`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)
        val cardPlaysInFirstDeal = readCardPlaysFromResource("/fixed_card_plays_no_passing.txt").asSequence().toList()

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        playCards(cardPlaysInFirstDeal.subList(0, 4))
        assertThat(scoreOfPlayer("Mary")).isEqualTo(0)
        assertThat(scoreOfPlayer("Joe")).isEqualTo(0)
        assertThat(scoreOfPlayer("Bob")).isEqualTo(0)
        assertThat(scoreOfPlayer("Jane")).isEqualTo(0)

        playCards(cardPlaysInFirstDeal.subList(4, cardPlaysInFirstDeal.size))
        assertThat(scoreOfPlayer("Mary")).isEqualTo(0)
        assertThat(scoreOfPlayer("Joe")).isEqualTo(4)
        assertThat(scoreOfPlayer("Bob")).isEqualTo(4)
        assertThat(scoreOfPlayer("Jane")).isEqualTo(18)
    }

    @Disabled
    @Test
    fun `game ends when a score of 100 or higher is reached`() {
        onDeal(::dealFixedCards)
        setPassingRuleTo(NoPassing)
        val cardPlays = readCardPlaysFromResource("/fixed_card_plays_no_passing.txt").asSequence().toList()

        joinGame("Mary")
        joinGame("Joe")
        joinGame("Bob")
        joinGame("Jane")
        startGame()

        repeat(5) { playCards(cardPlays) }
        assertThat(hasGameEnded()).isFalse()

        playCards(cardPlays)
        assertThat(scoreOfPlayer("Jane")).isEqualTo(108)
        assertThat(hasGameEnded()).isTrue()
    }

    private fun onDeal(dealCardsToPlayer: (PlayerName) -> List<Card>) {
    }

    private fun playCards(cardPlays: Iterable<PlayCard>) {
        cardPlays.forEach(this::playCard)
    }

    private fun setPassingRuleTo(rule: PassingRule) {
        // hint: for now, ignore this method by commenting out the line below
        // this method will become relevant when you encounter the first test where players have to pass cards
    }

    private fun playCards(cardPlays: Iterator<PlayCard>) {
        cardPlays.forEach(this::playCard)
    }

    private fun passCards(passedBy: PlayerName, cards: Set<Card>) =
        context.commandExecutor.execute(PassCards(cards, passedBy))

    private fun playCard(player: PlayerName, card: Card) = playCard(PlayCard(card, player))

    private fun playCard(command: PlayCard) = context.commandExecutor.execute(command)

    private fun joinGame(player: PlayerName) = context.commandExecutor.execute(MakePlayerJoinGame(player))

    private fun startGame(): StartGameResponse = context.commandExecutor.execute(StartGame())

    private fun gameHasStarted(): Boolean = context.queryExecutor.execute(HasGameStarted)

    private fun cardsInHandOf(player: PlayerName) = context.queryExecutor.execute(CardsInHandOf(player))

    private fun whoseTurnIsIt(): PlayerName = context.queryExecutor.execute(WhoseTurnIsIt)

    private fun playRound(action: () -> Unit) {
        action()
    }

    private fun scoreOfPlayer(player: PlayerName) = context.queryExecutor.execute(WhatIsScoreOfPlayer(player))

    private fun hasGameEnded() = context.queryExecutor.execute(HasGameEnded)

    companion object {
        @JvmStatic
        fun data(): Stream<Arguments> {
            return Stream.of(Arguments.of("Bob"))
        }
    }
}