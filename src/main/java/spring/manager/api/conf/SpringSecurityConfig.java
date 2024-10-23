package spring.manager.api.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/api/users").authenticated());
        httpSecurity.authorizeHttpRequests(auth ->
            auth.requestMatchers(
                new AndRequestMatcher(
                    new RequestHeaderRequestMatcher("X-Appengine-Cron", "True"),
                    new AntPathRequestMatcher("/warmup-db", HttpMethod.GET.name())
                )
            ).permitAll());
        httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/warmup").permitAll());
        httpSecurity.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.logout(AbstractHttpConfigurer::disable);
        httpSecurity.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

        return httpSecurity.build();
    }
}