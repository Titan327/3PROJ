package sample_test_app.com.http.Security.JwtUtils
// JwtUtils.kt
import android.util.Base64

object JwtUtils {
    fun decodeJWT(jwt: String): String {
        val parts = jwt.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token")
        }

        val decodedBytes = Base64.decode(parts[1], Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }
}
