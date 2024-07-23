package br.com.bb.t99.integracao;

import org.eclipse.microprofile.opentracing.Traced;

import jakarta.interceptor.InterceptorBinding;
import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Traced
@NameBinding
@InterceptorBinding
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface IntegracaoIIB {}

