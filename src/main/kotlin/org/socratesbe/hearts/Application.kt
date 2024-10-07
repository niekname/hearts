package org.socratesbe.hearts

import org.socratesbe.hearts.application.api.command.Command
import org.socratesbe.hearts.application.api.command.MakePlayerJoinGame
import org.socratesbe.hearts.application.api.command.PassCards
import org.socratesbe.hearts.application.api.command.PassedCards
import org.socratesbe.hearts.application.api.command.PlayCard
import org.socratesbe.hearts.application.api.command.PlayedCard
import org.socratesbe.hearts.application.api.command.StartGame
import org.socratesbe.hearts.application.api.query.CardsInHandOf
import org.socratesbe.hearts.application.api.query.HasGameEnded
import org.socratesbe.hearts.application.api.query.Query
import org.socratesbe.hearts.application.api.query.WhatIsScoreOfPlayer
import org.socratesbe.hearts.application.api.query.WhoseTurnIsIt
import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.PlayerName

fun main() {
    org.socratesbe.hearts.Application.run()
}

object Application {
    private val context = org.socratesbe.hearts.Context()
    private var round = 1

    fun run() {
        val players = listOf("Joe", "Mary", "Bob", "Sue")
        players.forEach { org.socratesbe.hearts.Application.joinGame(it) }
        org.socratesbe.hearts.Application.startGame()

        while (!org.socratesbe.hearts.Application.hasGameEnded()) {
            org.socratesbe.hearts.Application.passAllCardsFor(players)
            org.socratesbe.hearts.Application.playRoundWith(players)
        }

        println("=== Final Scores ===")
        players.forEach { player ->
            println("$player: ${org.socratesbe.hearts.Application.scoreOf(player)}")
        }
    }

    private fun passAllCardsFor(players: List<String>) {
        players.forEach { org.socratesbe.hearts.Application.passFirstThreeCards(it) }
    }

    private fun scoreOf(player: PlayerName) = org.socratesbe.hearts.Application.execute(WhatIsScoreOfPlayer(player))

    private fun playRoundWith(players: List<String>) {
        println("=== Round ${org.socratesbe.hearts.Application.round} ===")
        for (turn in 0 until 13) {
            for (player in players) {
                val currentPlayer = org.socratesbe.hearts.Application.whoseTurnIsIt()
                val playedCard = org.socratesbe.hearts.Application.cardsInHandOf(currentPlayer)
                    .first { org.socratesbe.hearts.Application.playCard(it, currentPlayer) == PlayedCard }
                println("$currentPlayer played $playedCard")
            }
            println("------------")
        }
        org.socratesbe.hearts.Application.round++
    }

    private fun hasGameEnded() = org.socratesbe.hearts.Application.execute(HasGameEnded)

    private fun playCard(card: Card, playedBy: PlayerName) = org.socratesbe.hearts.Application.execute(PlayCard(card, playedBy))

    private fun whoseTurnIsIt() = org.socratesbe.hearts.Application.execute(WhoseTurnIsIt)

    private fun passFirstThreeCards(player: PlayerName) {
        val hand = org.socratesbe.hearts.Application.cardsInHandOf(player)
        val firstThreeCards = hand.subList(0, 3).toSet()
        val result = org.socratesbe.hearts.Application.passCards(firstThreeCards, player)
        if (result == PassedCards) {
            println("$player passed cards: ${firstThreeCards.joinToString(", ")}")
        }
    }

    private fun passCards(cards: Set<Card>, passedBy: PlayerName) = org.socratesbe.hearts.Application.execute(PassCards(cards, passedBy))

    private fun joinGame(player: PlayerName) {
        org.socratesbe.hearts.Application.execute(MakePlayerJoinGame(player))
    }

    private fun startGame() {
        org.socratesbe.hearts.Application.execute(StartGame())
    }

    private fun cardsInHandOf(player: PlayerName) = org.socratesbe.hearts.Application.execute(CardsInHandOf(player))

    private fun <Result> execute(query: Query<Result>) = org.socratesbe.hearts.Application.context.queryExecutor.execute(query)

    private fun <Result> execute(command: Command<Result>) = org.socratesbe.hearts.Application.context.commandExecutor.execute(command)
}
