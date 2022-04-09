package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidStudentIdException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.services.Pager
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
import be.osoc.team1.backend.services.statusFilter
import be.osoc.team1.backend.services.studentNameFilter
import be.osoc.team1.backend.services.suggestedFilter
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class StudentServiceTests {

    private val testStudent = Student("Tom", "Alard")
    private val studentId = testStudent.id
    private val testCoach = User("", "", Role.Coach, "")
    private val testSuggestion = StatusSuggestion(testCoach.id, SuggestionEnum.Yes, "test motivation")
    private val userService = mockk<UserService>()
    private val defaultStatusFilter = listOf(StatusEnum.Yes, StatusEnum.No, StatusEnum.Maybe, StatusEnum.Undecided)
    private val defaultStudentFilter =
        listOf(statusFilter(defaultStatusFilter), studentNameFilter(""), suggestedFilter(true, testCoach))
    private val defaultPager = Pager(0, 50)
    private val defaultSort = Sort.by("id")

    private fun getRepository(studentAlreadyExists: Boolean): StudentRepository {
        val repository: StudentRepository = mockk()
        every { repository.existsById(studentId) } returns studentAlreadyExists
        every { repository.findByIdOrNull(studentId) } returns if (studentAlreadyExists) testStudent else null
        every { repository.deleteById(studentId) } just Runs
        val differentIdTestStudent = Student("Tom", "Alard")
        every { repository.save(testStudent) } returns differentIdTestStudent
        every { repository.findAll() } returns listOf(testStudent)
        every { repository.findAll(Sort.by("id")) } returns listOf(testStudent)
        return repository
    }

    @Test
    fun `getAllStudents does not fail`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(
            listOf(testStudent),
            service.getAllStudents(defaultPager, defaultSort, defaultStudentFilter)
        )
    }

    @Test
    fun `getAllStudents paging returns the correct amount`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(
            listOf(testStudent),
            service.getAllStudents(Pager(0, 1), defaultSort, defaultStudentFilter)
        )
    }

    @Test
    fun `getAllStudents paging with filtering returns the correct amount`() {
        val testStudent1 = Student("ATestoon", "Tamzia")
        testStudent1.status = StatusEnum.Maybe
        val testStudent2 = Student("BTestien", "Tamzia")
        testStudent2.status = StatusEnum.Yes
        val testStudent3 = Student("CTestaan", "Tamzia")
        testStudent3.status = StatusEnum.Yes

        val repository: StudentRepository = mockk()
        every { repository.findAll(Sort.by("name")) } returns listOf(testStudent1, testStudent2, testStudent3)
        val service = StudentService(repository, userService)
        assertEquals(
            listOf(testStudent2, testStudent3),
            service.getAllStudents(
                Pager(0, 2),
                Sort.by("name"),
                listOf(statusFilter(listOf(StatusEnum.Yes)), studentNameFilter(""), suggestedFilter(true, testCoach))
            )
        )
    }

    @Test
    fun `getAllStudents paging should not fail when list is empty`() {
        val repository: StudentRepository = mockk()
        val testList = listOf<Student>()
        every { repository.findAll(Sort.by("id")) } returns testList
        val service = StudentService(repository, userService)
        assertEquals(testList, service.getAllStudents(Pager(0, 1), defaultSort, defaultStudentFilter))
    }

    @Test
    fun `getAllStudents status filtering returns only students with those statuses`() {
        val testStudent = Student("Lars", "Cauter")
        val testStudent2 = Student("Sral", "Retuac")
        val testStudent3 = Student("Arsl", "Auterc")
        val testStudent4 = Student("Rsla", "Uterca")
        val allStudents = listOf(testStudent, testStudent2, testStudent3, testStudent4)
        val repository: StudentRepository = mockk()
        every { repository.findAll(Sort.by("id")) } returns allStudents
        val service = StudentService(repository, userService)
        testStudent.status = StatusEnum.Undecided
        testStudent2.status = StatusEnum.Yes
        testStudent3.status = StatusEnum.No
        testStudent4.status = StatusEnum.Maybe
        assertEquals(
            listOf(testStudent),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(
                    statusFilter(listOf(StatusEnum.Undecided)),
                    studentNameFilter(""),
                    suggestedFilter(true, testCoach)
                )
            )
        )
        assertEquals(
            listOf(testStudent2),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(statusFilter(listOf(StatusEnum.Yes)), studentNameFilter(""), suggestedFilter(true, testCoach))
            )
        )
        assertEquals(
            listOf(testStudent3),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(statusFilter(listOf(StatusEnum.No)), studentNameFilter(""), suggestedFilter(true, testCoach))
            )
        )
        assertEquals(
            listOf(testStudent4),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(
                    statusFilter(listOf(StatusEnum.Maybe)), studentNameFilter(""), suggestedFilter(true, testCoach)
                )
            )
        )
        assertEquals(
            allStudents,
            service.getAllStudents(defaultPager, defaultSort, defaultStudentFilter)
        )
    }

    @Test
    fun `getAllStudents name filtering returns only students with those names`() {
        val testStudent = Student("Lars", "Cauter")
        val testStudent2 = Student("Sral", "Retuac")
        val testStudent3 = Student("Arsl", "Auterc")
        val testStudent4 = Student("Rsla", "Uterca")
        val repository: StudentRepository = mockk()
        val allStudents = listOf(testStudent, testStudent2, testStudent3, testStudent4)
        every { repository.findAll(Sort.by("id")) } returns allStudents
        val service = StudentService(repository, userService)
        assertEquals(
            listOf(testStudent),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(statusFilter(defaultStatusFilter), studentNameFilter("lars"), suggestedFilter(true, testCoach))
            ),
        )
        assertEquals(
            listOf(testStudent, testStudent3),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(statusFilter(defaultStatusFilter), studentNameFilter("ars"), suggestedFilter(true, testCoach))
            ),
        )
        assertEquals(
            listOf(testStudent, testStudent3, testStudent4),
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(statusFilter(defaultStatusFilter), studentNameFilter("uter"), suggestedFilter(true, testCoach))
            ),
        )
        assertEquals(
            allStudents,
            service.getAllStudents(
                defaultPager,
                defaultSort,
                listOf(statusFilter(defaultStatusFilter), studentNameFilter(""), suggestedFilter(true, testCoach))
            ),
        )
    }

    @Test
    fun `getAllStudents include filtering works`() {
        val testStudent = Student("Lars", "Van")
        testStudent.statusSuggestions.add(StatusSuggestion(testCoach.id, SuggestionEnum.Yes, "Nice!"))
        val repository: StudentRepository = mockk()
        every { repository.findAll(Sort.by("id")) } returns listOf(testStudent)
        val service = StudentService(repository, userService)

        assertEquals(
            listOf<Student>(),
            service.getAllStudents(
                defaultPager, defaultSort,
                listOf(statusFilter(defaultStatusFilter), studentNameFilter(""), suggestedFilter(false, testCoach))
            )
        )
        assertEquals(
            listOf(testStudent),
            service.getAllStudents(
                defaultPager, defaultSort,
                listOf(statusFilter(defaultStatusFilter), studentNameFilter(""), suggestedFilter(true, testCoach))
            )
        )
    }

    @Test
    fun `getStudentById succeeds when student with id exists`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(testStudent, service.getStudentById(studentId))
    }

    @Test
    fun `getStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        assertThrows<InvalidStudentIdException> { service.getStudentById(studentId) }
    }

    @Test
    fun `deleteStudentById succeeds when student with id exists`() {
        val repository = getRepository(true)
        val service = StudentService(repository, userService)
        service.deleteStudentById(studentId)
        verify { repository.deleteById(studentId) }
    }

    @Test
    fun `deleteStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        assertThrows<InvalidStudentIdException> { service.deleteStudentById(studentId) }
    }

    @Test
    fun `addStudent saves student`() {
        val repository = getRepository(false)
        val service = StudentService(repository, userService)
        service.addStudent(testStudent)
        verify { repository.save(testStudent) }
    }

    @Test
    fun `addStudent returns student with some other id than what was passed`() {
        val service = StudentService(getRepository(false), userService)
        assertNotEquals(service.addStudent(testStudent).id, studentId)
    }

    @Test
    fun `setStudentStatus changes student status when student with id exists`() {
        val repository = getRepository(true)
        val service = StudentService(repository, userService)
        service.setStudentStatus(studentId, StatusEnum.Yes)
        testStudent.status = StatusEnum.Yes // Bit of a hack
        verify { repository.save(testStudent) }
        testStudent.status = StatusEnum.Undecided
    }

    @Test
    fun `setStudentStatus fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        assertThrows<InvalidStudentIdException> { service.setStudentStatus(studentId, StatusEnum.Yes) }
    }

    @Test
    fun `addStudentStatusSuggestion adds status suggestion to list when student with id exists`() {
        val repository: StudentRepository = mockk()
        val student: Student = mockk()
        every { student.statusSuggestions.add(testSuggestion) } returns true
        every { student.statusSuggestions.iterator() } returns mutableListOf<StatusSuggestion>().iterator()
        every { repository.findByIdOrNull(studentId) } returns student
        every { repository.save(student) } returns student
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns testCoach
        val service = StudentService(repository, customUserService)
        service.addStudentStatusSuggestion(studentId, testSuggestion)
        val suggestionId = testSuggestion.id
        verify { student.statusSuggestions.add(testSuggestion) }
        assert(testSuggestion.id == suggestionId)
        assert(testSuggestion.student == student)
    }

    @Test
    fun `addStudentStatusSuggestion fails when no student with that id exists`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns testCoach
        val service = StudentService(getRepository(false), customUserService)
        assertThrows<InvalidStudentIdException> { service.addStudentStatusSuggestion(studentId, testSuggestion) }
    }

    @Test
    fun `addStudentStatusSuggestion fails when coach already made suggestion for student`() {
        val repository: StudentRepository = mockk()
        val student: Student = mockk()
        val coachId2 = UUID.randomUUID()
        val testSuggestion2 = StatusSuggestion(coachId2, SuggestionEnum.No, "test motivation2")
        every { student.statusSuggestions.iterator() } returns mutableListOf(testSuggestion2, testSuggestion).iterator()
        every { repository.findByIdOrNull(studentId) } returns student
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns testCoach
        val service = StudentService(repository, customUserService)
        assertThrows<ForbiddenOperationException> { service.addStudentStatusSuggestion(studentId, testSuggestion) }
    }

    @Test
    fun `addStudentStatusSuggestion fails when coach does not exist`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) }.throws(InvalidUserIdException())
        val service = StudentService(getRepository(true), customUserService)
        assertThrows<InvalidUserIdException> { service.addStudentStatusSuggestion(studentId, testSuggestion) }
    }

    @Test
    fun `addStudentStatusSuggestion fails when user does not have coach role`() {
        val disabledUser = User("", "", Role.Disabled, "")
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns disabledUser
        val service = StudentService(getRepository(true), customUserService)
        assertThrows<ForbiddenOperationException> { service.addStudentStatusSuggestion(studentId, testSuggestion) }
    }

    @Test
    fun `deleteStudentStatusSuggestion removes suggestion when student, suggestion and coach exist`() {
        val repository: StudentRepository = mockk()
        val student: Student = mockk()
        val coachId2 = UUID.randomUUID()
        val testSuggestion2 = StatusSuggestion(coachId2, SuggestionEnum.No, "test motivation2")
        every { student.statusSuggestions.remove(testSuggestion) } returns true
        every { student.statusSuggestions.iterator() } returns mutableListOf(testSuggestion2, testSuggestion).iterator()
        every { repository.findByIdOrNull(studentId) } returns student
        every { repository.save(student) } returns student
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns testCoach
        val service = StudentService(repository, customUserService)
        service.deleteStudentStatusSuggestion(studentId, testCoach.id)
        verify { student.statusSuggestions.remove(testSuggestion) }
    }

    @Test
    fun `deleteStudentStatusSuggestion fails when no student with that id exists`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns testCoach
        val service = StudentService(getRepository(false), customUserService)
        assertThrows<InvalidStudentIdException> { service.deleteStudentStatusSuggestion(studentId, testCoach.id) }
    }

    @Test
    fun `deleteStudentStatusSuggestion fails when given coach hasn't made a suggestion for this student`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) } returns testCoach
        val service = StudentService(getRepository(true), customUserService)
        assertThrows<FailedOperationException> { service.deleteStudentStatusSuggestion(studentId, testCoach.id) }
    }

    @Test
    fun `deleteStudentStatusSuggestion fails when coach does not exist`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.coachId) }.throws(InvalidUserIdException())
        val service = StudentService(getRepository(true), customUserService)
        assertThrows<InvalidUserIdException> { service.deleteStudentStatusSuggestion(studentId, testCoach.id) }
    }

    @Test
    fun `addCommunicationToStudent adds communication to list of student`() {
        val repository = getRepository(true)
        val service = StudentService(repository, userService)
        val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
        service.addCommunicationToStudent(studentId, testCommunication)
        testStudent.communications.add(testCommunication) // Bit of a hack
        verify { repository.save(testStudent) }
        testStudent.communications.remove(testCommunication)
    }

    @Test
    fun `addCommunicationToStudent fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
        assertThrows<InvalidStudentIdException> { service.addCommunicationToStudent(studentId, testCommunication) }
    }
}
