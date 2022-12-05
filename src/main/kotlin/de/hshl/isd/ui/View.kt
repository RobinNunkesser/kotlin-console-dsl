package de.hshl.isd.ui

import destination

enum class Destination {
    SwiftUI, JetpackCompose
}

abstract class Element {
    abstract fun render(builder: StringBuilder, indent: String, destination: Destination)

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "", destination)
        return builder.toString()
    }
}

@DslMarker
annotation class ViewTagMarker

@ViewTagMarker
abstract class Tag(val name: String) : Element() {
    val children = arrayListOf<Element>()

    override fun render(builder: StringBuilder, indent: String, destination: Destination) {
        when (destination) {
            Destination.SwiftUI -> print("Sw")
            Destination.JetpackCompose -> print("Je")
        }
        builder.append("$indent<$name>\n")
        for (c in children) {
            c.render(builder, indent + "  ", destination)
        }
        builder.append("$indent</$name>\n")
    }


}

abstract class WithChildren : Element() {
    val children = arrayListOf<Element>()
}

class View(val name: String) : WithChildren() {

    fun <T> state(name: String) : Element {
        val state = State(name)
        children.add(state)
        return state
    }

    fun dependencies(init: Dependencies.() -> Unit) : Dependencies {
        val dependencies = Dependencies()
        dependencies.init()
        children.add(dependencies)
        return dependencies
    }

    override fun render(
        builder: StringBuilder,
        indent: String,
        destination: Destination
    ) {
        when (destination) {
            Destination.SwiftUI -> builder.append("struct ${name}View: View {\n")
            Destination.JetpackCompose -> builder.append("@Composable\nfun ${name}Content() {\n")
        }
        for (c in children) {
            c.render(builder, indent + "  ", destination)
        }
        when (destination) {
            Destination.SwiftUI -> builder.append("}")
            Destination.JetpackCompose -> builder.append("}")
        }
    }

}

fun view(name: String, init: View.() -> Unit): View {
    val view = View(name)
    view.init()
    return view
}

class State(val name: String) : Element() {
    override fun render(builder: StringBuilder, indent: String, destination: Destination) {
        when (destination) {
            Destination.SwiftUI -> builder.append("${indent}@State var $name = \"\"\n")
            Destination.JetpackCompose -> builder.append("${indent}var $name by remember { mutableStateOf(\"\") }\n")
        }
    }
}

class Dependencies() : WithChildren() {

    operator fun String.unaryPlus() {
       children.add(TextElement(this))
    }
    override fun render(
        builder: StringBuilder,
        indent: String,
        destination: Destination
    ) {
        for (c in children) {
            c.render(builder, indent + "  ", destination)
        }
    }

}

class TextElement(val text: String) : Element() {
    override fun render(
        builder: StringBuilder,
        indent: String,
        destination: Destination
    ) {
        builder.append("$indent$text\n")
    }
}

