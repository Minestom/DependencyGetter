import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import net.minestom.dependencies.DependencyGetter
import net.minestom.dependencies.DependencyResolver
import net.minestom.dependencies.ResolvedDependency
import net.minestom.dependencies.UnresolvedDependencyException
import net.minestom.dependencies.maven.MavenRepository
import net.minestom.dependencies.maven.MavenResolver
import java.nio.file.Files
import java.nio.file.Path

class Tests : AnnotationSpec() {

    companion object {
        private val targetFolder = Path.of(".", "test_output/")

        @BeforeAll
        @JvmStatic
        fun init() {
            Files.createDirectories(targetFolder)
        }

        @AfterAll
        @JvmStatic
        fun cleanup() {
            Files.walk(targetFolder).sorted(Comparator.reverseOrder()).forEach(Files::delete)
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
        resolved.group shouldBe "com.google.guava"
        resolved.name shouldBe "guava"
        resolved.version shouldBe "30.0-jre"
        resolved.contentsLocation.toExternalForm() shouldBe targetFolder.resolve( "com/google/guava/guava/30.0-jre/guava-30.0-jre.jar").toUri().toURL().toExternalForm()
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
        resolved.group shouldBe "com.github.Minestom"
        resolved.name shouldBe "Minestom"
        resolved.version shouldBe "32d13dcbd1"
    }

    @Test
    fun throwIfNotFound() {
        shouldThrow<UnresolvedDependencyException> {
            val repositories = listOf(
                MavenRepository.Jitpack,
            )
            val resolver = MavenResolver(repositories)
            resolver.resolve("com.google.guava:guava:30.0-jre", targetFolder)
        }
    }

    @Test
    fun helloDependency() {
        class MyResolver: DependencyResolver {
            override fun resolve(id: String, targetFolder: Path): ResolvedDependency {
                throw UnresolvedDependencyException(id)
            }
        }

        val dependencyGetter = DependencyGetter()
            .addResolver(MyResolver())
            .addMavenResolver(repositories = listOf(
                MavenRepository.Jitpack,
                MavenRepository.Central,
                MavenRepository.JCenter,
                MavenRepository("Minecraft Libs", "https://libraries.minecraft.net"),
                MavenRepository("Sponge", "https://repo.spongepowered.org/maven"),
            ))
        val resolved = dependencyGetter.get("com.github.Minestom:Minestom:32d13dcbd1", targetFolder)
        resolved.printTree()
        shouldThrow<UnresolvedDependencyException> {
            dependencyGetter.get("somethingthatdoesnotexist.xyz", targetFolder)
        }
    }
}
