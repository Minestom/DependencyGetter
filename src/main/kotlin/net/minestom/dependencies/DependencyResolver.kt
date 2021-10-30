package net.minestom.dependencies

import java.net.URL
import java.nio.file.Path
import kotlin.jvm.Throws

/**
 * Resolves & downloads a dependency via its id.
 * Ids formats are up to the resolver.
 */
interface DependencyResolver {
    /**
     * Resolve and download a dependency to local storage.
     * Allowed to avoid redownload if there is a local version cached.
     * @throws UnresolvedDependencyException if the dependency could not be resolved via this resolver
     */
    @Throws(UnresolvedDependencyException::class)
    fun resolve(id: String, targetFolder: Path): ResolvedDependency
}

/**
 * Resolved Dependency.
 * Holds its coordinates (group, artifact, version), which are allowed to be empty if needed
 *
 * The contentsLocation URL represents the location of the dependency, on local storage.
 */
data class ResolvedDependency(
    val group: String, val name: String, val version: String,
    val contentsLocation: URL, val subdependencies: List<ResolvedDependency>
) {
    fun printTree(indent: String = "") {
        println("$indent- $group:$name:$version ($contentsLocation)")
        subdependencies.forEach { it.printTree("$indent  ") }
    }
}
