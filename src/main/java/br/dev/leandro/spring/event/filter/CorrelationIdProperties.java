package br.dev.leandro.spring.event.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de configuração para manipulação do ID de correlação.
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "correlation")
public class CorrelationIdProperties {

    /**
     * O nome do cabeçalho HTTP para o ID de correlação.
     */
    private String headerName = "X-Correlation-ID";

    /**
     * A chave MDC para o ID de correlação.
     */
    private String mdcKey = "correlationId";

    /**
     * Se deve adicionar o ID de correlação aos cabeçalhos de resposta.
     */
    private boolean addToResponse = true;

    /**
     * Padrão de expressão regular para validar IDs de correlação.
     * O padrão permite formato UUID e strings alfanuméricas.
     */
    private String validationPattern = "^[a-zA-Z0-9-_]+$";

}
