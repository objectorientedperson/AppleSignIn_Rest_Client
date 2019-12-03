import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by @author ali on 12/3/19
 */
public class Apple_SignedIn_UserData implements Serializable {

    /**
     * The issuer-registered claim key, which has the value https://appleid.apple.com.
     */
    @SerializedName("iss")
    public String iss;

    /**
     * Your client_id in your Apple Developer account.
     */
    @SerializedName("aud")
    public String aud;

    /**
     * The expiry time for the token. This value is typically set to 5 minutes.
     */
    @SerializedName("exp")
    public long exp;

    /**
     * The time the token was issued.
     */
    @SerializedName("iat")
    public long iat;

    /**
     * The unique identifier for the user.
     */
    @SerializedName("sub")
    public String sub;

    @SerializedName("at_hash")
    public String atHash;

    /**
     * The user's email address.
     */
    @SerializedName("email")
    public String email;

    /**
     * A Boolean value that indicates whether the service has verified the email. The value of this claim is always
     * true because the servers only return verified email addresses.
     */
    @SerializedName("email_verified")
    public String emailVerified;

    @SerializedName("auth_time")
    public long authTime;

}
