package com.smartral.apple_signin;

import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by @author ali on 12/2/19
 */
public class Apple_SignIn {

    private static final String APP_AUD = "https://appleid.apple.com";
    private static final String APP_URL = APP_AUD + "/auth/token";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final PrivateKeyData pkd = new PrivateKeyData();

    private String APP_SUB_CLIENT_ID;
    private String APP_REDIRECT_URL;

    public Apple_SignIn(String issTeamId, String subClientId, String keyId, String redirectUrl, String keyFilePath) {
        pkd.getHeader().put("kid", keyId);

        pkd.getPayload().put("iss", issTeamId);
        pkd.getPayload().put("sub", subClientId);
        pkd.getPayload().put("aud", APP_AUD);

        pkd.setKeyFileOrPrivateKey(keyFilePath);

        this.APP_SUB_CLIENT_ID = subClientId;
        this.APP_REDIRECT_URL = redirectUrl;
    }

    /**
     * @param privateKeyData
     * @return
     */
    private static String generateToken(PrivateKeyData privateKeyData) {
        String token = null;
        try {
            // get the private key from encoded key.
            String mPrivateKey = privateKeyData.getKeyFileOrPrivateKey();
            PrivateKey pvtKey = getClientSecret(mPrivateKey);
            if (!mPrivateKey.isEmpty()) {
                token = Jwts.builder()
                        .setClaims(privateKeyData.getPayload())
                        .setHeaderParams(privateKeyData.getHeader())
                        .setExpiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                        .signWith(SignatureAlgorithm.ES256, pvtKey)
                        .compact();
            } else {
                token = "Something went wrong!!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * @return
     */
    private static PrivateKey getClientSecret(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8.name());

        // strip of header, footer, newlines, whitespaces
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // decode to get the binary DER representation
        byte[] privateKeyDER = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
    }

    /**
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static byte[] getQuery(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8.name()));
            postData.append('=');
            postData.append(URLEncoder.encode(param.getValue().toString(), StandardCharsets.UTF_8.name()));
        }
        return postData.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * If null, token has expired.
     *
     * @param clientToken
     * @return
     */
    private Apple_SignIn_Response authenticate(String clientToken) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("scope", "name email");
            params.put("client_id", APP_SUB_CLIENT_ID);
            params.put("redirect_uri", APP_REDIRECT_URL);
            params.put("client_secret", generateToken(pkd));
            params.put("code", clientToken);

            URL url = new URL(APP_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", CONTENT_TYPE);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.connect();
            conn.getOutputStream().write(getQuery(params));
            conn.getOutputStream().close();

            // Read response.
            String data = IOUtils.toString(conn.getInputStream());
            return new Gson().fromJson(data, Apple_SignIn_Response.class);
        } catch (Exception e) {
            Logger.getLogger(Apple_SignIn.class.getName()).log(Level.FINE, e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Extracts user data from response.
     *
     * @param clientToken
     * @return
     */
    public Apple_SignedIn_UserData getUserData(String clientToken) {
        Apple_SignIn_Response response = authenticate(clientToken);
        if (response == null) return null;

        byte[] data = Base64.getDecoder().decode(response.idToken.split("\\.")[1]);
        return new Gson().fromJson(new String(data), Apple_SignedIn_UserData.class);
    }
}
