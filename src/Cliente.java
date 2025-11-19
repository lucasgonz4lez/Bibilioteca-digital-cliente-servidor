import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Cliente extends JFrame {

    private JTextField titleField;
    private JTextField authorField;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton saveButton;

    private String serverIP = "localhost";
    private int serverPort = 6002;

    public Cliente() {
        super("Buscador de Libros");

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Título:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Autor:"));
        authorField = new JTextField();
        inputPanel.add(authorField);

        searchButton = new JButton("Buscar");
        inputPanel.add(searchButton);

        saveButton = new JButton("Guardar");
        inputPanel.add(saveButton);

        inputPanel.add(new JLabel());

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarLibros();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarResultados();
            }
        });

    }

    private void buscarLibros() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        resultArea.setText("Buscando...");
        searchButton.setEnabled(false);

        new SwingWorker<ArrayList<Libro>, Void>() {
            @Override
            protected ArrayList<Libro> doInBackground() {
                try (Socket socket = new Socket(serverIP, serverPort)) {
                    DataIO.WriteString(socket, title + ";" + author);
                    Object response = DataIO.ReadObject(socket);
                    if (response != null) {
                        return (ArrayList<Libro>) response;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Libro> results = get();
                    resultArea.setText("");
                    if (results != null && !results.isEmpty()) {
                        for (Libro libro : results) {
                            resultArea.append(libro.toString() + "\n-------------------------\n");
                        }
                    } else {
                        resultArea.setText("No se encontraron libros que coincidan con la búsqueda.");
                    }
                } catch (Exception e) {
                    resultArea.setText("Error al conectarse al servidor: " + e.getMessage());
                } finally {
                    searchButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void guardarResultados() {
        try {
            String contenido = resultArea.getText();
            if (contenido.isBlank()) {
                JOptionPane.showMessageDialog(this, "No hay resultados para guardar.");
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar archivo");
            int seleccion = fileChooser.showSaveDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                writer.write(contenido);
                writer.close();
                JOptionPane.showMessageDialog(this, "Archivo guardado correctamente.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
            Cliente cliente = new Cliente();
            cliente.setVisible(true);
    }
}
