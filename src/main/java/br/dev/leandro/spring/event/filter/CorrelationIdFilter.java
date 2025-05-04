package br.dev.leandro.spring.event.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Filtro que adiciona um ID de correlação a cada requisição.
 * <p>
 * O ID de correlação é extraído do cabeçalho da requisição, se presente,
 * ou gerado como um novo UUID, se não estiver. Em seguida, é adicionado ao MDC para
 * fins de registro de log e aos cabeçalhos de resposta para rastreamento.
 * </p>
 */
@Slf4j
@Component
public class CorrelationIdFilter implements Filter {

    private final CorrelationIdProperties properties;
    private Pattern validationPattern;

    /**
     * Construtor com dependências necessárias.
     *
     * @param properties Propriedades de configuração para manipulação do ID de correlação
     */
    public CorrelationIdFilter(final CorrelationIdProperties properties) {
        this.properties = properties;
        this.validationPattern = Pattern.compile(properties.getValidationPattern());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String correlationId = null;

        try {
            // Extrai ou gera o ID de correlação
            correlationId = extractOrGenerateCorrelationId(httpRequest);

            // Adiciona ao MDC para registro de log
            MDC.put(properties.getMdcKey(), correlationId);

            // Adiciona aos cabeçalhos de resposta, se configurado
            if (properties.isAddToResponse()) {
                httpResponse.setHeader(properties.getHeaderName(), correlationId);
            }

            log.debug("Processando requisição com ID de correlação: {}", correlationId);

            // Continua com a cadeia de filtros
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Erro ao processar requisição com ID de correlação: {}", correlationId, e);
            throw e;
        } finally {
            log.debug("Requisição concluída com ID de correlação: {}", correlationId);
            MDC.remove(properties.getMdcKey());
        }
    }

    /**
     * Extrai o ID de correlação do cabeçalho da requisição ou gera um novo.
     *
     * @param request A requisição HTTP
     * @return O ID de correlação
     */
    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(properties.getHeaderName());

        // Se o cabeçalho estiver ausente ou vazio, gera um novo ID de correlação
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Gerado novo ID de correlação: {}", correlationId);
        } else {
            // Valida o formato do ID de correlação, se fornecido
            if (!validationPattern.matcher(correlationId).matches()) {
                log.warn("Formato de ID de correlação inválido: {}. Gerando um novo.", correlationId);
                correlationId = UUID.randomUUID().toString();
            } else {
                log.debug("Usando ID de correlação existente da requisição: {}", correlationId);
            }
        }

        return correlationId;
    }
}
