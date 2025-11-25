import java.awt.Color;

/**
 * Representa um terreno do tipo Água na simulação.
 * Este terreno atua como uma barreira natural para animais terrestres,
 * impedindo sua movimentação, mas serve como habitat exclusivo para 
 * plantas aquáticas, especificamente a Vitória-Régia.
 * * @version 1.0
 */
public class Agua extends Terreno
{
    private static final Color COR = new Color(65, 105, 225); // Azul royal

    /**
     * Verifica se um determinado ator pode habitar ou transitar sobre a água.
     * * Regras de habitação:
     * - Animais: Não podem habitar (retorna false).
     * - Vitória-Régia: Pode habitar (retorna true).
     * - Outros atores: Não podem habitar por padrão.
     * * @param ator O ator que deseja ocupar este terreno.
     * @return true se o ator for uma Vitória-Régia, false caso contrário.
     */
    @Override
    public boolean ehHabitavel(Ator ator)
    {
        if (ator instanceof Animal) {
            return false;
        }
        if (ator instanceof VitoriaRegia) {
            return true;
        }
        return false;
    }
    
    /**
     * Retorna a cor associada a este tipo de terreno para renderização.
     * * @return A cor definida para a água.
     */
    @Override
    public Color getCor()
    {
        return COR;
    }
}