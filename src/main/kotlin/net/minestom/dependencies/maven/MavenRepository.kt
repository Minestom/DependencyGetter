package net.minestom.dependencies.maven

import java.net.URL

/**
 * Represents a maven repository by its name and URL.
 * Equality/hashCode/toString is based only on the URL.
 * The name is only used as an id for the maven resolver, and shows up in resolution errors.
 */
class MavenRepository(val name: String, url: String) {
    companion object {
        val Central = MavenRepository("Central", "https://repo1.maven.org/maven2/")
        val Sonatype = MavenRepository("Sonatype", "https://oss.sonatype.org/content/repositories/releases/")
        val JCenter = MavenRepository("JCenter", "https://jcenter.bintray.com/")
        val Jitpack = MavenRepository("Jitpack.io", "https://jitpack.io/")
    }

    val url = URL(if(url.endsWith("/")) url else "$url/")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MavenRepository

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun toString(): String {
        return url.toExternalForm()
    }
}
