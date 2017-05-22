package crypto

// import com.sun.org.apache.xml.internal.security.utils.Base64
import java.security.*
import java.security.PrivateKey
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

fun toString(key: Key): String {
    return Base64.getEncoder().encodeToString(key.encoded);
}

fun toPrivateKey(key: String): PrivateKey {
    val keyBytes = Base64.getDecoder().decode(key.toByteArray(Charsets.UTF_8))
    val keySpec = PKCS8EncodedKeySpec(keyBytes)
    val fact = KeyFactory.getInstance("RSA")
    return fact.generatePrivate(keySpec)
}

fun toPublicKey(key: String): PublicKey {
    val keyBytes = Base64.getDecoder().decode(key.toByteArray(Charsets.UTF_8))
    val spec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(spec)
}

fun generateKeyPair(): KeyPair {
    val keyGen = KeyPairGenerator.getInstance("RSA")
    val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
    keyGen.initialize(2048, random)

    return keyGen.generateKeyPair()
}


