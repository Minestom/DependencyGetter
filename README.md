# Dependency-Getter
Abstraction layer over dependencies.
Allows downloading artifacts from a coordinate.

This library is centered around resolvers. Resolvers are the objects responsible for finding the dependency
inside its locations (repositories for Maven, for instance).

Throws an `UnresolvedDependencyException` if the dependency could not be resolved.

# On Maven resolvers
Maven resolvers will download to the given target folder. They also use this target folder as a local
maven repository: any already downloaded artifact will not be redownloaded (with the exception of snapshots).

They also create a temporary folder `.tmp` inside the target folder to store a Maven `settings.xml` file to specify
a local repository and remote repositories.

# Example

## Hello Dependency
```kotlin
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
```

## Using a Resolver directly
```kotlin
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
```