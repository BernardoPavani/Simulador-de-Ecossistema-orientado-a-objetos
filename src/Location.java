/**
 * Representa uma localização (coordenada) em uma grade retangular.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-09
 */
public class Location
{
    // Posição da linha (coordenada vertical).
    private int row;
    // Posição da coluna (coordenada horizontal).
    private int col;

    /**
     * Cria uma nova localização com as coordenadas especificadas.
     * * @param row A coordenada da linha.
     * @param col A coordenada da coluna.
     */
    public Location(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Implementa a igualdade de conteúdo.
     * Verifica se o objeto fornecido é uma localização que representa 
     * as mesmas coordenadas de linha e coluna que este objeto.
     * * @param obj O objeto a ser comparado.
     * @return true se os objetos forem iguais, false caso contrário.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }
    
    /**
     * Retorna uma representação textual da localização.
     * O formato é "linha,coluna".
     * * @return Uma string representando a localização.
     */
    public String toString()
    {
        return row + "," + col;
    }
    
    /**
     * Gera um código hash único para esta localização.
     * Usa os 16 bits superiores para o valor da linha e os inferiores para a coluna.
     * Exceto para grades muito grandes, isso deve fornecer um código hash único
     * para cada par (linha, coluna).
     * * @return Um código hash inteiro para a localização.
     */
    public int hashCode()
    {
        return (row << 16) + col;
    }
    
    /**
     * Retorna a coordenada da linha.
     * * @return A linha.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * Retorna a coordenada da coluna.
     * * @return A coluna.
     */
    public int getCol()
    {
        return col;
    }
}
