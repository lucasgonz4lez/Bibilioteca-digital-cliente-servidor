import java.net.Socket;
import java.util.ArrayList;

public class Cliente {

    //este main es solo para probar si funciona lo del server
    //deberías montar algo con java swing tbh (also quizás dejar al usuario poner una ip?)
    //-Rvp
    public static void main(String[] args){
        try{
            Socket socket = new Socket("localhost",6002);
            DataIO.WriteString(socket,";tessEverest");

            ArrayList<Libro> libros;
            libros = (ArrayList<Libro>) DataIO.ReadObject(socket);

            if(libros==null){
                System.out.println("!- No se han econtrado resultados");
            }
            for(Libro i : libros){
                System.out.println(i.toString());
            }
            socket.close();
        }catch(Exception e){}
    }
}
