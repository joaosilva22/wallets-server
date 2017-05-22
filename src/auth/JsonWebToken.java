package auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.regex.Pattern;

public class JsonWebToken {
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private int uid;
    private Timestamp iat, exp;
    private String token;

    public JsonWebToken(String type, int uid, PrivateKey key) throws Exception {
        switch (type) {
            case ACCESS_TOKEN:
                generateToken(key, uid, 15);
                break;
            case REFRESH_TOKEN:
                generateToken(key, uid, 10500);
                break;
            default:
                throw new InvalidJsonWebTokenException("invalid token type");
        }
    }

    public JsonWebToken(String token, PublicKey key) throws Exception {
        String[] parts = token.split(Pattern.quote("."));
        if (parts.length != 3) {
            System.out.println(parts.length);
            throw new InvalidJsonWebTokenException("invalid JWT format");
        }

        String header = new String(Base64.getDecoder().decode(parts[0].getBytes()));
        String payload = new String(Base64.getDecoder().decode(parts[1].getBytes()));

        byte[] signature = Base64.getDecoder().decode(parts[2].getBytes());
        byte[] unsignedToken = getUnsignedToken(header.getBytes(), payload.getBytes());

        if (!verify(key, unsignedToken, signature)) {
            throw new InvalidJsonWebTokenException("signature does not match");
        }

        JsonObject jsonPayload = new JsonParser().parse(payload).getAsJsonObject();
        iat = new Timestamp(jsonPayload.get("iat").getAsLong());
        exp = new Timestamp(jsonPayload.get("exp").getAsLong());
        uid = jsonPayload.get("sub").getAsInt();

        if (hasExpired()) {
            throw new InvalidJsonWebTokenException("expired token");
        }
    }

    private byte[] getUnsignedToken(byte[] header, byte[] payload) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(Base64.getEncoder().encode(header));
        bos.write(".".getBytes());
        bos.write(Base64.getEncoder().encode(payload));

        return bos.toByteArray();
    }

    private byte[] sign(PrivateKey key, byte[] unsignedToken) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");

        sig.initSign(key);
        sig.update(unsignedToken);

        return sig.sign();
    }

    private boolean verify(PublicKey key, byte[] unsignedToken, byte[] signature) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");

        sig.initVerify(key);
        sig.update(unsignedToken);

        return sig.verify(signature);
    }

    private void generateToken(PrivateKey key, int uid, int minutes) throws Exception {
        iat = generateIat();
        exp = generateExp(iat, Calendar.MINUTE, minutes);
        this.uid = uid;

        String header =  "{\"alg\": \"RSA\", \"typ\":\"JWT\" }";
        String payload = "{\"sub\":" + uid + ",\"iat\":" + iat.getTime() + ",\"exp\":" + exp.getTime() + "}";

        byte[] unsignedToken = getUnsignedToken(header.getBytes(), payload.getBytes());
        byte[] signature = sign(key, unsignedToken);

        token = Base64.getEncoder().encodeToString(header.getBytes()) + "." +
                Base64.getEncoder().encodeToString(payload.getBytes()) + "." +
                Base64.getEncoder().encodeToString(signature);
    }

    private Timestamp generateIat() {
        return new Timestamp(System.currentTimeMillis());
    }

    private Timestamp generateExp(Timestamp iat, int type, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(iat.getTime());
        calendar.add(type, amount);
        return new Timestamp(calendar.getTime().getTime());
    }

    public boolean hasExpired() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (exp.before(now)) {
            return true;
        }
        return false;
    }

    public String getToken() {
        return token;
    }

    public Timestamp getIat() {
        return iat;
    }

    public Timestamp getExp() {
        return exp;
    }

    public int getUid() {
        return uid;
    }
}
