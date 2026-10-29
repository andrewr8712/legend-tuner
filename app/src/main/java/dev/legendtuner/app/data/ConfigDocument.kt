package dev.legendtuner.app.data

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * A path-addressable wrapper around Feral's XML registry file.
 *
 * The file is a Windows-registry-style tree:
 *   registry > key(name=HKEY_CURRENT_USER) > ... > value(name=..., type=..., text)
 *
 * We only ever mutate the exact value requested. Unknown keys, unknown value types,
 * attributes, comments and all unrelated entries are left in the document.
 */
class ConfigDocument private constructor(val document: Document) {

    data class ConfigValue(
        val path: String,
        val name: String,
        val type: String,
        val text: String
    ) {
        val intOrNull: Int? get() = text.trim().toIntOrNull()
        val firstToken: String get() = text.substringBefore(',').trim()
        val firstIntOrNull: Int? get() = firstToken.toIntOrNull()
    }

    data class FlatValue(
        val path: String,
        val name: String,
        val type: String,
        val text: String
    )

    val root: Element get() = document.documentElement

    init {
        require(root.tagName == "registry") { "Not a GRID Legends preferences file: root is <${root.tagName}>" }
    }

    // ---------------------------------------------------------------- lookup

    fun key(path: List<String>): Element? {
        var current: Element = root
        for (segment in path) {
            current = childKeys(current).firstOrNull { it.getAttribute("name") == segment } ?: return null
        }
        return current
    }

    fun value(path: List<String>, name: String): ConfigValue? {
        val parent = key(path) ?: return null
        val el = childValues(parent).firstOrNull { it.getAttribute("name") == name } ?: return null
        return ConfigValue(path.joinToString("/"), name, el.getAttribute("type"), el.textContent ?: "")
    }

    fun int(path: List<String>, name: String): Int? = value(path, name)?.intOrNull

    fun allValues(): List<FlatValue> {
        val out = ArrayList<FlatValue>(420)
        fun walk(element: Element, path: MutableList<String>) {
            for (child in element.childElements()) {
                when (child.tagName) {
                    "key" -> {
                        path.add(child.getAttribute("name"))
                        walk(child, path)
                        path.removeAt(path.lastIndex)
                    }
                    "value" -> out.add(
                        FlatValue(
                            path = path.joinToString("/"),
                            name = child.getAttribute("name"),
                            type = child.getAttribute("type"),
                            text = child.textContent ?: ""
                        )
                    )
                }
            }
        }
        walk(root, mutableListOf())
        return out
    }

    fun graphicsSettings(): Element? = key(GRAPHICS_SETTINGS_PATH)

    fun setup(): Element? = key(SETUP_PATH)

    // -------------------------------------------------------------- mutation

    fun setValue(path: List<String>, name: String, newText: String): Boolean {
        val parent = key(path) ?: return false
        val el = childValues(parent).firstOrNull { it.getAttribute("name") == name } ?: return false
        el.textContent = newText
        return true
    }

    /** Writes the first token of a comma-separated preset value, preserving the trailing tokens. */
    fun setPresetFirstToken(preset: Preset, name: String, newValue: Int): Boolean {
        val current = value(presetPath(preset), name)?.text ?: return false
        val comma = current.indexOf(',')
        val tail = if (comma >= 0) current.substring(comma) else ""
        return setValue(presetPath(preset), name, "$newValue$tail")
    }

    fun setPresetInteger(preset: Preset, name: String, newValue: Int): Boolean =
        setValue(presetPath(preset), name, newValue.toString())

    fun setLiveInteger(name: String, newValue: Int): Boolean =
        setValue(GRAPHICS_SETTINGS_PATH, name, newValue.toString())

    fun setSetupInteger(name: String, newValue: Int): Boolean =
        setValue(SETUP_PATH, name, newValue.toString())

    fun setActivePreset(preset: Preset): Boolean =
        setLiveInteger("DevicePreset", preset.index)

    fun activePreset(): Preset =
        Preset.fromIndex(int(GRAPHICS_SETTINGS_PATH, "DevicePreset") ?: Preset.GRAPHICS.index)

    fun presetValue(preset: Preset, name: String): ConfigValue? = value(presetPath(preset), name)

