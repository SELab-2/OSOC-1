package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidCoachIdException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.exceptions.InvalidStudentIdException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import org.junit.jupiter.api.Test
import java.lang.Exception

// This test class is only here to cover these Exception constructors
// No reason to not cover these
class ExceptionTests {
    @Test
    // Work in progress
    fun `exception constructor coverage`() {
        val failedOperationException = FailedOperationException()
        println(failedOperationException.message)
        // assert(failedOperationException.message == "Failed operation")

        val forbiddenOperationException = ForbiddenOperationException(Exception())
        println(forbiddenOperationException.message)
        // assert(forbiddenOperationException.message == "Forbidden operation")

        val invalidCoachIdException = InvalidCoachIdException(Exception())
        println(invalidCoachIdException.message)
        // assert(invalidCoachIdException.message == "Wrong coach id given")

        val invalidIdException = InvalidIdException(Exception())
        println(invalidIdException.message)
        // assert(invalidIdException.message == "Invalid id")

        val invalidProjectIdException = InvalidProjectIdException(Exception())
        assert(invalidProjectIdException.message == "Wrong project id given")

        val invalidStudentIdException = InvalidStudentIdException(Exception())
        assert(invalidStudentIdException.message == "Wrong student id given")

        val invalidUserIdException = InvalidUserIdException(Exception())
        assert(invalidUserIdException.message == "Wrong user id given")
    }
}