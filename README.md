# Sistema de Rotas Inteligentes para Metro

Projeto Java de Estrutura de Dados que modela uma rede de metro como grafo.
As estacoes sao vertices e as conexoes sao arestas ponderadas por tempo.

## Organizacao

```text
src/
├── main/java/
│   ├── app/                 # Interface grafica Swing
│   └── metro/
│       ├── algoritmos/      # Dijkstra, BFS, Floyd-Warshall, Betweenness e conectividade
│       ├── dados/           # Dados reais da rede
│       ├── enums/           # Linhas do metro/trem
│       └── model/           # Grafo, Estacao e Conexao
└── test/java/testes/        # Testes simples por main
```

A arquitetura antiga foi removida. A aplicacao, os dados e os algoritmos usam o pacote `metro.*`.

## Principais Classes

- `metro.model.Grafo`: estrutura principal da rede.
- `metro.model.Estacao`: representa uma estacao, com id, nome, linha e status ativo.
- `metro.model.Conexao`: representa uma aresta ponderada entre estacoes.
- `metro.dados.DadosMetroSP`: carrega os dados da rede.
- `metro.algoritmos.Dijkstra`: menor caminho por tempo e rota com menos baldeacoes.
- `metro.algoritmos.BFS`: caminho com menor numero de estacoes.
- `metro.algoritmos.FloydWarshall`: menores caminhos entre todos os pares.
- `metro.algoritmos.VerificadorConectividade`: verifica se a rede esta conexa.
- `app.MapaMetroSPApp`: interface Swing.

## Como Compilar

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

No PowerShell:

```powershell
$fontes = Get-ChildItem -Recurse -Filter *.java src\main\java,src\test\java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $fontes
```

## Como Executar

Interface grafica:

```bash
java -cp out app.MapaMetroSPApp
```

Testes simples:

```bash
java -cp out testes.TesteConexao
java -cp out testes.TesteCaminho
java -cp out testes.TesteMenosBaldeacoes
```

## Observacao

O projeto usa colecoes do `java.util` normalmente. A implementacao manual de lista,
pilha e outras estruturas foi descartada pela decisao atual do projeto.

O arquivo `RESUMO_CONTINUIDADE.md` guarda o estado mais recente do trabalho para
facilitar continuidade em outra conversa ou por outra IA.
