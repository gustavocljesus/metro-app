# Resumo de Continuidade

Use este arquivo para continuar o trabalho com outra IA ou em outra conversa.

## Padrao combinado

Ao final de cada alteracao, verificacao ou pedido, gerar um resumo simples com:

- o que foi feito;
- arquivos principais alterados;
- verificacao executada;
- proximo passo sugerido, se houver.

## Estado atual

- Projeto reorganizado para o layout Java padrao:
  - `src/main/java`: codigo principal.
  - `src/test/java`: testes simples.
- A aplicacao Swing fica em `src/main/java/app/MapaMetroSPApp.java` com `package app`.
- A arquitetura foi unificada em `metro.*`.
- Os pacotes antigos `algoritmos`, `dados`, `enums`, `grafo` e `model` foram removidos.
- A decisao atual e usar `java.util` normalmente no projeto.
- Os algoritmos principais estao em `src/main/java/metro/algoritmos`.

## Observacao importante

Nao existem mais duas arquiteturas ativas no projeto.

- `metro.model.Grafo` e o grafo principal.
- `metro.dados.DadosMetroSP` carrega os dados reais.
- `app.MapaMetroSPApp` usa diretamente `metro.*`.
- `metro.algoritmos` contem Dijkstra, BFS, Floyd-Warshall, Betweenness e VerificadorConectividade.

## Como a interface funciona hoje

- A interface principal e uma janela Swing com o mapa no centro e um painel lateral a direita.
- O mapa e esquematico: as linhas sao organizadas horizontalmente, cada linha tem sua cor e as estacoes aparecem como circulos.
- Estacoes de baldeacao aparecem com circulos maiores quando existem estacoes com o mesmo nome em linhas diferentes.
- O usuario clica com o botao esquerdo em uma estacao para escolher a origem.
- Depois clica com o botao esquerdo em outra estacao para escolher o destino e calcular a rota.
- A rota mais rapida e destacada em preto e a rota de menos baldeacoes aparece tracejada em azul quando tiver trechos diferentes.
- Uma bolinha amarela animada percorre a rota mais rapida calculada.
- O botao direito interdita ou reativa uma estacao; se ja existir origem e destino, a rota e recalculada.
- O painel lateral mostra instrucoes, status em tempo real, botoes de acao e legenda visual das cores das linhas.
- O status informa selecao de origem, pedido de destino, calculo de rota, rota encontrada, ausencia de rota e interdicao/reativacao.
- O botao `Limpar selecao` zera origem, destino, rotas e animacao.
- O botao `Ver detalhes da rota` mostra em uma janela a rota mais rapida e a rota com menos baldeacoes.
- O botao `Estacoes mais importantes` mostra o ranking calculado por Betweenness.
- O painel lateral tem botoes de zoom `-` e `+`, com indicador percentual.
- O zoom escala o desenho do mapa e os cliques continuam funcionando porque a coordenada do mouse e ajustada pelo fator de zoom.
- A area do mapa aparece dentro de um quadro visual com borda e titulo centralizado `Metro de Sao Paulo`.
- Ao passar o mouse sobre uma estacao, aparece tooltip com nome e linha.

## Ultima verificacao

- Foram adicionados botoes de zoom no painel lateral, variando de 60% a 180%.
- O mapa agora e desenhado dentro de um quadro retangular com titulo centralizado no topo.
- Arquivo alterado nesta etapa: `src/main/java/app/MapaMetroSPApp.java`.
- Compilacao completa com `javac -encoding UTF-8 -d metro-app\out`.
- `testes.TesteConexao`: rede conectada retornou `true`.
- `testes.TesteCaminho`: BFS encontrou ida e volta na Linha 1.
- `testes.TesteMenosBaldeacoes`: 0 falhas.

## Verificacao anterior

- Painel lateral recebeu status em tempo real, legenda visual das linhas e acabamento basico melhorado.
- O status agora informa selecao de origem, destino, calculo de rota, rota encontrada, ausencia de rota e interdicao/reativacao de estacao.
- Arquivo alterado nesta etapa: `src/main/java/app/MapaMetroSPApp.java`.
- Compilacao completa com `javac -encoding UTF-8 -d metro-app\out`.
- `testes.TesteConexao`: rede conectada retornou `true`.
- `testes.TesteCaminho`: BFS encontrou ida e volta na Linha 1.
- `testes.TesteMenosBaldeacoes`: 0 falhas.

## Verificacao inicial da UI esquematica

- Interface Swing substituida por uma versao esquematica com linhas organizadas e bolinha animada na rota.
- Compilacao completa com `javac -encoding UTF-8 -d out`.
- `testes.TesteConexao`: rede conectada retornou `true`.
- `testes.TesteCaminho`: BFS encontrou ida e volta na Linha 1.
- `testes.TesteMenosBaldeacoes`: 0 falhas.
- `app.MapaMetroSPApp`: UI nova iniciou e permaneceu em execucao por 5 segundos.

## Proximo passo sugerido

- Melhorar visual das conexoes entre linhas e estacoes, principalmente em regioes de baldeacao.
- Permitir busca digitada de origem e destino com lista/autocomplete.
- Verificar visualmente se a bolinha animada esta seguindo exatamente o caminho mais curto calculado.
- Continuar refinando cores, espacamento e acabamento geral da tela.
- Criar testes para funcionalidades importantes da aplicacao e dos algoritmos.
