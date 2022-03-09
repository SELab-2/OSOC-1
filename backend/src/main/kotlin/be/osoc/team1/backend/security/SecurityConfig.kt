package be.osoc.team1.backend.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    // @Autowired
    // private val userRepository: UserRepository? = null

    // private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()

    private val passwordEncoder: PasswordEncoder = NoOpPasswordEncoder.getInstance()

    @Autowired
    @Throws(Exception::class)
    protected fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(
            UserDetailsService { s ->
                // val user: User = userRepository.findOneByUsername(s)
                //     ?: throw UsernameNotFoundException("The user with email $s was not found")
                User("user", "pwd", emptyList())
            }
        ).passwordEncoder(passwordEncoder)
    }

    @Throws(Exception::class)
    override fun configure(webSecurity: WebSecurity) {
        webSecurity
            .ignoring()
            .antMatchers(
                "/api/login", "api/logout", // "/ui/**", "/401.html", "/404.html", "/500.html", "/resources/**", "/templates/**",
            )
    }

    // @Configuration
    // @EnableAuthorizationServer
    // class OAuth2Configuration : AuthorizationServerConfigurerAdapter() {
    //     @Autowired
    //     private val authenticationManager: AuthenticationManager? = null
    //     @Bean
    //     fun accessTokenConverter(): JwtAccessTokenConverter {
    //         return JwtAccessTokenConverter()
    //     }
    //
    //     @Throws(Exception::class)
    //     fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
    //         oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
    //             .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
    //     }
    //
    //     @Throws(Exception::class)
    //     fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
    //         endpoints.authenticationManager(authenticationManager).accessTokenConverter(accessTokenConverter())
    //     }
    //
    //     @Throws(Exception::class)
    //     fun configure(clients: ClientDetailsServiceConfigurer) {
    //         clients.inMemory()
    //             .withClient("xxx")
    //             .resourceIds("xxx")
    //             .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
    //             .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
    //             .scopes("read", "write", "trust", "update")
    //             .accessTokenValiditySeconds(300)
    //             .refreshTokenValiditySeconds(240000)
    //             .secret("xxx")
    //     }
    // }

    /**
     * Github authentication
     */
    @Configuration
    @Order(0)
    class WebSecurityConfig : WebSecurityConfigurerAdapter() {
        @Throws(Exception::class)
        protected override fun configure(http: HttpSecurity) {
            http
                .authorizeRequests()
                .antMatchers("/api").permitAll()
                .antMatchers("/students").authenticated()
                // .anyRequest().authenticated()
                .and()
                .oauth2Login()
        }
    }

    /**
     * authentication with User object
     */
    @Configuration
    @Order(1)
    class FormLoginWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {
        @Throws(Exception::class)
        public override fun configure(http: HttpSecurity) {
            http
                .csrf().disable()
                .authorizeRequests()
                // .antMatchers("/ui/admin.xhtml").hasAnyAuthority("admin", "ADMIN")
                // .antMatchers("/thymeleaf").hasAnyAuthority("admin", "ADMIN")
                .antMatchers("/students").authenticated()
                .and()
                .formLogin()
                // .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=1")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .rememberMe()
                .and().exceptionHandling().accessDeniedPage("/error/403")
        }
    }

    // @Order(2)
    // @Configuration
    // @EnableResourceServer
    // class CustomResourceServerConfigurerAdapter : ResourceServerConfigurerAdapter() {
    //     @Bean
    //     fun loggerBean(): ApplicationListener<AbstractAuthorizationEvent> {
    //         return AuthenticationLoggerListener()
    //     }
    //
    //     @Bean
    //     fun accessDeniedHandler(): AccessDeniedHandler {
    //         return AccessDeniedExceptionHandler()
    //     }
    //
    //     @Bean
    //     fun entryPointBean(): AuthenticationEntryPoint {
    //         return UnauthorizedEntryPoint()
    //     }
    //
    //     @Throws(Exception::class)
    //     fun configure(http: HttpSecurity) {
    //         var contentNegotiationStrategy = http.getSharedObject(
    //             ContentNegotiationStrategy::class.java
    //         )
    //         if (contentNegotiationStrategy == null) {
    //             contentNegotiationStrategy = HeaderContentNegotiationStrategy()
    //         }
    //         val preferredMatcher = MediaTypeRequestMatcher(
    //             contentNegotiationStrategy,
    //             MediaType.APPLICATION_FORM_URLENCODED,
    //             MediaType.APPLICATION_JSON,
    //             MediaType.MULTIPART_FORM_DATA
    //         )
    //         http.authorizeRequests()
    //             .and()
    //             .anonymous().disable()
    //             .sessionManagement()
    //             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    //             .and().httpBasic()
    //             .and()
    //             .exceptionHandling()
    //             .accessDeniedHandler(accessDeniedHandler()) // handle access denied in general (for example comming from @PreAuthorization
    //             .authenticationEntryPoint(entryPointBean()) // handle authentication exceptions for unauthorized calls.
    //             .defaultAuthenticationEntryPointFor(entryPointBean(), preferredMatcher)
    //             .and()
    //             .authorizeRequests()
    //             .antMatchers("/api/**").fullyAuthenticated()
    //     }
    // }
}
