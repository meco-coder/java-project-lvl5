package hexlet.code.config.security;


import hexlet.code.filter.JWTFilter;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepo;
    private UserServiceImpl userDetailsService;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private JWTFilter jwtFilter;

    public SecurityConfig(@Lazy UserServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).
                passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.headers().frameOptions().disable();

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/login").permitAll();

        http.authorizeRequests().antMatchers("/api/users").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.PUT, "/api/users/**")
                .hasAnyAuthority("USER")
                .and().authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/users/**")
                .hasAnyAuthority("USER");

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/statuses/**").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.POST, "/api/statuses/**")
                .hasAuthority("USER")
                .and().authorizeRequests().antMatchers(HttpMethod.PUT, "/api/statuses/**")
                .hasAnyAuthority("USER")
                .and().authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/statuses/**")
                .hasAuthority("USER");

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/tasks/**").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.POST, "/api/tasks/**")
                .hasAuthority("USER")
                .and().authorizeRequests().antMatchers(HttpMethod.PUT, "/api/tasks/**")
                .hasAnyAuthority("USER")
                .and().authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/tasks/**")
                .hasAuthority("USER");

        http.authorizeRequests().antMatchers("/api/labels/**").hasAuthority("USER");


        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


    }

}
