
package co.edu.poli.mathshoprush;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Controlador de la vista principal de Math Shop Rush.
 * Gestiona la navegación desde la pantalla principal hacia la segunda vista.
 */
public class PrimaryController {

    /**
     * Cambia la vista principal por la vista secundaria.
     *
     * @throws IOException si ocurre un error al cargar la vista secundaria
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
