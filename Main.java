import grafo.RedeMetro;
import grafo.ResultadoRota;
import dados.DadosMetroSP;
import algoritmos.BFS;
import algoritmos.DFS;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        RedeMetro rede = DadosMetroSP.carregar();
        System.out.println(rede);
        System.out.println("Rede conectada? " + DFS.redeConectada(rede));

        // testar os dois sentidos da mesma linha
        List<String> ida = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Tucuruvi", "L1_AZUL:Jabaquara");
        List<String> volta = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Jabaquara", "L1_AZUL:Tucuruvi");
        System.out.println("Ida: " + ida);
        System.out.println("Volta: " + volta);
    }
}