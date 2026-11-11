
package com.wenderson.meuimovel.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // API REST sem sessao HTTP.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Autenticacao feita por Bearer Token.
                .csrf(AbstractHttpConfigurer::disable)

                // Desativa login por formulario e HTTP Basic.
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Regras de autorizacao.
                .authorizeHttpRequests(authorize -> {

                    // Documentacao Swagger acessivel em todos os ambientes.
                    authorize.requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**"
                    ).permitAll();

                    // Todos os demais endpoints protegidos.
                    authorize.anyRequest().authenticated();
                })

                // Validacao de JWT.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
