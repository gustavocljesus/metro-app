package metro.algoritmos;

import metro.model.Conexao;
import metro.model.Estacao;
import metro.model.Grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Implementa busca em largura para encontrar caminho com menos estacoes.
 */
public class BFS {

    public static List<String> caminhoMenorSaltos(Grafo grafo, String origemId, String destinoId) {
        Estacao origem = grafo.getEstacao(origemId);
        Estacao destino = grafo.getEstacao(destinoId);

        if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
            return Collections.emptyList();
        }
        if (origemId.equals(destinoId)) {
            return Collections.singletonList(origemId);
        }

        Set<String> visitados = new HashSet<>();
        Map<String, String> anteriores = new HashMap<>();
        Queue<String> fila = new LinkedList<>();

        visitados.add(origemId);
        fila.add(origemId);

        while (!fila.isEmpty()) {
            String atualId = fila.poll();
            for (Conexao conexao : grafo.getConexoes(atualId)) {
                Estacao vizinho = conexao.getDestino();
                String vizinhoId = vizinho.getId();

                if (!visitados.contains(vizinhoId) && vizinho.isAtiva()) {
                    visitados.add(vizinhoId);
                    anteriores.put(vizinhoId, atualId);

                    if (vizinhoId.equals(destinoId)) {
                        return reconstruirCaminho(anteriores, destinoId);
                    }
                    fila.add(vizinhoId);
                }
            }
        }

        return Collections.emptyList();
    }

    private static List<String> reconstruirCaminho(Map<String, String> anteriores, String destinoId) {
        List<String> caminho = new ArrayList<>();
        String atual = destinoId;

        while (atual != null) {
            caminho.add(atual);
            atual = anteriores.get(atual);
        }

        Collections.reverse(caminho);
        return caminho;
    }
}
