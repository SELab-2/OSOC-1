package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User

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
 * [Pager] is used as a data-storing class that can paginate collections given a [pageNumber] and a [pageSize]
 */
class Pager(val pageNumber: Int, val pageSize: Int) {
    val startOfPaging = pageNumber * pageSize

    /**
     * paginate a [collection] based on [pageNumber] and [pageSize]
     */
    fun <T> paginate(collection: List<T>): List<T> {
        val endOfPaging = Integer.min(collection.size - 1, startOfPaging + pageSize)
        return collection.slice(startOfPaging..endOfPaging)
    }

    /**
     * This overwrite is necessary for the test classes,
     * without it the mockk won't recognize a [Pager] based on its arguments and will therefor fail
     */
    override fun equals(other: Any?): Boolean {
        if (other is Pager) {
            return pageNumber == other.pageNumber && pageSize == other.pageSize
        }
        return false
    }
}

/**
 * New filters can be defined here following this format:
 * ```
 *      fun filter(REQUIRED VARIABLES FOR FILTER) = { obj: Obj -> BOOLEAN FILTER STATEMENT }
 * ```
 * These filters can then later be applied to a collection with [applyFilterList]
 *
 * This function takes a list of [filters] as defined above and a list of [values] over which those [filters] will be applied
 */
fun <T> applyFilterList(filters: Iterable<(T) -> Boolean>, values: Iterable<T>): List<T> =
    values.filter { filters.all { filter -> filter(it) } }

// Student related filters
/**
 * Filter students based on their status field
 */
fun statusFilter(statuses: List<StatusEnum>) = { student: Student -> statuses.contains(student.status) }

/**
 * Filter students based on whether its name matches the query (in preprocessed form, see [nameMatchesSearchQuery])
 */
fun studentNameFilter(nameQuery: String) =
    { student: Student -> nameMatchesSearchQuery("${student.firstName} ${student.lastName}", nameQuery) }

/**
 * Filter students based on whether the given [callee] has already made a suggestion for them
 */
fun suggestedFilter(includeSuggested: Boolean, callee: User) =
    { student: Student -> includeSuggested || student.statusSuggestions.none { it.coachId == callee.id } }

