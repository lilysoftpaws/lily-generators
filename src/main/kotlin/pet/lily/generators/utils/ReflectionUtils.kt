package pet.lily.generators.utils

import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import kotlin.reflect.KCallable
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.kotlinFunction

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

    /**
     * Finds all methods annotated with the specified annotation in the given package, and returns them along with their instances.
     *
     * @param A The annotation type to search for.
     * @param packageName The package where to look for annotated methods.
     * @return A list of pairs, where each pair contains an instance of the class (or Kotlin object) and the annotated method.
     */
    inline fun <reified A : Annotation> getAnnotatedMethods(packageName: String): List<Pair<Any, KCallable<*>>> {
        val reflections = Reflections(packageName, MethodAnnotationsScanner())
        val annotatedMethods = reflections.getMethodsAnnotatedWith(A::class.java)

        return annotatedMethods.mapNotNull { method ->
            val kFunction = method.kotlinFunction ?: return@mapNotNull null
            val instance = method.declaringClass.kotlin.objectInstance ?: try {
                method.declaringClass.kotlin.createInstance()
            } catch (e: Exception) {
                null
            }

            instance?.let { it to kFunction }
        }
    }
}
