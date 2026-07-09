package testes;

import metro.dados.DadosMetroSP;
import metro.model.Grafo;
import metro.algoritmos.VerificadorConectividade;

/**
 * Teste simples para verificar conectividade da rede.
 */
public class TesteConexao {
    public static void main(String[] args) {
        Grafo rede = DadosMetroSP.carregar();
        System.out.println("Rede conectada? " + new VerificadorConectividade(rede).estaConexa());
    }
}