package be.osoc.team1.backend.security
//
// import org.springframework.boot.SpringApplication
// import org.springframework.boot.autoconfigure.SpringBootApplication
// import org.springframework.security.core.annotation.AuthenticationPrincipal
// import org.springframework.security.oauth2.core.user.OAuth2User
// import org.springframework.web.bind.annotation.GetMapping
// import org.springframework.web.bind.annotation.RestController
// import java.util.Collections
//
// @SpringBootApplication
// @RestController
// class SocialApplication {
//     @GetMapping("/user")
//     fun user(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any> {
//         return Collections.singletonMap("name", principal.getAttribute("name"))
//     }
//
//     companion object {
//         @JvmStatic
//         fun main(args: Array<String>) {
//             SpringApplication.run(SocialApplication::class.java, *args)
//         }
//     }
// }