package metro.algoritmos;

import metro.model.Conexao;
import metro.model.Estacao;
import metro.model.Grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implementa Dijkstra para encontrar rotas de menor custo na rede.
 */
public class Dijkstra {
    private static final double INFINITO = Double.MAX_VALUE / 4;

    public static ResultadoRota calcularCaminhoMinimo(Grafo grafo, String origemId, String destinoId) {
        Estacao origem = grafo.getEstacao(origemId);
        Estacao destino = grafo.getEstacao(destinoId);

        if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
            return new ResultadoRota(Collections.emptyList(), 0, 0);
        }
        if (origemId.equals(destinoId)) {
            return new ResultadoRota(Collections.singletonList(origem), 0, 0);
        }

        Map<String, Double> distancias = new HashMap<>();
        Map<String, Estacao> anteriores = new HashMap<>();
        PriorityQueue<Par> fila = new PriorityQueue<>(Comparator.comparingDouble(p -> p.custo));

        for (Estacao estacao : grafo.getEstacoes()) {
            distancias.put(estacao.getId(), INFINITO);
        }
        distancias.put(origemId, 0.0);
        fila.add(new Par(origemId, 0));

        while (!fila.isEmpty()) {
            Par atual = fila.poll();
            if (atual.custo > distancias.get(atual.estacaoId)) {
                continue;
            }

            Estacao estacaoAtual = grafo.getEstacao(atual.estacaoId);
            if (estacaoAtual == null || !estacaoAtual.isAtiva()) {
                continue;
            }
            if (atual.estacaoId.equals(destinoId)) {
                break;
            }

            for (Conexao conexao : grafo.getConexoes(atual.estacaoId)) {
                Estacao vizinho = conexao.getDestino();
                if (!vizinho.isAtiva()) {
                    continue;
                }

                double novaDistancia = atual.custo + conexao.getTempo();
                if (novaDistancia < distancias.get(vizinho.getId())) {
                    distancias.put(vizinho.getId(), novaDistancia);
                    anteriores.put(vizinho.getId(), estacaoAtual);
                    fila.add(new Par(vizinho.getId(), novaDistancia));
                }
            }
        }

        if (!anteriores.containsKey(destinoId)) {
            return new ResultadoRota(Collections.emptyList(), 0, 0);
        }

        List<Estacao> caminho = reconstruirCaminho(grafo, anteriores, destinoId);
        return new ResultadoRota(caminho, distancias.get(destinoId), contarBaldeacoes(caminho));
    }

    public static ResultadoRota calcularMenosBaldeacoes(Grafo grafo, String origemId, String destinoId) {
        Estacao origem = grafo.getEstacao(origemId);
        Estacao destino = grafo.getEstacao(destinoId);

        if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
            return new ResultadoRota(Collections.emptyList(), 0, 0);
        }
        if (origemId.equals(destinoId)) {
            return new ResultadoRota(Collections.singletonList(origem), 0, 0);
        }

        Map<String, Integer> baldeacoes = new HashMap<>();
        Map<String, Double> tempos = new HashMap<>();
        Map<String, Estacao> anteriores = new HashMap<>();
        PriorityQueue<ParLex> fila = new PriorityQueue<>(
                Comparator.comparingInt((ParLex p) -> p.baldeacoes)
                        .thenComparingDouble(p -> p.tempo)
        );

        for (Estacao estacao : grafo.getEstacoes()) {
            baldeacoes.put(estacao.getId(), Integer.MAX_VALUE);
            tempos.put(estacao.getId(), INFINITO);
        }

        baldeacoes.put(origemId, 0);
        tempos.put(origemId, 0.0);
        fila.add(new ParLex(origemId, 0, 0));

        while (!fila.isEmpty()) {
            ParLex atual = fila.poll();
            if (atual.baldeacoes > baldeacoes.get(atual.estacaoId)
                    || (atual.baldeacoes == baldeacoes.get(atual.estacaoId)
                    && atual.tempo > tempos.get(atual.estacaoId))) {
                continue;
            }

            Estacao estacaoAtual = grafo.getEstacao(atual.estacaoId);
            if (estacaoAtual == null || !estacaoAtual.isAtiva()) {
                continue;
            }
            if (atual.estacaoId.equals(destinoId)) {
                break;
            }

            for (Conexao conexao : grafo.getConexoes(atual.estacaoId)) {
                Estacao vizinho = conexao.getDestino();
                if (!vizinho.isAtiva()) {
                    continue;
                }

                int novaBaldeacao = baldeacoes.get(atual.estacaoId)
                        + (estacaoAtual.getLinha().equals(vizinho.getLinha()) ? 0 : 1);
                double novoTempo = tempos.get(atual.estacaoId) + conexao.getTempo();

                boolean melhor = novaBaldeacao < baldeacoes.get(vizinho.getId())
                        || (novaBaldeacao == baldeacoes.get(vizinho.getId())
                        && novoTempo < tempos.get(vizinho.getId()));

                if (melhor) {
                    baldeacoes.put(vizinho.getId(), novaBaldeacao);
                    tempos.put(vizinho.getId(), novoTempo);
                    anteriores.put(vizinho.getId(), estacaoAtual);
                    fila.add(new ParLex(vizinho.getId(), novaBaldeacao, novoTempo));
                }
            }
        }

        if (!anteriores.containsKey(destinoId)) {
            return new ResultadoRota(Collections.emptyList(), 0, 0);
        }

        List<Estacao> caminho = reconstruirCaminho(grafo, anteriores, destinoId);
        return new ResultadoRota(caminho, tempos.get(destinoId), contarBaldeacoes(caminho));
    }

    private static List<Estacao> reconstruirCaminho(Grafo grafo, Map<String, Estacao> anteriores, String destinoId) {
        List<Estacao> caminho = new ArrayList<>();
        String atual = destinoId;
        while (atual != null) {
            Estacao estacao = grafo.getEstacao(atual);
            caminho.add(estacao);
            Estacao anterior = anteriores.get(atual);
            atual = anterior == null ? null : anterior.getId();
        }
        Collections.reverse(caminho);
        return caminho;
    }

    private static int contarBaldeacoes(List<Estacao> caminho) {
        int total = 0;
        for (int i = 1; i < caminho.size(); i++) {
            if (!caminho.get(i).getLinha().equals(caminho.get(i - 1).getLinha())) {
                total++;
            }
        }
        return total;
    }

    private static class Par {
        private final String estacaoId;
        private final double custo;

        private Par(String estacaoId, double custo) {
            this.estacaoId = estacaoId;
            this.custo = custo;
        }
    }

    private static class ParLex {
        private final String estacaoId;
        private final int baldeacoes;
        private final double tempo;

        private ParLex(String estacaoId, int baldeacoes, double tempo) {
            this.estacaoId = estacaoId;
            this.baldeacoes = baldeacoes;
            this.tempo = tempo;
        }
    }
}
