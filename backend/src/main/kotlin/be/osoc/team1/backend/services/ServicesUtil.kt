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

/**
 * [Pager] is used as a data-storing class that can paginate collections given a [pageNumber] (starts at zero) and a [pageSize] (both are expected to be positive)
 */
class Pager(val pageNumber: Int, val pageSize: Int) {
    val startOfPaging = pageNumber * pageSize

    /**
     * Paginate a [collection] based on [pageNumber] and [pageSize]
     * If the given [pageNumber] and [pageSize] would refer to a page beyond the total number of results, an empty page will be returned.
     * If the requested [pageNumber] and [pageSize] would lead to the last page, only the remaining results will be returned in a (shorter) list.
     */
    fun <T> paginate(collection: List<T>): List<T> {
        val endOfPaging = Integer.min(collection.size, startOfPaging + pageSize) - 1
        return collection.slice(startOfPaging..endOfPaging)
    }

    /**
     * This overwrite is necessary for the test classes,
     * without it the mockk won't recognize a [Pager] based on its arguments and will therefore fail
     */
    override fun equals(other: Any?): Boolean {
        if (other is Pager) {
            return pageNumber == other.pageNumber && pageSize == other.pageSize
        }
        return false
    }
}

data class StudentFilter(val statusFilter: List<StatusEnum>, val nameQuery: String, val includeSuggested: Boolean)

fun <T> List<T>.page(pager: Pager) = pager.paginate(this)
