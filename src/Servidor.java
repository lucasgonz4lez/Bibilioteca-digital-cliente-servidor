import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor implements Runnable{
    Socket clientSocket;
    ArrayList<Libro> books;

    public Servidor(Socket clientSocket, ArrayList<Libro> books) {
        this.clientSocket = clientSocket;
        this.books = books;
    }

    public ArrayList<Libro> filter(String title,String author){
        ArrayList<Libro> results = new ArrayList<>();
        String bookTitle;
        String bookAuthor;

        title = title.trim().toLowerCase();
        author=author.trim().toLowerCase();

        if(!title.isBlank() && !author.isBlank()){
            for(Libro i : books){
                bookTitle = i.getTitle().toLowerCase();
                bookAuthor = i.getAuthor().toLowerCase();
                if(bookTitle.contains(title) && bookAuthor.contains(author)) {
                    results.add(i);
                }
            }
        } else{
            if(!title.isBlank()){
                for(Libro i : books){
                    bookTitle = i.getTitle().toLowerCase();

                    if(bookTitle.contains(title)){results.add(i);}
                }
            }
            if(!author.isBlank()){
                for(Libro i : books){
                    bookAuthor = i.getAuthor().toLowerCase();

                    if(bookAuthor.contains(author)){results.add(i);}
                }
            }
        }

        if(results.isEmpty()){results = null;}
        return results;
    }

    @Override
    public void run() {
        String received = DataIO.ReadString(clientSocket);

        // No se que era lo de la query pero petaba por que no habia un; por parte del cliente
        if (received == null || !received.contains(";")) {
            System.out.println("Solicitud inválida recibida: " + received);
            try { clientSocket.close(); } catch (Exception e) { e.printStackTrace(); }
            return;
        }

        // Separar en dos partes máximo: título y autor
        String[] query = received.split(";", 2);
        String titleQuery = query[0].trim();
        String authorQuery = query[1].trim();

        ArrayList<Libro> results = filter(titleQuery, authorQuery);

        DataIO.WriteObject(clientSocket, results);

        try {
            clientSocket.close();
        } catch (Exception e) { e.printStackTrace(); }
    }


    static void main(String[] args) {
        ArrayList<Libro> books;
        System.out.println("Cargando lista de libros...");
        books = loadLibrary();


        if (books == null) {
            System.out.println("!- Hubo un fallo al cargar el el archivo de libros, " +
                    "porfavor revise los contenidos de libros.csv");
            return;
        } else if (books.size()==0){
            System.out.println("!- No existen registros en el archivo de libros, " +
                    "porfavor añada contenido a libros.csv");
            return;
        } else{
            System.out.println("Se han cargado "+books.size()+" libros...");
        }

        try {
            ServerSocket serverSocket = new ServerSocket(6002);
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                new Thread(new Servidor(clientSocket,books)).start();
            }
        } catch (Exception e) {
        }
    }

    private static ArrayList<Libro> loadLibrary(){
        ArrayList<Libro> content = new ArrayList<>();

        try{
            FileInputStream stream = new FileInputStream(System.getProperty("java.class.path")+"/libros.csv");
            InputStreamReader streamReader = new InputStreamReader(stream);

            var rows = streamReader.readAllLines();
            for(String i : rows){
                if(i.isBlank()) continue; // Ya no me da null pointer
                String[] columns = i.split(";");
                if(columns.length < 4) continue; // Esta cosa ignora las lineas malas
                String title = columns[0];
                String author = columns[1];
                int releaseYear = Integer.parseInt(columns[2]);
                String synopsis = columns[3];
                content.add(new Libro(title, author, releaseYear, synopsis));
            }


        }catch(Exception e){
            content = null;
        }

        return content;
    }

}
