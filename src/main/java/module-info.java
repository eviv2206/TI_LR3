module com.example.lr3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lr3 to javafx.fxml;
    exports com.example.lr3;
}