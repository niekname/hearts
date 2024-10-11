package org.socratesbe.hearts.application.impl.command

import org.socratesbe.hearts.application.api.command.*
import org.socratesbe.hearts.domain.Game

internal fun interface CommandHandler<Result, C : Command<Result>> {
    fun execute(command: C): Result
}

internal class MakePlayerJoinGameHandler(private val game: Game) :
    CommandHandler<PlayerJoinResponse, MakePlayerJoinGame> {
    override fun execute(command: MakePlayerJoinGame): PlayerJoinResponse {
        game.playerJoins()
        return PlayerCouldNotJoin("", "")
    }
}

internal class StartGameHandler(private val game: Game) : CommandHandler<StartGameResponse, StartGame> {
    override fun execute(command: StartGame): StartGameResponse {
        game.start()
        return when {
            game.hasStarted() -> GameHasStarted
            else -> GameHasNotStarted("Not enough players joined")
        }
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
