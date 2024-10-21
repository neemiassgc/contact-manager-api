package spring.manager.api.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(it -> it.requestMatchers(HttpMethod.POST, "/api/users").authenticated());
        httpSecurity.authorizeHttpRequests(it -> it.requestMatchers(HttpMethod.GET, "/_ah/warmup").permitAll());
        httpSecurity.authorizeHttpRequests(it -> it.anyRequest().authenticated());
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity.oauth2ResourceServer(it -> it.jwt(Customizer.withDefaults()));

        return httpSecurity.build();
    }
}