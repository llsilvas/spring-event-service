package br.dev.leandro.spring.event.unit.filter;

import br.dev.leandro.spring.event.filter.CorrelationIdFilter;
import br.dev.leandro.spring.event.filter.CorrelationIdProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o {@link CorrelationIdFilter}.
 * 
 * <p>Esta classe testa o comportamento do filtro de ID de correlação em diferentes cenários,
 * garantindo que ele funcione corretamente em todas as situações possíveis.</p>
 */
@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CorrelationIdProperties properties;
    private CorrelationIdFilter filter;

    @BeforeEach
    void configurar() {
        // Configuração padrão das propriedades
        properties = new CorrelationIdProperties();
        properties.setHeaderName("X-Correlation-ID");
        properties.setMdcKey("correlationId");
        properties.setAddToResponse(true);
        properties.setValidationPattern("^[a-zA-Z0-9-_]+$");

        filter = new CorrelationIdFilter(properties);
    }

    @Nested
    @DisplayName("Testes de geração de ID de correlação")
    class GeracaoIdCorrelacao {

        @Test
        @DisplayName("Deve gerar novo ID de correlação quando não estiver presente")
        void deveGerarNovoIdQuandoNaoPresente() throws ServletException, IOException {
            // Dado
            when(request.getHeader(properties.getHeaderName())).thenReturn(null);

            // Quando
            filter.doFilter(request, response, filterChain);

            // Então
            verify(filterChain).doFilter(request, response);

            // Captura o ID gerado para verificação
            ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
            verify(response).setHeader(eq(properties.getHeaderName()), idCaptor.capture());

            // Verifica se o ID capturado não é nulo e segue o padrão UUID
            String capturedId = idCaptor.getValue();
            assertNotNull(capturedId);
            assertTrue(capturedId.matches("^[a-zA-Z0-9-]+$"), "O ID gerado deve seguir o padrão UUID");

            // O MDC deve ser limpo após a execução do filtro
            assertNull(MDC.get(properties.getMdcKey()), "O MDC deve ser limpo após a execução");
        }

        @Test
        @DisplayName("Deve usar ID de correlação existente quando presente e válido")
        void deveUsarIdExistenteQuandoValido() throws ServletException, IOException {
            // Dado
            String idCorrelacaoExistente = UUID.randomUUID().toString();
            when(request.getHeader(properties.getHeaderName())).thenReturn(idCorrelacaoExistente);

            // Quando
            filter.doFilter(request, response, filterChain);

            // Então
            verify(filterChain).doFilter(request, response);
            verify(response).setHeader(properties.getHeaderName(), idCorrelacaoExistente);

            // O MDC deve ser limpo após a execução do filtro
            assertNull(MDC.get(properties.getMdcKey()), "O MDC deve ser limpo após a execução");
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid@correlation#id", "correlation/id", "id com espaço"})
        @DisplayName("Deve gerar novo ID quando o formato for inválido")
        void deveGerarNovoIdQuandoFormatoInvalido(String idInvalido) throws ServletException, IOException {
            // Dado
            when(request.getHeader(properties.getHeaderName())).thenReturn(idInvalido);

            // Quando
            filter.doFilter(request, response, filterChain);

            // Então
            verify(filterChain).doFilter(request, response);

            // Captura o ID gerado para verificação
            ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
            verify(response).setHeader(eq(properties.getHeaderName()), idCaptor.capture());

            // Verifica se o ID capturado não é o mesmo que o inválido
            String capturedId = idCaptor.getValue();
            assertNotEquals(idInvalido, capturedId, "O ID inválido deve ser substituído por um novo");
            assertTrue(capturedId.matches("^[a-zA-Z0-9-]+$"), "O ID gerado deve seguir o padrão válido");

            // O MDC deve ser limpo após a execução do filtro
            assertNull(MDC.get(properties.getMdcKey()), "O MDC deve ser limpo após a execução");
        }
    }

    @Nested
    @DisplayName("Testes de configuração do filtro")
    class ConfiguracaoFiltro {

        @Test
        @DisplayName("Não deve adicionar aos cabeçalhos de resposta quando configurado")
        void naoDeveAdicionarAosCabecalhosQuandoConfigurado() throws ServletException, IOException {
            // Dado
            properties.setAddToResponse(false);
            filter = new CorrelationIdFilter(properties);
            when(request.getHeader(properties.getHeaderName())).thenReturn(null);

            // Quando
            filter.doFilter(request, response, filterChain);

            // Então
            verify(filterChain).doFilter(request, response);
            verify(response, never()).setHeader(eq(properties.getHeaderName()), anyString());

            // O MDC deve ser limpo após a execução do filtro
            assertNull(MDC.get(properties.getMdcKey()), "O MDC deve ser limpo após a execução");
        }

        @Test
        @DisplayName("Deve usar padrão de validação personalizado")
        void deveUsarPadraoValidacaoPersonalizado() throws ServletException, IOException {
            // Dado
            properties.setValidationPattern("^[0-9]+$"); // Apenas dígitos
            filter = new CorrelationIdFilter(properties);

            // ID válido segundo o novo padrão
            String idValido = "123456789";
            when(request.getHeader(properties.getHeaderName())).thenReturn(idValido);

            // Quando
            filter.doFilter(request, response, filterChain);

            // Então
            verify(response).setHeader(properties.getHeaderName(), idValido);

            // Agora com ID inválido segundo o novo padrão
            reset(response, filterChain);
            when(request.getHeader(properties.getHeaderName())).thenReturn("abc123");

            // Quando
            filter.doFilter(request, response, filterChain);

            // Então - deve gerar novo ID
            verify(response).setHeader(eq(properties.getHeaderName()), argThat(id -> !id.equals("abc123")));
        }
    }

    @Nested
    @DisplayName("Testes de tratamento de exceções")
    class TratamentoExcecoes {

        @Test
        @DisplayName("Deve tratar exceção e limpar MDC")
        void deveTratarExcecaoELimparMdc() throws ServletException, IOException {
            // Dado
            when(request.getHeader(properties.getHeaderName())).thenReturn(null);
            doThrow(new RuntimeException("Exceção de teste")).when(filterChain).doFilter(request, response);

            // Quando/Então
            Exception exception = assertThrows(RuntimeException.class, 
                    () -> filter.doFilter(request, response, filterChain),
                    "Deve propagar a exceção original");

            assertEquals("Exceção de teste", exception.getMessage());

            // O MDC deve ser limpo mesmo quando ocorre uma exceção
            assertNull(MDC.get(properties.getMdcKey()), "O MDC deve ser limpo mesmo com exceção");
        }

        @Test
        @DisplayName("Deve limpar MDC mesmo com exceção de ServletException")
        void deveLimparMdcComServletException() throws ServletException, IOException {
            // Dado
            when(request.getHeader(properties.getHeaderName())).thenReturn(null);
            doThrow(new ServletException("Erro de servlet")).when(filterChain).doFilter(request, response);

            // Quando/Então
            Exception exception = assertThrows(ServletException.class, 
                    () -> filter.doFilter(request, response, filterChain));

            assertEquals("Erro de servlet", exception.getMessage());

            // O MDC deve ser limpo mesmo quando ocorre uma exceção
            assertNull(MDC.get(properties.getMdcKey()), "O MDC deve ser limpo mesmo com exceção");
        }
    }
}
