package com.goyamoney.api.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * LOGOUT---o access_token(manipulado no JS) tem um tempo de vida menor
 * já o refresh_token, tem um periodo de expiracao maior, por outro lado é mais seguro
 * em Https, no caso do client angular, qdo o client clicar em logout, vai apagar o 
 * access_token da memoria que o browser tiver usando e chama no servidor p remover o 
 * refresh_token e tira-lo do cookie
 * Esse logout é simplesmente tirar o valor do refresh_token*/

@RestController
@RequestMapping("/tokens")
public class TokenResource {

	//revoke - revogar, invalidar um token
	@DeleteMapping("/revoke")
	public void revoke(HttpServletRequest req, HttpServletResponse resp) {
		//removendo o Cookie
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);//TODO: em producao true
		cookie.setPath(req.getContextPath() + "/oauth/token");
		cookie.setMaxAge(0);
		
		resp.addCookie(cookie);
		resp.setStatus(HttpStatus.NO_CONTENT.value());
		
	}
}
