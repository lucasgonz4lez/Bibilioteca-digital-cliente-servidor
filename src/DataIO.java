import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


//Robado de otro proyecto mío, quizás lo mueva a las clases pero de momento sirve para ambas
//-Rvp
public class DataIO {

    public static String ReadString(Socket source){
        String value = "";
        try{
            DataInputStream input = new DataInputStream(source.getInputStream());
            value=input.readUTF();
        }catch(Exception e){return null;}
        return value;
    }

    public static void WriteString(Socket source, String value){
        try{
            DataOutputStream output = new DataOutputStream(source.getOutputStream());
            output.writeUTF(value);
        }catch(Exception e){}
    }

    public static void WriteObject(Socket source, Object value){
        try{
            ObjectOutputStream output = new ObjectOutputStream(source.getOutputStream());
            output.writeObject(value);
        }catch(Exception e){}
    }

    public static Object ReadObject(Socket source){
        Object value;
        try{
            ObjectInputStream input = new ObjectInputStream(source.getInputStream());
            value=input.readObject();
        }catch(Exception e){return null;}
        return value;
    }
}
