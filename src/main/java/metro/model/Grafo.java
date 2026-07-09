package metro.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa a rede de metro como grafo com estacoes e listas de adjacencia.
 */
public class Grafo {
    private final Map<String, Estacao> estacoes;
    private final Map<String, List<Conexao>> adjacencias;
    private final boolean direcionado;

    public Grafo() {
        this(true);
    }

    public Grafo(boolean direcionado) {
        this.estacoes = new HashMap<>();
        this.adjacencias = new HashMap<>();
        this.direcionado = direcionado;
    }

    public void adicionarEstacao(Estacao estacao) {
        if (!estacoes.containsKey(estacao.getId())) {
            estacao.setIndice(estacoes.size());
            estacoes.put(estacao.getId(), estacao);
            adjacencias.put(estacao.getId(), new ArrayList<>());
        }
    }

    public void adicionarConexao(String origemId, String destinoId, double tempo) {
        Estacao origem = getEstacao(origemId);
        Estacao destino = getEstacao(destinoId);
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Estacoes nao encontradas: " + origemId + " ou " + destinoId);
        }
        adicionarConexao(origem, destino, tempo, tempo, origem.getLinha());
    }

    public void adicionarConexao(Estacao origem, Estacao destino, double tempo, double distancia, String linha) {
        validarEstacao(origem);
        validarEstacao(destino);
        adjacencias.get(origem.getId()).add(new Conexao(origem, destino, tempo, distancia, linha));
        if (!direcionado) {
            adjacencias.get(destino.getId()).add(new Conexao(destino, origem, tempo, distancia, linha));
        }
    }

    public Estacao getEstacao(String id) {
        return estacoes.get(id);
    }

    public Estacao getEstacao(int indice) {
        for (Estacao estacao : estacoes.values()) {
            if (estacao.getIndice() == indice) {
                return estacao;
            }
        }
        throw new IndexOutOfBoundsException("Indice de estacao invalido: " + indice);
    }

    public List<Conexao> getConexoes(String id) {
        return Collections.unmodifiableList(adjacencias.getOrDefault(id, Collections.emptyList()));
    }

    public List<Conexao> getConexoes(Estacao estacao) {
        validarEstacao(estacao);
        return getConexoes(estacao.getId());
    }

    public Collection<Estacao> getEstacoes() {
        return Collections.unmodifiableCollection(estacoes.values());
    }

    public Set<String> getIds() {
        return Collections.unmodifiableSet(estacoes.keySet());
    }

    public List<Estacao> getAtivas() {
        return estacoes.values().stream()
                .filter(Estacao::isAtiva)
                .collect(Collectors.toList());
    }

    public Set<String> getIdsAtivos() {
        Set<String> ids = new HashSet<>();
        for (Estacao estacao : estacoes.values()) {
            if (estacao.isAtiva()) {
                ids.add(estacao.getId());
            }
        }
        return ids;
    }

    public void interditar(String id) {
        Estacao estacao = getEstacao(id);
        if (estacao != null) {
            estacao.setAtiva(false);
        }
    }

    public void reativar(String id) {
        Estacao estacao = getEstacao(id);
        if (estacao != null) {
            estacao.setAtiva(true);
        }
    }

    public boolean temEstacoesAtivas() {
        return !getAtivas().isEmpty();
    }

    public int totalEstacoes() {
        return estacoes.size();
    }

    public boolean isDirecionado() {
        return direcionado;
    }

    private void validarEstacao(Estacao estacao) {
        if (estacao == null || estacoes.get(estacao.getId()) != estacao) {
            throw new IllegalArgumentException("Estacao nao pertence a este grafo.");
        }
    }

    @Override
    public String toString() {
        return "Grafo{estacoes=" + estacoes.size() + ", ativas=" + getAtivas().size() + '}';
    }
}
