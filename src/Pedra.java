import java.awt.Color;

/**
 * Representa um terreno do tipo Pedra na simulação.
 * Este terreno atua como um obstáculo físico intransponível, delimitando áreas
 * e impedindo o movimento de animais ou o crescimento de vegetação.
 * * @version 1.0
 */
public class Pedra extends Terreno
{
    private static final Color COR = Color.GRAY; // Cinza

    /**
     * Verifica se um determinado ator pode habitar ou transitar sobre a pedra.
     * Como pedras são obstáculos sólidos, nenhum ator pode habitá-las.
     * * @param ator O ator que deseja ocupar este terreno.
     * @return false sempre, pois pedras não são habitáveis por ninguém.
     */
    @Override
    public boolean ehHabitavel(Ator ator)
    {
        if (ator instanceof Animal) {
            return false;
        }
        return false;
    }
    
    /**
     * Retorna a cor associada a este tipo de terreno para renderização.
     * * @return A cor definida para a pedra (Cinza).
     */
    @Override
    public Color getCor()
    {
        return COR;
    }
}