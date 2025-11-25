import java.util.HashMap;
import java.util.Iterator;

/**
 * Esta classe coleta e fornece dados estatísticos sobre o estado do campo.
 * Ela é flexível: cria e mantém um contador para qualquer classe de objeto 
 * que seja encontrada dentro do campo durante a simulação.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 */
public class FieldStats
{
    // Contadores para cada tipo de entidade (raposa, coelho, etc.) na simulação.
    private HashMap counters;
    // Indica se os contadores estão atualmente atualizados com o estado do campo.
    private boolean countsValid;

    /**
     * Constrói um objeto de estatísticas do campo.
     * Inicializa o mapa de contadores.
     */
    public FieldStats()
    {
        // Configura uma coleção para contadores de cada tipo de ator que possamos encontrar
        counters = new HashMap();
        countsValid = true;
    }

    /**
     * Retorna uma string descrevendo o estado atual da população no campo.
     * Se as estatísticas não estiverem atualizadas, este método força uma nova contagem.
     * * @param field O campo a ser analisado.
     * @return Uma string listando cada espécie e sua contagem atual (ex: "Fox: 10 Rabbit: 50").
     */
    public String getPopulationDetails(Field field)
    {
        StringBuffer buffer = new StringBuffer();
        if(!countsValid) {
            generateCounts(field);
        }
        Iterator keys = counters.keySet().iterator();
        while(keys.hasNext()) {
            Counter info = (Counter) counters.get(keys.next());
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }
    
    /**
     * Invalida o conjunto atual de estatísticas e reseta todas as contagens para zero.
     * Deve ser chamado antes de iniciar uma nova varredura de contagem no campo.
     */
    public void reset()
    {
        countsValid = false;
        Iterator keys = counters.keySet().iterator();
        while(keys.hasNext()) {
            Counter cnt = (Counter) counters.get(keys.next());
            cnt.reset();
        }
    }

    /**
     * Incrementa a contagem para uma classe específica de ator.
     * Se a classe ainda não tiver um contador registrado, um novo será criado.
     * * @param atorClass A classe do ator a ser contado (ex: Fox.class).
     */
    public void incrementCount(Class atorClass)
    {
        Counter cnt = (Counter) counters.get(atorClass);
        if(cnt == null) {
            // Ainda não temos um contador para esta espécie - cria um
            cnt = new Counter(atorClass.getName());
            counters.put(atorClass, cnt);
        }
        cnt.increment();
    }

    /**
     * Indica que uma contagem de atores foi concluída.
     * Marca as estatísticas atuais como válidas.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determina se a simulação ainda é viável (se deve continuar a rodar).
     * A simulação é considerada viável se houver mais de uma espécie viva 
     * (contagem > 0) no campo.
     * * @param field O campo a ser verificado.
     * @return true se houver mais de uma espécie viva, false caso contrário.
     */
    public boolean isViable(Field field)
    {
        // Quantos contadores não são zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        Iterator keys = counters.keySet().iterator();
        while(keys.hasNext()) {
            Counter info = (Counter) counters.get(keys.next());
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Gera as contagens do número de raposas, coelhos e outros atores.
     * Itera por todo o campo verificando o tipo de ator em cada posição.
     * Estas contagens não são mantidas atualizadas automaticamente a cada movimento,
     * mas sim geradas sob demanda quando as informações são solicitadas.
     * * @param field O campo a ser analisado.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                //Alterando da classe Object para Ator
                Ator ator = field.getObjectAt(row, col);
                if(ator != null) {
                    incrementCount(ator.getClass());
                }
            }
        }
        countsValid = true;
    }
}
