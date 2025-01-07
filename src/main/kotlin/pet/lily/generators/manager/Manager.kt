package pet.lily.generators.manager

import pet.lily.generators.Generators

interface Manager {
    fun initialize(plugin: Generators)
}