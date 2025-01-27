package deti.fitmonitor.gyms.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    //change this later based on roles
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) //NOSONAR
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/default/api/token/*").permitAll()
                                .requestMatchers("/default/api/token/test").permitAll()
                                .requestMatchers("/api/token/*").permitAll()
                                .requestMatchers("/swagger-ui/*").permitAll()
                                .requestMatchers("/v3/api-docs").permitAll()
                                .requestMatchers("/default/api/gyms/machine").hasRole("Admin")
                                .requestMatchers("/actuator/health").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
        
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    
    
}
