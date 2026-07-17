package app;

import metro.dados.DadosMetroSP;
import metro.model.Grafo;
import metro.model.Estacao;
import metro.model.Conexao;
import metro.algoritmos.Dijkstra;
import metro.algoritmos.ResultadoRota;
import metro.algoritmos.Betweenness;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class MapaMetroSPApp {

    private static final Color BG_MAPA          = new Color(0x11, 0x11, 0x22);
    private static final Color BG_PAINEL        = new Color(0x16, 0x21, 0x3e);
    private static final Color BG_PAINEL_SCROLL = new Color(0x0d, 0x0d, 0x1a);
    private static final Color COR_TEXTO        = new Color(0xDD, 0xDD, 0xDD);
    private static final Color COR_TEXTO_BTN    = new Color(0xEE, 0xEE, 0xEE);
    private static final Color COR_BORDA_BTN    = new Color(0x33, 0x41, 0x55);
    private static final Color COR_BORDA_HOVER  = new Color(0x60, 0xA5, 0xFA);
    private static final Color COR_STATUS_OK    = new Color(0x4A, 0xDE, 0x80);
    private static final Color COR_STATUS_ERRO  = new Color(0xF8, 0x71, 0x71);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MapaMetroSPApp::criarJanela);
    }

    private static void estilizarScrollBar(JScrollPane scrollPane) {
        Color trackColor = new Color(0x0d, 0x0d, 0x1a);
        Color thumbColor = new Color(0x33, 0x41, 0x55);
        BasicScrollBarUI uiV = new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {}
            @Override protected JButton createDecreaseButton(int o) { JButton b=new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected JButton createIncreaseButton(int o) { JButton b=new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { g.setColor(trackColor); g.fillRect(r.x, r.y, r.width, r.height); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor); g2.fillRoundRect(r.x+2,r.y+2,r.width-4,r.height-4,6,6); g2.dispose();
            }
        };
        scrollPane.getVerticalScrollBar().setUI(uiV);
        scrollPane.getHorizontalScrollBar().setUI(uiV);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_PAINEL_SCROLL);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    private static void criarJanela() {
        Grafo rede = DadosMetroSP.carregar();
        JFrame frame = new JFrame("Mapa do Metro de Sao Paulo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_MAPA);
        MapaPanel mapaPanel = new MapaPanel(rede);
        JScrollPane scrollPane = new JScrollPane(mapaPanel);
        estilizarScrollBar(scrollPane);
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
        painel.setBackground(BG_PAINEL);

        JLabel titulo = new JLabel("Mapa do Metro - SP");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(COR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instrucoes = new JLabel("<html><body style='width:230px'><font color='#cccccc'>Clique esquerdo: escolha origem e depois destino.</font><br><br><font color='#cccccc'>Clique direito: interdita ou reativa a estacao.</font></body></html>");
        instrucoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusTitulo = criarTituloSecao("Status");
        JLabel status = new JLabel();
        status.setOpaque(true);
        status.setBackground(new Color(0x1e, 0x29, 0x3b));
        status.setForeground(COR_TEXTO);
        status.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        status.setFont(new Font("SansSerif", Font.PLAIN, 13));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        status.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        mapaPanel.setStatusLabel(status);

        JButton bLimpar = criarBotaoEstilizado("Limpar selecao");
        bLimpar.addActionListener(e -> mapaPanel.limparSelecao());
        JButton bDetalhes = criarBotaoEstilizado("Ver detalhes da rota");
        bDetalhes.addActionListener(e -> mapaPanel.mostrarDetalhesRota());
        JButton bImp = criarBotaoEstilizado("Estacoes mais importantes");
        bImp.addActionListener(e -> mapaPanel.mostrarEstacoesImportantes());

        JLabel rotaTitulo = criarTituloSecao("Tipo de Rota");
        ButtonGroup grupoRota = new ButtonGroup();
        JRadioButton rbRapida = new JRadioButton("Mais rapida");
        JRadioButton rbMenosBal = new JRadioButton("Menos baldeacoes");
        rbRapida.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rbMenosBal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rbRapida.setForeground(COR_TEXTO);
        rbMenosBal.setForeground(COR_TEXTO);
        rbRapida.setOpaque(false);
        rbMenosBal.setOpaque(false);
        rbRapida.setSelected(true);
        rbRapida.setEnabled(false);
        rbMenosBal.setEnabled(false);
        rbRapida.addActionListener(e -> mapaPanel.selecionarRota(0));
        rbMenosBal.addActionListener(e -> mapaPanel.selecionarRota(1));
        grupoRota.add(rbRapida);
        grupoRota.add(rbMenosBal);

        JPanel painelRota = new JPanel();
        painelRota.setLayout(new BoxLayout(painelRota, BoxLayout.Y_AXIS));
        painelRota.setOpaque(false);
        painelRota.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelRota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        painelRota.add(rbRapida);
        painelRota.add(rbMenosBal);
        mapaPanel.setRadioButtons(rbRapida, rbMenosBal);

        JLabel zoomTitulo = criarTituloSecao("Zoom");
        JLabel zoomValor = new JLabel("100%");
        zoomValor.setFont(new Font("SansSerif", Font.BOLD, 13));
        zoomValor.setForeground(COR_TEXTO);
        zoomValor.setHorizontalAlignment(SwingConstants.CENTER);
        zoomValor.setPreferredSize(new Dimension(58, 34));
        JButton bDim = criarBotaoZoom("-");
        JButton bAum = criarBotaoZoom("+");
        bDim.addActionListener(e -> { mapaPanel.alterarZoom(-0.1); zoomValor.setText(mapaPanel.getZoomPercentual()+"%"); });
        bAum.addActionListener(e -> { mapaPanel.alterarZoom(0.1); zoomValor.setText(mapaPanel.getZoomPercentual()+"%"); });
        JPanel painelZoom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelZoom.setOpaque(false); painelZoom.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelZoom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        painelZoom.add(bDim); painelZoom.add(zoomValor); painelZoom.add(bAum);

        painel.add(titulo); painel.add(Box.createRigidArea(new Dimension(0, 12)));
        painel.add(instrucoes); painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(statusTitulo); painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(status); painel.add(Box.createRigidArea(new Dimension(0, 14)));
        painel.add(rotaTitulo); painel.add(Box.createRigidArea(new Dimension(0, 4)));
        painel.add(painelRota); painel.add(Box.createRigidArea(new Dimension(0, 14)));
        painel.add(bLimpar); painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(bDetalhes); painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(bImp); painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(zoomTitulo); painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(painelZoom); painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(criarTituloSecao("Legenda")); painel.add(Box.createRigidArea(new Dimension(0, 8)));
        painel.add(criarLegenda(mapaPanel.getCoresLinhas()));
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private static JLabel criarTituloSecao(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(COR_TEXTO); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private static JButton criarBotaoEstilizado(String texto) {
        JButton btn = new JButton(texto) {
            private boolean h = false;
            { addMouseListener(new MouseAdapter() { public void mouseEntered(MouseEvent e){h=true;repaint();} public void mouseExited(MouseEvent e){h=false;repaint();} }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(),h2=getHeight();
                g2.setColor(new Color(0x1e,0x29,0x3b)); g2.fillRoundRect(0,0,w,h2,8,8);
                g2.setColor(h?COR_BORDA_HOVER:COR_BORDA_BTN); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(0,0,w-1,h2-1,8,8);
                g2.setColor(COR_TEXTO_BTN); g2.setFont(getFont()); FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(w-fm.stringWidth(getText()))/2,(h2+fm.getAscent()-fm.getDescent())/2); g2.dispose();
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        btn.setFocusPainted(false); btn.setContentAreaFilled(false); btn.setOpaque(false); btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif",Font.PLAIN,12)); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static JButton criarBotaoZoom(String texto) {
        JButton btn = new JButton(texto) {
            private boolean h = false;
            { addMouseListener(new MouseAdapter() { public void mouseEntered(MouseEvent e){h=true;repaint();} public void mouseExited(MouseEvent e){h=false;repaint();} }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(),h2=getHeight();
                g2.setColor(new Color(0x1e,0x29,0x3b)); g2.fillRoundRect(0,0,w,h2,8,8);
                g2.setColor(h?COR_BORDA_HOVER:COR_BORDA_BTN); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(0,0,w-1,h2-1,8,8);
                g2.setColor(COR_TEXTO_BTN); g2.setFont(getFont()); FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(w-fm.stringWidth(getText()))/2,(h2+fm.getAscent()-fm.getDescent())/2); g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(42,34)); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setOpaque(false); btn.setBorderPainted(false); btn.setFont(new Font("SansSerif",Font.BOLD,16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }

    private static JPanel criarLegenda(Map<String, Color> coresLinhas) {
        JPanel legenda = new JPanel(); legenda.setLayout(new BoxLayout(legenda, BoxLayout.Y_AXIS));
        legenda.setOpaque(false); legenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Map.Entry<String, Color> e : coresLinhas.entrySet()) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3)); item.setOpaque(false); item.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel cor = new JLabel(); cor.setOpaque(true); cor.setBackground(e.getValue()); cor.setPreferredSize(new Dimension(22, 8));
            JLabel nome = new JLabel("  " + e.getKey()); nome.setFont(new Font("SansSerif", Font.PLAIN, 12)); nome.setForeground(COR_TEXTO);
            item.add(cor); item.add(nome); legenda.add(item);
        }
        return legenda;
    }

    static class MapaPanel extends JPanel {
        private static final int MARGEM = 24;
        private static final int MARGEM_ESQ = 120, MARGEM_SUP = 100;
        private static final int DIST_E = 90, DIST_L = 90;
        private static final int RAIO_ESTACAO = 5, RAIO_BALDEACAO = 8, RAIO_TERMINAL = 7;
        private static final int RAIO_CLIQUE = 16, RAIO_LEDAtual = 9;
        private static final double ZOOM_MIN = 0.6, ZOOM_MAX = 1.8;
        private final Grafo rede;
        private final Map<String, Point2D.Double> coordenadas = new HashMap<>();
        private final Map<String, Color> coresLinhas = new LinkedHashMap<>();
        private final Set<String> nomesBaldeacao = new HashSet<>();
        private final Set<String> estacoesTerminal = new HashSet<>();
        private final Map<String, List<String>> estacoesPorLinha = new LinkedHashMap<>();
        private JLabel statusLabel;
        private Dimension tamanhoBase = new Dimension(1000, 800);
        private double zoomAtual = 1.0, zoomDestino = 1.0;
        private javax.swing.Timer timerZoom;
        private Estacao origem, destino;
        private ResultadoRota rotaMaisRapida, rotaMenosBaldeacoes;
        private javax.swing.Timer timerAnimacao;
        private List<Estacao> rotaEstacoes;
        private Set<Integer> estacoesAcesas = new HashSet<>();
        private int estacaoAtualAnimIndex = -1;
        private double progressoTrecho = 0.0;
        private Point2D.Double posicaoLedAtual;
        private javax.swing.Timer timerPulse;
        private double fasePulse = 0.0;
        private javax.swing.Timer timerGlow;
        private double faseGlow = 0.0;
        private Point mousePos;
        private final List<Point2D.Double> ledsFundo = new ArrayList<>();
        private JRadioButton rbRapida, rbMenosBal;
        private int tipoRotaSelecionada = 0;

        MapaPanel(Grafo rede) {
            this.rede = rede;
            setBackground(BG_MAPA);
            montarCoresLinhas();
            calcularCoordenadasEsquematicas();
            identificarTerminais();
            configurarPreferredSize();
            gerarLedsFundo();
            MouseAdapter ma = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { tratarClique(e); }
                @Override public void mouseMoved(MouseEvent e) { mousePos = e.getPoint(); Estacao est = estacaoMaisProxima(e.getPoint()); setToolTipText(est != null ? est.getNome() + " (" + est.getLinha() + ")" : null); repaint(); }
                @Override public void mouseExited(MouseEvent e) { mousePos = null; repaint(); }
            };
            addMouseListener(ma); addMouseMotionListener(ma);
            ToolTipManager.sharedInstance().registerComponent(this);
            timerGlow = new javax.swing.Timer(30, e -> { faseGlow += 0.06; repaint(); });
            timerGlow.start();
        }

        Map<String, Color> getCoresLinhas() { return Collections.unmodifiableMap(coresLinhas); }
        void setStatusLabel(JLabel sl) { this.statusLabel = sl; atualizarStatus("Selecione a origem.", COR_TEXTO); }
        void setRadioButtons(JRadioButton r1, JRadioButton r2) { this.rbRapida = r1; this.rbMenosBal = r2; }
        void selecionarRota(int tipo) { this.tipoRotaSelecionada = tipo; if (tipo == 0 && rotaMaisRapida != null && !rotaMaisRapida.estaVazio()) iniciarAnimacaoRota(rotaMaisRapida); else if (tipo == 1 && rotaMenosBaldeacoes != null && !rotaMenosBaldeacoes.estaVazio()) iniciarAnimacaoRota(rotaMenosBaldeacoes); }

        void alterarZoom(double delta) {
            zoomDestino = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoomDestino + delta));
            if (timerZoom == null || !timerZoom.isRunning()) { timerZoom = new javax.swing.Timer(15, e -> { zoomAtual += (zoomDestino - zoomAtual) * 0.20; if (Math.abs(zoomAtual - zoomDestino) < 0.001) { zoomAtual = zoomDestino; timerZoom.stop(); } aplicarPreferredSizeComZoom(); revalidate(); repaint(); }); timerZoom.start(); }
        }
        int getZoomPercentual() { return (int) Math.round(zoomAtual * 100); }
        private void atualizarStatus(String t, Color c) { if (statusLabel != null) { String h = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()); statusLabel.setText("<html><body style='width:220px'><font color='" + h + "'>" + t + "</font></body></html>"); } }

        private void montarCoresLinhas() {
            coresLinhas.put("Linha 1 - Azul", new Color(0, 90, 180));
            coresLinhas.put("Linha 2 - Verde", new Color(0, 150, 70));
            coresLinhas.put("Linha 3 - Vermelha", new Color(200, 20, 20));
            coresLinhas.put("Linha 4 - Amarela", new Color(230, 180, 0));
            coresLinhas.put("Linha 5 - Lilas", new Color(150, 90, 180));
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
        private Color corDaLinha(String l) { return coresLinhas.getOrDefault(l, Color.DARK_GRAY); }
        private void gerarLedsFundo() { Random rng = new Random(42); for (int i = 0; i < 120; i++) ledsFundo.add(new Point2D.Double(MARGEM + 40 + rng.nextDouble() * 2000, MARGEM + 60 + rng.nextDouble() * 1400)); }

        private void calcularCoordenadasEsquematicas() {
            Map<String, List<Estacao>> porLinha = new LinkedHashMap<>();
            for (Estacao e : rede.getEstacoes()) porLinha.computeIfAbsent(e.getLinha(), k -> new ArrayList<>()).add(e);
            int idx = 0;
            for (Map.Entry<String, List<Estacao>> ent : porLinha.entrySet()) {
                List<Estacao> seq = montarSequenciaLinha(ent.getKey(), ent.getValue());
                estacoesPorLinha.put(ent.getKey(), new ArrayList<>());
                int y = MARGEM_SUP + idx * DIST_L;
                for (int i = 0; i < seq.size(); i++) { coordenadas.put(seq.get(i).getId(), new Point2D.Double(MARGEM_ESQ + i * DIST_E, y)); estacoesPorLinha.get(ent.getKey()).add(seq.get(i).getId()); }
                idx++;
            }
            aproximarEstacoesDeBaldeacao();
        }
        private List<Estacao> montarSequenciaLinha(String linha, List<Estacao> estacoes) { Set<String> vis = new HashSet<>(); List<Estacao> seq = new ArrayList<>(); for (Estacao ini : estacoes) { if (vis.contains(ini.getId())) continue; Estacao at = ini; while (at != null && !vis.contains(at.getId())) { seq.add(at); vis.add(at.getId()); Estacao prox = null; for (Conexao cn : rede.getConexoes(at.getId())) { Estacao d = cn.getDestino(); if (linha.equals(d.getLinha()) && !vis.contains(d.getId())) { prox = d; break; } } at = prox; } } return seq; }
        private void aproximarEstacoesDeBaldeacao() { Map<String, List<Estacao>> porNome = new HashMap<>(); for (Estacao e : rede.getEstacoes()) porNome.computeIfAbsent(e.getNome(), k -> new ArrayList<>()).add(e); for (Map.Entry<String, List<Estacao>> ent : porNome.entrySet()) { List<Estacao> g = ent.getValue(); if (g.size() < 2) continue; nomesBaldeacao.add(ent.getKey()); double cx = 0, cy = 0; for (Estacao e : g) { Point2D.Double p = coordenadas.get(e.getId()); cx += p.x; cy += p.y; } cx /= g.size(); cy /= g.size(); for (Estacao e : g) { Point2D.Double p = coordenadas.get(e.getId()); coordenadas.put(e.getId(), new Point2D.Double(p.x + (cx - p.x) * 0.3, p.y + (cy - p.y) * 0.3)); } } }
        private void identificarTerminais() { for (Map.Entry<String, List<String>> ent : estacoesPorLinha.entrySet()) { List<String> ids = ent.getValue(); if (ids.size() >= 2) { estacoesTerminal.add(ids.get(0)); estacoesTerminal.add(ids.get(ids.size() - 1)); } } }
        private void configurarPreferredSize() { double mx = 0, my = 0; for (Point2D.Double p : coordenadas.values()) { mx = Math.max(mx, p.x); my = Math.max(my, p.y); } tamanhoBase = new Dimension((int) mx + 240, (int) my + 220); aplicarPreferredSizeComZoom(); }
        private void aplicarPreferredSizeComZoom() { setPreferredSize(new Dimension((int) Math.round(tamanhoBase.width * zoomAtual), (int) Math.round(tamanhoBase.height * zoomAtual))); }

        private void tratarClique(MouseEvent e) {
            Estacao est = estacaoMaisProxima(e.getPoint()); if (est == null) return;
            if (SwingUtilities.isRightMouseButton(e)) { String msg; if (est.isAtiva()) { rede.interditar(est.getId()); msg = "Estacao " + est.getNome() + " interditada."; } else { rede.reativar(est.getId()); msg = "Estacao " + est.getNome() + " reativada."; } if (origem != null && destino != null) { calcularRotas(); atualizarStatus(msg + " " + resumoRotaAtual(), COR_TEXTO); } else atualizarStatus(msg, COR_TEXTO); repaint(); return; }
            if (SwingUtilities.isLeftMouseButton(e)) { if (origem == null) { origem = est; destino = null; limparEstadoAnimacao(); atualizarStatus("Origem: " + origem.getNome() + ". Selecione o destino.", COR_TEXTO); } else if (destino == null) { if (!est.getId().equals(origem.getId())) { destino = est; calcularRotas(); } else atualizarStatus("Ja e a origem. Selecione outra estacao.", COR_TEXTO); } else { origem = est; destino = null; limparEstadoAnimacao(); atualizarStatus("Nova origem: " + origem.getNome() + ". Selecione o destino.", COR_TEXTO); } repaint(); }
        }
        private Estacao estacaoMaisProxima(Point c) { Estacao best = null; double bestD = Double.MAX_VALUE; double cx = c.x / zoomAtual, cy = c.y / zoomAtual; for (Estacao e : rede.getEstacoes()) { Point2D.Double p = coordenadas.get(e.getId()); if (p == null) continue; double d = p.distance(cx, cy); if (d < bestD) { bestD = d; best = e; } } return bestD <= RAIO_CLIQUE ? best : null; }

        private void calcularRotas() {
            atualizarStatus("Calculando rota...", COR_TEXTO);
            rotaMaisRapida = Dijkstra.calcularCaminhoMinimo(rede, origem.getId(), destino.getId());
            rotaMenosBaldeacoes = Dijkstra.calcularMenosBaldeacoes(rede, origem.getId(), destino.getId());
            boolean temRapida = rotaMaisRapida != null && !rotaMaisRapida.estaVazio();
            boolean temMenosBal = rotaMenosBaldeacoes != null && !rotaMenosBaldeacoes.estaVazio();
            if (rbRapida != null) rbRapida.setEnabled(temRapida);
            if (rbMenosBal != null) rbMenosBal.setEnabled(temMenosBal);
            if (rbRapida != null) rbRapida.setSelected(true);
            tipoRotaSelecionada = 0;
            if (!temRapida) atualizarStatus("Sem rota disponivel.", COR_STATUS_ERRO); else atualizarStatus(resumoRotaAtual(), COR_STATUS_OK);
            iniciarAnimacaoRota(rotaMaisRapida);
        }
        private String resumoRotaAtual() { ResultadoRota rotaAtiva = tipoRotaSelecionada == 0 ? rotaMaisRapida : rotaMenosBaldeacoes; if (rotaAtiva == null || rotaAtiva.estaVazio()) return "Sem rota disponivel."; String tipo = tipoRotaSelecionada == 0 ? "Rapida" : "Menos baldeacoes"; return String.format(Locale.US, "[%s] %.0f min, %d bal.", tipo, rotaAtiva.getCustoTotal(), rotaAtiva.getBaldeacoes()); }
        void limparSelecao() { origem = null; destino = null; limparEstadoAnimacao(); atualizarStatus("Selecione a origem.", COR_TEXTO); repaint(); }
        private void limparEstadoAnimacao() { rotaMaisRapida = null; rotaMenosBaldeacoes = null; pararAnimacao(); pararPulse(); rotaEstacoes = null; estacoesAcesas.clear(); estacaoAtualAnimIndex = -1; progressoTrecho = 0.0; posicaoLedAtual = null; if (rbRapida != null) { rbRapida.setEnabled(false); rbRapida.setSelected(true); } if (rbMenosBal != null) rbMenosBal.setEnabled(false); tipoRotaSelecionada = 0; }
        void mostrarDetalhesRota() { if (rotaMaisRapida == null || rotaMaisRapida.estaVazio()) { JOptionPane.showMessageDialog(this, "Selecione origem e destino primeiro.", "Detalhes da rota", JOptionPane.INFORMATION_MESSAGE); return; } StringBuilder m = new StringBuilder("Rota mais rapida:\n").append(rotaMaisRapida.toString()).append("\n\n"); if (rotaMenosBaldeacoes != null && !rotaMenosBaldeacoes.estaVazio()) m.append("Rota com menos baldeacoes:\n").append(rotaMenosBaldeacoes.toString()); else m.append("Sem rota alternativa."); JOptionPane.showMessageDialog(this, m.toString(), "Detalhes da rota", JOptionPane.INFORMATION_MESSAGE); }
        void mostrarEstacoesImportantes() { List<Map.Entry<String, Integer>> top = Betweenness.top(rede, 10); if (top.isEmpty()) { JOptionPane.showMessageDialog(this, "Dados insuficientes.", "Estacoes mais importantes", JOptionPane.INFORMATION_MESSAGE); return; } StringBuilder m = new StringBuilder("Estacoes mais importantes:\n\n"); int p = 1; for (Map.Entry<String, Integer> e : top) { Estacao est = rede.getEstacao(e.getKey()); m.append(p++).append(". ").append(est != null ? est.getNome() : e.getKey()).append(" (").append(e.getValue()).append(")\n"); } JOptionPane.showMessageDialog(this, m.toString(), "Estacoes mais importantes", JOptionPane.INFORMATION_MESSAGE); }

        private void iniciarAnimacaoRota(ResultadoRota rota) {
            pararAnimacao(); pararPulse(); estacoesAcesas.clear();
            if (rota == null || rota.estaVazio()) { rotaEstacoes = null; posicaoLedAtual = null; estacaoAtualAnimIndex = -1; repaint(); return; }
            rotaEstacoes = rota.getCaminho(); estacaoAtualAnimIndex = 0; estacoesAcesas.add(0);
            posicaoLedAtual = coordenadas.get(rotaEstacoes.get(0).getId());
            timerPulse = new javax.swing.Timer(30, e -> { fasePulse += 0.12; repaint(); }); timerPulse.start();
            timerAnimacao = new javax.swing.Timer(200, e -> { if (rotaEstacoes == null) return; progressoTrecho += 0.06; if (progressoTrecho >= 1.0) { progressoTrecho = 0.0; estacaoAtualAnimIndex++; if (estacaoAtualAnimIndex >= rotaEstacoes.size()) { estacaoAtualAnimIndex = rotaEstacoes.size() - 1; posicaoLedAtual = coordenadas.get(rotaEstacoes.get(estacaoAtualAnimIndex).getId()); pararAnimacao(); repaint(); return; } estacoesAcesas.add(estacaoAtualAnimIndex); } if (estacaoAtualAnimIndex < rotaEstacoes.size() - 1) { Point2D.Double a = coordenadas.get(rotaEstacoes.get(estacaoAtualAnimIndex).getId()); Point2D.Double b = coordenadas.get(rotaEstacoes.get(estacaoAtualAnimIndex + 1).getId()); if (a != null && b != null) posicaoLedAtual = new Point2D.Double(a.x + (b.x - a.x) * progressoTrecho, a.y + (b.y - a.y) * progressoTrecho); } repaint(); }); timerAnimacao.start();
        }
        private void pararAnimacao() { if (timerAnimacao != null) { timerAnimacao.stop(); timerAnimacao = null; } }
        private void pararPulse() { if (timerPulse != null) { timerPulse.stop(); timerPulse = null; } fasePulse = 0.0; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.scale(zoomAtual, zoomAtual);
            desenharFundo(g2); desenharConexoes(g2); desenharSetasDecorativas(g2); desenharRotaAtiva(g2); desenharSetasRota(g2); desenharEstacoes(g2); desenharLedAtual(g2); desenharNomesLinhas(g2); desenharLabels(g2); desenharHUDRota(g2); desenharVinheta(g2);
        }

        private void desenharFundo(Graphics2D g2) {
            int l = tamanhoBase.width - MARGEM * 2, a = tamanhoBase.height - MARGEM * 2;
            g2.setPaint(new RadialGradientPaint(new Point2D.Double(tamanhoBase.width / 2.0, tamanhoBase.height / 2.0), Math.max(l, a) * 0.6f, new float[]{0, 1}, new Color[]{new Color(0x18, 0x18, 0x30), new Color(0x0a, 0x0a, 0x16)}));
            g2.fill(new Rectangle2D.Double(0, 0, tamanhoBase.width, tamanhoBase.height));
            g2.setColor(new Color(0xFF, 0xFF, 0xFF, 8)); g2.setStroke(new BasicStroke(0.5f));
            for (int x = MARGEM; x < MARGEM + l; x += 40) g2.drawLine(x, MARGEM, x, MARGEM + a);
            for (int y = MARGEM; y < MARGEM + a; y += 40) g2.drawLine(MARGEM, y, MARGEM + l, y);
            for (Point2D.Double led : ledsFundo) { if (led.x < MARGEM || led.x > MARGEM + l || led.y < MARGEM || led.y > MARGEM + a) continue; g2.setColor(new Color(0xFF, 0xFF, 0xFF, 6)); g2.fill(new Ellipse2D.Double(led.x - 1, led.y - 1, 2, 2)); }
            g2.setColor(new Color(0x2a, 0x2a, 0x44)); g2.setStroke(new BasicStroke(2)); g2.draw(new RoundRectangle2D.Double(MARGEM - 4, MARGEM - 4, l + 8, a + 8, 14, 14));
            g2.setColor(new Color(0x11, 0x11, 0x24)); g2.fill(new RoundRectangle2D.Double(MARGEM, MARGEM, l, a, 12, 12));
            g2.setColor(new Color(0x40, 0x40, 0x60)); g2.setStroke(new BasicStroke(1.5f)); int c = 22;
            g2.drawLine(MARGEM+2,MARGEM+c,MARGEM+2,MARGEM+2); g2.drawLine(MARGEM+2,MARGEM+2,MARGEM+c,MARGEM+2); g2.fillRect(MARGEM, MARGEM, 4, 4);
            g2.drawLine(MARGEM+l-c,MARGEM+2,MARGEM+l-2,MARGEM+2); g2.drawLine(MARGEM+l-2,MARGEM+2,MARGEM+l-2,MARGEM+c); g2.fillRect(MARGEM+l-4, MARGEM, 4, 4);
            g2.drawLine(MARGEM+2,MARGEM+a-c,MARGEM+2,MARGEM+a-2); g2.drawLine(MARGEM+2,MARGEM+a-2,MARGEM+c,MARGEM+a-2); g2.fillRect(MARGEM, MARGEM+a-4, 4, 4);
            g2.drawLine(MARGEM+l-c,MARGEM+a-2,MARGEM+l-2,MARGEM+a-2); g2.drawLine(MARGEM+l-2,MARGEM+a-c,MARGEM+l-2,MARGEM+a-2); g2.fillRect(MARGEM+l-4, MARGEM+a-4, 4, 4);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20)); FontMetrics fm = g2.getFontMetrics(); String t = "Metr\u00f4 de S\u00e3o Paulo"; int tx = MARGEM + (l - fm.stringWidth(t)) / 2; int ty = MARGEM + 38; g2.setColor(new Color(0x70, 0x80, 0xa0)); g2.drawString(t, tx, ty);
            int ly = MARGEM + 54; g2.setColor(new Color(0x30, 0x30, 0x50)); g2.setStroke(new BasicStroke(1)); g2.drawLine(tx - 12, ly, tx + fm.stringWidth(t) + 12, ly);
            g2.setColor(new Color(0x50, 0x60, 0x80)); g2.fillRect(tx - 14, ly - 3, 3, 6); g2.fillRect(tx + fm.stringWidth(t) + 12, ly - 3, 3, 6); g2.fill(new Ellipse2D.Double(tx + fm.stringWidth(t) / 2.0 - 2, ly - 2, 4, 4));
        }

        private void desenharConexoes(Graphics2D g2) {
            Set<String> des = new HashSet<>(); boolean temRota = rotaEstacoes != null && !estacoesAcesas.isEmpty(); Set<String> arRota = temRota ? obterArestasRota() : Collections.emptySet(); float fPulse = (float)(0.85 + 0.15 * Math.sin(faseGlow * 1.2));
            for (Estacao est : rede.getEstacoes()) { Point2D.Double pO = coordenadas.get(est.getId()); if (pO == null) continue; for (Conexao cn : rede.getConexoes(est.getId())) { Estacao dE = cn.getDestino(); Point2D.Double pD = coordenadas.get(dE.getId()); if (pD == null) continue; String ch = chaveAresta(est.getId(), dE.getId()); if (des.contains(ch)) continue; des.add(ch); if (!est.getLinha().equals(dE.getLinha())) continue; Color cor = corDaLinha(est.getLinha()); float op = temRota && !arRota.contains(ch) ? 0.30f * fPulse : fPulse; desenharTrilhoLuminoso(g2, pO, pD, cor, op); } }
        }
        private void desenharTrilhoLuminoso(Graphics2D g2, Point2D.Double a, Point2D.Double b, Color cor, float op) {
            Composite orig = g2.getComposite(); g2.setColor(cor);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op * 0.12f)); g2.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, b));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op * 0.28f)); g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, b));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op * 0.95f)); g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, b));
            double dx = b.x - a.x, dy = b.y - a.y; double len = Math.sqrt(dx * dx + dy * dy);
            if (len > 40) { int numReflexos = (int)(len / 35); for (int i = 1; i <= numReflexos; i++) { double t = (double)i / (numReflexos + 1); double rx = a.x + dx * t, ry = a.y + dy * t; float reflexOp = op * 0.3f * (0.6f + 0.4f * (float)Math.sin(faseGlow * 2.0 + i)); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.05f, reflexOp))); g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Double(rx - 1.5, ry - 1.5, 3, 3)); } }
            g2.setComposite(orig);
        }
        private Set<String> obterArestasRota() { Set<String> a = new HashSet<>(); if (rotaEstacoes == null) return a; int lim = Math.min(estacaoAtualAnimIndex + 1, rotaEstacoes.size()); for (int i = 0; i < lim - 1; i++) a.add(chaveAresta(rotaEstacoes.get(i).getId(), rotaEstacoes.get(i+1).getId())); return a; }
        private String chaveAresta(String a, String b) { return a.compareTo(b) < 0 ? a+"|"+b : b+"|"+a; }

        private void desenharSetasDecorativas(Graphics2D g2) {
            Set<String> des = new HashSet<>();
            for (Estacao est : rede.getEstacoes()) { Point2D.Double pO = coordenadas.get(est.getId()); if (pO == null) continue; for (Conexao cn : rede.getConexoes(est.getId())) { Estacao dE = cn.getDestino(); Point2D.Double pD = coordenadas.get(dE.getId()); if (pD == null) continue; String ch = chaveAresta(est.getId(), dE.getId()); if (des.contains(ch)) continue; des.add(ch); if (!est.getLinha().equals(dE.getLinha())) continue; double dx = pD.x - pO.x, dy = pD.y - pO.y; double len = Math.sqrt(dx * dx + dy * dy); if (len < 50) continue; double nx = dx / len, ny = dy / len; double mx = pO.x + dx * 0.5, my = pO.y + dy * 0.5; float op = 0.22f + 0.08f * (float)Math.sin(faseGlow * 1.5); desenharMiniSeta(g2, mx, my, nx, ny, corDaLinha(est.getLinha()), op); } }
        }
        private void desenharMiniSeta(Graphics2D g2, double x, double y, double nx, double ny, Color cor, float op) { Composite orig = g2.getComposite(); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op)); double t = 5; double px1 = x + nx * t, py1 = y + ny * t; double px2 = x - nx * t * 0.4 + ny * t * 0.5, py2 = y - ny * t * 0.4 - nx * t * 0.5; double px3 = x - nx * t * 0.4 - ny * t * 0.5, py3 = y - ny * t * 0.4 + nx * t * 0.5; GeneralPath tri = new GeneralPath(); tri.moveTo(px1, py1); tri.lineTo(px2, py2); tri.lineTo(px3, py3); tri.closePath(); g2.setColor(cor); g2.fill(tri); g2.setComposite(orig); }

        private void desenharRotaAtiva(Graphics2D g2) {
            if (rotaEstacoes == null || estacoesAcesas.isEmpty()) return; Composite orig = g2.getComposite(); float fP = (float)(0.9 + 0.1 * Math.sin(faseGlow * 2.0)); int lim = Math.min(estacaoAtualAnimIndex + 1, rotaEstacoes.size());
            for (int i = 0; i < lim - 1; i++) { Point2D.Double a = coordenadas.get(rotaEstacoes.get(i).getId()); Point2D.Double b = coordenadas.get(rotaEstacoes.get(i + 1).getId()); if (a == null || b == null) continue;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f)); g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a.x + 2, a.y + 3, b.x + 2, b.y + 3));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f * fP)); g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(18, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, b));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f * fP)); g2.setStroke(new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, b));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f)); g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, b));
            }
            if (estacaoAtualAnimIndex < rotaEstacoes.size() - 1 && progressoTrecho > 0 && progressoTrecho < 1.0) { Point2D.Double a = coordenadas.get(rotaEstacoes.get(estacaoAtualAnimIndex).getId()); Point2D.Double b = coordenadas.get(rotaEstacoes.get(estacaoAtualAnimIndex + 1).getId()); if (a != null && b != null) { Point2D.Double mid = new Point2D.Double(a.x + (b.x - a.x) * progressoTrecho, a.y + (b.y - a.y) * progressoTrecho); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f)); g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(18, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, mid)); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f)); g2.setStroke(new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, mid)); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f)); g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(new Line2D.Double(a, mid)); } }
            if (rotaMenosBaldeacoes != null && !rotaMenosBaldeacoes.estaVazio()) desenharRotaAlternativa(g2, rotaMenosBaldeacoes, rotaMaisRapida);
            g2.setComposite(orig);
        }
        private void desenharRotaAlternativa(Graphics2D g2, ResultadoRota rota, ResultadoRota excluir) { Set<String> ex = new HashSet<>(); if (excluir != null && !excluir.estaVazio()) { List<Estacao> c = excluir.getCaminho(); for (int i = 0; i < c.size() - 1; i++) ex.add(chaveAresta(c.get(i).getId(), c.get(i+1).getId())); } Composite orig = g2.getComposite(); g2.setColor(new Color(80, 140, 255)); g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10, 8}, 0)); List<Estacao> cam = rota.getCaminho(); for (int i = 0; i < cam.size() - 1; i++) { String ch = chaveAresta(cam.get(i).getId(), cam.get(i+1).getId()); if (ex.contains(ch)) continue; Point2D.Double a = coordenadas.get(cam.get(i).getId()), b = coordenadas.get(cam.get(i+1).getId()); if (a != null && b != null) g2.draw(new Line2D.Double(a, b)); } g2.setComposite(orig); }

        private void desenharSetasRota(Graphics2D g2) {
            if (rotaEstacoes == null || estacoesAcesas.isEmpty()) return; Composite orig = g2.getComposite(); int lim = Math.min(estacaoAtualAnimIndex + 1, rotaEstacoes.size()); double oscilacao = Math.sin(faseGlow * 3.0);
            for (int i = 0; i < lim - 1; i++) { Point2D.Double a = coordenadas.get(rotaEstacoes.get(i).getId()); Point2D.Double b = coordenadas.get(rotaEstacoes.get(i + 1).getId()); if (a == null || b == null) continue; double dx = b.x - a.x, dy = b.y - a.y; double len = Math.sqrt(dx * dx + dy * dy); if (len < 1) continue; double nx = dx / len, ny = dy / len;
                for (int s = 1; s <= 2; s++) { double t = (double) s / 3.0; double offset = oscilacao * 2.5 * (s % 2 == 0 ? 1 : -1); double sx = a.x + dx * t - ny * offset, sy = a.y + dy * t + nx * offset; float op = 0.70f + 0.25f * (float) Math.sin(faseGlow * 3.0 + s * 1.5); op = Math.max(0.5f, Math.min(0.95f, op)); if (i >= estacaoAtualAnimIndex) op *= 0.5f; desenharSeta(g2, sx, sy, nx, ny, op); }
            }
            g2.setComposite(orig);
        }
        private void desenharSeta(Graphics2D g2, double x, double y, double nx, double ny, float op) { Composite orig = g2.getComposite(); double sz = 9; double px1 = x + nx*sz, py1 = y + ny*sz; double px2 = x - nx*sz*0.35 + ny*sz*0.55, py2 = y - ny*sz*0.35 - nx*sz*0.55; double px3 = x - nx*sz*0.35 - ny*sz*0.55, py3 = y - ny*sz*0.35 + nx*sz*0.55; GeneralPath tri = new GeneralPath(); tri.moveTo(px1,py1); tri.lineTo(px2,py2); tri.lineTo(px3,py3); tri.closePath(); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op)); g2.setColor(new Color(0x44, 0xAA, 0xFF)); g2.fill(tri); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op * 0.95f)); g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(1.5f)); g2.draw(tri); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op)); g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Double(x - 2.5, y - 2.5, 5, 5)); g2.setComposite(orig); }

        private void desenharEstacoes(Graphics2D g2) {
            Set<String> rIds = new HashSet<>(); if (rotaEstacoes != null) for (Estacao e : rotaEstacoes) rIds.add(e.getId());
            for (Estacao est : rede.getEstacoes()) { Point2D.Double p = coordenadas.get(est.getId()); if (p == null) continue; boolean bal = nomesBaldeacao.contains(est.getNome()); boolean ter = estacoesTerminal.contains(est.getId()); boolean rot = rIds.contains(est.getId()); boolean ori = origem != null && origem.getId().equals(est.getId()); boolean des = destino != null && destino.getId().equals(est.getId()); boolean aceso = false; if (rot && estacoesAcesas != null) for (int idx : estacoesAcesas) if (idx < rotaEstacoes.size() && rotaEstacoes.get(idx).getId().equals(est.getId())) { aceso = true; break; }
                Color cor = corDaLinha(est.getLinha()); int raio; if (ori || des) { raio = RAIO_ESTACAO + 2; cor = Color.WHITE; } else if (bal) raio = RAIO_BALDEACAO; else if (ter) raio = RAIO_TERMINAL; else raio = RAIO_ESTACAO; if (aceso) { cor = Color.WHITE; raio += 1; }
                desenharLedStation(g2, p, raio, cor, bal, ter, ori || des, aceso); if (!est.isAtiva()) desenharInterdicao(g2, p, raio);
            }
        }
        private void desenharLedStation(Graphics2D g2, Point2D.Double p, int raio, Color cor, boolean bal, boolean ter, boolean pulse, boolean aceso) {
            Composite orig = g2.getComposite();
            float opHE = aceso ? 0.14f : 0.06f; int rHE = raio + 10; g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opHE)); g2.setColor(cor); g2.fill(new Ellipse2D.Double(p.x - rHE, p.y - rHE, rHE * 2.0, rHE * 2.0));
            float opHI = aceso ? 0.28f : 0.12f; int rHI = raio + 5; g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opHI)); g2.setColor(cor); g2.fill(new Ellipse2D.Double(p.x - rHI, p.y - rHI, rHI * 2.0, rHI * 2.0));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); g2.setColor(cor); g2.fill(new Ellipse2D.Double(p.x - raio, p.y - raio, raio * 2.0, raio * 2.0));
            int rB = Math.max(1, raio - 2); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Double(p.x - rB, p.y - rB, rB * 2.0, rB * 2.0));
            int rSpec = Math.max(1, raio / 2); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f)); g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Double(p.x - raio * 0.35, p.y - raio * 0.35, rSpec, rSpec));
            if (bal) { g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); g2.setColor(new Color(0x88, 0x88, 0x99)); g2.setStroke(new BasicStroke(1.5f)); int rA = raio + 4; g2.draw(new Ellipse2D.Double(p.x - rA, p.y - rA, rA * 2.0, rA * 2.0)); }
            if (ter && !pulse) { g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); g2.setColor(cor); g2.setStroke(new BasicStroke(2f)); int rA = raio + 3; g2.draw(new Ellipse2D.Double(p.x - rA, p.y - rA, rA * 2.0, rA * 2.0)); }
            if (pulse) { float pul = (float)(0.15 + 0.15 * Math.sin(fasePulse)); int rP = raio + 12 + (int)(4 * Math.sin(fasePulse)); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pul)); g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Double(p.x - rP, p.y - rP, rP * 2.0, rP * 2.0)); }
            g2.setComposite(orig);
        }
        private void desenharInterdicao(Graphics2D g2, Point2D.Double p, int raio) { g2.setColor(new Color(0xFF, 0x44, 0x44)); g2.setStroke(new BasicStroke(2.5f)); int o = raio + 4; g2.draw(new Line2D.Double(p.x-o, p.y-o, p.x+o, p.y+o)); g2.draw(new Line2D.Double(p.x-o, p.y+o, p.x+o, p.y-o)); }

        private void desenharLedAtual(Graphics2D g2) {
            if (posicaoLedAtual == null) return; Composite orig = g2.getComposite(); double pul = 0.7 + 0.3 * Math.sin(faseGlow * 2.5);
            int rg = RAIO_LEDAtual + 10 + (int)(3 * Math.sin(faseGlow * 2.5)); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(0.15 * pul))); g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Double(posicaoLedAtual.x - rg, posicaoLedAtual.y - rg, rg*2.0, rg*2.0));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(0.3 * pul))); int rm = RAIO_LEDAtual + 5; g2.fill(new Ellipse2D.Double(posicaoLedAtual.x - rm, posicaoLedAtual.y - rm, rm*2.0, rm*2.0));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); g2.fill(new Ellipse2D.Double(posicaoLedAtual.x - RAIO_LEDAtual, posicaoLedAtual.y - RAIO_LEDAtual, RAIO_LEDAtual*2.0, RAIO_LEDAtual*2.0));
            int rb = RAIO_LEDAtual - 2; g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)); g2.fill(new Ellipse2D.Double(posicaoLedAtual.x - rb, posicaoLedAtual.y - rb, rb*2.0, rb*2.0));
            g2.setComposite(orig);
        }

        private void desenharNomesLinhas(Graphics2D g2) { Font fonte = new Font("SansSerif", Font.BOLD, 10); g2.setFont(fonte); for (Map.Entry<String, List<String>> ent : estacoesPorLinha.entrySet()) { String[] parts = ent.getKey().split(" - "); if (parts.length < 2) continue; String num = parts[0].replace("Linha ", "").trim(), nome = parts[1].toUpperCase(); List<String> ids = ent.getValue(); if (ids.isEmpty()) continue; Color cor = corDaLinha(ent.getKey()); Point2D.Double pI = coordenadas.get(ids.get(0)); if (pI != null) desenharLabelLinha(g2, num + "-" + nome, pI, cor, true); Point2D.Double pF = coordenadas.get(ids.get(ids.size()-1)); if (pF != null) desenharLabelLinha(g2, nome, pF, cor, false); } }
        private void desenharLabelLinha(Graphics2D g2, String txt, Point2D.Double p, Color cor, boolean esq) { FontMetrics fm = g2.getFontMetrics(); int w = fm.stringWidth(txt); int x = esq ? (int)p.x - w - 16 : (int)p.x + 16, y = (int)p.y + 4; g2.setColor(new Color(0, 0, 0, 120)); g2.fillRoundRect(x - 4, y - fm.getAscent() - 2, w + 8, fm.getHeight() + 4, 4, 4); g2.setColor(cor); g2.drawString(txt, x, y); }

        private void desenharLabels(Graphics2D g2) {
            Set<String> rIds = new HashSet<>(); if (rotaEstacoes != null) for (Estacao e : rotaEstacoes) rIds.add(e.getId());
            for (Estacao est : rede.getEstacoes()) { Point2D.Double p = coordenadas.get(est.getId()); if (p == null) continue; boolean ter = estacoesTerminal.contains(est.getId()); boolean bal = nomesBaldeacao.contains(est.getNome()); boolean rot = rIds.contains(est.getId()); boolean mostrar = ter || bal || rot; if (!mostrar && mousePos != null) { double cx = mousePos.x / zoomAtual, cy = mousePos.y / zoomAtual; if (p.distance(cx, cy) <= RAIO_CLIQUE) mostrar = true; } if (!mostrar) continue; desenharStationLabel(g2, est.getNome(), p, ter || bal); desenharTagOrigemDestino(g2, est, p); }
        }
        private void desenharStationLabel(Graphics2D g2, String nome, Point2D.Double p, boolean imp) { Font fonte = imp ? new Font("SansSerif", Font.BOLD, 10) : new Font("SansSerif", Font.PLAIN, 9); g2.setFont(fonte); FontMetrics fm = g2.getFontMetrics(); int w = fm.stringWidth(nome), x = (int)p.x - w / 2, y = (int)p.y - 14; g2.setColor(new Color(0, 0, 0, 100)); g2.fillRoundRect(x - 4, y - fm.getAscent() - 2, w + 8, fm.getHeight() + 4, 4, 4); g2.setColor(new Color(0x0a, 0x0a, 0x18, 180)); g2.fillRoundRect(x - 3, y - fm.getAscent() - 1, w + 6, fm.getHeight() + 2, 3, 3); g2.setColor(imp ? Color.WHITE : new Color(0xBB, 0xBB, 0xBB)); g2.drawString(nome, x, y); }
        private void desenharTagOrigemDestino(Graphics2D g2, Estacao est, Point2D.Double p) { if (origem != null && origem.getId().equals(est.getId())) { Point2D.Double pO = coordenadas.get(origem.getId()); if (pO != null && pO.distance(p) < 2) desenharTagStatus(g2, "ORIGEM", (int)p.x, (int)p.y - 26, new Color(0x4A, 0xDE, 0x80)); } if (destino != null && destino.getId().equals(est.getId())) { Point2D.Double pD = coordenadas.get(destino.getId()); if (pD != null && pD.distance(p) < 2) desenharTagStatus(g2, "DESTINO", (int)p.x, (int)p.y - 26, new Color(0xF8, 0x71, 0x71)); } }
        private void desenharTagStatus(Graphics2D g2, String texto, int cx, int y, Color cor) { Font fonte = new Font("SansSerif", Font.BOLD, 8); g2.setFont(fonte); FontMetrics fm = g2.getFontMetrics(); int w = fm.stringWidth(texto), x = cx - w / 2; g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); g2.setColor(new Color(0x0a, 0x0a, 0x18, 200)); g2.fillRoundRect(x - 3, y - fm.getAscent(), w + 6, fm.getHeight() + 2, 3, 3); g2.setColor(cor); g2.drawString(texto, x, y); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); }

        private void desenharHUDRota(Graphics2D g2) {
            int pw = 260, ph = 110; int px = 60, py = 60; Composite orig = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.92f)); g2.setColor(new Color(0x0f, 0x17, 0x2a)); g2.fill(new RoundRectangle2D.Double(px, py, pw, ph, 10, 10));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)); g2.setColor(new Color(0x44, 0xAA, 0xFF)); g2.setStroke(new BasicStroke(2f)); g2.draw(new RoundRectangle2D.Double(px, py, pw, ph, 10, 10));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.setFont(new Font("SansSerif", Font.BOLD, 13)); g2.setColor(COR_STATUS_OK); g2.drawString("Metro SP", px + 14, py + 24);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12)); g2.setColor(COR_TEXTO);
            if (origem != null && destino != null && rotaMaisRapida != null && !rotaMaisRapida.estaVazio()) {
                g2.setColor(new Color(0x4A, 0xDE, 0x80)); g2.fill(new Ellipse2D.Double(px + 14, py + 38, 8, 8)); g2.setColor(COR_TEXTO); g2.drawString(origem.getNome(), px + 28, py + 46);
                g2.setColor(new Color(0xF8, 0x71, 0x71)); g2.fill(new Ellipse2D.Double(px + 14, py + 56, 8, 8)); g2.setColor(COR_TEXTO); g2.drawString(destino.getNome(), px + 28, py + 64);
                g2.setColor(new Color(0x90, 0xA0, 0xC0)); g2.setStroke(new BasicStroke(1.2f)); g2.draw(new Ellipse2D.Double(px + 14, py + 76, 8, 8)); g2.drawLine(px + 18, py + 80, px + 18, py + 78); g2.drawLine(px + 18, py + 80, px + 20, py + 82);
                g2.drawString(String.format(Locale.US, "%.0f min  |  %d bal.", rotaMaisRapida.getCustoTotal(), rotaMaisRapida.getBaldeacoes()), px + 28, py + 85);
            } else if (origem != null) { g2.setColor(new Color(0x4A, 0xDE, 0x80)); g2.fill(new Ellipse2D.Double(px + 14, py + 38, 8, 8)); g2.setColor(COR_TEXTO); g2.drawString(origem.getNome(), px + 28, py + 46); g2.setColor(new Color(0x70, 0x80, 0xA0)); g2.drawString("Selecione o destino...", px + 14, py + 66);
            } else { g2.setColor(new Color(0x70, 0x80, 0xA0)); g2.drawString("Selecione origem e", px + 14, py + 46); g2.drawString("destino no mapa", px + 14, py + 64); }
            g2.setColor(new Color(0x33, 0x41, 0x55)); g2.setStroke(new BasicStroke(1)); g2.drawLine(px + 14, py + 34, px + pw - 14, py + 34);
            g2.setComposite(orig);
        }

        private void desenharVinheta(Graphics2D g2) { Composite orig = g2.getComposite(); int w = tamanhoBase.width, h = tamanhoBase.height; Paint vinheta = new RadialGradientPaint(new Point2D.Double(w / 2.0, h / 2.0), Math.max(w, h) * 0.55f, new float[]{0.0f, 0.7f, 1.0f}, new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0, 60)}); g2.setPaint(vinheta); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); g2.fillRect(0, 0, w, h); g2.setComposite(orig); }
    }
}
