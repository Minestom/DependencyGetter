package net.minestom.dependencies.maven

import net.minestom.dependencies.DependencyResolver
import net.minestom.dependencies.ResolvedDependency
import net.minestom.dependencies.UnresolvedDependencyException
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException
import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact
import java.io.File
import java.util.logging.LogManager


class MavenResolver(val repositories: List<MavenRepository>): DependencyResolver {

    companion object {
        init {
            LogManager.getLogManager().readConfiguration(javaClass.getResourceAsStream("/logging.properties"))
        }
    }

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
            val hasMavenCentral = repositories.any { it.url.sameFile(MavenRepository.Central.url) }
            val resolver = Maven.configureResolver().withMavenCentralRepo(hasMavenCentral).fromFile(settingsFile)
            val artifacts = resolver.resolve(id).withTransitivity().asResolvedArtifact()
            val dependencies = mutableListOf<ResolvedDependency>()
            for(dep in artifacts.drop(1)) {
                dependencies += convertToDependency(dep)
            }
            val coords = artifacts[0].coordinate
            return ResolvedDependency(coords.groupId, coords.artifactId, coords.version, artifacts[0].asFile().toURI().toURL(), dependencies)
        } catch(e: NoResolvedResultException) {
            throw UnresolvedDependencyException("Failed to resolve $id", e)
        } finally {
            tmpFolder.deleteRecursively()
        }
    }

    private fun convertToDependency(artifact: MavenResolvedArtifact): ResolvedDependency {
        return ResolvedDependency(artifact.coordinate.groupId, artifact.coordinate.artifactId, artifact.coordinate.version, artifact.asFile().toURI().toURL(), emptyList())
    }
}
