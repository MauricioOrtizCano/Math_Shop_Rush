module co.edu.poli.mathshoprush {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.poli.mathshoprush to javafx.fxml;
    exports co.edu.poli.mathshoprush;
}
