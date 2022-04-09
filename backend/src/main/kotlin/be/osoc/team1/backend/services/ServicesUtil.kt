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

class Pager(val pageNumber: Int, val pageSize: Int) {
    val startOfPaging = pageNumber * pageSize

    fun <T> paginate(collection: List<T>): List<T> {
        val endOfPaging = Integer.min(collection.size - 1, startOfPaging + pageSize)
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

fun filterStudents(students: Iterable<Student>, filters: StudentFilter, callee: User): List<Student> {
    val filteredStudents = mutableListOf<Student>()
    for (student in students) {
        val studentHasStatus = filters.statusFilter.contains(student.status)
        val fullName = "${student.firstName} ${student.lastName}"
        val studentHasMatchingName = nameMatchesSearchQuery(fullName, filters.nameQuery)
        val studentBeenSuggestedByUserCheck =
            filters.includeSuggested || student.statusSuggestions.none { it.coachId == callee.id }
        if (studentHasStatus && studentHasMatchingName && studentBeenSuggestedByUserCheck) {
            filteredStudents.add(student)
        }
    }
    return filteredStudents
}

fun <T> List<T>.page(pager: Pager) = pager.paginate(this)
