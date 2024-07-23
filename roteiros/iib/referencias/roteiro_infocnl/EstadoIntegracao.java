package br.com.bb.t99.integracao;

import jakarta.enterprise.context.RequestScoped;
import java.util.Optional;

@RequestScoped
public class EstadoIntegracao {
	private String token = "";

	void setToken(String token) {
		this.token = Optional.ofNullable(token).orElse("");
	}

	public String getToken() {
		return this.token;
	}
}
