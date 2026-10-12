module ni.uam.edu.test {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.uam.edu.test to javafx.fxml;
    exports ni.uam.edu.test;
}