package metro.algoritmos;

import metro.model.Estacao;

import java.util.Collections;
import java.util.List;

/**
 * Guarda o caminho, custo total e quantidade de baldeacoes de uma rota.
 */
public class ResultadoRota {
    private final List<Estacao> caminho;
    private final double custoTotal;
    private final int baldeacoes;

    public ResultadoRota(List<Estacao> caminho, double custoTotal, int baldeacoes) {
        this.caminho = Collections.unmodifiableList(caminho);
        this.custoTotal = custoTotal;
        this.baldeacoes = baldeacoes;
    }

    public List<Estacao> getCaminho() {
        return caminho;
    }

    public double getCustoTotal() {
        return custoTotal;
    }

    public int getBaldeacoes() {
        return baldeacoes;
    }

    public boolean éVazio() {
        return caminho == null || caminho.isEmpty();
    }

    @Override
    public String toString() {
        if (éVazio()) {
            return "Sem rota disponivel";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Rota: ");
        for (int i = 0; i < caminho.size(); i++) {
            sb.append(caminho.get(i).getNome());
            if (i < caminho.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append(String.format("%nCusto total: %.1f min", custoTotal));
        sb.append(String.format("%nBaldeacoes: %d", baldeacoes));
        return sb.toString();
    }
}
