package io.microconfig.plugin.microconfig.impl

import io.microconfig.core.Microconfig
import io.microconfig.core.configtypes.ConfigType
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithExtensionOf
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName
import io.microconfig.core.configtypes.StandardConfigType.APPLICATION
import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.ConfigFormat.YAML
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.FileBasedComponent
import io.microconfig.core.properties.Resolver
import io.microconfig.core.properties.repository.ConfigFile
import io.microconfig.core.properties.repository.Include
import io.microconfig.core.properties.repository.Includes
import io.microconfig.core.properties.resolvers.placeholder.Placeholder
import io.microconfig.core.properties.resolvers.placeholder.PlaceholderBorders.Companion.findPlaceholderIn
import io.microconfig.core.properties.serializers.PropertySerializers.asString
import io.microconfig.plugin.actions.preview.UnresolvedPlaceholders
import io.microconfig.plugin.microconfig.ConfigOutput
import io.microconfig.plugin.microconfig.FilePosition
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.MicroconfigInitializer
import java.io.File
import kotlin.jvm.optionals.getOrNull

class MicroconfigApiImpl(
    private val initializer: MicroconfigInitializer = MicroconfigInitializer.create(),
) : MicroconfigApi {
    override fun findIncludeSource(includeLine: String, currentColumn: Int, currentFile: File, projectDir: File): File {
        val include = parseInclude(includeLine, currentColumn, currentFile)
        val microconfig = initializer.getMicroconfig(projectDir)
        return findSourceFile(include.component, include.environment, currentFile, microconfig)
    }

    override fun findPlaceholderSource(placeholderValue: String, currentFile: File, projectDir: File): FilePosition {
        val placeholder = parsePlaceholder(placeholderValue, currentFile)
        val microconfig = initializer.getMicroconfig(projectDir)
        return placeholderPosition(placeholder, currentFile, microconfig)
    }

    override fun resolvePlaceholderForEachEnv(
        placeholderValue: String, currentFile: File, projectDir: File,
    ): Map<String, String> = resolveFullLineForEachEnv(placeholderValue, currentFile, projectDir)

    override fun resolveFullLineForEachEnv(currentLine: String, currentFile: File, projectDir: File): Map<String, String> {
        val microconfig = initializer.getMicroconfig(projectDir)
        val configType = configTypeOf(currentFile, microconfig).typeName
        val resolver = microconfig.resolver()
        return envsFor(currentLine, currentFile, microconfig).associateWith { env ->
            resolveValue(currentLine, currentFile.parentFile.name, env, configType, resolver)
        }.toSortedMap()
    }

    override fun findAnyComponentFile(component: String, env: String, projectDir: File): File {
        val microconfig = initializer.getMicroconfig(projectDir)
        val configFiles = componentFiles(component, env, microconfig)
        requireConfigFiles(configFiles, component)
        return configFiles.last().file
    }

    override fun buildConfigs(currentFile: File, projectDir: File, env: String): ConfigOutput {
        val microconfig = initializer.getMicroconfig(projectDir)
            .withAdditionalPlaceholderResolvers(listOf(UnresolvedPlaceholders.strategy()))
        microconfig.logger(false)
        val properties = microconfig.environments()
            .getOrCreateByName(env)
            .findComponentWithName(currentFile.parentFile.name) // TODO: Support aliases.
            .getPropertiesFor(configTypeWithExtensionOf(currentFile))
            .first()
            .resolveBy(microconfig.resolver())
        return ConfigOutput(
            if (properties.properties.any { it.configFormat == YAML }) YAML else PROPERTIES,
            properties.save(asString(microconfig.dependencies().configIo)),
        )
    }

    override fun getEnvs(projectDir: File): Set<String> =
        initializer.getMicroconfig(projectDir).environments().environmentNames()

    override fun detectEnvOr(currentFile: File): String? {
        val name = currentFile.name
        val start = name.indexOf('.')
        val end = name.indexOf('.', start + 1)
        return if (end > 0) name.substring(start + 1, end) else null
    }

    private fun parseInclude(line: String, column: Int, currentFile: File): Include {
        val includes = Includes.from(line).withDefaultEnv(detectEnvOr(currentFile).orEmpty())
        return if (includes.size == 1) includes.first() else includes[includedComponentIndex(line, column)]
    }

    private fun includedComponentIndex(line: String, column: Int): Int {
        var position = column
        var index = -1
        do {
            index++
            position = line.lastIndexOf(',', (position - 1).coerceAtLeast(0))
        } while (position > 0)
        return index
    }

    private fun findSourceFile(component: String, env: String, currentFile: File, microconfig: Microconfig): File {
        val files = microconfig.dependencies().componentGraph()
            .getConfigFilesOf(component, env, configTypeOf(currentFile, microconfig))
        requireConfigFiles(files, component)
        return (if (env.isEmpty()) files.first() else files.last()).file
    }

    private fun requireConfigFiles(files: List<ConfigFile>, component: String) {
        check(files.isNotEmpty()) { "Can't find config files of component '$component'" }
    }

    private fun configTypeOf(currentFile: File, microconfig: Microconfig): ConfigType =
        configTypeWithExtensionOf(currentFile)
            .selectTypes(microconfig.dependencies().configTypeRepository().configTypes)
            .firstOrNull() ?: error("Unsupported config type of '${currentFile.name}'")

    private fun parsePlaceholder(value: String, currentFile: File): Placeholder {
        val placeholder = (findPlaceholderIn(value).getOrNull() ?: error("Can't parse $value"))
            .toPlaceholder("", detectEnvOr(currentFile).orEmpty())
        return if (placeholder.isSelfReferenced) placeholder.withComponent(currentFile.parentFile.name) else placeholder
    }

    private fun placeholderPosition(placeholder: Placeholder, currentFile: File, microconfig: Microconfig): FilePosition {
        val filter = if (placeholder.configType.isEmpty()) configTypeWithExtensionOf(currentFile)
            else configTypeWithName(placeholder.configType)
        val source = microconfig.environments()
            .getOrCreateByName(placeholder.environment)
            .findComponentWithName(placeholder.component)
            .getPropertiesFor(filter)
            .getPropertyWithKey(placeholder.key)
            .getOrNull()?.declaringComponent
        return if (source is FileBasedComponent) FilePosition.positionFromFileSource(source)
            else FilePosition(findSourceFile(placeholder.component, placeholder.environment, currentFile, microconfig), 0)
    }

    private fun envsFor(line: String, currentFile: File, microconfig: Microconfig): List<String> {
        if (findPlaceholderIn(line).isEmpty) return listOf("")
        val parts = currentFile.name.split('.')
        return if (parts.size > 2) parts.drop(1).dropLast(1)
            else listOf("") + microconfig.environments().environmentNames()
    }

    private fun resolveValue(line: String, component: String, env: String, configType: String, resolver: Resolver): String =
        try {
            val root = DeclaringComponentImpl(configType, component, env)
            resolver.resolve(line, root, root)
        } catch (_: RuntimeException) {
            "Can't resolve"
        }

    private fun componentFiles(component: String, env: String, microconfig: Microconfig): List<ConfigFile> {
        val graph = microconfig.dependencies().componentGraph()
        return graph.getConfigFilesOf(component, env, APPLICATION).ifEmpty {
            graph.getConfigFilesOf(component, env, compositeConfigType(microconfig))
        }
    }

    private fun compositeConfigType(microconfig: Microconfig): ConfigType = object : ConfigType {
        override val typeName = "preview"
        override val sourceExtensions = microconfig.dependencies().configTypeRepository().configTypes
            .flatMap { it.sourceExtensions }.toSet()
        override val resultFileName = "preview"
    }
}
