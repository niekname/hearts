package org.socratesbe.hearts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.Arguments
import org.socratesbe.hearts.DealMother.dealFixedCards
import org.socratesbe.hearts.DealMother.dealCardsSoJoeHas2ofClubs
import org.socratesbe.hearts.application.api.command.MakePlayerJoinGame
import org.socratesbe.hearts.application.api.command.StartGame
import org.socratesbe.hearts.application.api.command.StartGameResponse
import org.socratesbe.hearts.application.api.query.WhoseTurnIsIt
import org.socratesbe.hearts.domain.Dealer
import org.socratesbe.hearts.domain.Game
import org.socratesbe.hearts.domain.NoPassing
import org.socratesbe.hearts.domain.PassingRule
import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.Player
import org.socratesbe.hearts.vocabulary.PlayerName
import org.socratesbe.hearts.vocabulary.PlayerWithCards
import java.util.stream.Stream

class GameSecondPlayerStartsTrickTest {

    private val context = Context(Game(FixedDealer()))

    class FixedDealer : Dealer {
        override fun dealCardsFor(players: List<Player>) =
            players.map { PlayerWithCards(it, dealCardsSoJoeHas2ofClubs(it.name)) }
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

        assertThat(whoseTurnIsIt()).isEqualTo("Joe")
    }


    private fun onDeal(dealCardsToPlayer: (PlayerName) -> List<Card>) {
    }

    private fun setPassingRuleTo(rule: PassingRule) {
        // hint: for now, ignore this method by commenting out the line below
        // this method will become relevant when you encounter the first test where players have to pass cards
    }

    private fun joinGame(player: PlayerName) = context.commandExecutor.execute(MakePlayerJoinGame(player))

    private fun startGame(): StartGameResponse = context.commandExecutor.execute(StartGame())

    private fun whoseTurnIsIt(): PlayerName = context.queryExecutor.execute(WhoseTurnIsIt)

    companion object {
        @JvmStatic
        fun data(): Stream<Arguments> {
            return Stream.of(Arguments.of("Bob"), Arguments.of("Bab"))
        }
    }

}