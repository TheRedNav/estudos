> :grey_exclamation: **Importante** 
> 
> A página de *troubleshooting* serve como um recurso central para solucionar problemas comuns relacionados aos Bancos de Dados. <br>
>O problema é sempre identificado no título, e abaixo oferecemos soluções testadas e diretrizes simples para ajudar na resolução.

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/troubleshooting.md&amp;action_name=databases/troubleshooting.md)

# Redis

## Falha ao tentar obter uma conexão do pool de conexões fornecido pela biblioteca Java Jedis

**Descrição:** durante a operação normal da aplicação, o usuário tentou adicionar ou atualizar registros no Redis e recebeu o erro:
```
Exception in thread "main" redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
```
**Solução:** o servidor Redis está configurado no modo somente leitura (*read-only*). Modifique a configuração do servidor para alterar a restrição, lembrando que entre os servidores Redis disponibilizados, dois funcionam como réplicas e aceitam apenas consultas. <br> 
```
CONFIG SET read-only no
``` 
> :warning: **Atenção** 
> 
> Observe que alterar essa configuração não significa que seja seguro expor uma instância de réplica a clientes não confiáveis. Comandos administrativos como DEBUG e CONFIG ainda estão habilitados. Para garantir a segurança de uma instância, consulte a página de [Segurança no site do Redis](https://redis.io/docs/latest/operate/oss_and_stack/management/security/).

## Ainda não encontrou a solução do seu problema?
Abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!   
