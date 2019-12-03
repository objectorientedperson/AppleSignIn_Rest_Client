Java based Restful API for Apple Sign In.

Grab the jar file from build/libs.

Usage:

        String issTeamId = "TEAM_ID";
        String subClientId = "APP_PACKAGE_ID";
        String keyId = "KEY_ID";
        String redirectUrl = "REDIRECT_URL";

        String keyFilePath = Main.class.getClassLoader().getResource("CERTIFICATE_PATH").getPath();

        Apple_SignIn appSignIn = new Apple_SignIn(
                issTeamId,
                subClientId,
                keyId,
                redirectUrl,
                keyFilePath);

        Apple_SignedIn_UserData data = appSignIn.getUserData("AUTHORIZATION_CODE_FROM_CLIENT");
        System.out.println(data == null ? "null" : data.email);