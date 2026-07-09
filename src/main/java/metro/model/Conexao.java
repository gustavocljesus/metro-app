package metro.model;

/**
 * Representa uma conexao ponderada entre duas estacoes.
 */
public class Conexao {
    private final Estacao origem;
    private final Estacao destino;
    private final double tempo;
    private final double distancia;
    private final String linha;

    public Conexao(Estacao origem, Estacao destino, double tempo, double distancia, String linha) {
        this.origem = origem;
        this.destino = destino;
        this.tempo = tempo;
        this.distancia = distancia;
        this.linha = linha;
    }

    public Estacao getOrigem() {
        return origem;
    }

    public Estacao getDestino() {
        return destino;
    }

    public double getTempo() {
        return tempo;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getPeso() {
        return tempo;
    }

    public String getLinha() {
        return linha;
    }

    @Override
    public String toString() {
        return "Conexao{origem='%s', destino='%s', tempo=%.1f}"
                .formatted(origem.getNome(), destino.getNome(), tempo);
    }
}
