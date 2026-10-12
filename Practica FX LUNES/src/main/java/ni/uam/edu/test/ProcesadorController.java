package ni.uam.edu.test;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {
    @FXML private Label lblEstado;
    @FXML private ProgressBar barraProgreso;
    @FXML private Label lblPorcentaje;
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private Button btnReiniciar;

    @FXML private Label lblEstadoClientes;
    @FXML private ProgressBar barraClientes;
    @FXML private Label lblPorcentajeClientes;
    @FXML private Button btnIniciarClientes;
    @FXML private Button btnDetenerClientes;

    @FXML private Label lblEstadoProductos;
    @FXML private ProgressBar barraProductos;
    @FXML private Label lblPorcentajeProductos;
    @FXML private Button btnIniciarProductos;
    @FXML private Button btnDetenerProductos;
    @FXML private Button btnReiniciarReto;

    private Thread hiloProceso;
    private Thread hiloClientes;
    private Thread hiloProductos;

    @FXML
    private void initialize() {
        actualizarBotonesPrincipal(false);
        actualizarBotonesClientes(false);
        actualizarBotonesProductos(false);
    }

    @FXML
    private void iniciarProceso() {
        if (estaActivo(hiloProceso)) return;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Procesando registros...");
        actualizarBotonesPrincipal(true);

        Runnable tarea = () -> procesar(100, 100, avance -> Platform.runLater(() -> {
            barraProgreso.setProgress(avance / 100.0);
            lblPorcentaje.setText(avance + "%");
        }), resultado -> Platform.runLater(() -> {
            lblEstado.setText(resultado);
            actualizarBotonesPrincipal(false);
        }));
        hiloProceso = new Thread(tarea, "Hilo-Procesamiento-Registros");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void detenerProceso() {
        if (estaActivo(hiloProceso)) {
            btnDetener.setDisable(true);
            lblEstado.setText("Solicitando detención...");
            hiloProceso.interrupt();
        }
    }

    @FXML
    private void reiniciarProceso() {
        if (estaActivo(hiloProceso)) return;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");
        actualizarBotonesPrincipal(false);
    }

    @FXML
    private void iniciarClientes() {
        if (estaActivo(hiloClientes)) return;
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0%");
        lblEstadoClientes.setText("Procesando clientes...");
        actualizarBotonesClientes(true);
        hiloClientes = new Thread(() -> procesar(100, 80, avance -> Platform.runLater(() -> {
            barraClientes.setProgress(avance / 100.0);
            lblPorcentajeClientes.setText(avance + "%");
        }), resultado -> Platform.runLater(() -> {
            lblEstadoClientes.setText(resultado);
            actualizarBotonesClientes(false);
        })), "Hilo-Clientes");
        hiloClientes.setDaemon(true);
        hiloClientes.start();
    }

    @FXML
    private void detenerClientes() {
        if (estaActivo(hiloClientes)) {
            btnDetenerClientes.setDisable(true);
            lblEstadoClientes.setText("Solicitando detención...");
            hiloClientes.interrupt();
        }
    }

    @FXML
    private void iniciarProductos() {
        if (estaActivo(hiloProductos)) return;
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0%");
        lblEstadoProductos.setText("Procesando productos...");
        actualizarBotonesProductos(true);
        hiloProductos = new Thread(() -> procesar(50, 140, avance -> Platform.runLater(() -> {
            barraProductos.setProgress(avance / 100.0);
            lblPorcentajeProductos.setText(avance + "%");
        }), resultado -> Platform.runLater(() -> {
            lblEstadoProductos.setText(resultado);
            actualizarBotonesProductos(false);
        })), "Hilo-Productos");
        hiloProductos.setDaemon(true);
        hiloProductos.start();
    }

    @FXML
    private void detenerProductos() {
        if (estaActivo(hiloProductos)) {
            btnDetenerProductos.setDisable(true);
            lblEstadoProductos.setText("Solicitando detención...");
            hiloProductos.interrupt();
        }
    }

    @FXML
    private void reiniciarReto() {
        if (estaActivo(hiloClientes) || estaActivo(hiloProductos)) return;
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0%");
        lblEstadoClientes.setText("Esperando inicio");
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0%");
        lblEstadoProductos.setText("Esperando inicio");
        actualizarBotonesClientes(false);
        actualizarBotonesProductos(false);
    }

    private void procesar(int total, long pausaMs, Actualizador avanceUi, Finalizador finalUi) {
        try {
            for (int i = 1; i <= total; i++) {
                if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                Thread.sleep(pausaMs);
                int porcentaje = (int) Math.round(i * 100.0 / total);
                avanceUi.actualizar(porcentaje);
            }
            finalUi.finalizar("Proceso finalizado");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            finalUi.finalizar("Proceso detenido");
        }
    }

    private boolean estaActivo(Thread hilo) {
        return hilo != null && hilo.isAlive();
    }

    private void actualizarBotonesPrincipal(boolean activo) {
        btnIniciar.setDisable(activo);
        btnDetener.setDisable(!activo);
        btnReiniciar.setDisable(activo);
    }

    private void actualizarBotonesClientes(boolean activo) {
        btnIniciarClientes.setDisable(activo);
        btnDetenerClientes.setDisable(!activo);
        btnReiniciarReto.setDisable(activo || estaActivo(hiloProductos));
    }

    private void actualizarBotonesProductos(boolean activo) {
        btnIniciarProductos.setDisable(activo);
        btnDetenerProductos.setDisable(!activo);
        btnReiniciarReto.setDisable(activo || estaActivo(hiloClientes));
    }

    public void detenerTodosAlCerrar() {
        if (estaActivo(hiloProceso)) hiloProceso.interrupt();
        if (estaActivo(hiloClientes)) hiloClientes.interrupt();
        if (estaActivo(hiloProductos)) hiloProductos.interrupt();
    }

    @FunctionalInterface private interface Actualizador { void actualizar(int porcentaje); }
    @FunctionalInterface private interface Finalizador { void finalizar(String resultado); }
}
