package testes;

import metro.model.Grafo;
import metro.dados.DadosMetroSP;
import metro.algoritmos.BFS;
import java.util.List;

/**
 * Teste simples para consultar caminho por BFS.
 */
public class TesteCaminho {
    public static void main(String[] args) {
        Grafo rede = DadosMetroSP.carregar();

        List<String> ida = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Tucuruvi", "L1_AZUL:Jabaquara");
        List<String> volta = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Jabaquara", "L1_AZUL:Tucuruvi");
        System.out.println("Ida: " + ida);
        System.out.println("Volta: " + volta);
    }
}