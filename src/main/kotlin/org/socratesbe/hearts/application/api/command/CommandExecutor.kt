package org.socratesbe.hearts.application.api.command

interface CommandExecutor {
    fun <Result> execute(command: Command<Result>): Result
}
