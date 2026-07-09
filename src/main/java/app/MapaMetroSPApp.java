package app;

import metro.dados.DadosMetroSP;
import metro.model.Grafo;
import metro.model.Estacao;
import metro.model.Conexao;
import metro.algoritmos.Dijkstra;
import metro.algoritmos.ResultadoRota;
import metro.algoritmos.Betweenness;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class MapaMetroSPApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MapaMetroSPApp::criarJanela);
    }

    private static void criarJanela() {
        Grafo rede = DadosMetroSP.carregar();

        JFrame frame = new JFrame("Mapa do Metrô de São Paulo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        MapaPanel mapaPanel = new MapaPanel(rede);
        JScrollPane scrollPane = new JScrollPane(mapaPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(226, 230, 235));

        JPanel painelLateral = criarPainelLateral(mapaPanel, rede);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(painelLateral, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private static JPanel criarPainelLateral(MapaPanel mapaPanel, Grafo rede) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setPreferredSize(new Dimension(280, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        painel.setBackground(new Color(238, 240, 243));

        JLabel titulo = new JLabel("Mapa do Metrô - SP");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(32, 38, 46));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instrucoes = new JLabel(
                "<html><body style='width:230px'>"
                        + "Clique esquerdo: escolha origem e depois destino.<br><br>"
                        + "Clique direito: interdita ou reativa a estação.</body></html>");
        instrucoes.setForeground(new Color(73, 80, 89));
        instrucoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusTitulo = criarTituloSecao("Status");
        JLabel status = new JLabel();
        status.setOpaque(true);
        status.setBackground(Color.WHITE);
        status.setForeground(new Color(32, 38, 46));
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 222, 228)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        status.setFont(new Font("SansSerif", Font.PLAIN, 13));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        status.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        mapaPanel.setStatusLabel(status);

        JButton botaoLimpar = new JButton("Limpar seleção");
        configurarBotao(botaoLimpar);
        botaoLimpar.addActionListener(e -> mapaPanel.limparSelecao());

        JButton botaoDetalhes = new JButton("Ver detalhes da rota");
        configurarBotao(botaoDetalhes);
        botaoDetalhes.addActionListener(e -> mapaPanel.mostrarDetalhesRota());

        JButton botaoImportantes = new JButton("Estações mais importantes");
        configurarBotao(botaoImportantes);
        botaoImportantes.addActionListener(e -> mapaPanel.mostrarEstacoesImportantes());

        JLabel zoomTitulo = criarTituloSecao("Zoom");
        JLabel zoomValor = new JLabel("100%");
        zoomValor.setFont(new Font("SansSerif", Font.BOLD, 13));
        zoomValor.setForeground(new Color(32, 38, 46));
        zoomValor.setHorizontalAlignment(SwingConstants.CENTER);
        zoomValor.setPreferredSize(new Dimension(58, 34));

        JButton botaoDiminuirZoom = new JButton("-");
        JButton botaoAumentarZoom = new JButton("+");
        configurarBotaoZoom(botaoDiminuirZoom);
        configurarBotaoZoom(botaoAumentarZoom);
        botaoDiminuirZoom.addActionListener(e -> {
            mapaPanel.alterarZoom(-0.1);
            zoomValor.setText(mapaPanel.getZoomPercentual() + "%");
        });
        botaoAumentarZoom.addActionListener(e -> {
            mapaPanel.alterarZoom(0.1);
            zoomValor.setText(mapaPanel.getZoomPercentual() + "%");
        });

        JPanel painelZoom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelZoom.setOpaque(false);
        painelZoom.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelZoom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        painelZoom.add(botaoDiminuirZoom);
        painelZoom.add(zoomValor);
        painelZoom.add(botaoAumentarZoom);

        painel.add(titulo);
        painel.add(Box.createRigidArea(new Dimension(0, 12)));
        painel.add(instrucoes);
        painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(statusTitulo);
        painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(status);
        painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(botaoLimpar);
        painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(botaoDetalhes);
        painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(botaoImportantes);
        painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(zoomTitulo);
        painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(painelZoom);
        painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(criarTituloSecao("Legenda"));
        painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(criarLegenda(mapaPanel.getCoresLinhas()));
        painel.add(Box.createVerticalGlue());

        return painel;
    }

    private static JLabel criarTituloSecao(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(32, 38, 46));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static void configurarBotao(JButton botao) {
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        botao.setFocusPainted(false);
        botao.setBackground(Color.WHITE);
        botao.setForeground(new Color(32, 38, 46));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 213, 220)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
    }

    private static void configurarBotaoZoom(JButton botao) {
        botao.setPreferredSize(new Dimension(42, 34));
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.BOLD, 16));
        botao.setBackground(Color.WHITE);
        botao.setForeground(new Color(32, 38, 46));
        botao.setBorder(BorderFactory.createLineBorder(new Color(207, 213, 220)));
    }

    private static JPanel criarLegenda(Map<String, Color> coresLinhas) {
        JPanel legenda = new JPanel();
        legenda.setLayout(new BoxLayout(legenda, BoxLayout.Y_AXIS));
        legenda.setOpaque(false);
        legenda.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Map.Entry<String, Color> entrada : coresLinhas.entrySet()) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            item.setOpaque(false);
            item.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel cor = new JLabel();
            cor.setOpaque(true);
            cor.setBackground(entrada.getValue());
            cor.setPreferredSize(new Dimension(22, 8));
            cor.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100, 70)));

            JLabel nome = new JLabel("  " + entrada.getKey());
            nome.setFont(new Font("SansSerif", Font.PLAIN, 12));
            nome.setForeground(new Color(73, 80, 89));

            item.add(cor);
            item.add(nome);
            legenda.add(item);
        }

        return legenda;
    }

    /**
     * Painel que desenha o mapa esquemático e trata toda a interação do usuário
     * (seleção de estações, cálculo de rota, interdição, animação).
     */
    static class MapaPanel extends JPanel {

        private static final int MARGEM_ESQUERDA = 120;
        private static final int MARGEM_SUPERIOR = 100;
        private static final int DIST_ESTACOES = 90;
        private static final int DIST_LINHAS = 90;

        private static final int RAIO_ESTACAO = 6;
        private static final int RAIO_BALDEACAO = 9;
        private static final int RAIO_CLIQUE = 14;
        private static final int RAIO_BOLINHA = 7;
        private static final double ZOOM_MINIMO = 0.6;
        private static final double ZOOM_MAXIMO = 1.8;

        private final Grafo rede;
        private final Map<String, Point2D.Double> coordenadas = new HashMap<>();
        private final Map<String, Color> coresLinhas = new LinkedHashMap<>();
        private final Set<String> nomesBaldeacao = new HashSet<>();
        private JLabel statusLabel;
        private Dimension tamanhoBase = new Dimension(1000, 800);
        private double zoom = 1.0;

        private Estacao origem;
        private Estacao destino;
        private ResultadoRota rotaMaisRapida;
        private ResultadoRota rotaMenosBaldeacoes;

        private javax.swing.Timer timerAnimacao;
        private List<Point2D.Double> pontosAnimacao;
        private int trechoAnimacao;
        private double progressoTrecho;
        private Point2D.Double posicaoBolinha;

        MapaPanel(Grafo rede) {
            this.rede = rede;
            setBackground(new Color(246, 248, 250));
            montarCoresLinhas();
            calcularCoordenadasEsquematicas();
            configurarPreferredSize();

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tratarClique(e);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    tratarMovimentoMouse(e);
                }
            };
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);

            ToolTipManager.sharedInstance().registerComponent(this);
        }

        Map<String, Color> getCoresLinhas() {
            return Collections.unmodifiableMap(coresLinhas);
        }

        void setStatusLabel(JLabel statusLabel) {
            this.statusLabel = statusLabel;
            atualizarStatus("Selecione a origem.");
        }

        void alterarZoom(double delta) {
            double novoZoom = Math.max(ZOOM_MINIMO, Math.min(ZOOM_MAXIMO, zoom + delta));
            if (Math.abs(novoZoom - zoom) < 0.001) {
                return;
            }
            zoom = novoZoom;
            aplicarPreferredSizeComZoom();
            revalidate();
            repaint();
        }

        int getZoomPercentual() {
            return (int) Math.round(zoom * 100);
        }

        private void atualizarStatus(String texto) {
            if (statusLabel != null) {
                statusLabel.setText("<html><body style='width:220px'>" + texto + "</body></html>");
            }
        }

        // ---------- Cores das linhas ----------

        private void montarCoresLinhas() {
            coresLinhas.put("Linha 1 - Azul", new Color(0, 90, 180));
            coresLinhas.put("Linha 2 - Verde", new Color(0, 150, 70));
            coresLinhas.put("Linha 3 - Vermelha", new Color(200, 20, 20));
            coresLinhas.put("Linha 4 - Amarela", new Color(230, 180, 0));
            coresLinhas.put("Linha 5 - Lilás", new Color(150, 90, 180));
            coresLinhas.put("Linha 7 - Rubi", new Color(140, 20, 60));
            coresLinhas.put("Linha 8 - Diamante", new Color(120, 120, 120));
            coresLinhas.put("Linha 9 - Esmeralda", new Color(0, 130, 90));
            coresLinhas.put("Linha 10 - Turquesa", new Color(0, 160, 160));
            coresLinhas.put("Linha 11 - Coral", new Color(230, 100, 80));
            coresLinhas.put("Linha 12 - Safira", new Color(20, 40, 130));
            coresLinhas.put("Linha 13 - Jade", new Color(0, 120, 80));
            coresLinhas.put("Linha 15 - Prata", new Color(170, 170, 170));
            coresLinhas.put("Linha 17 - Ouro", new Color(190, 150, 40));
        }

        private Color corDaLinha(String linha) {
            return coresLinhas.getOrDefault(linha, Color.DARK_GRAY);
        }

        // ---------- Layout esquemático ----------

        private void calcularCoordenadasEsquematicas() {
            Map<String, List<Estacao>> porLinha = new LinkedHashMap<>();
            for (Estacao estacao : rede.getEstacoes()) {
                porLinha.computeIfAbsent(estacao.getLinha(), k -> new ArrayList<>()).add(estacao);
            }

            int indiceLinha = 0;
            for (Map.Entry<String, List<Estacao>> entrada : porLinha.entrySet()) {
                List<Estacao> sequencia = montarSequenciaLinha(entrada.getKey(), entrada.getValue());
                int y = MARGEM_SUPERIOR + indiceLinha * DIST_LINHAS;
                for (int i = 0; i < sequencia.size(); i++) {
                    int x = MARGEM_ESQUERDA + i * DIST_ESTACOES;
                    coordenadas.put(sequencia.get(i).getId(), new Point2D.Double(x, y));
                }
                indiceLinha++;
            }

            aproximarEstacoesDeBaldeacao();
        }

        /**
         * Tenta montar uma sequência linear das estações de uma linha,
         * seguindo as conexões do grafo que permanecem na mesma linha.
         * Se sobrarem estações desconectadas, começa um novo segmento.
         */
        private List<Estacao> montarSequenciaLinha(String linha, List<Estacao> estacoesDaLinha) {
            Set<String> visitadas = new HashSet<>();
            List<Estacao> sequencia = new ArrayList<>();

            for (Estacao inicio : estacoesDaLinha) {
                if (visitadas.contains(inicio.getId())) {
                    continue;
                }
                Estacao atual = inicio;
                while (atual != null && !visitadas.contains(atual.getId())) {
                    sequencia.add(atual);
                    visitadas.add(atual.getId());

                    Estacao proximo = null;
                    for (Conexao conexao : rede.getConexoes(atual.getId())) {
                        Estacao destinoConexao = conexao.getDestino();
                        if (linha.equals(destinoConexao.getLinha())
                                && !visitadas.contains(destinoConexao.getId())) {
                            proximo = destinoConexao;
                            break;
                        }
                    }
                    atual = proximo;
                }
            }
            return sequencia;
        }

        /**
         * Estações com o mesmo nome em linhas diferentes representam a mesma
         * estação física (baldeação). Aproxima visualmente essas estações,
         * deslocando-as levemente em direção ao centro do grupo.
         */
        private void aproximarEstacoesDeBaldeacao() {
            Map<String, List<Estacao>> porNome = new HashMap<>();
            for (Estacao estacao : rede.getEstacoes()) {
                porNome.computeIfAbsent(estacao.getNome(), k -> new ArrayList<>()).add(estacao);
            }

            for (Map.Entry<String, List<Estacao>> entrada : porNome.entrySet()) {
                List<Estacao> grupo = entrada.getValue();
                if (grupo.size() < 2) {
                    continue;
                }
                nomesBaldeacao.add(entrada.getKey());

                double centroX = 0;
                double centroY = 0;
                for (Estacao estacao : grupo) {
                    Point2D.Double ponto = coordenadas.get(estacao.getId());
                    centroX += ponto.x;
                    centroY += ponto.y;
                }
                centroX /= grupo.size();
                centroY /= grupo.size();

                for (Estacao estacao : grupo) {
                    Point2D.Double ponto = coordenadas.get(estacao.getId());
                    double novoX = ponto.x + (centroX - ponto.x) * 0.3;
                    double novoY = ponto.y + (centroY - ponto.y) * 0.3;
                    coordenadas.put(estacao.getId(), new Point2D.Double(novoX, novoY));
                }
            }
        }

        private void configurarPreferredSize() {
            double maxX = 0;
            double maxY = 0;
            for (Point2D.Double ponto : coordenadas.values()) {
                maxX = Math.max(maxX, ponto.x);
                maxY = Math.max(maxY, ponto.y);
            }
            tamanhoBase = new Dimension((int) maxX + 240, (int) maxY + 220);
            aplicarPreferredSizeComZoom();
        }

        private void aplicarPreferredSizeComZoom() {
            setPreferredSize(new Dimension(
                    (int) Math.round(tamanhoBase.width * zoom),
                    (int) Math.round(tamanhoBase.height * zoom)));
        }

        // ---------- Interação: clique e movimento do mouse ----------

        private void tratarClique(MouseEvent e) {
            Estacao estacao = estacaoMaisProxima(e.getPoint());
            if (estacao == null) {
                return;
            }

            if (SwingUtilities.isRightMouseButton(e)) {
                String statusInterdicao;
                if (estacao.isAtiva()) {
                    rede.interditar(estacao.getId());
                    statusInterdicao = "Estação " + estacao.getNome() + " interditada.";
                } else {
                    rede.reativar(estacao.getId());
                    statusInterdicao = "Estação " + estacao.getNome() + " reativada.";
                }
                if (origem != null && destino != null) {
                    calcularRotas();
                    atualizarStatus(statusInterdicao + " " + resumoRotaAtual());
                } else {
                    atualizarStatus(statusInterdicao);
                }
                repaint();
                return;
            }

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (origem == null) {
                    origem = estacao;
                    destino = null;
                    rotaMaisRapida = null;
                    rotaMenosBaldeacoes = null;
                    pararAnimacao();
                    atualizarStatus("Origem escolhida: " + origem.getNome()
                            + ". Agora selecione o destino.");
                } else if (destino == null) {
                    if (!estacao.getId().equals(origem.getId())) {
                        destino = estacao;
                        calcularRotas();
                    } else {
                        atualizarStatus("Origem já escolhida: " + origem.getNome()
                                + ". Selecione uma estação diferente como destino.");
                    }
                } else {
                    // já havia uma rota calculada: começa uma nova seleção
                    origem = estacao;
                    destino = null;
                    rotaMaisRapida = null;
                    rotaMenosBaldeacoes = null;
                    pararAnimacao();
                    atualizarStatus("Nova origem escolhida: " + origem.getNome()
                            + ". Agora selecione o destino.");
                }
                repaint();
            }
        }

        private void tratarMovimentoMouse(MouseEvent e) {
            Estacao estacao = estacaoMaisProxima(e.getPoint());
            if (estacao != null) {
                setToolTipText(estacao.getNome() + " (" + estacao.getLinha() + ")");
            } else {
                setToolTipText(null);
            }
        }

        private Estacao estacaoMaisProxima(Point clique) {
            Estacao maisProxima = null;
            double menorDistancia = Double.MAX_VALUE;
            double cliqueX = clique.x / zoom;
            double cliqueY = clique.y / zoom;

            for (Estacao estacao : rede.getEstacoes()) {
                Point2D.Double ponto = coordenadas.get(estacao.getId());
                if (ponto == null) {
                    continue;
                }
                double distancia = ponto.distance(cliqueX, cliqueY);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    maisProxima = estacao;
                }
            }

            return (menorDistancia <= RAIO_CLIQUE) ? maisProxima : null;
        }

        // ---------- Cálculo de rota ----------

        private void calcularRotas() {
            atualizarStatus("Calculando rota de " + origem.getNome() + " até "
                    + destino.getNome() + "...");
            rotaMaisRapida = Dijkstra.calcularCaminhoMinimo(rede, origem.getId(), destino.getId());
            rotaMenosBaldeacoes = Dijkstra.calcularMenosBaldeacoes(rede, origem.getId(), destino.getId());
            if (rotaMaisRapida == null || rotaMaisRapida.éVazio()) {
                atualizarStatus("Sem rota disponível com as estações atuais.");
            } else {
                atualizarStatus(resumoRotaAtual());
            }
            iniciarAnimacao(rotaMaisRapida);
        }

        private String resumoRotaAtual() {
            if (rotaMaisRapida == null || rotaMaisRapida.éVazio()) {
                return "Sem rota disponível com as estações atuais.";
            }
            return String.format(Locale.US,
                    "Rota encontrada: %.0f min, %d baldeação(ões).",
                    rotaMaisRapida.getCustoTotal(), rotaMaisRapida.getBaldeacoes());
        }

        void limparSelecao() {
            origem = null;
            destino = null;
            rotaMaisRapida = null;
            rotaMenosBaldeacoes = null;
            pararAnimacao();
            posicaoBolinha = null;
            atualizarStatus("Selecione a origem.");
            repaint();
        }

        void mostrarDetalhesRota() {
            if (rotaMaisRapida == null || rotaMaisRapida.éVazio()) {
                JOptionPane.showMessageDialog(this,
                        "Selecione uma estação de origem e uma de destino primeiro.",
                        "Detalhes da rota", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder mensagem = new StringBuilder();
            mensagem.append("Rota mais rápida:\n").append(rotaMaisRapida.toString());
            mensagem.append("\n\n");
            if (rotaMenosBaldeacoes != null && !rotaMenosBaldeacoes.éVazio()) {
                mensagem.append("Rota com menos baldeações:\n").append(rotaMenosBaldeacoes.toString());
            } else {
                mensagem.append("Não foi possível calcular a rota com menos baldeações.");
            }

            JOptionPane.showMessageDialog(this, mensagem.toString(),
                    "Detalhes da rota", JOptionPane.INFORMATION_MESSAGE);
        }

        void mostrarEstacoesImportantes() {
            List<Map.Entry<String, Integer>> top = Betweenness.top(rede, 10);

            if (top.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Não há dados suficientes para calcular as estações mais importantes.",
                        "Estações mais importantes", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder mensagem = new StringBuilder("Estações mais importantes da rede:\n\n");
            int posicao = 1;
            for (Map.Entry<String, Integer> entrada : top) {
                Estacao estacao = rede.getEstacao(entrada.getKey());
                String nome = (estacao != null) ? estacao.getNome() : entrada.getKey();
                mensagem.append(posicao).append(". ").append(nome)
                        .append(" (").append(entrada.getValue()).append(")\n");
                posicao++;
            }

            JOptionPane.showMessageDialog(this, mensagem.toString(),
                    "Estações mais importantes", JOptionPane.INFORMATION_MESSAGE);
        }

        // ---------- Animação ----------

        private void iniciarAnimacao(ResultadoRota rota) {
            pararAnimacao();

            if (rota == null || rota.éVazio()) {
                posicaoBolinha = null;
                repaint();
                return;
            }

            pontosAnimacao = new ArrayList<>();
            for (Estacao estacao : rota.getCaminho()) {
                Point2D.Double ponto = coordenadas.get(estacao.getId());
                if (ponto != null) {
                    pontosAnimacao.add(ponto);
                }
            }

            if (pontosAnimacao.size() < 2) {
                posicaoBolinha = pontosAnimacao.isEmpty() ? null : pontosAnimacao.get(0);
                repaint();
                return;
            }

            trechoAnimacao = 0;
            progressoTrecho = 0.0;
            posicaoBolinha = new Point2D.Double(pontosAnimacao.get(0).x, pontosAnimacao.get(0).y);

            timerAnimacao = new javax.swing.Timer(30, e -> {
                progressoTrecho += 0.03;

                if (progressoTrecho >= 1.0) {
                    progressoTrecho = 0.0;
                    trechoAnimacao++;
                    if (trechoAnimacao >= pontosAnimacao.size() - 1) {
                        posicaoBolinha = pontosAnimacao.get(pontosAnimacao.size() - 1);
                        pararAnimacao();
                        repaint();
                        return;
                    }
                }

                Point2D.Double a = pontosAnimacao.get(trechoAnimacao);
                Point2D.Double b = pontosAnimacao.get(trechoAnimacao + 1);
                double x = a.x + (b.x - a.x) * progressoTrecho;
                double y = a.y + (b.y - a.y) * progressoTrecho;
                posicaoBolinha = new Point2D.Double(x, y);
                repaint();
            });
            timerAnimacao.start();
        }

        private void pararAnimacao() {
            if (timerAnimacao != null) {
                timerAnimacao.stop();
                timerAnimacao = null;
            }
        }

        // ---------- Desenho ----------

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale(zoom, zoom);

            desenharQuadroMapa(g2);
            desenharConexoes(g2);
            desenharRota(g2, rotaMenosBaldeacoes, rotaMaisRapida, new Color(30, 90, 220),
                    new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            0, new float[]{10, 8}, 0));
            desenharRota(g2, rotaMaisRapida, null, Color.BLACK,
                    new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            desenharEstacoes(g2);
            desenharBolinha(g2);
        }

        private void desenharQuadroMapa(Graphics2D g2) {
            int margem = 32;
            int largura = tamanhoBase.width - margem * 2;
            int altura = tamanhoBase.height - margem * 2;

            RoundRectangle2D.Double sombra = new RoundRectangle2D.Double(
                    margem + 5, margem + 5, largura, altura, 10, 10);
            RoundRectangle2D.Double quadro = new RoundRectangle2D.Double(
                    margem, margem, largura, altura, 10, 10);

            g2.setColor(new Color(210, 215, 222));
            g2.fill(sombra);
            g2.setColor(Color.WHITE);
            g2.fill(quadro);
            g2.setColor(new Color(178, 186, 196));
            g2.setStroke(new BasicStroke(2));
            g2.draw(quadro);

            String titulo = "Metrô de São Paulo";
            Font fonteTitulo = new Font("SansSerif", Font.BOLD, 22);
            g2.setFont(fonteTitulo);
            FontMetrics metricas = g2.getFontMetrics();
            int x = margem + (largura - metricas.stringWidth(titulo)) / 2;
            int y = margem + 36;

            g2.setColor(new Color(32, 38, 46));
            g2.drawString(titulo, x, y);
            g2.setColor(new Color(218, 222, 228));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(margem + 24, margem + 54, margem + largura - 24, margem + 54);
        }

        private void desenharConexoes(Graphics2D g2) {
            Set<String> desenhadas = new HashSet<>();

            for (Estacao estacao : rede.getEstacoes()) {
                Point2D.Double origemPonto = coordenadas.get(estacao.getId());
                if (origemPonto == null) {
                    continue;
                }

                for (Conexao conexao : rede.getConexoes(estacao.getId())) {
                    Estacao destinoEstacao = conexao.getDestino();
                    Point2D.Double destinoPonto = coordenadas.get(destinoEstacao.getId());
                    if (destinoPonto == null) {
                        continue;
                    }

                    String chave = chaveAresta(estacao.getId(), destinoEstacao.getId());
                    if (desenhadas.contains(chave)) {
                        continue;
                    }
                    desenhadas.add(chave);

                    boolean mesmaLinha = estacao.getLinha().equals(destinoEstacao.getLinha());
                    if (mesmaLinha) {
                        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.setColor(corDaLinha(estacao.getLinha()));
                    } else {
                        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                0, new float[]{4, 4}, 0));
                        g2.setColor(Color.LIGHT_GRAY);
                    }

                    g2.draw(new Line2D.Double(origemPonto, destinoPonto));
                }
            }
        }

        private String chaveAresta(String idA, String idB) {
            return (idA.compareTo(idB) < 0) ? idA + "|" + idB : idB + "|" + idA;
        }

        /**
         * Desenha os trechos de uma rota. Se rotaParaExcluir for informada,
         * só desenha os trechos que NÃO existem na rota a excluir
         * (usado para destacar só o que é exclusivo da rota de menos baldeações).
         */
        private void desenharRota(Graphics2D g2, ResultadoRota rota, ResultadoRota rotaParaExcluir,
                                   Color cor, Stroke stroke) {
            if (rota == null || rota.éVazio()) {
                return;
            }

            Set<String> arestasExcluidas = new HashSet<>();
            if (rotaParaExcluir != null && !rotaParaExcluir.éVazio()) {
                List<Estacao> caminhoExcluido = rotaParaExcluir.getCaminho();
                for (int i = 0; i < caminhoExcluido.size() - 1; i++) {
                    arestasExcluidas.add(chaveAresta(
                            caminhoExcluido.get(i).getId(), caminhoExcluido.get(i + 1).getId()));
                }
            }

            List<Estacao> caminho = rota.getCaminho();
            g2.setColor(cor);
            g2.setStroke(stroke);

            for (int i = 0; i < caminho.size() - 1; i++) {
                Estacao a = caminho.get(i);
                Estacao b = caminho.get(i + 1);
                String chave = chaveAresta(a.getId(), b.getId());
                if (arestasExcluidas.contains(chave)) {
                    continue;
                }

                Point2D.Double pontoA = coordenadas.get(a.getId());
                Point2D.Double pontoB = coordenadas.get(b.getId());
                if (pontoA != null && pontoB != null) {
                    g2.draw(new Line2D.Double(pontoA, pontoB));
                }
            }
        }

        private void desenharEstacoes(Graphics2D g2) {
            for (Estacao estacao : rede.getEstacoes()) {
                Point2D.Double ponto = coordenadas.get(estacao.getId());
                if (ponto == null) {
                    continue;
                }

                boolean ehBaldeacao = nomesBaldeacao.contains(estacao.getNome());
                int raio = ehBaldeacao ? RAIO_BALDEACAO : RAIO_ESTACAO;

                Color corPreenchimento = corDaLinha(estacao.getLinha());
                if (origem != null && origem.getId().equals(estacao.getId())) {
                    corPreenchimento = Color.MAGENTA;
                } else if (destino != null && destino.getId().equals(estacao.getId())) {
                    corPreenchimento = Color.ORANGE;
                }

                Ellipse2D.Double circulo = new Ellipse2D.Double(
                        ponto.x - raio, ponto.y - raio, raio * 2.0, raio * 2.0);

                g2.setColor(Color.WHITE);
                g2.fill(circulo);
                g2.setColor(corPreenchimento);
                g2.setStroke(new BasicStroke(2));
                g2.draw(circulo);
                g2.fill(new Ellipse2D.Double(
                        ponto.x - raio + 2, ponto.y - raio + 2, raio * 2.0 - 4, raio * 2.0 - 4));

                if (!estacao.isAtiva()) {
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(2.5f));
                    int offset = raio + 3;
                    g2.draw(new Line2D.Double(ponto.x - offset, ponto.y - offset,
                            ponto.x + offset, ponto.y + offset));
                    g2.draw(new Line2D.Double(ponto.x - offset, ponto.y + offset,
                            ponto.x + offset, ponto.y - offset));
                }
            }
        }

        private void desenharBolinha(Graphics2D g2) {
            if (posicaoBolinha == null) {
                return;
            }

            double raio = RAIO_BOLINHA;
            Ellipse2D.Double bolinha = new Ellipse2D.Double(
                    posicaoBolinha.x - raio, posicaoBolinha.y - raio, raio * 2, raio * 2);

            g2.setColor(Color.YELLOW);
            g2.fill(bolinha);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.draw(bolinha);
        }
    }
}
