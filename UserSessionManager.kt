import com.google.firebase.auth.FirebaseAuth
import android.content.Context


// Create a new file: UserSessionManager.kt
class UserSessionManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }
}