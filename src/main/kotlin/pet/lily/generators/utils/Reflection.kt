package pet.lily.generators.utils

import org.reflections.Reflections

/**
 * Finds all classes implementing the specified type [T] in the given package.
 *
 * @param T The type to search for.
 * @param packageName The package to search within.
 * @return A list of instances of classes that implement [T].
 */
inline fun <reified T> getImplementations(packageName: String): List<T> {
    val reflections = Reflections(packageName)
    val classes = reflections.getSubTypesOf(T::class.java)

    return classes.map { clazz ->
        val constructor = clazz.getDeclaredConstructor()
        constructor.isAccessible = true
        constructor.newInstance() as T
    }
}