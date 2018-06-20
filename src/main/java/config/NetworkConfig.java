package config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class NetworkConfig {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    /******************************************************************************************
    *              SINGLETON PATTERN
    ******************************************************************************************/
   private static String filename;

   private static class SingletonHelper {
       private static final NetworkConfig INSTANCE = new NetworkConfig();
   }

   public static NetworkConfig getInstance() {
       if (StringUtils.isBlank(filename)) {
           throw new RuntimeException("Application Configuration not initialised before use");
       }
       return SingletonHelper.INSTANCE;
   }

   public static void init(String file) throws RuntimeException {
       if (StringUtils.isBlank(filename)) {
           if (new File(file).isFile()) {
               filename = file;
           } else {
               throw new RuntimeException("Application Configuration File " + filename + " does not exist on filepath.");
           }
       } else {
           throw new RuntimeException("Application Configuration has already been initialised");
       }
   }
   /*****************************************************************************************/
    
    // absolute filepath for the vault_stock.config file

    private SimpleStringProperty protocol = new SimpleStringProperty();
    private SimpleStringProperty host = new SimpleStringProperty();
    private SimpleStringProperty port = new SimpleStringProperty();
    private SimpleStringProperty loginUrl = new SimpleStringProperty();
    private SimpleStringProperty logoutUrl = new SimpleStringProperty();
    private SimpleStringProperty vaultUrl = new SimpleStringProperty();
  
    private NetworkConfig() {
        
        PropertyLoader loader = new PropertyLoader(filename);
      
        try {
            protocol.set(loader.getProperty("protocol"));
            host.set(loader.getProperty("host"));
            port.set(loader.getProperty("port"));
            loginUrl.set(loader.getProperty("loginUrl"));
            logoutUrl.set(loader.getProperty("logoutUrl"));
            vaultUrl.set(loader.getProperty("vaultUrl"));
        } catch (RuntimeException ex) {
            LOGGER.fatal(ex.getMessage());
            System.exit(1);
        }
    }
    
    public StringProperty protocolProperty() {
        return protocol;
    }

    public StringProperty hostProperty() {
        return host;
    }
    
    public StringProperty portProperty() {
        return port;
    }
    
    public StringProperty loginUrlProperty() {
        return loginUrl;
    }
    
    public StringProperty logoutUrlProperty() {
        return logoutUrl;
    }
    
    public StringProperty vaultUrlProperty() {
        return vaultUrl;
    }
    
    public boolean saveProperties() {
        try {
            // Get props as byte array
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Properties props = new Properties();
            props.setProperty("protocol", this.protocol.get());
            props.setProperty("host", this.host.get());
            props.setProperty("port", this.port.get());
            props.setProperty("loginUrl", this.loginUrl.get());
            props.setProperty("logoutUrl", this.logoutUrl.get());
            props.setProperty("vaultUrl", this.vaultUrl.get());
            
            props.store(output, null);
            // return encrypted props byte array
            
            byte[] encryptedBytes = Cryptifier.encrypt(output.toByteArray());
            LOGGER.debug(filename);
            Files.write(Paths.get(filename), encryptedBytes);
            return true;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException ex) {
            LOGGER.fatal("Unable to save config file!\n{}",ExceptionUtils.getStackTrace(ex));
            return false;
        }
    }
}
