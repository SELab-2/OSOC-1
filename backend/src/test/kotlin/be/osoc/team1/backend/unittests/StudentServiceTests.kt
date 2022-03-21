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
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class StudentServiceTests {

    private val testStudent = Student("Tom", "Alard")
    private val studentId = testStudent.id
    private val testCoach = User("", "", Role.Coach, "")
    private val testSuggestion = StatusSuggestion(testCoach.id, SuggestionEnum.Yes, "test motivation")
    private val userService = mockk<UserService>()

    private fun getRepository(studentAlreadyExists: Boolean): StudentRepository {
        val repository: StudentRepository = mockk()
        every { repository.existsById(studentId) } returns studentAlreadyExists
        every { repository.findByIdOrNull(studentId) } returns if (studentAlreadyExists) testStudent else null
        every { repository.deleteById(studentId) } just Runs
        val differentIdTestStudent = Student("Tom", "Alard")
        every { repository.save(testStudent) } returns differentIdTestStudent
        every { repository.findAll() } returns listOf(testStudent)
        return repository
    }

    @Test
    fun `getAllStudents does not fail`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(service.getAllStudents(), listOf(testStudent))
    }

    @Test
    fun `getStudentById succeeds when student with id exists`() {
        val service = StudentService(getRepository(true), userService)
        assertEquals(service.getStudentById(studentId), testStudent)
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
    fun `addStudent returns some other id than what was passed`() {
        val service = StudentService(getRepository(false), userService)
        assertNotEquals(service.addStudent(testStudent), studentId)
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
