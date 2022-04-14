package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.StatusEnum

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

class Pager(val pageNumber: Int, val pageSize: Int) {
    val startOfPaging = pageNumber * pageSize

    fun <T> paginate(collection: List<T>): List<T> {
        val endOfPaging = Integer.min(collection.size, startOfPaging + pageSize) - 1
        return collection.slice(startOfPaging..endOfPaging)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Pager) {
            return pageNumber == other.pageNumber && pageSize == other.pageSize
        }
        return false
    }
}

data class StudentFilter(val statusFilter: List<StatusEnum>, val nameQuery: String, val includeSuggested: Boolean)

fun <T> List<T>.page(pager: Pager) = pager.paginate(this)
