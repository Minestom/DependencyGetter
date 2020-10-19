package net.minestom.dependencies

import java.lang.Exception

class UnresolvedDependencyException(msg: String, cause: Throwable?): Exception(msg, cause) {
    constructor(msg: String): this(msg, null)
}
