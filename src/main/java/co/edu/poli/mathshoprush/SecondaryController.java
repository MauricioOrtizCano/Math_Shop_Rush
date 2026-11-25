
package co.edu.poli.mathshoprush;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Controlador de la vista secundaria de Math Shop Rush.
 * Gestiona la navegación desde la segunda pantalla hacia la vista principal.
 */
public class SecondaryController {

    /**
     * Regresa desde la vista secundaria a la vista principal.
     *
     * @throws IOException si ocurre un error al cargar la vista principal
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
