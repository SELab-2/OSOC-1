package be.osoc.team1.backend.services


private fun preprocess(string: String) = string.lowercase().replace(" ", "")

/**
 * Checks whether the [searchQuery] matches with the given [name]. This is done by first
 * calling the [preprocess] function on both arguments. If the modified [name] contains
 * the modified [searchQuery], this is considered a match and true is returned.
 * Otherwise, there is no match and false is returned.
 * Note that every string is considered to contain the empty string. This means that passing the empty
 * string as parameter to [searchQuery] will always return true.
 */
fun nameMatchesSearchQuery(name: String, searchQuery: String): Boolean =
    preprocess(name).contains(preprocess(searchQuery))