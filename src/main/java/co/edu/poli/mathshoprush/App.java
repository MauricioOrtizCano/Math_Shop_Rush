
package co.edu.poli.mathshoprush;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal que inicia la aplicación Math Shop Rush mediante JavaFX.
 * Configura la ventana principal y permite cargar las vistas FXML.
 */
public class App extends Application {

    /**
     * Escena principal de la aplicación, utilizada para cambiar entre vistas.
     */
    private static Scene scene;

    /**
     * Inicia la aplicación JavaFX y muestra la ventana principal.
     *
     * @param stage ventana principal proporcionada por JavaFX
     * @throws IOException si ocurre un error al cargar el archivo FXML inicial
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cambia la vista que se muestra en la escena principal.
     *
     * @param fxml nombre del archivo FXML que se desea cargar, sin extensión
     * @throws IOException si ocurre un error al cargar el archivo FXML
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga una vista desde su archivo FXML correspondiente.
     *
     * @param fxml nombre del archivo FXML que se desea cargar, sin extensión
     * @return componente raíz de la vista cargada
     * @throws IOException si ocurre un error durante la carga del archivo
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {
        launch();
    }

}
