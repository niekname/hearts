package org.socratesbe.hearts.application.impl.command

import org.socratesbe.hearts.application.api.command.Command
import org.socratesbe.hearts.application.api.command.MakePlayerJoinGame
import org.socratesbe.hearts.application.api.command.PassCards
import org.socratesbe.hearts.application.api.command.PassCardsResponse
import org.socratesbe.hearts.application.api.command.PlayCard
import org.socratesbe.hearts.application.api.command.PlayCardResponse
import org.socratesbe.hearts.application.api.command.PlayerJoinResponse
import org.socratesbe.hearts.application.api.command.StartGame
import org.socratesbe.hearts.application.api.command.StartGameResponse
import org.socratesbe.hearts.domain.Game

internal fun interface CommandHandler<Result, C : Command<Result>> {
    fun execute(command: C): Result
}

internal class MakePlayerJoinGameHandler(private val game: Game) : CommandHandler<PlayerJoinResponse, MakePlayerJoinGame> {
    override fun execute(command: MakePlayerJoinGame): PlayerJoinResponse {
        TODO()
    }
}

internal class StartGameHandler(private val game: Game) : CommandHandler<StartGameResponse, StartGame> {
    override fun execute(command: StartGame): StartGameResponse {
        TODO()
    }
}

internal class PlayCardHandler(private val game: Game) : CommandHandler<PlayCardResponse, PlayCard> {
    override fun execute(command: PlayCard): PlayCardResponse {
        TODO()
    }
}

internal class PassCardsHandler(private val game: Game) : CommandHandler<PassCardsResponse, PassCards> {
    override fun execute(command: PassCards): PassCardsResponse {
        TODO()
    }
}
