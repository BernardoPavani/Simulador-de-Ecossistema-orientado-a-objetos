import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


import javax.swing.Timer;

/**
 * Classe principal do simulador predador-presa.
 * Gerencia o ciclo de vida da simulação, incluindo o controle de tempo,
 * a gestão das listas de atores (animais e plantas), a manipulação dos campos (atual e atualizado)
 * e a coordenação com a interface gráfica.
 * * Esta classe atua como o controlador central, orquestrando a interação entre
 * o modelo (Atores, Field) e a visão (SimulatorView).
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-09
 */
public class Simulator
{
    // Constantes de configuração da simulação.

    // Largura padrão para a grade de simulação.
    private static final int DEFAULT_WIDTH = 50;

    // Profundidade (altura) padrão para a grade de simulação.
    private static final int DEFAULT_DEPTH = 50;

    // Probabilidade de uma raposa ser criada em qualquer posição da grade na inicialização.
    private static final double FOX_CREATION_PROBABILITY = 0.02;

    // Probabilidade de um coelho ser criado em qualquer posição da grade na inicialização.
    private static final double RABBIT_CREATION_PROBABILITY = 0.08; 

    // Probabilidade de uma flor ser criada em um terreno de grama na inicialização (15%)
    private static final double FLOR_CREATION_PROBABILITY = 0.15; 

    // Probabilidade de uma vitória-régia ser criada em um terreno de água na inicialização (10%)
    private static final double VITORIAREGIA_CREATION_PROBABILITY = 0.10;
    
    // Dimensões do campo de simulação.
    private int depth;
    private int width;

    // Lista de todos os atores (animais e plantas) atualmente vivos no campo.
    private List<Ator> atores;

    // Lista temporária para armazenar atores recém-nascidos durante um passo de simulação.
    private List<Ator> newAtores;

    // O estado atual do campo.
    private Field field;

    // Um segundo campo usado como buffer para construir o próximo estado da simulação.
    private Field updatedField;

    // O contador de passos (iterações) da simulação atual.
    private int step;

    // A interface gráfica que exibe a simulação.
    private SimulatorView view;

    // Timer responsável por executar os passos da simulação periodicamente.
    private Timer timer;

    // Intervalo de tempo em milissegundos entre cada passo da simulação
    private static final int TIMER_DELAY_MS = 200;

    // Matriz única que armazena a configuração do terreno
    private Terreno[][] terrenos;

    // O tipo de terreno padrão utilizado na inicialização (Grama).
    private static final Terreno TERRENO_PADRAO = new Grama();

    // Mapeamento entre IDs numéricos (do arquivo de mapa) e objetos Terreno correspondentes.
    private static final HashMap<Integer, Terreno> TERRENO_MAP = new HashMap<>();
    static {
        TERRENO_MAP.put(0, new Grama());
        TERRENO_MAP.put(1, new Agua());
        TERRENO_MAP.put(2, new Pedra());
    }
    
    /**
     * Constrói um simulador com o tamanho padrão.
     * Inicializa a grade com as dimensões definidas em DEFAULT_DEPTH e DEFAULT_WIDTH.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Cria um simulador com as dimensões especificadas.
     * Inicializa as estruturas de dados, carrega o terreno, cria a interface gráfica
     * e configura o timer de execução.
     * * @param depth A profundidade (altura) do campo. Deve ser maior que zero.
     * @param width A largura do campo. Deve ser maior que zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        this.depth = depth;
        this.width = width;

        // 1. Cria a matriz de terrenos que será compartilhada
        terrenos = new Terreno[depth][width];
        initializeTerrenos(); // Inicializa com o padrão

        // 2. Cria as listas de atores
        atores = new ArrayList<Ator>();
        newAtores = new ArrayList<Ator>();

        // 3. Cria os DOIS campos, passando a MESMA matriz de terrenos
        field = new Field(depth, width, terrenos);
        updatedField = new Field(depth, width, terrenos);

        // 4. Configura a GUI e o Timer
        view = new SimulatorView(this, depth, width);
        timer = new Timer(TIMER_DELAY_MS, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulateOneStep();
            }
        });
        
        // Prepara o estado inicial válido.
        reset();
    }
    

    /**
     * Inicia ou retoma a execução automática da simulação.
     * O timer começará a disparar eventos que chamam simulateOneStep.
     */
    public void startSimulation()
    {
        timer.start();
    }
    
    /**
     * Pausa a execução automática da simulação.
     * O timer é interrompido, congelando o estado atual.
     */
    public void stopSimulation()
    {
        timer.stop();
    }

    /**
     * Reinicia a simulação para o estado inicial.
     * Para o timer, recarrega o mapa, repopula o campo e atualiza a visualização.
     */
    public void resetSimulation()
    {
        timer.stop(); // Para o timer
        reset();      // Chama o reset lógico
        // Atualiza a view para mostrar o estado resetado
        view.showStatus(step, field); 
    }

    
    /**
     * Executa um único passo da simulação.
     * Percorre todos os atores vivos, permitindo que ajam (mover, comer, reproduzir).
     * Remove atores mortos e adiciona novos nascimentos.
     * Ao final, troca os buffers de campo (field e updatedField) e atualiza a tela.
     */
    public void simulateOneStep()
    {
        step++;
        newAtores.clear();
        
        // Permite que todos os atores ajam
        for(Iterator<Ator> iter = atores.iterator(); iter.hasNext(); ) {
            Ator ator = iter.next();
            if(ator.isAlive()) {
                ator.acao(field, updatedField, newAtores);
            }else {
                iter.remove();   
            }
        }
        // Adiciona animais recém-nascidos à lista principal
        atores.addAll(newAtores);
        
        // Troca o campo e o updatedField para o próximo passo
        Field temp = field;
        field = updatedField;
        updatedField = temp;
        updatedField.clear();

        // Exibe o novo campo na tela
        view.showStatus(step, field);
    }
        
