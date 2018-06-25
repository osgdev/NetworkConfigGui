package uk.gov.dvla.osg.rpd.config.gui;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/Mainform.fxml"));
        primaryStage.setTitle("RPD Network Config");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/folder.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {
        String type = "JCEKS";
        String name = "HumptyDumpty.jks";
        String password = "James";
        
        JavaKeyStore jks = new JavaKeyStore(type, name, password);
        jks.getInstance();
        jks.loadKeyStore();
        System.out.println("DONE!");
        System.exit(1);
        try {
            if (args.length != 1) {
                LOGGER.fatal("Incorrect number of args, Usage: {file}.jar {properties_filepath}");
                System.exit(1);
            }
            
            String inputFile = args[0];
            NetworkConfig.init(inputFile);
            
            launch(args);
            
        } catch (Exception e) {
            LOGGER.fatal(ExceptionUtils.getStackTrace(e));
        }
    }

}
