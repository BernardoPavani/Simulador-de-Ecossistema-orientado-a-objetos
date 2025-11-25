import java.awt.Color;

/**
 * Classe base abstrata que representa um tipo de terreno na simulação.
 * O terreno forma a camada estática do campo, definindo as regras de movimentação
 * e habitação para os atores (animais e plantas), além da aparência visual do ambiente.
 * * @version 1.0
 */
public abstract class Terreno
{
    /**
     * Verifica se um determinado Ator pode habitar este terreno.
     * @param ator O Ator a ser verificado.
     * @return true se o ator pode habitar, false caso contrário.
     */
    public abstract boolean ehHabitavel(Ator ator);
    
    /**
     * Retorna a cor deste terreno para a renderização na GUI.
     * @return A cor do terreno.
     */
    public abstract Color getCor();
}