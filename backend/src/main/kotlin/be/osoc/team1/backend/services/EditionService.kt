package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Role
import org.springframework.stereotype.Service

@Service
class EditionService(
    private val studentService: StudentService,
    private val projectService: ProjectService,
    private val userService: UserService
) {

    fun createNewEdition() {
        deleteStudents()
        deleteProjects()
        deleteCoachesAndDisabledUsers()
    }

    private fun deleteStudents() {
        for (student in studentService.getAllStudents()) {
            studentService.deleteStudentById(student.id)
        }
    }

    private fun deleteProjects() {
        for (project in projectService.getAllProjects()) {
            projectService.deleteProjectById(project.id)
        }
    }

    private fun deleteCoachesAndDisabledUsers() {
        for (user in userService.getAllUsers()) {
            if (!user.role.hasPermissionLevel(Role.Admin)) {
                userService.deleteUserById(user.id)
            }
        }
    }
}
