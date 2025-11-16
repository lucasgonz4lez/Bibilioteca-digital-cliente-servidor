import java.io.Serializable;

public class Libro implements Serializable {
    String title = "Sin Título";
    String author = "Autor Desconocido";
    int releaseYear = 0;
    String synopsis = "";

    public Libro(String title, String author, int releaseYear, String synopsis){
        this.title=title;
        this.author=author;
        this.releaseYear=releaseYear;
        this.synopsis=synopsis;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor(){
        return author;
    }

    //Honestamente esto se podría usar para la exportación del Cliente
    //-Rvp
    @Override
    public String toString(){
        return "Título: "+title+"\nAutor: "+author+"\nFecha de lanzamiento: "+releaseYear+"\nSynopsis: "+synopsis;
    }
}
