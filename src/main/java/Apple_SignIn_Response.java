import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by @author ali on 12/3/19
 */
public class Apple_SignIn_Response implements Serializable {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("id_token")
    public String idToken;

}
