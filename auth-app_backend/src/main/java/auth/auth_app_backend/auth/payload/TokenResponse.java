package auth.auth_app_backend.auth.payload;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        UserDto user
) {

        public static TokenResponse of(String accesToken,String refreshToken, long expireIn,String tokenType ,UserDto user){
            return new TokenResponse(accesToken,refreshToken,expireIn,"Bearer",user);
        }
    }


