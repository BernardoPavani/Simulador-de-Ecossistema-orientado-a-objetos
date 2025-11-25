import java.awt.Color;
import java.util.Iterator;

/**
 * Modelo de uma raposa na simulação.
 * As raposas envelhecem, movem-se, comem coelhos e morrem.
 * As raposas também podem se reproduzir.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-11
 */
public class Fox extends Animal
{
    // Características compartilhadas por todas as raposas (campos estáticos).
    
    /** Idade mínima para que a raposa possa se reproduzir. */
    private static final int BREEDING_AGE = 10;
    
    /** Idade máxima que uma raposa pode atingir. */
    private static final int MAX_AGE = 150;
    
    /** Probabilidade de uma raposa se reproduzir em cada passo (0 a 1). */
    private static final double BREEDING_PROBABILITY = 0.09;
    
    /** Número máximo de filhotes em um nascimento. */
    private static final int MAX_LITTER_SIZE = 3;
    
    /** Valor nutritivo de um coelho em passos de vida.
     * Representa quantos passos a raposa pode sobreviver após comer um coelho. */
    private static final int RABBIT_FOOD_VALUE = 8;
    
    /** Cor de representação visual da raposa na simulação. */
    private static final Color COR = Color.BLUE;
    
    // Características individuais (campos de instância).
    
    /** Nível de fome da raposa. Aumenta com cada ação e diminui ao comer um coelho.
     * Quando atinge 0, a raposa morre. */
    private int foodLevel;

    /**
     * Cria uma nova raposa.
     * Uma raposa pode ser criada como recém-nascida (idade zero e bem alimentada)
     * ou com idade aleatória.
     * 
     * @param randomAge {@code true} se a raposa deve ter idade e nível de fome aleatórios.
     *                  {@code false} se deve ser criada como recém-nascida.
     */
    public Fox(boolean randomAge)
    {
        super(randomAge); // Chama o construtor da classe Animal
        if(randomAge) {
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }

    /**
     * Retorna a cor de representação visual desta raposa.
     * 
     * @return {@code Color.BLUE} - a cor padrão das raposas na simulação.
     */
    @Override
    public Color getCor()
    {
        return COR; 
    }
    
    /**
     * Aumenta o nível de fome da raposa em uma unidade.
     * Se o nível de fome atinge zero ou menos, a raposa morre.
     * Este método é chamado automaticamente a cada ação da raposa.
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
     * Determina a próxima localização para a qual a raposa deve se mover.
     * Implementa a estratégia de movimento: primeiro busca comida (coelhos),
     * e se não encontrar, move-se para uma localização adjacente livre e habitável.
     * 
     * @param currentField O campo atual da simulação (campo de leitura).
     * @param updatedField O campo atualizado (para verificar terreno e disponibilidade).
     * @return A localização de movimento (onde há comida ou um local livre),
     *         ou {@code null} se nenhuma localização apropriada for encontrada.
     */
    @Override
    protected Location findMoveLocation(Field currentField, Field updatedField)
    {
        Location foodLocation = findFood(currentField, updatedField);
        if(foodLocation != null) {
            return foodLocation;
        }
        
        return updatedField.freeHabitableAdjacentLocation(location, this);
    }

    /**
     * Retorna a idade máxima que uma raposa pode atingir.
     * 
     * @return A idade máxima ({@value #MAX_AGE} passos).
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Retorna a idade mínima para que uma raposa possa se reproduzir.
     * 
     * @return A idade mínima para reprodução ({@value #BREEDING_AGE} passos).
     */
    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Retorna a probabilidade de uma raposa se reproduzir.
     * 
     * @return A probabilidade de reprodução (valor entre 0 e 1: {@value #BREEDING_PROBABILITY}).
     */
    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Retorna o número máximo de filhotes em um nascimento.
     * 
     * @return O número máximo de filhotes ({@value #MAX_LITTER_SIZE}).
     */
    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Cria um novo filhote de raposa.
     * Este método é chamado durante o processo de reprodução.
     * 
     * @param randomAge {@code true} para criar um filhote com idade aleatória,
     *                  {@code false} para criar um recém-nascido.
     * @return Uma nova instância de {@code Fox}.
     */
    @Override
    protected Animal createNewborn(boolean randomAge)
    {
        return new Fox(randomAge);
    }
    
    // --- Métodos Específicos da Raposa ---

    /**
     * Procura por uma presa (coelho) em uma localização adjacente habitável.
     * Se encontrar um coelho vivo, a raposa o consome, aumentando seu nível de fome.
     * 
     * <p>Este método verifica:</p>
     * <ul>
     *   <li>Se a localização adjacente é habitável para a raposa</li>
     *   <li>Se há um coelho naquela localização</li>
     *   <li>Se o coelho está vivo</li>
     * </ul>
     * 
     * @param currentField O campo atual da simulação (contém os animais).
     * @param updatedField O campo atualizado (contém informações de terreno).
     * @return A localização do coelho encontrado, ou {@code null} se nenhum coelho
     *         habitável e vivo for encontrado nas adjacências.
     */
    private Location findFood(Field currentField, Field updatedField)
    {
        Iterator adjacentLocations = currentField.adjacentLocations(location);
        while(adjacentLocations.hasNext()) {
            Location where = (Location) adjacentLocations.next();

            if (updatedField.getTerrenoAt(where).ehHabitavel(this)) {
                Object animal = currentField.getObjectAt(where);

                if(animal instanceof Rabbit) {
                    Rabbit rabbit = (Rabbit) animal;
                    if(rabbit.isAlive()) { 
                        rabbit.setEaten();
                        foodLevel = RABBIT_FOOD_VALUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }

}
