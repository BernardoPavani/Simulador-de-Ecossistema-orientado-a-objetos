import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Representa uma grade retangular de posições de campo.
 * Esta classe atua como uma "camada" que gerencia a posição dos atores (animais e plantas)
 * e fornece acesso à matriz de terrenos subjacente.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-09
 */
public class Field
{
    private static final Random rand = new Random();
    
    // A profundidade (número de linhas) do campo. & A largura (número de colunas) do campo.
    private int depth, width;

    // Matriz que armazena os atores em cada posição
    private Ator[][] field;

    // Matriz que armazena os tipos de terreno
    private Terreno[][] terrenos; 

    /**
     * Cria um campo com as dimensões e a configuração de terreno fornecidas.
     * * @param depth A profundidade do campo.
     * @param width A largura do campo.
     * @param terrenos A matriz de terrenos COMPARTILHADA que este campo usará
     * para verificar a habitabilidade das posições.
     */
    public Field(int depth, int width, Terreno[][] terrenos)
    {
        this.depth = depth;
        this.width = width;
        this.field = new Ator[depth][width];
        this.terrenos = terrenos;
    }
    
    /**
     * Esvazia o campo.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }
    
    /**
     * Posiciona um ator na localização fornecida usando coordenadas.
     * Se já houver um ator no local, ele será sobrescrito (perdido).
     * * @param ator O ator a ser posicionado.
     * @param row Coordenada da linha.
     * @param col Coordenada da coluna.
     */
    public void place(Ator ator, int row, int col)
    {
        place(ator, new Location(row, col));
    }
    
    /**
     * Posiciona um ator na localização fornecida.
     * Se já houver um ator no local, ele será sobrescrito (perdido).
     * * @param ator O ator a ser posicionado.
     * @param location O objeto Location onde posicionar o ator.
     */
    public void place(Ator ator, Location location)
    {
        field[location.getRow()][location.getCol()] = ator;
    }

    /**
     * Retorna o ator na localização fornecida, se houver.
     * * @param location O objeto Location a ser verificado.
     * @return O ator na localização, ou {@code null} se estiver vazia.
     */
    public Ator getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Retorna o ator na localização fornecida por coordenadas.
     * * @param row A linha desejada.
     * @param col A coluna desejada.
     * @return O ator na localização, ou {@code null} se estiver vazia.
     */
    public Ator getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Retorna o tipo de terreno na localização fornecida.
     * * @param location O objeto Location a ser verificado.
     * @return O objeto {@code Terreno} naquela localização.
     */
    public Terreno getTerrenoAt(Location location)
    {
        return getTerrenoAt(location.getRow(), location.getCol());
    }
    
    /**
     * Retorna o tipo de terreno na localização fornecida por coordenadas.
     * * @param row A linha desejada.
     * @param col A coluna desejada.
     * @return O objeto {@code Terreno} naquela localização.
     */
    public Terreno getTerrenoAt(int row, int col)
    {
        return terrenos[row][col];
    }

    /**
     * Gera uma localização aleatória adjacente à localização fornecida,
     * ou retorna a própria localização.
     * A localização retornada estará sempre dentro dos limites válidos do campo.
     * * @param location A localização base para gerar a adjacência.
     * @return Uma localização válida dentro da área da grade.
     */
    public Location randomAdjacentLocation(Location location)
    {
        int row = location.getRow();
        int col = location.getCol();
        // Gera um deslocamento de -1, 0, ou +1 para linha e coluna.
        int nextRow = row + rand.nextInt(3) - 1;
        int nextCol = col + rand.nextInt(3) - 1;
        // Verifica se a nova localização está fora dos limites.
        if(nextRow < 0 || nextRow >= depth || nextCol < 0 || nextCol >= width) {
            return location;
        }
        else if(nextRow != row || nextCol != col) {
            return new Location(nextRow, nextCol);
        }
        else {
            return location;
        }
    }
    
    /**
     * Tenta encontrar uma localização livre adjacente à localização fornecida.
     * Se não houver nenhuma livre, verifica se a própria localização atual está livre.
     * * @param location A localização base para busca.
     * @return Uma localização válida e livre, ou {@code null} se todas as 
     * localizações adjacentes e a atual estiverem ocupadas.
     */
    public Location freeAdjacentLocation(Location location)
    {
        Iterator<Location> adjacent = adjacentLocations(location);
        while(adjacent.hasNext()) {
            Location next = (Location) adjacent.next();
            if(field[next.getRow()][next.getCol()] == null) {
                return next;
            }
        }
        //Verifica se a localização atual está livre
        if(field[location.getRow()][location.getCol()] == null) {
            return location;
        } 
        else {
            return null;
        }
    }

    /**
     * Tenta encontrar uma localização adjacente que seja livre (sem ator)
     * E habitável pelo ator fornecido (de acordo com o terreno).
     * * @param location A localização base para busca.
     * @param ator O ator que deseja se mover (usado para verificar habitabilidade do terreno).
     * @return Uma localização válida, livre e habitável, ou {@code null} se nenhuma for encontrada.
     */
    public Location freeHabitableAdjacentLocation(Location location, Ator ator)
    {
        Iterator<Location> adjacent = adjacentLocations(location);
        while(adjacent.hasNext()) {
            Location next = (Location) adjacent.next();
            
            // Verifica se está vazio na camada de atores
            boolean estaVazio = (field[next.getRow()][next.getCol()] == null);
            // Verifica se é habitável na camada de terrenos
            boolean ehHabitavel = getTerrenoAt(next).ehHabitavel(ator);
            
            if(estaVazio && ehHabitavel) {
                return next;
            }
        }
        
        // Não achou local adjacente. Verifica o local atual.
        boolean estaVazio = (field[location.getRow()][location.getCol()] == null);
        boolean ehHabitavel = getTerrenoAt(location).ehHabitavel(ator);
        
        if(estaVazio && ehHabitavel) {
            return location;
        } 
        else {
            return null; // Nenhum local livre e habitável foi encontrado
        }
    }

    /**
     * Gera um iterador sobre uma lista embaralhada de localizações adjacentes
     * à fornecida. A lista não inclui a própria localização.
     * Todas as localizações retornadas estarão dentro dos limites da grade.
     * * @param location A localização da qual gerar adjacências.
     * @return Um iterador sobre localizações adjacentes válidas.
     */
    public Iterator<Location> adjacentLocations(Location location)
    {
        int row = location.getRow();
        int col = location.getCol();
        LinkedList<Location> locations = new LinkedList<Location>();
        for(int roffset = -1; roffset <= 1; roffset++) {
            int nextRow = row + roffset;
            if(nextRow >= 0 && nextRow < depth) {
                for(int coffset = -1; coffset <= 1; coffset++) {
                    int nextCol = col + coffset;
                    // Exclude invalid locations and the original location.
                    if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }
        Collections.shuffle(locations,rand);
        return locations.iterator();
    }

    /**
     * Retorna a profundidade do campo.
     * @return A profundidade (número de linhas).
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Retorna a largura do campo.
     * @return A largura (número de colunas).
     */
    public int getWidth()
    {
        return width;
    }
}
