package net.minestom.dependencies

import net.minestom.dependencies.model.ResolvedDependency
import java.io.File
import java.net.URL
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
    fun resolve(id: String, targetFolder: File): ResolvedDependency
}