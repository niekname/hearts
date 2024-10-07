package org.socratesbe.hearts.domain

// TODO: properly implement these
interface PassingRule
object NoPassing : PassingRule
object AlwaysPassLeft : PassingRule
object FourWayPassing : PassingRule
