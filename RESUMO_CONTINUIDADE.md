# Resumo de Continuidade

Use este arquivo para continuar o trabalho com outra IA ou em outra conversa.

## Padrão combinado

Ao final de cada alteração, verificação ou pedido, gerar um resumo simples com:
- o que foi feito;
- arquivos principais alterados;
- verificação executada;
- próximo passo sugerido, se houver.

## Estado atual do projeto

### Estrutura
- Layout Java padrão: `src/main/java` (código), `src/test/java` (testes)
- Interface Swing: `src/main/java/app/MapaMetroSPApp.java` (original) e `MapaMetroSPApp_Test.java` (cópia de teste com visual atualizado)
- Pacotes: `metro.model`, `metro.algoritmos`, `metro.dados`, `metro.enums`

### Interface visual (MapaMetroSPApp_Test.java)
A interface passou por uma modernização completa com tema "painel LED do Metrô de SP":

**Tema escuro:**
- Fundo `#111122` com gradiente radial
- Painel lateral `#16213e` com texto branco/cinza
- Scrollbar customizada (thumb arredondado, sem setas)
- Grid ultra-sutil no fundo (opacidade ~3%)
- LEDs desligados decorativos (pontos aleatórios)
- Vinheta nas bordas (escurecimento radial)

**Trilhos luminosos:**
- Glow de 3 camadas (16px, 10px, 5px) com opacidade progressiva
- Glow pulsante lento (variação senoidal)
- Reflexos pontuais ao longo das linhas (LEDs individuais)
- Setas decorativas discretas no meio de cada trecho (cor da linha, opacidade ~22%)
- Conexões de baldeação removidas (só anel na estação)

**Estações (LEDs):**
- Tamanhos: comum 5px, baldeação 8px, terminal 7px, origem/destino 11px
- Halo multi-camada (glow externo + interno)
- Brilho especular (reflexo branco superior esquerdo)
- Anel de baldeação (cinza) e anel de terminal (cor da linha)
- Pulse animado para origem/destino
- Tags "ORIGEM" (verde) e "DESTINO" (vermelho) acima da estação

**Rota ativa:**
- Branco com glow pulsante (3 camadas)
- Sombra sob a linha para profundidade
- Animação sequencial de acendimento (~200ms por estação)
- LED indicador pulsante (substitui bolinha amarela)
- Setas de sentido na rota (azul `#44AAFF`, contorno branco, tamanho 9)
- Rota de menos baldeações tracejada em azul

**Labels:**
- Terminais, baldeações e estações da rota sempre visíveis
- Hover mostra nome de qualquer estação
- Fundo escuro com cantos arredondados
- Nomes das linhas no mapa (ex: "1-AZUL")

**HUD de rota:**
- Sempre visível (mostra estado atual)
- Fundo translúcido com borda azul
- Ícones desenhados em Graphics2D (círculo verde/vermelho, relógio)
- Mostra origem, destino, tempo e baldeações

**Moldura:**
- Borda dupla com cantos industriais (quadrados 4x4)
- Título "Metrô de São Paulo" com linha decorativa e marcadores laterais

**Zoom:**
- Suave (interpolação com Timer, fator 0.20)
- Range: 60% a 180%

### Algoritmos (metro.algoritmos)
- **Dijkstra** (`calcularCaminhoMinimo`): rota de menor custo (tempo)
- **Dijkstra** (`calcularMenosBaldeacoes`): rota com menos baldeações (prioridade), tempo como desempate
- **BFS**: busca em largura
- **Floyd-Warshall**: caminhos mais curtos entre todos os pares
- **Betweenness**: centralidade de intermediação
- **VerificadorConectividade**: verifica se a rede está conectada

### Dados (metro.dados)
- `DadosMetroSP.carregar()`: carrega 14 linhas com ~200 estações
- Conexões bidirecionais dentro de cada linha
- Integrações (baldeações) entre linhas com peso adicional
- Pesos: Metrô 2.0min, ViaQuatro 2.5min, CPTM/TIC 3.0min, Jade 4.0min

### Testes (src/test/java)
- `TesteConexao`: rede conectada
- `TesteCaminho`: BFS ida/volta Linha 1
- `TesteMenosBaldeacoes`: rotas com menos baldeações

## Análise do algoritmo Dijkstra

O Dijkstra está implementado corretamente:
- Usa PriorityQueue com ordenação por custo
- Relaxamento de arestas correto
- Reconstrução de caminho via mapa de anteriores
- `break` ao encontrar destino é válido (Dijkstra garante custo mínimo)

**Possível causa do "ultrapasso":**
O grafo é bidirecionado dentro de cada linha, mas as baldeações são conexões específicas. O Dijkstra pode encontrar uma rota que visualmente parece "ultrapassar" o destino porque:
1. A rota mais rápida pode usar uma baldeação que passa "perto" visualmente do destino
2. O layout esquemático (linhas horizontais) pode tornar uma rota indireta mais curta em tempo
3. Exemplo: ir de uma estação A para B pode passar por C (que fica visualmente depois de B) se a baldeação em C for mais eficiente

**Não há bug no algoritmo** — o comportamento é correto do ponto de vista de custo/tempo.

## Funcionalidades pendentes

### Alta prioridade
- [x] **Escolha de rotas**: radio buttons no sidebar ("Mais rápida" / "Menos baldeações"), habilitados ao calcular rota, com animação da rota selecionada
- [ ] **Verificação visual da rota**: confirmar que a animação segue exatamente o caminho calculado

### Média prioridade
- [ ] Busca digitada de origem e destino com autocomplete
- [ ] Testes automatizados para os algoritmos

### Baixa prioridade
- [ ] Melhorar visual das conexões entre linhas
- [ ] Refinamento contínuo da interface

## Arquivos principais

| Arquivo | Descrição |
|---------|-----------|
| `app/MapaMetroSPApp.java` | Interface original (sem visual LED) |
| `app/MapaMetroSPApp_Test.java` | Interface com visual LED atualizado |
| `metro/model/Grafo.java` | Grafo direcionado com adjacências |
| `metro/model/Estacao.java` | Modelo de estação (id, nome, linha, ativa) |
| `metro/model/Conexao.java` | Conexão ponderada (origem, destino, tempo) |
| `metro/algoritmos/Dijkstra.java` | Dijkstra (custo mínimo + menos baldeações) |
| `metro/algoritmos/ResultadoRota.java` | Resultado: caminho, custo, baldeações |
| `metro/dados/DadosMetroSP.java` | Dados da rede de SP |
| `metro/enums/Linha.java` | Enum das linhas com pesos |

## Última verificação

- Escolha de rotas implementada com radio buttons no sidebar
- Radio buttons desabilitados sem rota, habilitados ao calcular
- Animação muda conforme rota selecionada (rápida ou menos baldeações)
- Status mostra tipo de rota ativa: `[Rápida] 25 min, 2 bal.`
- Compilação sem erros
- Aplicação executa e interage corretamente
- HUD sempre visível (mostra estado sem rota)
- Tags ORIGEM/DESTINO funcionais
