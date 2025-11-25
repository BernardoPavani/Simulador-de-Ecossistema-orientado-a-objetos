import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * Classe base para todos os animais na simulação.
 * Implementa a interface Ator e contém a lógica 
 * comum a todos os animais, como envelhecer, reproduzir e morrer.
 */
public abstract class Animal implements Ator
{
    // O gerador de números aleatórios compartilhado por todos os animais.
    protected static final Random rand = new Random();
    
    protected int age;
    protected boolean alive;
    protected Location location;

    /**
     * Cria um novo animal.
     * O animal pode ser criado com idade zero (recém-nascido) ou com uma idade
     * aleatória (para popular a simulação inicial).
     * * @param randomAge true se o animal deve começar com uma idade aleatória,
     * false se deve começar com idade 0.
     */
    public Animal(boolean randomAge)
    {
        age = 0;
        alive = true;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
        }
    }
    
    /**
     * Executa as ações do animal durante um passo da simulação.
     * O ciclo de vida padrão definido aqui é:
     * 1. O animal envelhece.
     * 2. A fome aumenta (se aplicável à espécie).
     * 3. Se estiver vivo, tenta se reproduzir.
     * 4. Se estiver vivo, tenta se mover para uma nova localização.
     * * @param currentField O campo atual contendo o estado atual da simulação.
     * @param updatedField O campo onde o novo estado está sendo construído.
     * @param newActors Lista onde novos animais criados (filhotes) são adicionados.
     */
    @Override
    public void acao(Field currentField, Field updatedField, List<Ator> newActors)
    {
        incrementAge();
        incrementHunger(); // Implementação vazia para coelhos, com lógica para raposas
        
        if(isAlive()) {
            darALuz(updatedField, newActors);
            
            // Tenta se mover para uma nova localização
            Location newLocation = findMoveLocation(currentField, updatedField);
            if(newLocation != null) {
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            }
            else {
                // Não pode se mover (superlotação)
                setDead();
            }
        }
    }
    
    /**
     * Verifica se o animal ainda está vivo.
     * * @return true se o animal está vivo, false caso contrário.
     */
    @Override
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Define a localização do animal no campo.
     * * @param location A nova localização (objeto Location).
     */
    @Override
    public void setLocation(Location location)
    {
        this.location = location;
    }
    
    /**
     * Define a localização do animal.
     * @param row A linha.
     * @param col A coluna.
     */
    public void setLocation(int row, int col)
    {
        this.location = new Location(row, col);
    }
    
    /**
     * Marca o animal como morto.
     * O animal será removido da simulação no próximo ciclo de limpeza.
     */
    protected void setDead()
    {
        alive = false;
    }
    
    /**
     * Incrementa a idade do animal.
     * Se a idade exceder a idade máxima, o animal morre.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Gerencia o processo de reprodução do animal.
     * Verifica se nascimentos ocorrem e tenta colocar os filhotes no campo.
     * Os filhotes só são criados se houver espaço livre e habitável adjacente.
     * * @param updatedField O campo onde os filhotes serão colocados.
     * @param newActors A lista para registrar os novos filhotes.
     */
    protected void darALuz(Field updatedField, List<Ator> newActors)
    {
        int births = breed();
        for(int b = 0; b < births; b++) {
            // Usa o método abstrato para criar o tipo correto de animal
            Animal newborn = createNewborn(false);

            // Encontra um local livre E habitável para o filhote
            Location loc = updatedField.freeHabitableAdjacentLocation(location, newborn);

            if(loc != null){ // Só adiciona e posiciona se houver espaço
                newActors.add(newborn);
                newborn.setLocation(loc);
                updatedField.place(newborn, loc);
            }
        }
    }

    /**
     * Calcula o número de nascimentos baseado na probabilidade de reprodução.
     * @return O número de nascimentos (pode ser zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Verifica se o animal pode se reproduzir.
     * @return true se o animal pode se reproduzir.
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge();
    }

    // --- Métodos Abstratos (Devem ser implementados pelas subclasses) ---
      
    /**
     * @return A idade máxima para este tipo de animal.
     */
    protected abstract int getMaxAge();
    
    /**
     * @return A idade de reprodução para este tipo de animal.
     */
    protected abstract int getBreedingAge();
    
    /**
     * @return A probabilidade de reprodução (entre 0.0 e 1.0).
     */
    protected abstract double getBreedingProbability();
    
    /**
     * @return O tamanho máximo da ninhada.
     */
    protected abstract int getMaxLitterSize();
    
    /**
     * Cria uma nova instância (filhote) da espécie específica.
     * Funciona como uma fábrica (Factory Method) para garantir que o tipo correto
     * de animal seja criado durante a reprodução.
     * * @param randomAge se true, o animal nasce com idade aleatória; se false, nasce com idade 0.
     * @return Um novo objeto Animal da subclasse correta.
     */
    protected abstract Animal createNewborn(boolean randomAge);
    
    /**
     * Incrementa a fome do animal.
     * Para animais que não têm fome (como coelhos), este método
     * pode ter uma implementação vazia.
     */
    protected abstract void incrementHunger();
    
    /**
     * Encontra uma nova localização para o animal se mover.
     * @param currentField O campo atual (para consulta).
     * @param updatedField O campo atualizado (para verificar espaços livres).
     * @return A nova localização, ou null se não houver local disponível.
     */
    protected abstract Location findMoveLocation(Field currentField, Field updatedField);

    /**
     * Retorna a cor que representa este animal na interface gráfica.
     * * @return Um objeto Color.
     */
    @Override
    public abstract Color getCor();
}