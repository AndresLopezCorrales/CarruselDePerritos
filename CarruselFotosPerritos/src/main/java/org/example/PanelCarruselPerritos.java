//Creado por Andrés López Corrales
package org.example;
//imports de librerias externas
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//imports de librerias java
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PanelCarruselPerritos extends JFrame {

    private final JComboBox<String> comboRazaPerro;
    private final JLabel imagenPerrito;
    private final Timer tiempoEntreImagenes;
    private ArrayList<String> urlImagenes;
    private int indexActual;
    JPanel panelLayout;

    public PanelCarruselPerritos() throws HeadlessException {

        //JFrame
        setTitle("Carrusel Perritos"); //titulo
        setSize(500,500); //tamaño
        setLocationRelativeTo(null); //centrado
        setDefaultCloseOperation(3); //Exit on close
        setResizable(false);

        //JPanel
        panelLayout = new JPanel();
        panelLayout.setLayout(new BorderLayout());  // BorderLayout

        //Meter los tipos de razas al comboBox
        comboRazaPerro = new JComboBox<>();
        cargarRazasACombo();

        //JLabel donde se veran las imagenes de perritos
        imagenPerrito = new JLabel();
        imagenPerrito.setVisible(true);
        imagenPerrito.setEnabled(true);
        imagenPerrito.setSize(200,500);
        imagenPerrito.setHorizontalAlignment(JLabel.CENTER);

        //Acomodar Combo y label en el BorderLayout
        panelLayout.add(comboRazaPerro, BorderLayout.NORTH); //Norte - Hasta arriba
        panelLayout.add(imagenPerrito, BorderLayout.CENTER); //Centro

        //Cuando se selecciona algo del comboBox
        comboRazaPerro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarImagenesPerritos(comboRazaPerro.getSelectedItem().toString());

            }
        });

        //Cuando pasa el tiempo va a cambiar de imagen
        tiempoEntreImagenes = new Timer(15000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarSigImg();
            }
        });

        indexActual = 0;

        cargarImagenesPerritos(""); //Empieza dando imagenes random

        add(panelLayout);
        panelLayout.setVisible(true);
        setVisible(true);

    }//end constructor


    private void cargarImagenesPerritos(String raza) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        int valorEntre10y20 = (int) (10 + (Math.random() * 11)); //valores de 10 entre 20

        String urlDogs = "https://dog.ceo/api/breeds/image/random/" + valorEntre10y20; //url de donde se sacan las imagenes

        if (!raza.isEmpty()) {
            urlDogs = "https://dog.ceo/api/breed/" + raza + "/images/random/" + valorEntre10y20;
        }
        if (raza.equals(comboRazaPerro.getItemAt(0))){
            urlDogs = "https://dog.ceo/api/breeds/image/random/" + valorEntre10y20;
        }
        System.out.println(urlDogs); //ver cuantas imagenes descargará

        try {
            HttpGet peticion = new HttpGet(urlDogs);
            try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(peticion)) {
                if (closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entidad = closeableHttpResponse.getEntity();
                    String respuesta = EntityUtils.toString(entidad);

                    JSONParser parser = new JSONParser(); //parser de Json

                    JSONObject jsonObject = (JSONObject) parser.parse(respuesta);

                    JSONArray imageArray = (JSONArray) jsonObject.get("message"); //obtener lo que hay dentro del "message" del JSON

                    //obtener las URLS de las imagenes y meterlas al ArrayList
                    urlImagenes = new ArrayList<>();
                    for (Object image : imageArray) {
                        urlImagenes.add((String) image);
                        System.out.println(image); //ver que imagenes descargó
                    }

                    indexActual = 0;

                    mostrarSigImg(); //iniciar poniendo imagenes para luego ir cambiando de imagen

                    tiempoEntreImagenes.start(); //iniciar tiempo para luego cambiar imagen
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }//end cargarImagenesPerritos

    private void mostrarSigImg() {
        if (urlImagenes != null && !urlImagenes.isEmpty()) { //NO null y arraylist NO vacio
            try {
                //Descargar img
                URL urlImagen = new URL(urlImagenes.get(indexActual));
                Image image = ImageIO.read(urlImagen);

                //Escala de imagen
                int width = getWidth();
                int height = getHeight();
                image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); //escalarlo
                ImageIcon imageIcon = new ImageIcon(image);

                imagenPerrito.setIcon(imageIcon); //set imagen en el Label

                indexActual = (indexActual + 1) % urlImagenes.size(); //incrementar indice

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//end mostrarSigImg

    private void cargarRazasACombo() {
        comboRazaPerro.removeAllItems(); //Quitar items de combo
        comboRazaPerro.addItem("Todas las razas"); //op predeterminada

        try {
            URL url = new URL("https://dog.ceo/api/breeds/list/all"); //url donde estan las razas
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int codeRespuesta = connection.getResponseCode();

            if (codeRespuesta == 200) {
                InputStream inputStream = connection.getInputStream(); //abrir el flujo
                JSONParser parser = new JSONParser(); //parser JSON
                JSONObject respuestaJson = (JSONObject) parser.parse(new InputStreamReader(inputStream, "UTF-8")); //lectura - respuesta
                JSONObject message = (JSONObject) respuestaJson.get("message"); //obtener la info que esta dentro del message del JSON

                //meter las razas dentro del comboBox
                for (Object breed : message.keySet()) {
                    comboRazaPerro.addItem((String) breed);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }//end cargarRazasCombo

}//end class
