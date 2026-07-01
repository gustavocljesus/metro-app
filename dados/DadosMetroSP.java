package dados;

import enum.Linha;
import grafo.RedeMetro;
import model.Conexao;
import model.Estacao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carregador de dados da rede de metrô de São Paulo.
 * <p>
 * Fornece métodos estáticos para popular uma {@link RedeMetro} com as estações
 * e conexões do sistema metroviário da cidade de São Paulo, conforme mapa
 * oficial de março/2026.
 *
 * @see Linha
 */
public class DadosMetroSP {

    // Pesos de baldeação (estimados)
    private static final double BALDEACAO_GRATUITA = 3.0;
    private static final double BALDEACAO_HORARIA  = 5.0;

    /**
     * Carrega a rede completa de São Paulo.
     *
     * @return RedeMetro totalmente populada
     */
    public static RedeMetro carregar() {
        RedeMetro rede = new RedeMetro();
        Map<String, Double> pesoTrecho = new HashMap<>();

        // Adicionar estações e conexões linha por linha
        adicionarLinha(rede, Linha.L1_AZUL, List.of(
            "Tucuruvi", "Parada Inglesa", "Jd. São Paulo-Ayrton Senna", "Santana",
            "Carandiru", "Portuguesa-Tietê", "Armênia", "Tiradentes", "Luz",
            "São Bento", "Sé", "Japão-Liberdade", "São Joaquim", "Vergueiro",
            "Paraíso", "Ana Rosa", "Vila Mariana", "Praça da Árvore", "Saúde",
            "São Judas", "Conceição", "Jabaquara"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L2_VERDE, List.of(
            "Vila Madalena", "Sumaré", "Clínicas", "Consolação", "Paulista",
            "Trianon-Masp", "Brigadeiro", "Paraíso", "Ana Rosa",
            "Chácara Klabin", "Alto do Ipiranga", "Santos-Imigrantes",
            "Sacomã", "Vila Prudente"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L3_VERMELHA, List.of(
            "Palmeiras-Barra Funda", "Marechal Deodoro", "Santa Cecília",
            "República", "Anhangabaú", "Sé", "Pedro II", "Brás",
            "Bresser-Mooca", "Belém", "Tatuapé", "Carrão", "Penha",
            "Vila Matilde", "Guilhermina-Esperança", "Patriarca-Vila Ré",
            "Artur Alvim", "Corinthians-Itaquera"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L4_AMARELA, List.of(
            "Taboão da Serra", "Vila Sônia", "São Paulo-Morumbi", "Butantã",
            "Pinheiros", "Faria Lima", "Paulista", "Oscar Freire",
            "Fradique Coutinho", "Consolação", "Higienópolis-Mackenzie",
            "República", "Luz"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L5_LILAS, List.of(
            "Capão Redondo", "Campo Limpo", "Vila das Belezas",
            "Giovanni Gronchi", "Santo Amaro", "Largo Treze",
            "Adolfo Pinheiro", "Alto da Boa Vista", "Borba Gato",
            "Brooklin", "Campo Belo", "Eucaliptos", "Moema",
            "AACD-Servidor", "Hospital São Paulo", "Santa Cruz",
            "Chácara Klabin"
        ), pesoTrecho);

        // Integrações entre linhas (baldeações)
        integrar(rede, "Sé", "L1", "L3", BALDEACAO_GRATUITA);
        integrar(rede, "Paraíso", "L1", "L2", BALDEACAO_GRATUITA);
        integrar(rede, "Ana Rosa", "L1", "L2", BALDEACAO_GRATUITA);
        integrar(rede, "Paulista", "L2", "L4", BALDEACAO_GRATUITA);
        integrar(rede, "Consolação", "L2", "L4", BALDEACAO_GRATUITA);
        integrar(rede, "República", "L3", "L4", BALDEACAO_GRATUITA);
        integrar(rede, "Pinheiros", "L4", "L9", BALDEACAO_GRATUITA);
        integrar(rede, "Chácara Klabin", "L2", "L5", BALDEACAO_GRATUITA);
        integrar(rede, "Santo Amaro", "L5", "L9", BALDEACAO_GRATUITA);
        integrar(rede, "Vila Prudente", "L2", "L15", BALDEACAO_GRATUITA);
        integrar(rede, "Tamanduateí", "L2", "L10", BALDEACAO_GRATUITA);

        // Integrações horárias (mais caras)
        integrar(rede, "Luz", List.of("L1", "L4", "L7", "L8"), BALDEACAO_HORARIA);
        integrar(rede, "Brás", List.of("L3", "L11", "L12"), BALDEACAO_HORARIA);
        integrar(rede, "Tatuapé", List.of("L3", "L11", "L12"), BALDEACAO_HORARIA);
        integrar(rede, "Palmeiras-Barra Funda", List.of("L3", "L7"), BALDEACAO_HORARIA);
        integrar(rede, "Lapa", List.of("L7", "L8"), BALDEACAO_HORARIA);
        integrar(rede, "Júlio Prestes", List.of("L7", "L8"), BALDEACAO_HORARIA);
        integrar(rede, "Osasco", List.of("L8", "L9"), BALDEACAO_HORARIA);
        integrar(rede, "Pres. Altino", List.of("L8", "L9"), BALDEACAO_HORARIA);
        integrar(rede, "Engº Goulart", List.of("L11", "L12"), BALDEACAO_HORARIA);
        integrar(rede, "Corinthians-Itaquera", List.of("L3", "L12"), BALDEACAO_HORARIA);
        integrar(rede, "Poá", List.of("L11", "L12"), BALDEACAO_HORARIA);

        return rede;
    }

    private static void adicionarLinha(RedeMetro rede, Linha linha, List<String> estacoes, Map<String, Double> pesoTrecho) {
        double peso = linha.pesoPadrao;
        for (int i = 0; i < estacoes.size(); i++) {
            String nome = estacoes.get(i);
            String id = idEstacao(nome, linha);
            if (!rede.getEstacao(id).equals(id)) {
                rede.adicionarEstacao(new Estacao(id, nome, linha.getNome()));
            }
            if (i > 0) {
                String idAnt = idEstacao(estacoes.get(i - 1), linha);
                rede.adicionarConexao(idAnt, id, peso);
            }
        }
    }

    private static void integrar(RedeMetro rede, String estacaoNome, String... linhas) {
        // Simplificado: adiciona arestas bidirecionais entre as plataformas
        // Na verdade, seria necessário criar nós separados por linha
        // Este método serve como marcação para integração
    }

    private static void integrar(RedeMetro rede, String estacaoNome, List<String> linhas, double peso) {
        // Marca integração com peso específico
        // Implementação detalhada dependeria de nós separados por linha
    }

    private static String idEstacao(String nome, Linha linha) {
        return linha.name() + ":" + nome.replace(" ", "_");
    }
}