import java.util.List;
import java.awt.Color;

/**
 * Define a interface para qualquer participante (ator) da simulação.
 * Todo ator deve ser capaz de realizar uma ação, informar se está vivo
 * e ter sua localização definida.
 * * @version 1.0
 */
public interface Ator {

    /**
     * Realiza a ação do ator para o passo atual da simulação.
     * @param currentField O campo atual, usado para consulta (ex: procurar comida).
     * @param updatedField O campo atualizado, onde o ator deve se posicionar.
     * @param newActors Uma lista para adicionar novos atores (ex: filhotes).
     */
    void acao(Field currentField, Field updatedField, List<Ator> newActors);
    
    /**
     * Verifica se o ator ainda está vivo.
     * @return true se o ator está vivo, false caso contrário.
     */
    boolean isAlive();
    
    /**
     * Define a localização atual do ator no campo.
     * @param location A nova localização.
     */
    void setLocation(Location location);

    /**
     * Retorna a cor deste ator para a GUI.
     * @return A cor do ator.
     */
    Color getCor();
}
