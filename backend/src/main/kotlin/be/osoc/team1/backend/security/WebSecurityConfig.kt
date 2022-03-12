package be.osoc.team1.backend.security

// import org.springframework.context.annotation.Configuration
// import org.springframework.security.config.annotation.web.builders.HttpSecurity
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
// import org.springframework.security.config.http.SessionCreationPolicy

// @Configuration
// @EnableWebSecurity
// class WebSecurityConfig : WebSecurityConfigurerAdapter() {
//     @Throws(Exception::class)
//     protected override fun configure(http: HttpSecurity) {
//         http
//             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             .csrf().disable()
//             .authorizeRequests()
//             .antMatchers("/").permitAll()
//             .anyRequest().authenticated()
//             .and()
//             .formLogin()
//             // .loginPage("/login")
//             .permitAll()
//     }
//
//     // @Bean
//     // override fun userDetailsService(): UserDetailsService {
//     //     val user: UserDetails = User.withDefaultPasswordEncoder()
//     //         .username("user")
//     //         .password("password")
//     //         .roles("USER")
//     //         .build()
//     //     return InMemoryUserDetailsManager(user)
//     // }
// }

// @EnableWebSecurity
// @Configuration
// class WebSecurityConfig : WebSecurityConfigurerAdapter() {
//     override fun configure(http: HttpSecurity) {
//         http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//             .csrf().disable()
//             .authorizeRequests()
//             .antMatchers("/").permitAll()
//             .anyRequest().authenticated()
//             .and()
//             .httpBasic()
//     }
// }
