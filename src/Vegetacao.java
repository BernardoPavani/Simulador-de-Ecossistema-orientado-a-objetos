import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * Classe base abstrata para todos os tipos de vegetação na simulação.
 * Esta classe implementa a interface Ator e gerencia a lógica comum do ciclo de vida
 * das plantas, como envelhecimento, estado de vida e localização.
 * * Diferente dos animais, a vegetação não se move, mas pode se espalhar (reproduzir)
 * para terrenos adjacentes compatíveis.
 * * @version 1.0
 */
public abstract class Vegetacao implements Ator
{
    // O gerador de números aleatórios compartilhado.
    protected static final Random rand = new Random();
    
    // A idade atual da planta em passos da simulação
    protected int age;

    // Indica se a planta está viva ou morta.
    protected boolean alive;

    // A localização atual da planta no campo.
    protected Location location;

    /**
     * Cria uma nova planta.
     * Inicializa a idade como zero e o estado como vivo.
     */
    public Vegetacao()
    {
        this.age = 0;
        this.alive = true;
    }

    /**
     * Executa as ações da vegetação durante um passo da simulação.
     * O ciclo de vida padrão definido aqui é:
     * 1. A planta envelhece.
     * 2. Se estiver viva, tenta executar sua ação específica (geralmente espalhar-se).
     * 3. Se ainda estiver viva após a ação, ela é mantida na mesma localização no campo atualizado.
     * * @param currentField O campo atual contendo o estado atual da simulação.
     * @param updatedField O campo onde o novo estado está sendo construído.
     * @param newActors Lista onde novas plantas criadas são adicionadas.
     */
    @Override
    public void acao(Field currentField, Field updatedField, List<Ator> newActors)
    {
        incrementAge();
        if (isAlive()) {
            executarAcao(currentField, updatedField, newActors);
            
            // Se a planta não foi "comida" ou destruída na sua ação,
            // ela permanece no local no campo atualizado.
            if (isAlive()) {
                updatedField.place(this, location);
            }
        }
    }

    /**
     * Aumenta a idade da planta em uma unidade.
     * Se a idade atingir o limite máximo da espécie, a planta morre naturalmente.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Verifica se a planta ainda está viva.
     * * @return true se a planta está viva, false caso contrário.
     */
    @Override
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Define a localização da planta no campo.
     * * @param location A nova localização (objeto Location).
     */
    @Override
    public void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Define a localização da planta no campo usando coordenadas.
     * Método utilitário para facilitar a definição de posição.
     * * @param row A linha da localização.
     * @param col A coluna da localização.
     */
    public void setLocation(int row, int col)
    {
        this.location = new Location(row, col);
    }

    /**
     * Define o estado da planta como morta.
     * A planta será removida da simulação no próximo ciclo.
     */
    protected void setDead()
    {
        alive = false;
    }

    // --- MÉTODOS ABSTRATOS (para subclasses) ---

    /**
     * Retorna a idade máxima que este tipo de planta pode viver.
     * * @return A idade máxima em passos.
     */
    protected abstract int getMaxAge();
    
    /**
     * Define a cor desta planta para a GUI.
     * @return A cor da planta.
     */
    public abstract Color getCor();

    /**
     * Executa a lógica específica desta planta.
     * Geralmente envolve a tentativa de se espalhar para locais adjacentes
     * compatíveis com o tipo da planta.
     * * @param currentField O campo atual (para verificar arredores).
     * @param updatedField O campo atualizado (para alocar novas plantas).
     * @param newActors Lista para adicionar novas plantas geradas.
     */
    protected abstract void executarAcao(Field currentField, Field updatedField, List<Ator> newActors);
}