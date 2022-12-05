import com.example.html.* // see declarations below
import de.hshl.isd.ui.Destination
import de.hshl.isd.ui.view

fun buildView() =
    view("Main") {
        state<String>("poem")
    }

fun result(args: Array<String>) =
    html {
        head {
            title {+"XML encoding with Kotlin"}
        }
        body {
            h1 {+"XML encoding with Kotlin"}
            p  {+"this format can be used as an alternative markup to XML"}

            // an element with attributes and text content
            a(href = "https://kotlinlang.org") {+"Kotlin"}

            // mixed content
            p {
                +"This is some"
                b {+"mixed"}
                +"text. For more see the"
                a(href = "https://kotlinlang.org") {+"Kotlin"}
                +"project"
            }
            p {+"some text"}

            // content generated by
            p {
                for (arg in args)
                    +arg
            }
        }
    }

val destination = Destination.SwiftUI
fun main(args: Array<String>) {
    println(buildView())
    //println(result(args))
}