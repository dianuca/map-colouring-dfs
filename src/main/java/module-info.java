module com.example.proiect_dianaciodolan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.proiect_dianaciodolan to javafx.fxml;
    exports com.example.proiect_dianaciodolan;
}