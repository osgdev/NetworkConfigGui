package uk.gov.dvla.osg.rpd.config.gui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class MainformController {

    @FXML
    private TextField txtProtocol;
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtLoginUrl;
    @FXML
    private TextField txtLogoutUrl;
    @FXML
    private TextField txtVaultUrl;
    @FXML
    private Button btnSave;
    @FXML
    private Label lblMessage;
    
    
    @FXML
    private void initialize() {
        NetworkConfig config = NetworkConfig.getInstance();
        txtProtocol.textProperty().bindBidirectional(config.protocolProperty());
        txtHost.textProperty().bindBidirectional(config.hostProperty());
        txtPort.textProperty().bindBidirectional(config.portProperty());
        txtLoginUrl.textProperty().bindBidirectional(config.loginUrlProperty());
        txtLogoutUrl.textProperty().bindBidirectional(config.logoutUrlProperty());
        txtVaultUrl.textProperty().bindBidirectional(config.vaultUrlProperty());
    }
    
    @FXML
    private void save() {
        
        NetworkConfig config = NetworkConfig.getInstance();
        
        if (config.saveProperties()) {
            lblMessage.setText("Saved!");
        } else {
            lblMessage.setText("Save Error!");
        }
       
        lblMessage.setOpacity(1);
       
       FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4), lblMessage);
       fadeTransition.setDelay(Duration.seconds(4));
       fadeTransition.setFromValue(0.99);
       fadeTransition.setToValue(0.0);
       fadeTransition.play();
    }
}
