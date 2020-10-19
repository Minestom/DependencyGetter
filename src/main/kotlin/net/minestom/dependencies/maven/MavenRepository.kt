package net.minestom.dependencies.maven

import java.net.URL

class MavenRepository(val name: String, url: String) {
    companion object {
        val Central = MavenRepository("Central", "https://repo1.maven.org/maven2/")
        val Sonatype = MavenRepository("Sonatype", "https://oss.sonatype.org/content/repositories/releases/")
        val JCenter = MavenRepository("JCenter", "https://jcenter.bintray.com/")
        val Jitpack = MavenRepository("Jitpack.io", "https://jitpack.io/")
    }

    val url = URL(if(url.endsWith("/")) url else "$url/")

    // TODO: sub-dependencies

    fun expand(artifactGroup: String, artifactID: String, version: String, fileExtension: String): URL {
        return URL(
            "${url.toExternalForm()}${
                artifactGroup.replace(
                    ".",
                    "/"
                )
            }/$artifactID/$version/$artifactID-$version.$fileExtension"
        )
    }
}
