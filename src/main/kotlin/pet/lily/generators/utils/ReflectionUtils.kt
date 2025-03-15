package pet.lily.generators.utils

import org.reflections.Reflections
import kotlin.reflect.full.createInstance

object ReflectionUtils {

    /**
     * Finds and returns all instances of classes that implement the specified type [T] within the given package.
     *
     * @param T The type (interface or superclass) to search for implementations of.
     * @param packageName The package where to look for implementations.
     * @return A list of instances of classes implementing [T].
     */
    inline fun <reified T : Any> getImplementations(packageName: String): List<T> {
        val reflections = Reflections(packageName)
        val classes = reflections.getSubTypesOf(T::class.java)

        return classes.mapNotNull { clazz ->
            try {
                (clazz.kotlin.objectInstance ?: clazz.kotlin.createInstance()) as T
            } catch (e: Exception) {
                null
            }
        }
    }
}
