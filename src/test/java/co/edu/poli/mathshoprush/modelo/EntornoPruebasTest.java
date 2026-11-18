package co.edu.poli.mathshoprush.modelo;

import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.Assertions;

public class EntornoPruebasTest {
    @Test
    void pruebaTest() {
        int resultado = 2 + 2;
        
        Assertions.assertEquals(4, resultado, "La suma debe ser 4");
    }
}
