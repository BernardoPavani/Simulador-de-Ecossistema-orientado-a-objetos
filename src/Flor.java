import java.awt.Color;
import java.util.Iterator;
import java.util.List;

/**
 * Representa uma Flor, um tipo específico de vegetação terrestre na simulação.
 * As flores possuem um ciclo de vida curto e têm a capacidade de se espalhar 
 * (reproduzir) exclusivamente para terrenos do tipo Grama adjacentes e desocupados.
 * * @version 1.0
 */
public class Flor extends Vegetacao
{
    // A idade máxima que a flor pode viver.
    private static final int MAX_AGE = 15;

    // A probabilidade de se espalhar.
    private static final double SPREAD_PROBABILITY = 0.11;

    // A cor da flor.
    private static final Color COR = Color.RED;

    /**
     * Constrói uma nova instância de Flor.
     * A flor nasce com idade zero e está viva.
     */
    public Flor()
    {
        super();
    }

    /**
     * Retorna a cor visual desta planta para a interface gráfica.
     * * @return A cor Vermelha (Color.RED).
     */
    @Override 
    public Color getCor()
    {
        return COR;
    }

    /**
     * Retorna a idade máxima de vida permitida para esta planta.
     * * @return A idade máxima em passos.
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Marca a flor como comida (morta).
     */
    public void setEaten()
    {
        setDead();
    }

    /**
     * Executa a lógica específica de ação (reprodução) da flor.
     * A flor tenta se espalhar para um local adjacente se as condições forem atendidas:
     * 1. O sorteio da probabilidade de espalhamento for favorável.
     * 2. O terreno adjacente for do tipo Grama (verificado no campo atual).
     * 3. O local adjacente estiver vazio (verificado no campo atualizado).
     * * @param currentField O campo atual (usado para verificar o tipo de terreno vizinho).
     * @param updatedField O campo atualizado (usado para verificar ocupação e alocar a nova planta).
     * @param newActors A lista onde a nova planta será adicionada caso nasça.
     */
    @Override
    protected void executarAcao(Field currentField, Field updatedField, List<Ator> newActors)
    {
        if (rand.nextDouble() <= SPREAD_PROBABILITY) {
            Iterator<Location> adjacent = currentField.adjacentLocations(location);
            while (adjacent.hasNext()) {
                Location next = adjacent.next();
                
                // Verifica se o local adjacente é Grama E está vazio
                boolean ehGrama = (currentField.getTerrenoAt(next) instanceof Grama);
                boolean estaVazio = (updatedField.getObjectAt(next) == null);
                
                if (ehGrama && estaVazio) {
                    Flor newPlant = new Flor();
                    newActors.add(newPlant);
                    newPlant.setLocation(next);
                    updatedField.place(newPlant, next);
                    break; // Espalha apenas uma vez
                }
            }
        }
    }
}