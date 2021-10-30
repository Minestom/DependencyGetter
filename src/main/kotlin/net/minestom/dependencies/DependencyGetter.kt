package net.minestom.dependencies

import net.minestom.dependencies.maven.MavenRepository
import net.minestom.dependencies.maven.MavenResolver
import java.io.File
import java.nio.file.Path

class DependencyGetter {

    private val resolverList = mutableListOf<DependencyResolver>()

    fun addResolver(resolver: DependencyResolver)
        = apply { resolverList += resolver }

    /**
     * Shorthand to add a MavenResolver with the given repositories
     */
    fun addMavenResolver(repositories: List<MavenRepository>) = addResolver(MavenResolver(repositories))

    fun get(id: String, targetFolder: Path): ResolvedDependency {
        resolverList.forEach { resolver ->
            try {
                return resolver.resolve(id, targetFolder)
            } catch (e: UnresolvedDependencyException) {
                // silence and go to next resolver
            }
        }

        throw UnresolvedDependencyException("Could not find $id inside resolver list: ${resolverList.joinToString { it.toString() }}")
    }
}