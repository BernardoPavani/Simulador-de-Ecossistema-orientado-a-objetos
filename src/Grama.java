import java.awt.Color;

/**
 * Representa um terreno do tipo Grama na simulação.
 * Este é o terreno padrão e predominante, servindo de habitat para 
 * todos os animais terrestres e permitindo o crescimento de flores.
 * * @version 1.0
 */
public class Grama extends Terreno
{
    private static final Color COR = new Color(34, 139, 34); // Verde escuro

    /**
     * Verifica se um determinado ator pode habitar ou transitar sobre a grama.
     * * @param ator O ator que deseja ocupar este terreno.
     * @return true se o ator for um Animal ou uma Flor, false caso contrário.
     */
    @Override
    public boolean ehHabitavel(Ator ator)
    {
        // Por enquanto, consideramos que todo animal pode andar na grama.
        if (ator instanceof Animal) {
            return true;
        }
        // Quando criarmos a Flor, ela poderá habitar aqui
        else if (ator instanceof Flor) { 
            return true;
        }else{
        return false; // Poderia ser diferente para Plantas
        }
    }
    
    /**
     * Retorna a cor associada a este tipo de terreno para renderização.
     * * @return A cor definida para a grama.
     */
    @Override
    public Color getCor()
    {
        return COR;
    }
}