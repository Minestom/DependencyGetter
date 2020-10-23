package net.minestom.dependencies

import java.lang.Exception

/**
 * Thrown when a given dependency could not be found inside the given resolvers
 */
class UnresolvedDependencyException(msg: String, cause: Throwable?): Exception(msg, cause) {
    constructor(msg: String): this(msg, null)
}
