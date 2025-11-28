import java.awt.Color;
import java.util.Iterator;
import java.util.List;

/**
 * Representa uma Vitória-Régia, um tipo específico de vegetação aquática na simulação.
 * Estas plantas habitam exclusivamente terrenos do tipo Água e possuem a capacidade
 * de se espalhar (reproduzir) para locais aquáticos adjacentes que estejam desocupados.
 * * @version 1.0
 */
public class VitoriaRegia extends Vegetacao
{
    // A idade máxima que uma vitória-régia pode atingir em passos da simulação antes de morrer.
    private static final int MAX_AGE = 20;

    // A probabilidade de uma vitória-régia gerar uma nova planta em um local adjacente a cada passo.
    private static final double SPREAD_PROBABILITY = 0.05;

    // A cor da vitória-régia para representação visual na simulação.
    private static final Color COR = Color.MAGENTA;

    /**
     * Constrói uma nova instância de Vitória-Régia.
     * A planta nasce com idade zero e está viva.
     */
    public VitoriaRegia()
    {
        super();
    }

    /**
     * Retorna a cor visual desta planta para a interface gráfica.
     * * @return A cor Magenta (Color.MAGENTA).
     */
    @Override 
    public Color getCor()
    {
        return COR;
    }

    /**
     * Retorna a idade máxima de vida permitida para esta planta aquática.
     * * @return A idade máxima em passos.
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Executa a lógica específica de ação (reprodução) da vitória-régia.
     * A planta tenta se espalhar para um local adjacente se as condições forem atendidas:
     * 1. O sorteio da probabilidade de espalhamento for favorável.
     * 2. O terreno adjacente for do tipo Água (verificado no campo atual).
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
                
                // Verifica se o local adjacente é Água E está vazio
                boolean ehAgua = (currentField.getTerrenoAt(next) instanceof Agua);
                boolean estaVazio = (updatedField.getObjectAt(next) == null);
                
                if (ehAgua && estaVazio) {
                    VitoriaRegia newPlant = new VitoriaRegia();
                    newActors.add(newPlant);
                    newPlant.setLocation(next);
                    updatedField.place(newPlant, next);
                    break; // Espalha apenas uma vez
                }
            }
        }
    }
}