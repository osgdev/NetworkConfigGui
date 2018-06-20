package uk.gov.dvla.osg.rpd.config.gui;

import java.io.ByteArrayInputStream;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyLoader {
    
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Properties properties = new Properties();
    
    public PropertyLoader(String filename) {
        try {
            // decrypt file
            byte[] fileContents = Files.readAllBytes(Paths.get(filename));
            byte[] decryptedBytes = Cryptifier.decrypt(fileContents);

            /********************** FOR TESTING ONLY******************************
             // encrypt file 
             String path = "C:\\Users\\peter\\Desktop\\network.config"; 
             byte[] testFileContents = Files.readAllBytes(Paths.get(path)); 
             byte[] encryptedBytes = Cryptifier.encrypt(testFileContents); 
             Files.write(Paths.get(path), encryptedBytes); 
             System.out.println("Done!"); 
             System.exit(0);
             /*********************************************************************/

            // load properties file
            properties.load(new ByteArrayInputStream(decryptedBytes));
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.fatal("Unable to load application properties.");
            System.exit(1);
        }
    }
    
    public String getProperty(String key) throws RuntimeException {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        throw new RuntimeException("Unable to load property ["+key+"] from file.");
    }
    
    public int getPropertyInt(String key) throws RuntimeException {
        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            if (StringUtils.isNumeric(value)) {
                return Integer.parseInt(value);
            }
            LOGGER.fatal("Value [{}] is not valid for the property [{}].", value, key);
            throw new RuntimeException("Value ["+value+"] is not valid for the property ["+key+"].");
        }
        LOGGER.fatal("Unable to load property [{}] from Production Configuration file.", key);
        throw new RuntimeException("Unable to load property ["+key+"] from Production Configuration file.");
    }
}