    /**
     * Mirrors the selected preset into the live GraphicsSettings integers.
     *
     * The game reads the preset block on startup and then materialises the "live"
     * values. Writing both means the change is visible even if the game does not
     * re-apply the preset immediately.
     */
    fun applyPresetToLive(preset: Preset): Int {
        var changed = 0
        val presetKey = key(presetPath(preset)) ?: return 0
        val liveKey = graphicsSettings() ?: return 0
        for (presetValue in childValues(presetKey)) {
            val name = presetValue.getAttribute("name")
            val liveValue = childValues(liveKey).firstOrNull { it.getAttribute("name") == name } ?: continue
            if (liveValue.getAttribute("type") != "integer") continue
            val first = (presetValue.textContent ?: "").substringBefore(',').trim().toIntOrNull() ?: continue
            liveValue.textContent = first.toString()
            changed++
        }
        if (setActivePreset(preset)) changed++
        return changed
    }

    fun maximumFor(name: String): Int? = int(GRAPHICS_SETTINGS_PATH, "${name}_maximum")

    fun ensureActivePresetExists(): Preset {
        val preset = activePreset()
        if (key(presetPath(preset)) == null) {
            // Fall back to whichever preset block actually exists.
            return Preset.entries.firstOrNull { key(presetPath(it)) != null } ?: Preset.GRAPHICS
        }
        return preset
    }

    // ------------------------------------------------------------------ io

    fun serialize(): ByteArray {
        stripWhitespace(root)
        val transformer = TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        }
        val out = ByteArrayOutputStream()
        transformer.transform(DOMSource(document), StreamResult(out))
        val bytes = out.toByteArray()
        return if (bytes.isNotEmpty() && bytes.last() == '\n'.code.toByte()) bytes
        else bytes + '\n'.code.toByte()
    }

    fun validate() {
        require(root.tagName == "registry") { "Invalid root element" }
        val gs = graphicsSettings() ?: error("Missing GRIDLegends/GraphicsSettings section")
        if (childValues(gs).isEmpty()) error("GraphicsSettings is empty")
        val active = activePreset()
        if (key(presetPath(active)) == null) error("Active preset block '${active.keyName}' is missing")
    }

    private fun stripWhitespace(element: Element) {
        val children = element.childNodes
        var i = children.length - 1
        while (i >= 0) {
            val node = children.item(i)
            if (node.nodeType == Node.TEXT_NODE && node.nodeValue.isNullOrBlank()) {
                element.removeChild(node)
            } else if (node.nodeType == Node.ELEMENT_NODE) {
                stripWhitespace(node as Element)
            }
            i--
        }
    }

    companion object {
        val GRAPHICS_SETTINGS_PATH = listOf(
            "HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRIDLegends", "GraphicsSettings"
        )
        val SETUP_PATH = listOf(
            "HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRIDLegends", "Setup"
        )
        val GRID_PATH = listOf(
            "HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRIDLegends"
        )

        fun presetPath(preset: Preset): List<String> = GRAPHICS_SETTINGS_PATH + preset.keyName

        fun parse(bytes: ByteArray): ConfigDocument {
            try {
                val factory = DocumentBuilderFactory.newInstance().apply {
                    isNamespaceAware = false
                    runCatching { isXIncludeAware = false }
                    runCatching { isExpandEntityReferences = false }
                    // Not every Android XML provider implements every hardening
                    // feature. Try them individually so a missing flag does not
                    // make a valid local config unreadable.
                    runCatching { setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true) }
                    runCatching { setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
                    runCatching { setFeature("http://xml.org/sax/features/external-general-entities", false) }
                    runCatching { setFeature("http://xml.org/sax/features/external-parameter-entities", false) }
                }
                val builder = factory.newDocumentBuilder()
                val doc = builder.parse(ByteArrayInputStream(bytes))
                doc.documentElement.normalize()
                return ConfigDocument(doc)
            } catch (e: Exception) {
                throw ConfigParseException(e.message ?: "Could not parse XML", e)
            }
        }

        fun parse(text: String): ConfigDocument = parse(text.toByteArray(StandardCharsets.UTF_8))
    }
}

class ConfigParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

private fun Element.childElements(): List<Element> {
    val out = ArrayList<Element>(childNodes.length)
    val nodes = childNodes
    for (i in 0 until nodes.length) {
        val n = nodes.item(i)
        if (n.nodeType == Node.ELEMENT_NODE) out.add(n as Element)
    }
    return out
}

private fun childKeys(parent: Element): List<Element> =
    parent.childElements().filter { it.tagName == "key" }

private fun childValues(parent: Element): List<Element> =
    parent.childElements().filter { it.tagName == "value" }
