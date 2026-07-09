package metro.algoritmos;

import metro.model.Conexao;
import metro.model.Estacao;
import metro.model.Grafo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Verifica conectividade da rede com DFS iterativo.
 */
public class VerificadorConectividade {

    private final Grafo grafo;

    public VerificadorConectividade(Grafo grafo) {
        this.grafo = grafo;
    }

    public boolean estaConexa() {
        Estacao inicio = primeiraEstacaoAtiva();
        if (inicio == null) {
            return true;
        }

        Set<String> visitados = new HashSet<>();
        dfsIterativo(inicio, visitados);

        int totalAtivas = 0;
        for (Estacao estacao : grafo.getEstacoes()) {
            if (estacao.isAtiva()) {
                totalAtivas++;
            }
        }
        return visitados.size() == totalAtivas;
    }

    public boolean estaConexaSemEstacao(Estacao estacaoFechada) {
        boolean estadoAnterior = estacaoFechada.isAtiva();
        estacaoFechada.setAtiva(false);
        try {
            return estaConexa();
        } finally {
            estacaoFechada.setAtiva(estadoAnterior);
        }
    }

    public List<List<Estacao>> encontrarComponentesConexos() {
        List<List<Estacao>> componentes = new ArrayList<>();
        Set<String> visitados = new HashSet<>();

        for (Estacao estacao : grafo.getEstacoes()) {
            if (estacao.isAtiva() && !visitados.contains(estacao.getId())) {
                List<Estacao> componente = new ArrayList<>();
                dfsColetando(estacao, visitados, componente);
                componentes.add(componente);
            }
        }
        return componentes;
    }

    private void dfsIterativo(Estacao inicio, Set<String> visitados) {
        Stack<Estacao> pilha = new Stack<>();
        pilha.push(inicio);

        while (!pilha.isEmpty()) {
            Estacao atual = pilha.pop();
            if (!atual.isAtiva() || visitados.contains(atual.getId())) {
                continue;
            }

            visitados.add(atual.getId());
            adicionarVizinhos(atual, pilha, visitados);
        }
    }

    private void dfsColetando(Estacao inicio, Set<String> visitados, List<Estacao> componente) {
        Stack<Estacao> pilha = new Stack<>();
        pilha.push(inicio);

        while (!pilha.isEmpty()) {
            Estacao atual = pilha.pop();
            if (!atual.isAtiva() || visitados.contains(atual.getId())) {
                continue;
            }

            visitados.add(atual.getId());
            componente.add(atual);
            adicionarVizinhos(atual, pilha, visitados);
        }
    }

    private void adicionarVizinhos(Estacao atual, Stack<Estacao> pilha, Set<String> visitados) {
        for (Conexao conexao : grafo.getConexoes(atual)) {
            Estacao vizinho = conexao.getDestino();
            if (vizinho.isAtiva() && !visitados.contains(vizinho.getId())) {
                pilha.push(vizinho);
            }
        }
    }

    private Estacao primeiraEstacaoAtiva() {
        for (Estacao estacao : grafo.getEstacoes()) {
            if (estacao.isAtiva()) {
                return estacao;
            }
        }
        return null;
    }
}
