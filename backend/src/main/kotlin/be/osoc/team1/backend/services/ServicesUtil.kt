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

/**
 * [Pager] is used as a data-storing class that can paginate collections given a [pageNumber] (starts at zero) and a [pageSize] (both are expected to be positive)
 */
class Pager(val pageNumber: Int, val pageSize: Int) {
    val startOfPaging = pageNumber * pageSize

    /**
     * Paginate a [collection] based on [pageNumber] and [pageSize]
     * If the given [pageNumber] and [pageSize] would refer to a page beyond the total number of results, an empty page will be returned.
     * If the requested [pageNumber] and [pageSize] would lead to the last page, only the remaining results will be returned in a (shorter) list.
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

fun <T> List<T>.page(pager: Pager) = pager.paginate(this)

/**
 * Apply the function [block] on object of type [T] only if the [condition] is met. If the [condition] is not met then
 * no changes will be applied to the object.
 */
fun <T> T.applyIf(condition: Boolean, block: T.() -> T): T =
    if (condition) this.block() else this
