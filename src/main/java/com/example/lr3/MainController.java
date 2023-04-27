package com.example.lr3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private TextField bText;

    @FXML
    private RadioButton decodeBtn;

    @FXML
    private RadioButton encodeBtn;

    @FXML
    private Button executeBtn;

    @FXML
    private Button getPathBtn;

    @FXML
    private ToggleGroup method;

    @FXML
    private TextField pText;

    @FXML
    private TextField qText;

    @FXML
    private TextArea resultFileText;

    @FXML
    private TextField sourcePathText;

    @FXML
    private TextArea inputFileText;

    private String currentPath;

    @FXML
    void getSourcePath(ActionEvent event) {
        Stage stage = (Stage) getPathBtn.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            currentPath = selectedFile.getAbsolutePath();
            sourcePathText.setText(selectedFile.getName());
        }
    }

    @FXML
    void onExecute(ActionEvent event) {
        inputFileText.clear();
        resultFileText.clear();
        if (isValidInputData()){
            BigInteger pValue = new BigInteger(pText.getText());
            BigInteger qValue = new BigInteger(qText.getText());
            BigInteger nValue = qValue.multiply(pValue);
            BigInteger bValue;
            if (bText.getText().equals("")) {
                bValue = getRandomValue(nValue);
            } else {
                bValue = new BigInteger(bText.getText());
            }
            RabinCrypto rc = new RabinCrypto(this.currentPath, pValue, qValue, bValue);
            if (encodeBtn.isSelected()) {
                inputFileText.setText(rc.getBytesFromFile().toString());
                resultFileText.setText(rc.encrypt());
            } else {
                inputFileText.setText(rc.getArrayListFromFile().toString());
                resultFileText.setText(rc.decrypt());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setContentText("Данные введены с ошибкой или не выбран файл");
            alert.showAndWait();
        }
    }

    private boolean isValidInputData() {
        if (pText.getText().trim().equals("") && qText.getText().trim().equals("")) {
            return false;
        }
        if (sourcePathText.getText().equals("")){
            return false;
        }
        String p = pText.getText().trim();
        String q = qText.getText().trim();
        if (p.equals(q)){
            return false;
        }
        BigInteger pValue = new BigInteger(p);
        BigInteger qValue = new BigInteger(q);
        if (!MillerRabinTest.isProbablePrime(pValue, 100) && !MillerRabinTest.isProbablePrime(qValue, 100)){
            return false;
        }
        if (!pValue.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))){
            return false;
        }
        if (!qValue.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))){
            return false;
        }
        return true;
    }

    private BigInteger getRandomValue(BigInteger upperBound){
        SecureRandom random = new SecureRandom();
        BigInteger bi = new BigInteger(upperBound.bitLength(), random).mod(upperBound);
        bText.setText(bi.toString());
        return bi;
    }

    public void initialize() {
        pText.setText("787");
        qText.setText("859");
        bText.setText("675522");
    }

}
