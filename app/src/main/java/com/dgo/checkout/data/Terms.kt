package com.dgo.checkout.data

import android.content.Context
import org.json.JSONObject

data class TermsSection(val title: String, val body: String)

data class TermsDocument(
    val title: String,
    val scope: String,
    val preamble: String,
    val sections: List<TermsSection>,
    val acknowledgement: String,
)

/** Verbatim regional terms shared with the DHGO web prototype. */
object Terms {
    fun load(context: Context): Map<Boolean, TermsDocument> {
        val root = JSONObject(context.assets.open("terms.json").bufferedReader().use { it.readText() })
        return listOf(false to "nepal", true to "international").associate { (international, key) ->
            val document = root.getJSONObject(key)
            val sections = document.getJSONArray("sections")
            international to TermsDocument(
                title = document.getString("title"),
                scope = document.getString("scope"),
                preamble = document.getString("preamble"),
                sections = List(sections.length()) { index ->
                    val section = sections.getJSONObject(index)
                    TermsSection(section.getString("title"), section.getString("body"))
                },
                acknowledgement = document.getString("acknowledgement"),
            )
        }
    }
}
