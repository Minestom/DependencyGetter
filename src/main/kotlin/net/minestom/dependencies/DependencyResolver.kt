package net.minestom.dependencies

import java.io.File
import java.net.URL
import kotlin.jvm.Throws

interface DependencyResolver {
    @Throws(UnresolvedDependencyException::class)
    fun resolve(id: String, targetFolder: File): ResolvedDependency
}

data class ResolvedDependency(val group: String, val name: String, val version: String, val contentsLocation: URL, val subdependencies: List<ResolvedDependency>) {
    fun printTree(indent: String = "") {
        println("$indent- $group:$name:$version ($contentsLocation)")
        for (dep in subdependencies) {
            dep.printTree("$indent  ")
        }
    }
}
