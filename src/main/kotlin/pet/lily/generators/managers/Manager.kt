package pet.lily.generators.managers

import pet.lily.generators.Generators

interface Manager {
    fun initialize(plugin: Generators)
}