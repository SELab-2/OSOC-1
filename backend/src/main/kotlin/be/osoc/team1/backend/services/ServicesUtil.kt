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
     * if [pageNumber] is greater than the length of the collection the length of the collection will be used
     * returns a [PagedCollection]
     */
    fun <T> paginate(collection: List<T>): PagedCollection<T> {
        val endOfPaging = Integer.min(collection.size, startOfPaging + pageSize) - 1
        return PagedCollection(collection.slice(startOfPaging..endOfPaging), collection.size)
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

/**
 * [PagedCollection] is a data class that holds a paginated [collection] and its pre-pagination length ([totalLength])
 */
data class PagedCollection<T>(val collection: List<T>, val totalLength: Int)

data class StudentFilter(val statusFilter: List<StatusEnum>, val nameQuery: String, val includeSuggested: Boolean)

fun <T> List<T>.page(pager: Pager) = pager.paginate(this)
