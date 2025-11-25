/**
 * Fornece um contador para um participante na simulação.
 * Esta classe mantém uma string de identificação e uma contagem de quantos 
 * participantes deste tipo específico existem atualmente dentro da simulação.
 * * É utilizada pela classe FieldStats para gerar relatórios de população.
 * * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 */
public class Counter
{
    // Um nome para este tipo de participante da simulação.
    private String name;

    // Quantos deste tipo existem na simulação.
    private int count;

    /**
     * Inicializa um contador para um dos tipos de simulação.
     * * @param name Um nome identificador, por exemplo "Fox".
     */
    public Counter(String name)
    {
        this.name = name;
        count = 0;
    }
    
    /**
     * Retorna a descrição curta (nome) deste tipo.
     * * @return O nome do tipo associado a este contador.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retorna a contagem atual para este tipo.
     * * @return A quantidade atual de participantes deste tipo.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Incrementa a contagem atual em uma unidade.
     */
    public void increment()
    {
        count++;
    }
    
    /**
     * Reinicia a contagem atual para zero.
     * Geralmente chamado no início de um novo passo de contagem estatística.
     */
    public void reset()
    {
        count = 0;
    }
}
