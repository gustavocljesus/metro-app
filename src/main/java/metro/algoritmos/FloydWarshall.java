package metro.algoritmos;

import metro.model.Conexao;
import metro.model.Estacao;
import metro.model.Grafo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcula menores caminhos entre todos os pares de estacoes.
 */
public class FloydWarshall {

    public static Resultado executar(Grafo grafo) {
        List<Estacao> estacoes = grafo.getAtivas();
        int n = estacoes.size();
        if (n == 0) {
            return new Resultado(estacoes, new double[0][0], new int[0][0]);
        }

        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indices.put(estacoes.get(i).getId(), i);
        }

        double[][] distancias = new double[n][n];
        int[][] proximos = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(distancias[i], Double.POSITIVE_INFINITY);
            Arrays.fill(proximos[i], -1);
            distancias[i][i] = 0;
            proximos[i][i] = i;
        }

        for (Estacao origem : estacoes) {
            int i = indices.get(origem.getId());
            for (Conexao conexao : grafo.getConexoes(origem)) {
                Estacao destino = conexao.getDestino();
                if (!destino.isAtiva()) {
                    continue;
                }
                int j = indices.get(destino.getId());
                if (conexao.getTempo() < distancias[i][j]) {
                    distancias[i][j] = conexao.getTempo();
                    proximos[i][j] = j;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (Double.isInfinite(distancias[i][k])) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    double novaDistancia = distancias[i][k] + distancias[k][j];
                    if (novaDistancia < distancias[i][j]) {
                        distancias[i][j] = novaDistancia;
                        proximos[i][j] = proximos[i][k];
                    }
                }
            }
        }

        return new Resultado(estacoes, distancias, proximos);
    }

    public static List<String> reconstruirCaminho(Resultado resultado, String origemId, String destinoId) {
        Integer origem = resultado.indicePorId.get(origemId);
        Integer destino = resultado.indicePorId.get(destinoId);

        if (origem == null || destino == null || resultado.proximos[origem][destino] == -1) {
            return Collections.emptyList();
        }

        List<String> caminho = new ArrayList<>();
        int atual = origem;
        caminho.add(resultado.estacoes.get(atual).getId());

        while (atual != destino) {
            atual = resultado.proximos[atual][destino];
            if (atual == -1) {
                return Collections.emptyList();
            }
            caminho.add(resultado.estacoes.get(atual).getId());
        }

        return caminho;
    }

    /**
     * Resultado encapsulado do Floyd-Warshall.
     */
    public static class Resultado {
        private final List<Estacao> estacoes;
        private final Map<String, Integer> indicePorId;
        public final double[][] distancias;
        public final int[][] proximos;

        private Resultado(List<Estacao> estacoes, double[][] distancias, int[][] proximos) {
            this.estacoes = estacoes;
            this.indicePorId = new HashMap<>();
            this.distancias = distancias;
            this.proximos = proximos;

            for (int i = 0; i < estacoes.size(); i++) {
                indicePorId.put(estacoes.get(i).getId(), i);
            }
        }

        public List<Estacao> getEstacoes() {
            return Collections.unmodifiableList(estacoes);
        }
    }
}
