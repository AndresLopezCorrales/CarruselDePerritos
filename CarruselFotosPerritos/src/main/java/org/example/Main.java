//Creado por Andrés López Corrales
package org.example;

public class Main {
    /*
    Práctica REST API: Carrusel de perritos.

    Implementar un carrusel de imágenes de perritos. Usar el servicio DOG API para descargar aleatoriamente
    entre 10 y 20 imágenes. Al descargarlas, en una ventana se debe mostrar cada imagen 15 segundos y después
    pasar a la siguiente imagen. Después de desplegar la última imagen, volver a mostrar la primera.
    Se debe dar al usuario la opción de escoger que le muestre imágenes de una raza de perro solamente.
    Por ejemplo, solamente Husky. Si el usuario no indica la raza, seleccionar imágenes aleatorias.
     */
    public static void main(String[] args) {
       PanelCarruselPerritos ejecutar = new PanelCarruselPerritos(); //llamar a la clase del carrusel
        ejecutar.setVisible(true);

    }
}