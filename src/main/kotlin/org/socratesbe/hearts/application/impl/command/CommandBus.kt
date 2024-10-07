package org.socratesbe.hearts.application.impl.command

import org.socratesbe.hearts.application.api.command.Command
import org.socratesbe.hearts.application.api.command.CommandExecutor
import org.socratesbe.hearts.application.api.command.MakePlayerJoinGame
import org.socratesbe.hearts.application.api.command.PassCards
import org.socratesbe.hearts.application.api.command.PlayCard
import org.socratesbe.hearts.application.api.command.StartGame
import org.socratesbe.hearts.domain.Game
import kotlin.reflect.KClass

class CommandBus(game: Game) : CommandExecutor {
    private val commandHandlers: MutableMap<KClass<*>, CommandHandler<*, *>> = mutableMapOf()

    init {
        register(MakePlayerJoinGame::class to MakePlayerJoinGameHandler(game))
        register(StartGame::class to StartGameHandler(game))
        register(PlayCard::class to PlayCardHandler(game))
        register(PassCards::class to PassCardsHandler(game))
    }

    private fun <Result, C : Command<Result>> register(pair: Pair<KClass<C>, CommandHandler<Result, C>>) {
        val (commandType, handler) = pair
        commandHandlers[commandType] = handler
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Result> execute(command: Command<Result>): Result {
        val handler = commandHandlers[command::class] as? CommandHandler<Result, Command<Result>>
            ?: error("No handler found for command ${command::class.qualifiedName}")
        return handler.execute(command)
    }
}

