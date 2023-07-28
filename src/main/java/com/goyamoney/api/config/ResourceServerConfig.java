package com.goyamoney.api.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
//anothation para habilitar a seguranca nos metodos
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

	//injetando a dependencia
	@Autowired
	private UserDetailsService userDetailsService;
	
	//validando o usuário e senha na API, no caso o Postman
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder());
	}
	
	//configuração de autorização das requisições
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/categorias").permitAll()
				.anyRequest().authenticated()
				.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.csrf().disable()
					.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
	}
	
	
	private JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
	        List<String> authorities = jwt.getClaimAsStringList("authorities");

	        if (authorities == null) {
	            authorities = Collections.emptyList();
	        }

	        JwtGrantedAuthoritiesConverter scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	        Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);

	        grantedAuthorities.addAll(authorities.stream()
	                .map(SimpleGrantedAuthority::new)
	                .collect(Collectors.toList()));

	        return grantedAuthorities;
	    });

	    return jwtAuthenticationConverter;
	}

	//A configuração do JWT no Spring Security 5.5 exige a criação de um bean para a decodificação
	//do mesmo, o que pode ser feito com o seguinte código.
	@Bean
	public JwtDecoder jwtDecoder() {
	    String secretKeyString = "3032885ba9cd6621bcc4e7d6b6c35c2b";
	    var secretKey = new SecretKeySpec(secretKeyString.getBytes(), "HmacSHA256");

	    return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}
	
	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean();
	}

	//será usado para fazer o decode da senha do usuário e da secret do cliente, 
	//utilizando o BCrypt.	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }
}
