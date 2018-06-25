package uk.gov.dvla.osg.rpd.config.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

/**
 * Created by adi on 3/7/18.
 */
public class JavaKeyStore {

    private KeyStore keyStore;

    private String keyStoreName;
    private String keyStoreType;
    private String keyStorePassword;

    /**
     * This class represents a storage facility for cryptographic keys and certificates. 
     * @param keyStoreType the type of keystore. If null or empty the default jks store will be used.
     * @param keyStorePassword  the password used to check the integrity of the keystore, the password used to unlock the keystore
     * @param keyStoreName the file from which the keystore is loaded
     */
    JavaKeyStore(String keyStoreType, String keyStoreName, String keyStorePassword) {
        this.keyStoreType = keyStoreType;
        this.keyStoreName = keyStoreName;
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Creates a keystore of the specified type and name and protects its integrity with the given password.
     * @throws KeyStoreException if the keystore has not been initialized (loaded)
     * @throws CertificateException  if any of the certificates included in the keystore data could not be stored
     * @throws NoSuchAlgorithmException  if the appropriate data integrity algorithm could not be found
     * @throws IOException if there was an I/O problem with data
     */
    void createEmptyKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        //load
        char[] pwdArray = keyStorePassword.toCharArray();
        keyStore.load(null, pwdArray);

        // Save the keyStore
        try (FileOutputStream fos = new FileOutputStream(keyStoreName)) {
            keyStore.store(fos, pwdArray);
        }
    }

    /**
     * Loads this KeyStore using the given password. 
     * @throws IOException if there is an I/O or format problem with the keystore data, if a password is required but not given, or if the given password was incorrect. If the error is due to a wrong password, the cause of the IOException should be an UnrecoverableKeyException
     * @throws CertificateException if any of the certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     * @throws KeyStoreException 
     */
    void loadKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        char[] pwdArray = keyStorePassword.toCharArray();
        try (FileInputStream stream = new FileInputStream(keyStoreName)) {
            keyStore.load(stream, pwdArray);
        }
    }

    /**
     * Creates a keystore object of the specified type. 
     * @throws KeyStoreException if no Provider supports a KeyStoreSpi implementation for the specified type.
     */
    void getInstance() throws KeyStoreException {
        if(keyStoreType == null || keyStoreType.isEmpty()) {
            keyStoreType = KeyStore.getDefaultType();
        }
        keyStore = KeyStore.getInstance(keyStoreType);
    }

    /**
     * Saves a keystore Entry under the specified alias. The protection parameter is used to protect the Entry. 
     * @param alias save the keystore Entry under this alias
     * @param secretKeyEntry the Entry to save
     * @param protectionParameter the ProtectionParameter used to protect the Entry, which may be null
     * @throws KeyStoreException if the keystore has not been initialized (loaded), or if this operation fails for some other reason
     */
    void setEntry(String alias, KeyStore.SecretKeyEntry secretKeyEntry, KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        keyStore.setEntry(alias, secretKeyEntry, protectionParameter);
    }

    /**
     * Gets a keystore Entry for the specified alias with the specified protection parameter.
     * @param alias the keystore Entry for this alias
     * @return the keystore Entry for the specified alias, or null if there is no such entry
     * @throws UnrecoverableEntryException  if the specified ProtectionParameter were insufficient or invalid
     * @throws NoSuchAlgorithmException  if the algorithm for recovering the entry cannot be found
     * @throws KeyStoreException if the keystore has not been initialized (loaded), or if this operation fails for some other reason
     */
    KeyStore.Entry getEntry(String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword.toCharArray());
        return keyStore.getEntry(alias, protParam);
    }

    /**
     * Assigns the given key to the given alias, protecting it with the given password.
     * @param alias the alias name
     * @param privateKey the key to be associated with the alias
     * @param keyPassword the password to protect the key
     * @param certificateChain the certificate chain for the corresponding public key (only required if the given key is of type java.security.PrivateKey).
     * @throws KeyStoreException if the keystore has not been initialized (loaded), or if this operation fails for some other reason
     */
    void setKeyEntry(String alias, PrivateKey privateKey, String keyPassword, Certificate[] certificateChain) throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, keyPassword.toCharArray(), certificateChain);
    }

    /**
     * Assigns the given trusted certificate to the given alias. 
     * @param alias the alias name
     * @param certificate  the certificate
     * @throws KeyStoreException if the keystore has not been initialized, or the given alias already exists and does not identify an entry containing a trusted certificate, or this operation fails for some other reason.
     */
    void setCertificateEntry(String alias, Certificate certificate) throws KeyStoreException {
        keyStore.setCertificateEntry(alias, certificate);
    }

    /**
     * Returns the certificate associated with the given alias. 
     * @param alias the alias name
     * @return the certificate, or null if the given alias does not exist or does not contain a certificate.
     * @throws KeyStoreException if the keystore has not been initialized (loaded).
     */
    Certificate getCertificate(String alias) throws KeyStoreException {
        return keyStore.getCertificate(alias);
    }

    /**
     * Deletes the entry identified by the given alias from this keystore.
     * @param alias the alias name
     * @throws KeyStoreException if the keystore has not been initialized, or if the entry cannot be removed.
     */
    void deleteEntry(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    /**
     * Deletes the key store. The KeyStore must first be loaded before calling this method.
     * @throws KeyStoreException if the keystore has not been initialized, or if the entry cannot be removed.
     * @throws IOException if the KeyStore could not be deleted from the file system.
     */
    void deleteKeyStore() throws KeyStoreException, IOException {
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            keyStore.deleteEntry(alias);
        }
        keyStore = null;
        Files.delete(Paths.get(keyStoreName));
    }

}