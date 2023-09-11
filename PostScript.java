import java.util.Stack;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostScript {

    private static Stack<Double> pila = new Stack<>();
    private static List<String> operaciones = new ArrayList<>(); // Lista para almacenar operaciones.
    private static FileWriter archivoSalida;

    public static void main(String[] args) {
        try {
            archivoSalida = new FileWriter("registro_PostScript.log");
        } catch (IOException e) {
            e.printStackTrace();
            return; // Salir si no se puede abrir el archivo de salida.
        }

        boolean repetir = true;

        while (repetir) {
            String entrada = System.console().readLine("Ingrese una expresión PostScript \nComo ejemplo, puedes ingresarla de la siguientes formas:\n7 10 + \n7 7 / 5 *\n10 8 * 5 /\n's' para salir\nExpresion: ").toUpperCase();

            if (entrada.equals("S")) {
                System.out.println("Salió del programa");
                break;
            } else {
                if (entrada != null) {
                    try {
                        evaluarExpresion(entrada);
                    } catch (IllegalArgumentException e) {
                        String mensajeError = "Se ha generado una excepción: " + e.getMessage();
                        System.out.println(mensajeError);
                        escribirError(mensajeError);
                    } catch (Exception e) {
                        String mensajeError = "Se generó una excepción: " + e.getMessage();
                        System.out.println(mensajeError);
                        escribirError(mensajeError);
                    }
                }

                System.out.println("Accion realizada");

                String opc = System.console().readLine("\nDesea volver a repetir el programa? (S/N): ").toUpperCase();
                if (opc.equals("S")) {
                    repetir = true;
                } else {
                    System.out.println("Programa finalizado");
                    repetir = false;
                }
            }
        }

        // Escribir todas las operaciones al archivo de salida.
        try {
            if (archivoSalida != null) {
                for (String operacion : operaciones) {
                    archivoSalida.write("Operación: " + operacion + "\n");
                }
                archivoSalida.flush();
                archivoSalida.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void evaluarExpresion(String expresion) {
        String[] tokens = expresion.split("\\s+");
        StringBuilder operacion = new StringBuilder();

        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                pila.push(Double.parseDouble(token));
            } else if (operadorValido(token)) {
                realizarOperacion(token);
            } else if (token.startsWith("/")) {
                // Definición de símbolos (ejemplo: /pi 3.141592653 def)
                String simbolo = token.substring(1);
                pila.push(Double.parseDouble(simbolo));
            } else {
                throw new IllegalArgumentException("Dato no válido: " + token);
            }

            // Agregar el token actual a la operación
            operacion.append(token).append(" ");
        }

        if (pila.size() == 1) {
            double resultado = pila.pop();
            System.out.println("Resultado: " + resultado);
            escribirOperacion(operacion.toString() + "= " + resultado);
        } else {
            throw new IllegalArgumentException("La expresión ingresada es inválida");
        }
    }

    private static boolean operadorValido(String token) {
        return "+".equals(token) || "-".equals(token) || "*".equals(token) || "/".equals(token);
    }

    private static void realizarOperacion(String operador) {
        if (pila.size() < 2) {
            throw new IllegalArgumentException("No hay suficientes operandos en la pila para " + operador);
        }

        double b = pila.pop();
        double a = pila.pop();

        switch (operador) {
            case "+":
                pila.push(a + b);
                break;
            case "-":
                pila.push(a - b);
                break;
            case "*":
                pila.push(a * b);
                break;
            case "/":
                if (b == 0) {
                    throw new IllegalArgumentException("No se puede dividir por cero.");
                }
                pila.push(a / b);
                break;
        }
    }

    private static void escribirOperacion(String operacion) {
        operaciones.add(operacion); // Agregar la operación a la lista.
    }

    private static void escribirError(String mensajeError) {
        try {
            if (archivoSalida != null) {
                archivoSalida.write("Error: " + mensajeError + "\n");
                archivoSalida.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

