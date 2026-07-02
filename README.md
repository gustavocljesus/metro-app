# Sistema de Rotas Inteligentes para Estações de Metrô

## Visão Geral
Sistema em Java que modela uma rede de metrô como grafo e responde a consultas de rota usando algoritmos clássicos. O objetivo é implementar estruturas e algoritmos **do zero**, sem bibliotecas externas de grafos.

### Objetivo Acadêmico
- Implementar algoritmos de grafos (Dijkstra, BFS, DFS, Floyd‑Warshall) manualmente  
- Modelar linhas de metrô com dados reais de São Paulo (2026)  
- Implementar questões práticas de gráficos com penalidades de troca de linha

---

## Estrutura do Projeto
```
src/
├── model/               # Modelos de dados
│   ├── Estacao.java     # Representação das estações
│   └── Conexao.java     # Conexões entre estações
├── grafo/               # Estrutura do grafo
│   ├── RedeMetro.java   # Estrutura principal do grafo
│   └── ResultadoRota.java # Resultado das consultas
├── algoritmos/         # Implementações dos algoritmos
│   ├── Dijkstra.java
│   ├── BFS.java
│   ├── DFS.java
│   └── FloydWarshall.java
├── dados/              # Dados de exemplo
│   └── DadosMetroSP.java
└── enum/               # Enumerações
    └── Linha.java
```

---

## Modelagem do Domínio

### Entidades
- **Estação**: Representa uma estação com `id`, `nome`, `linha` e status de `ativa`
- **Conexão**: Representa uma ligação entre estações com `peso` e `bidirecional`
- **RedeMetro**: Estrutura principal do grafo com lista de adjacência

### Escolhas Técnicas
- **Lista de adjacência** em vez de matriz para eficiência em grafos esparsos  
- **Lista de pesos de baldeação** para calcular rotas com trocas de linha

---

## Algoritmos Implementados

| Algoritmo        | Função                                    | Complexidade       |
|------------------|-------------------------------------------|--------------------|
| Dijkstra         | Caminho mínimo em termos de tempo/distância| O((V+E) log V)     |
| BFS              | Menor número de estações entre A e B      | O(V + E)           |
| DFS              | Verificação de conectividade e ciclos     | O(V + E)           |
| Floyd‑Warshall   | Caminho mínimo entre todos os pares       | O(V³)              |

---

## Dados de Exemplo

O projeto utiliza dados da **Rede Metropolitana de São Paulo (2026)** com:
- 20+ linhas  
- 200+ estações  
- Pesos de viagem estimados (2–4 min por trecho)  
- Penalidades de transferência (3–5 min)

---

## Como Usar

### Requisitos
- Java 8+  
- IDE com suporte a Java

### Exemplo Básico
```java
// Carregar rede com dados reais
RedeMetro rede = DadosMetroSP.carregar();

// Consultar rota mais rápida
ResultadoRota rota = Dijkstra.calcularCaminhoMinimo(
    rede, 
    "Tucuruvi:L1", 
    "Corinthians-Itaquera:L12"
);
System.out.println(rota);

// Verificar conectividade da rede
boolean conectada = DFS.redeConectada(rede);
```

---

## Testes e Use Cases

```java
public static void main(String[] args) {
    RedeMetro rede = DadosMetroSP.carregar();
    
    // Exemplo de BFS (menor número de estações)
    List<String> caminho = BFS.caminhoMenorSaltos(rede, "Luz", "Jd. Colonial");
    System.out.println("BFS – Menor número de estações: " + caminho);
    
    // Exemplo de Dijkstra com penalidade de baldeação
    ResultadoRota rota = Dijkstra.calcularCaminhoMinimo(rede, "Tucuruvi:L1", "Corinthians-Itaquera:L12");
    System.out.println("Dijkstra – Rota com menor tempo:");
    System.out.println(rota);
}
```

---

## Referências

- [Sistema de Transportes Metropolitanos de SP](http://metrosp.gov.br)  
- [Mapa Oficial 2026](https://exemplo.com/mapa2026)  
- Estruturas de dados em grafos (Cormen et al. – CLRS)

---
