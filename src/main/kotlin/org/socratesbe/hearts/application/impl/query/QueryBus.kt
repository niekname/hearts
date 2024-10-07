package org.socratesbe.hearts.application.impl.query

import org.socratesbe.hearts.application.api.query.CardsInHandOf
import org.socratesbe.hearts.application.api.query.HasGameEnded
import org.socratesbe.hearts.application.api.query.HasGameStarted
import org.socratesbe.hearts.application.api.query.Query
import org.socratesbe.hearts.application.api.query.QueryExecutor
import org.socratesbe.hearts.application.api.query.WhatIsScoreOfPlayer
import org.socratesbe.hearts.application.api.query.WhoseTurnIsIt
import org.socratesbe.hearts.domain.Game
import kotlin.reflect.KClass

class QueryBus(game: Game) : QueryExecutor {
    private val queryHandlers: MutableMap<KClass<*>, QueryHandler<*, *>> = mutableMapOf()

    init {
        register(HasGameStarted::class to HasGameStartedHandler(game))
        register(CardsInHandOf::class to CardsInHandOfHandler(game))
        register(WhoseTurnIsIt::class to WhoseTurnIsItHandler(game))
        register(WhatIsScoreOfPlayer::class to WhatIsScoreOfPlayerHandler(game))
        register(HasGameEnded::class to HasGameEndedHandler(game))
    }

    private fun <Result, Q : Query<Result>> register(pair: Pair<KClass<Q>, QueryHandler<Result, Q>>) {
        val (queryType, handler) = pair
        queryHandlers[queryType] = handler
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Result> execute(query: Query<Result>): Result {
        val handler = queryHandlers[query::class] as? QueryHandler<Result, Query<Result>>
            ?: error("No handler found for query ${query::class.qualifiedName}")
        return handler.execute(query)
    }

}
