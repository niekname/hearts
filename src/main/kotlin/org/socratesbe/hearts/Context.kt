package org.socratesbe.hearts

import org.socratesbe.hearts.application.impl.command.CommandBus
import org.socratesbe.hearts.application.api.command.CommandExecutor
import org.socratesbe.hearts.application.impl.query.QueryBus
import org.socratesbe.hearts.application.api.query.QueryExecutor
import org.socratesbe.hearts.domain.Game

class Context(game: Game = Game()) {
    val commandExecutor: CommandExecutor = CommandBus(game)
    val queryExecutor: QueryExecutor = QueryBus(game)
}