    /**
     * Reseta a simulação para uma posição inicial.
     * Limpa os atores, recarrega o terreno do arquivo e popula o campo aleatoriamente.
     */
    public void reset()
    {
        step = 0;
        atores.clear();
        field.clear();
        updatedField.clear();
        loadTerrenoFromFile("mapa.txt");
        populate(field);
        
    }
    
    /**
     * Popula o campo com raposas, coelhos e vegetação.
     * A criação de cada ator depende das probabilidades definidas e da
     * compatibilidade com o terreno da posição (ex: Vitória-Régia apenas na água).
     * * @param field O campo a ser populado.
     */
    private void populate(Field field)
    {
        Random rand = new Random();
        field.clear(); // Limpa apenas os atores, não o terreno
        
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {

                // Pega o terreno da localização atual
                Terreno terreno = field.getTerrenoAt(row, col);
                
                // --- LÓGICA DOS ANIMAIS ---
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Fox fox = new Fox(true);
                    //So coloca a raposa se o terreno for habitavel por ela
                    if (terreno.ehHabitavel(fox)) {
                        atores.add(fox);
                        fox.setLocation(row, col);
                        field.place(fox, row, col);
                    }
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Rabbit rabbit = new Rabbit(true);
                    //So coloca o coelho se o terreno for habitavel por ele
                    if (terreno.ehHabitavel(rabbit)) {
                        atores.add(rabbit);
                        rabbit.setLocation(row, col);
                        field.place(rabbit, row, col);
                    }
                }
                                
                // Se o local ainda estiver vazio (nenhum animal foi criado nele)...
                if (field.getObjectAt(row, col) == null) {
                    
                    // Tentamos plantar uma Flor.
                    if (rand.nextDouble() <= FLOR_CREATION_PROBABILITY) {
                        Flor flor = new Flor();
                        if (terreno.ehHabitavel(flor)){
                            atores.add(flor);
                            flor.setLocation(row, col);
                            field.place(flor, row, col);
                        }
                    }
                    
                    // Tentamos plantar uma VitoriaRegia.
                    if (rand.nextDouble() <= VITORIAREGIA_CREATION_PROBABILITY) {
                        VitoriaRegia vixRegia = new VitoriaRegia();
                        if (terreno.ehHabitavel(vixRegia)){
                            atores.add(vixRegia);
                            vixRegia.setLocation(row, col);
                            field.place(vixRegia, row, col);
                        }
                    }
                }
                // --- FIM DA LÓGICA DAS PLANTAS ---
            }
        }
        Collections.shuffle(atores);
    }

    /**
     * Inicializa a matriz de terrenos preenchendo todas as posições com o terreno padrão (Grama).
     */
    private void initializeTerrenos()
    {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                terrenos[row][col] = TERRENO_PADRAO;
            }
        }
    }

    /**
     * Carrega a configuração do terreno a partir de um arquivo de texto.
     * O arquivo deve conter números inteiros separados por espaço, onde cada número
     * corresponde a um tipo de terreno (0=Grama, 1=Água, 2=Pedra).
     * Se o arquivo não for encontrado ou contiver erros, o terreno padrão é mantido.
     * * @param fileName O caminho ou nome do arquivo de mapa (ex: "mapa.txt").
     */
    public void loadTerrenoFromFile(String fileName)
    {
        // Reseta o terreno para o padrão primeiro
        initializeTerrenos(); 
        
        File file = new File(fileName);

        // Usando "try-with-resources" para garantir que o 'br' será fechado automaticamente.
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
            String linha;
            int row = 0;
            
            // Lê uma linha do arquivo 
            // Continua enquanto a linha não for nula e não passamos da altura do mapa
            while ((linha = br.readLine()) != null && row < depth) {
                
                // Quebra a linha em várias strings, usando " " como separador 
                String[] campos = linha.trim().split(" ");
                
                for (int col = 0; col < width && col < campos.length; col++) {
                    try {
                        // Converte o texto para um inteiro 
                        int terrenoID = Integer.parseInt(campos[col]);
                        Terreno terreno = TERRENO_MAP.get(terrenoID);
                        
                        if (terreno != null) {
                            terrenos[row][col] = terreno;
                        }
                    } catch (NumberFormatException e) {
                        // Se o texto no arquivo não for um número válido,
                        // apenas ignoramos e mantemos o terreno padrão (Grama).
                        System.err.println("Formato inválido no mapa em " + row + "," + col);
                    }
                }
                row++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo do mapa não encontrado: " + fileName);
            System.err.println("Usando terreno padrão (Grama).");
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo do mapa: " + e.getMessage());
            System.err.println("Usando terreno padrão (Grama).");
        }
    }
}
