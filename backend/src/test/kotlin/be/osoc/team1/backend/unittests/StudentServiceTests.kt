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
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.services.PagedCollection
import be.osoc.team1.backend.services.Pager
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
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

class StudentServiceTests {

    private val testEdition = "testEdition"
    private val testStudent = Student("Tom", "Alard", testEdition)
    private val studentId = testStudent.id
    private val testCoach = User("", "", Role.Coach, "")
    private val testSuggestion = StatusSuggestion(testCoach, SuggestionEnum.Yes, "test motivation")
    private val userService = mockk<UserService>()
    private val defaultSort = Sort.by("id")

    private fun getRepository(studentAlreadyExists: Boolean): StudentRepository {
        val repository: StudentRepository = mockk()
        every { repository.existsById(studentId) } returns studentAlreadyExists
        every { repository.findByIdAndEdition(studentId, testEdition) } returns if (studentAlreadyExists) testStudent else null
        every { repository.deleteById(studentId) } just Runs
        val differentIdTestStudent = Student("Tom", "Alard", testEdition)
        every { repository.save(testStudent) } returns differentIdTestStudent
        every { repository.findByEdition(testEdition, defaultSort) } returns listOf(testStudent)
        return repository
    }

    @Test
    fun `getAllStudents does not fail`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(listOf(testStudent), service.getAllStudents(defaultSort, testEdition))
    }

    @Test
    fun `getStudentById succeeds when student with id exists`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(testStudent, service.getStudentById(studentId, testEdition))
    }

    @Test
    fun `getStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        assertThrows<InvalidStudentIdException> { service.getStudentById(studentId, testEdition) }
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
        service.setStudentStatus(studentId, StatusEnum.Yes, testEdition)
        testStudent.status = StatusEnum.Yes // Bit of a hack
        verify { repository.save(testStudent) }
        testStudent.status = StatusEnum.Undecided
    }

    @Test
    fun `setStudentStatus fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        assertThrows<InvalidStudentIdException> { service.setStudentStatus(studentId, StatusEnum.Yes, testEdition) }
    }

    @Test
    fun `addStudentStatusSuggestion adds status suggestion to list when student with id exists`() {
        val repository: StudentRepository = mockk()
        val student: Student = mockk()
        every { student.statusSuggestions.add(testSuggestion) } returns true
        every { student.statusSuggestions.iterator() } returns mutableListOf<StatusSuggestion>().iterator()
        every { repository.findByIdAndEdition(studentId, testEdition) } returns student
        every { repository.save(student) } returns student
        val customUserService: UserService = mockk()
        val service = StudentService(repository, customUserService)
        service.addStudentStatusSuggestion(studentId, testSuggestion, testEdition)
        val suggestionId = testSuggestion.id
        verify { student.statusSuggestions.add(testSuggestion) }
        assert(testSuggestion.id == suggestionId)
    }

    @Test
    fun `addStudentStatusSuggestion fails when no student with that id exists`() {
        val customUserService: UserService = mockk()
        val service = StudentService(getRepository(false), customUserService)
        assertThrows<InvalidStudentIdException> { service.addStudentStatusSuggestion(studentId, testSuggestion, testEdition) }
    }

    @Test
    fun `addStudentStatusSuggestion fails when coach already made suggestion for student`() {
        val repository: StudentRepository = mockk()
        val student: Student = mockk()
        val coach2 = User("coach 2", "coach2@email.com", Role.Admin, "password")
        val testSuggestion2 = StatusSuggestion(coach2, SuggestionEnum.No, "test motivation2")
        every { student.statusSuggestions.iterator() } returns mutableListOf(testSuggestion2, testSuggestion).iterator()
        every { repository.findByIdAndEdition(studentId, testEdition) } returns student
        val customUserService: UserService = mockk()
        val service = StudentService(repository, customUserService)
        assertThrows<ForbiddenOperationException> { service.addStudentStatusSuggestion(studentId, testSuggestion, testEdition) }
    }

    @Test
    fun `deleteStudentStatusSuggestion removes suggestion when student, suggestion and suggester exist`() {
        val repository: StudentRepository = mockk()
        val student: Student = mockk()
        val coach2 = User("coach 2", "coach2@email.com", Role.Admin, "password")
        val testSuggestion2 = StatusSuggestion(coach2, SuggestionEnum.No, "test motivation2")
        every { student.statusSuggestions.remove(testSuggestion) } returns true
        every { student.statusSuggestions.iterator() } returns mutableListOf(testSuggestion2, testSuggestion).iterator()
        every { repository.findByIdAndEdition(studentId, testEdition) } returns student
        every { repository.save(student) } returns student
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testSuggestion.suggester.id) } returns testCoach
        val service = StudentService(repository, customUserService)
        service.deleteStudentStatusSuggestion(studentId, testCoach.id, testEdition)
        verify { student.statusSuggestions.remove(testSuggestion) }
    }

    @Test
    fun `deleteStudentStatusSuggestion fails when no student with that id exists`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testCoach.id) } returns testCoach
        val service = StudentService(getRepository(false), customUserService)
        assertThrows<InvalidStudentIdException> { service.deleteStudentStatusSuggestion(studentId, testCoach.id, testEdition) }
    }

    @Test
    fun `deleteStudentStatusSuggestion fails when given coach hasn't made a suggestion for this student`() {
        val customUserService: UserService = mockk()
        every { customUserService.getUserById(testCoach.id) } returns testCoach
        val service = StudentService(getRepository(true), customUserService)
        assertThrows<FailedOperationException> { service.deleteStudentStatusSuggestion(studentId, testCoach.id, testEdition) }
    }

    @Test
    fun `addCommunicationToStudent adds communication to list of student`() {
        val repository = getRepository(true)
        val service = StudentService(repository, userService)
        val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
        service.addCommunicationToStudent(studentId, testCommunication, testEdition)
        testStudent.communications.add(testCommunication) // Bit of a hack
        verify { repository.save(testStudent) }
        testStudent.communications.remove(testCommunication)
    }

    @Test
    fun `addCommunicationToStudent fails when no student with that id exists`() {
        val service = StudentService(getRepository(false), userService)
        val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
        assertThrows<InvalidStudentIdException> { service.addCommunicationToStudent(studentId, testCommunication, testEdition) }
    }

    @Test
    fun `pager class paginates collections correctly`() {
        val pager = Pager(0, 1)
        val student1 = Student("Testoon", "Tamzia", testEdition)
        val student2 = Student("Testien", "Tamzia", testEdition)
        val student3 = Student("Testaan", "Tamzia", testEdition)
        val collection = listOf(student1, student2, student3)
        assertEquals(listOf(student1), pager.paginate(collection).collection)
    }

    @Test
    fun `pager class returns less items than requested if the collection is smaller`() {
        val pager = Pager(0, 5)
        val student1 = Student("Testoon", "Tamzia", testEdition)
        val student2 = Student("Testien", "Tamzia", testEdition)
        val student3 = Student("Testaan", "Tamzia", testEdition)
        val collection = listOf(student1, student2, student3)
        assertEquals(collection, pager.paginate(collection).collection)
    }

    @Test
    fun `pager class returns empty list when start of paging-request is out of bounds`() {
        val pager = Pager(1, 5)
        val student1 = Student("Testoon", "Tamzia", testEdition)
        val student2 = Student("Testien", "Tamzia", testEdition)
        val student3 = Student("Testaan", "Tamzia", testEdition)
        val collection = listOf(student1, student2, student3)
        assertEquals(listOf<Student>(), pager.paginate(collection).collection)
    }
    @Test
    fun `pager class returns a pagedcollection with the correct total amount`() {
        val student1 = Student("Testoon", "Tamzia", testEdition)
        val student2 = Student("Testien", "Tamzia", testEdition)
        val student3 = Student("Testaan", "Tamzia", testEdition)
        val collection = listOf(student1, student2, student3)
        assertEquals(PagedCollection(collection, 3), Pager(0, 5).paginate(collection))
        assertEquals(PagedCollection(listOf(student1), 3), Pager(0, 1).paginate(collection))
        assertEquals(PagedCollection(collection, 3), Pager(0, 3).paginate(collection))
    }
}
