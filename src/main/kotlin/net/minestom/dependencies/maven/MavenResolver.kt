package net.minestom.dependencies.maven

import net.minestom.dependencies.DependencyResolver
import net.minestom.dependencies.ResolvedDependency
import net.minestom.dependencies.UnresolvedDependencyException
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException
import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact
import java.io.File
import java.util.logging.LogManager

/**
 * Resolves maven dependencies.
 * *Does not use the local maven repository, but uses the folder passed as an argument in resolve(String, File)*
 *
 * @param repositories list of repositories to use to resolve artifacts
 */
class MavenResolver(val repositories: List<MavenRepository>): DependencyResolver {

    companion object {
        init {
            // prevents ShrinkWrap's resolver's verbose output when trying to find which repository holds an artifact
            LogManager.getLogManager().readConfiguration(javaClass.getResourceAsStream("/logging.properties"))
        }
    }

    /**
     * Resolves and downloads maven artifacts related to the given id.
     */
    override fun resolve(id: String, targetFolder: File): ResolvedDependency {
        val tmpFolder = File(targetFolder, ".tmp")
        try {
            // create a temporary settings file to change the local maven repository and remote repositories
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
            // create the temporary settings.xml file
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
            // ShrinkWrap Resolver either always uses Central, or never, even if it is in the remote repositories
            val hasMavenCentral = repositories.any { it.url.sameFile(MavenRepository.Central.url) }
            val resolver = Maven.configureResolver().withMavenCentralRepo(hasMavenCentral).fromFile(settingsFile)
            val artifacts = resolver.resolve(id).withTransitivity().asResolvedArtifact()
            val dependencies = mutableListOf<ResolvedDependency>()
            for(dep in artifacts.drop(1)) { // [0] is the 'root' because we always resolve one artifact at once
                dependencies += convertToDependency(dep)
            }
            val coords = artifacts[0].coordinate
            return ResolvedDependency(coords.groupId, coords.artifactId, coords.version, artifacts[0].asFile().toURI().toURL(), dependencies)
        } catch(e: NoResolvedResultException) {
            throw UnresolvedDependencyException("Failed to resolve $id", e)
        } finally {
            tmpFolder.deleteRecursively() // ensure the temporary settings.xml is actually temporary
        }
    }

    private fun convertToDependency(artifact: MavenResolvedArtifact): ResolvedDependency {
        return ResolvedDependency(artifact.coordinate.groupId, artifact.coordinate.artifactId, artifact.coordinate.version, artifact.asFile().toURI().toURL(), emptyList())
    }
}
