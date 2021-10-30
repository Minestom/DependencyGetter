package net.minestom.dependencies

import java.lang.Exception

/**
 * Thrown when a given dependency could not be found inside the given resolvers
 */
class UnresolvedDependencyException @JvmOverloads constructor(msg: String, cause: Throwable? = null)
    : Exception(msg, cause)
