package com.goyamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//<OAuth2AccessToken>(parametro generico) - é o tipo do dado que será interceptado qdo estiver voltando 
@SuppressWarnings("deprecation")
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		//convertendo com casting...
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		//fazendo um casting do OAuth...
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		//criando um novo método
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		//removendo o refresh token do body
		removerRefreshTokenDoBody(token);
				
		return body;
	}

	//retira o refresh token do body
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req,
			HttpServletResponse resp) {
		//criando o cookie
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		//só acessivel em http
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(false); //TODO:Mudar para true em producao
		//qual caminho que esse Cokie sera enviado pelo browser automaticamente
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		//quanto tempo o Cookie vai expirar em dias,30 dias
		refreshTokenCookie.setMaxAge(2592000);
		//adicionando o Cookie na resposta
		resp.addCookie(refreshTokenCookie);
	}
	
}
