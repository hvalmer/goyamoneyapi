package com.goyamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)//ordem de prioridade alta, executando logo no início
public class CorsFilter implements Filter {

	private String originPermitida = "http://localhost:8000";//TODO: Configurar para diferentes ambientes, ou seja, Teste e Producao
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		//convertendo para Http
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;//Cookie do refresh-token para enviar
		
		//enviados sempre em todas as requisicoes, para continuar funcionando...
		//os dois headers precisam estar na resposta
		response.setHeader("Access-Control-Allow-Origin", originPermitida);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		//O CORS nada mais é que adicionar esses HTTP´s, que começam com Access-Control
		/*se a originPermitida = Origin, que veio do Browser e for uma req OPTIONS,
		 * permite setando os headers. Caso não, o CORS não estará habilitado*/ 
		if("OPTIONS".equals(request.getMethod()) && originPermitida.equals("Origin")) {
			//no response, setando varios headers
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");//metodos HTTP permitidos
			response.setHeader("Access-Control-Allow-Methods", "Authorization, Content-Type, Accept");//headers permitidos
			response.setHeader("Access-Control-Max-Age", "3600");//tempo de 1h p a proxima req
			
			response.setStatus(HttpServletResponse.SC_OK);
		}else {
			//se não for um OPTIONS de originPermitida, segue o fluxo normal
			chain.doFilter(req, resp);
		}
	}

}
