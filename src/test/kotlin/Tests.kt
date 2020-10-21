import net.minestom.dependencies.UnresolvedDependencyException
import net.minestom.dependencies.maven.MavenRepository
import net.minestom.dependencies.maven.MavenResolver
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class Tests {

    companion object {
        private val targetFolder = File(".", "test_output/")

        @BeforeAll
        @JvmStatic
        fun init() {
            targetFolder.mkdirs()
        }

        @AfterAll
        @JvmStatic
        fun cleanup() {
            targetFolder.deleteRecursively()
        }

    }

    @Test
    fun fetchMaven() {
        val repositories = listOf(
            MavenRepository.Central,
        )
        val resolver = MavenResolver(repositories)
        val resolved = resolver.resolve("com.google.guava:guava:30.0-jre", targetFolder)
        resolved.printTree()
        assertEquals("com.google.guava", resolved.group)
        assertEquals("guava", resolved.name)
        assertEquals("30.0-jre", resolved.version)
        assertEquals(File(targetFolder, "com/google/guava/guava/30.0-jre/guava-30.0-jre.jar").toURI().toURL().toExternalForm(), resolved.contentsLocation.toExternalForm())
    }

    @Test
    fun fetchMaven2() {
        val repositories = listOf(
                MavenRepository.Jitpack,
                MavenRepository.Central,
                MavenRepository.JCenter,
                MavenRepository("Minecraft Libs", "https://libraries.minecraft.net"),
                MavenRepository("Sponge", "https://repo.spongepowered.org/maven"),
        )
        val resolver = MavenResolver(repositories)
        val resolved = resolver.resolve("com.github.Minestom:Minestom:32d13dcbd1", targetFolder)
        resolved.printTree()
        assertEquals("com.github.Minestom", resolved.group)
        assertEquals("Minestom", resolved.name)
        assertEquals("32d13dcbd1", resolved.version)
    }

    @Test
    fun throwIfNotFound() {
        assertThrows(UnresolvedDependencyException::class.java) {
            val repositories = listOf(
                MavenRepository.Jitpack,
            )
            val resolver = MavenResolver(repositories)
            val resolved = resolver.resolve("com.google.guava:guava:30.0-jre", targetFolder)
        }
    }
}
