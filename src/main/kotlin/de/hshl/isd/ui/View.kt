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
    val children = arrayListOf<com.example.html.Element>()

    override fun render(builder: StringBuilder, indent: String, destination: Destination) {
        when (destination) {
            Destination.SwiftUI -> print("Sw")
            Destination.JetpackCompose -> print("Je")
        }
        builder.append("$indent<$name>\n")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent</$name>\n")
    }


}

abstract class WithChildren : Element() {
    val children = arrayListOf<com.example.html.Element>()
}

class View(val name: String) : WithChildren() {
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
            c.render(builder, indent + "  ")
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