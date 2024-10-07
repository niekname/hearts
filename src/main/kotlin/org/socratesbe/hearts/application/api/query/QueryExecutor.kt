package org.socratesbe.hearts.application.api.query

interface QueryExecutor {
    fun <Result> execute(query: Query<Result>): Result
}
