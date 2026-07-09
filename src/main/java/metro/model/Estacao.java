package metro.model;

import java.util.Objects;

/**
 * Representa uma estacao da rede, com id, nome, linha e indice no grafo.
 */
public class Estacao {
    private int indice;
    private final String id;
    private final String nome;
    private final String linha;
    private boolean ativa;

    public Estacao(String nome) {
        this(nome, nome, "");
    }

    public Estacao(String id, String nome, String linha) {
        this.indice = -1;
        this.id = id;
        this.nome = nome;
        this.linha = linha;
        this.ativa = true;
    }

    public int getIndice() {
        return indice;
    }

    void setIndice(int indice) {
        this.indice = indice;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLinha() {
        return linha;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public boolean isInterditada() {
        return !ativa;
    }

    public void setInterditada(boolean interditada) {
        this.ativa = !interditada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estacao estacao = (Estacao) o;
        return Objects.equals(id, estacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Estacao{id='%s', nome='%s', linha='%s', ativa=%s}"
                .formatted(id, nome, linha, ativa);
    }
}
