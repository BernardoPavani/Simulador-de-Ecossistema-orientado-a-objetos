import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Uma visualização gráfica da grade de simulação.
 * A visão exibe um retângulo colorido para cada localização representando seu conteúdo.
 * Ela utiliza cores específicas definidas pelos próprios atores e terrenos.
 * * Além da grade, esta classe gerencia os controles da simulação (botões) e
 * exibe as estatísticas populacionais e o contador de passos.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 */
public class SimulatorView extends JFrame
{
    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population;
    private FieldView fieldView;
    
    // Objeto de estatísticas para computar e armazenar informações da simulação.
    private FieldStats stats;

    // Referência ao controlador da simulação para vincular os botões de ação.
    private Simulator simulator;

    /**
     * Cria uma visualização com a largura e altura fornecidas.
     * Inicializa a interface gráfica, incluindo o painel do campo, 
     * rótulos de estatísticas e botões de controle.
     * * @param simulator A instância do simulador que controla a lógica.
     * @param height A altura (profundidade) da grade de simulação.
     * @param width A largura da grade de simulação.
     */
    public SimulatorView(Simulator sim, int height, int width)
    {
        this.simulator = sim;
        stats = new FieldStats();

        setTitle("Simulacão Ecológica");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        // Configuração do painel de botões
        JPanel buttonPanel = new JPanel();
        
        JButton startButton = new JButton("Iniciar");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulator.startSimulation();
            }
        });
        buttonPanel.add(startButton);
        
        JButton stopButton = new JButton("Pausar");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulator.stopSimulation();
            }
        });
        buttonPanel.add(stopButton);
        
        JButton resetButton = new JButton("Resetar");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulator.resetSimulation();
            }
        });
        buttonPanel.add(resetButton);

        // Organização dos componentes na janela
        Container contents = getContentPane();
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(population, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        contents.add(southPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }
    

    /**
     * Exibe o estado atual do campo na interface gráfica.
     * Atualiza o contador de passos, redesenha a grade (terrenos e atores)
     * e atualiza as estatísticas populacionais.
     * * @param step O passo atual da iteração.
     * @param field O campo contendo o estado atual a ser representado.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible())
            setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);

        stats.reset();
        fieldView.preparePaint();
            
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                
                // 1. Desenha o Terreno (fundo)
                Terreno terreno = field.getTerrenoAt(row, col);
                fieldView.drawMark(col, row, terreno.getCor());
                
                // 2. Desenha o Ator (frente), se houver
                Ator ator = field.getObjectAt(row, col);
                if(ator != null) {
                    stats.incrementCount(ator.getClass());
                    fieldView.drawMark(col, row, ator.getCor());
                }
                // Se o ator for null, o terreno que desenhamos no passo 1 já é o correto
                // (Não precisamos mais do EMPTY_COLOR)
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }
  
    /**
     * Determina se a simulação ainda é viável e deve continuar.
     * * @param field O campo a ser verificado.
     * @return true se houver mais de uma espécie viva, false caso contrário.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Fornece uma visualização gráfica de um campo retangular.
     * Esta é uma classe interna aninhada que define um componente personalizado
     * para a interface do usuário. Este componente exibe o campo.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Cria um novo componente FieldView.
         * * @param height A altura da grade.
         * @param width A largura da grade.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Informa ao gerenciador de GUI qual o tamanho preferido para este componente.
         * * @return A dimensão preferida (largura x altura).
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }
        
        /**
         * Prepara o componente para uma nova rodada de pintura.
         * Se o componente foi redimensionado, recalcula o fator de escala.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Pinta uma marca na localização da grade neste campo, usando a cor fornecida.
         * * @param x A coordenada x (coluna) da grade.
         * @param y A coordenada y (linha) da grade.
         * @param color A cor a ser usada para o desenho.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * O componente de visualização do campo precisa ser redesenhado.
         * Copia a imagem interna para a tela.
         * * @param g O contexto gráfico.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
    }
}
