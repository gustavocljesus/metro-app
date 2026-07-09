package metro.algoritmos;

import metro.model.Estacao;
import metro.model.Grafo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcula uma aproximacao de centralidade para identificar estacoes importantes.
 */
public class Betweenness {

    public static Map<String, Integer> calcular(Grafo grafo) {
        List<Estacao> estacoes = grafo.getAtivas();
        FloydWarshall.Resultado resultado = FloydWarshall.executar(grafo);

        Map<String, Integer> contagem = new HashMap<>();
        for (Estacao estacao : estacoes) {
            contagem.putIfAbsent(estacao.getNome(), 0);
        }

        for (int i = 0; i < estacoes.size(); i++) {
            for (int j = i + 1; j < estacoes.size(); j++) {
                List<String> caminho = FloydWarshall.reconstruirCaminho(
                        resultado,
                        estacoes.get(i).getId(),
                        estacoes.get(j).getId()
                );

                for (int k = 1; k < caminho.size() - 1; k++) {
                    Estacao estacao = grafo.getEstacao(caminho.get(k));
                    contagem.merge(estacao.getNome(), 1, Integer::sum);
                }
            }
        }

        return contagem;
    }

    public static List<Map.Entry<String, Integer>> top(Grafo grafo, int limite) {
        Map<String, Integer> contagem = calcular(grafo);
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagem.entrySet());
        lista.sort((a, b) -> b.getValue() - a.getValue());
        return lista.subList(0, Math.min(limite, lista.size()));
    }
}
