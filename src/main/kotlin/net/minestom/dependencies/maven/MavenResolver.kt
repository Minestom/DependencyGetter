package net.minestom.dependencies.maven

import net.minestom.dependencies.DependencyResolver
import net.minestom.dependencies.ResolvedDependency
import org.jboss.shrinkwrap.resolver.api.maven.Maven
import java.io.File


class MavenResolver(val repositories: List<MavenRepository>): DependencyResolver {

    override fun resolve(id: String, targetFolder: File): ResolvedDependency {
        val tmpFolder = File(targetFolder, ".tmp")
        try {
            tmpFolder.mkdirs()
            val settingsFile = File(tmpFolder, "settings.xml")
            val repoList =
                (repositories.joinToString("") { repo -> """
                <repository>
                    <id>${repo.name}</id>
                    <name>${repo.name}</name>
                    <url>${repo.url.toExternalForm()}</url>
                    <layout>default</layout>
                </repository>
            """ })
            settingsFile.writeText("""
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
    <localRepository>${targetFolder.absolutePath}</localRepository>
    <profiles>
        <profile>
            <id>dependency-getter-auto</id>
            <repositories>
            $repoList
            </repositories>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>dependency-getter-auto</activeProfile>
    </activeProfiles>
</settings>
            """)
            val resolver = Maven.configureResolver().fromFile(settingsFile)
            for(art in resolver.resolve(id).withTransitivity().asResolvedArtifact()) {
                println("-> ${art.coordinate} ${art.asFile().absolutePath}")
            }
            TODO()
        } finally {
            tmpFolder.deleteRecursively()
        }
    }
}
