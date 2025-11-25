import java.awt.Color;
import java.util.List;
import java.util.Iterator; 
import java.util.Random;

/**
 * Modelo simples de um Coelho (presa) na simulação.
 * Os coelhos envelhecem, movem-se aleatoriamente, reproduzem-se e morrem
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-11
 */
public class Rabbit extends Animal
{
    // Características compartilhadas por todos os coelhos (campos estáticos).

    // A idade mínima para que um coelho possa começar a se reproduzir.
    private static final int BREEDING_AGE = 5;

    // A idade máxima que um coelho pode atingir antes de morrer de velhice.
    private static final int MAX_AGE = 50;

    // A probabilidade de um coelho se reproduzir em cada passo.
    private static final double BREEDING_PROBABILITY = 0.10;

    // O número máximo de filhotes que podem nascer em uma única ninhada.
    private static final int MAX_LITTER_SIZE = 5;

    // AA cor de representação visual do coelho na simulação
    private static final Color COR = Color.ORANGE;

    // Valor nutricional de uma flor (quantos passos o coelho ganha)
    private static final int FLOWER_FOOD_VALUE = 8;

    // Nível de fome atual
    private int foodLevel;

    /**
     * Cria um novo coelho.
     * O coelho pode ser criado como recém-nascido (idade zero)
     * ou com uma idade aleatória (para popular a simulação inicial).
     * * @param randomAge true se o coelho deve ter idade aleatória,
     * false se deve ser criado como recém-nascido (idade 0).
     */
    public Rabbit(boolean randomAge)
    {
       super(randomAge);
       if(randomAge) {
           foodLevel = rand.nextInt(FLOWER_FOOD_VALUE);
       } else {
           foodLevel = FLOWER_FOOD_VALUE;
       }
    }

    /**
     * Retorna a cor de representação visual deste coelho.
     * * @return A cor Laranja (Color.ORANGE).
     */
    @Override
    public Color getCor()
    {
        return COR; 
    }
    
    /**
     * Aumenta a fome do animal. Se chegar a zero, o coelho morre.
     */
    @Override
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Determina a próxima localização para a qual o coelho deve se mover.
     * Coelhos se movem aleatoriamente para qualquer localização adjacente 
     * que esteja livre (sem outro animal) e seja habitável (terreno permitido).
     * * @param currentField O campo atual (para consulta).
     * @param updatedField O campo atualizado (para verificar disponibilidade).
     * @return Uma localização livre e habitável, ou null se não houver nenhuma.
     */
    @Override
    protected Location findMoveLocation(Field currentField, Field updatedField)
    {
        // 1. Tenta achar comida
        Location foodLocation = findFood(currentField, updatedField);
        if(foodLocation != null) {
            return foodLocation;
        }
        // 2. Se não, procura movimento livre
        return updatedField.freeHabitableAdjacentLocation(location, this);
    }

    /**
     * Retorna a idade máxima de vida do coelho.
     * * @return A idade máxima ({@value #MAX_AGE}).
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Retorna a idade mínima para reprodução.
     * * @return A idade de reprodução ({@value #BREEDING_AGE}).
     */
    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Retorna a probabilidade de reprodução.
     * * @return A probabilidade ({@value #BREEDING_PROBABILITY}).
     */
    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Retorna o tamanho máximo da ninhada.
     * * @return O tamanho máximo ({@value #MAX_LITTER_SIZE}).
     */
    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Cria um novo filhote de coelho.
     * * @param randomAge se o filhote deve ter idade aleatória (geralmente false).
     * @return Um novo objeto Rabbit.
     */
    @Override
    protected Animal createNewborn(boolean randomAge)
    {
        return new Rabbit(randomAge);
    }

    /**
     * Marca o coelho como morto por ter sido caçado.
     * Este método é chamado quando uma raposa "come" este coelho.
     */
    public void setEaten()
    {
       setDead(); // Usa o método protegido da classe Animal
    }

    /**
     * Procura por flores adjacentes para comer.
     * Se encontrar uma flor viva, o coelho a come (a flor morre)
     * e o nível de fome do coelho é restaurado.
     * * @param currentField O campo atual (para consulta).
     * @param updatedField O campo atualizado (para verificar disponibilidade).
     * @return A localização da flor comida, ou null se nenhuma flor for encontrada.
     */
    private Location findFood(Field currentField, Field updatedField)
    {
        Iterator adjacentLocations = currentField.adjacentLocations(location);
        while(adjacentLocations.hasNext()) {
            Location where = (Location) adjacentLocations.next();

            // Verifica se o coelho pode ir para lá (habitável)
            if (updatedField.getTerrenoAt(where).ehHabitavel(this)) {
                Object actor = currentField.getObjectAt(where);

                if(actor instanceof Flor) {
                    Flor flor = (Flor) actor;
                    if(flor.isAlive()) { 
                        flor.setEaten(); // Come a flor
                        foodLevel = FLOWER_FOOD_VALUE; // Enche a barriga
                        return where;
                    }
                }
            }
        }
        return null;
    }
}
