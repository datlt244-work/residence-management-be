package com.base.infra.util;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Utility class to generate RSA Key Pairs for JWT.
 * Run this class as a Java Application to generate a new pair of keys.
 */
public class RsaKeyGenerator {

    public static void main(String[] args) {
        try {
            // Generate RSA 2048-bit key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Format as PEM
            String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded()) +
                    "\n-----END PRIVATE KEY-----\n";

            String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(publicKey.getEncoded()) +
                    "\n-----END PUBLIC KEY-----\n";

            // Print to console
            System.out.println("================== PRIVATE KEY (Copy to application.yml or save as file) ==================");
            System.out.println(privateKeyPem);
            System.out.println("\n================== PUBLIC KEY (Copy to application.yml or save as file) ===================");
            System.out.println(publicKeyPem);

            // Save to files in infra/src/main/resources/certs if directory exists
            String resourcePath = "bootstrap/src/main/resources/keys";
            File dir = new File(resourcePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File privateKeyFile = new File(dir, "private_key_pkcs8.pem");
            try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
                fos.write(privateKeyPem.getBytes());
                System.out.println("\n[SUCCESS] Saved " + privateKeyFile.getAbsolutePath());
            }

            File publicKeyFile = new File(dir, "public_key.pem");
            try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
                fos.write(publicKeyPem.getBytes());
                System.out.println("[SUCCESS] Saved " + publicKeyFile.getAbsolutePath());
            }

            System.out.println("\nNote: Make sure to update your application.yml to point to these files or their contents.");

        } catch (Exception e) {
            System.err.println("Error generating RSA keys:");
            e.printStackTrace();
        }
    }
}
