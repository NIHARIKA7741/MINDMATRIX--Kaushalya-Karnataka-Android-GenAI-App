package com.kaushalya.karnataka.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kaushalya.karnataka.data.model.FirestoreUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository — wraps Firebase Auth + user document creation in Firestore.
 *
 * All functions are suspend and throw exceptions on failure.
 * The ViewModel is responsible for catching and mapping them to UI state.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ── Current Firebase user ────────────────────────────────────────────────

    val currentFirebaseUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Runs [callback] once after Firebase Auth has delivered its initial persisted session state.
     * (Avoids treating a temporarily-null [currentUser] as "signed out" during cold start.)
     */
    fun runWhenAuthStateReady(callback: (FirebaseAuth) -> Unit) {
        val listener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                auth.removeAuthStateListener(this)
                callback(firebaseAuth)
            }
        }
        auth.addAuthStateListener(listener)
    }

    // ── Login ────────────────────────────────────────────────────────────────

    /**
     * Signs in with email + password.
     * @return the Firestore user document after successful login.
     * @throws Exception on auth failure.
     */
    suspend fun login(email: String, password: String): FirestoreUser {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val uid = result.user?.uid ?: throw Exception("Login failed: no UID returned")
        return fetchUserDocument(uid)
    }

    // ── Register ─────────────────────────────────────────────────────────────

    /**
     * Creates a new Firebase Auth account + creates the Firestore user document.
     * @return the newly created FirestoreUser.
     * @throws Exception on failure.
     */
    suspend fun register(name: String, email: String, phone: String, password: String): FirestoreUser {
        // 1. Create Firebase Auth user
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid = result.user?.uid ?: throw Exception("Registration failed: no UID returned")

        // 2. Create Firestore user document (role defaults to "customer")
        val firestoreUser = FirestoreUser(
            uid = uid,
            name = name.trim(),
            email = email.trim(),
            phone = phone.trim(),
            role = "customer",
            hasWorkerProfile = false,
            createdAt = Timestamp.now()
        )
        db.collection("users").document(uid).set(firestoreUser.toMap()).await()

        return firestoreUser
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    fun logout() {
        auth.signOut()
    }

    // ── Fetch user document from Firestore ───────────────────────────────────

    /**
     * Fetches user document from Firestore. Creates a default doc if missing
     * (handles edge case where Firestore write failed during registration).
     */
    private fun DocumentSnapshot.toFirestoreUserEnsuringUid(expectedUid: String): FirestoreUser {
        val parsed = toObject(FirestoreUser::class.java)
            ?: throw Exception("Failed to parse user document")
        return if (parsed.uid.isBlank()) parsed.copy(uid = expectedUid) else parsed
    }

    suspend fun fetchUserDocument(uid: String): FirestoreUser {
        val snapshot = db.collection("users").document(uid).get().await()
        return if (snapshot.exists()) {
            snapshot.toFirestoreUserEnsuringUid(uid)
        } else {
            // If document is missing, create a minimal one from Firebase Auth data
            val firebaseUser = auth.currentUser
                ?: throw Exception("No authenticated user found")
            val fallback = FirestoreUser(
                uid = uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                phone = "",
                role = "customer",
                hasWorkerProfile = false,
                createdAt = Timestamp.now()
            )
            db.collection("users").document(uid).set(fallback.toMap()).await()
            fallback
        }
    }

    // ── Update role in Firestore ─────────────────────────────────────────────

    suspend fun updateUserRole(uid: String, role: String) {
        val ref = db.collection("users").document(uid)
        runCatching { ref.update("role", role).await() }
            .recoverCatching {
                ref.set(mapOf("role" to role, "uid" to uid), SetOptions.merge()).await()
            }
            .getOrThrow()
    }

    // ── Mark user as having a worker profile ────────────────────────────────

    suspend fun markHasWorkerProfile(uid: String, hasProfile: Boolean) {
        val ref = db.collection("users").document(uid)
        runCatching { ref.update("hasWorkerProfile", hasProfile).await() }
            .recoverCatching {
                ref.set(mapOf("hasWorkerProfile" to hasProfile, "uid" to uid), SetOptions.merge()).await()
            }
            .getOrThrow()
    }

    // ── Update profile image URL ─────────────────────────────────────────────

    suspend fun updateProfileImageUrl(uid: String, url: String) {
        val ref = db.collection("users").document(uid)
        val payload = mapOf("profileImageUrl" to url, "updatedAt" to Timestamp.now(), "uid" to uid)
        runCatching { ref.update("profileImageUrl", url, "updatedAt", Timestamp.now()).await() }
            .recoverCatching { ref.set(payload, SetOptions.merge()).await() }
            .getOrThrow()
    }

    /** Realtime listener for `users/{uid}` (logged-in profile). */
    fun observeUser(uid: String): Flow<FirestoreUser?> = callbackFlow {
        val reg = db.collection("users").document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    // Do not close the flow; a later snapshot may succeed (network/rules recovery).
                    return@addSnapshotListener
                }
                if (snap == null || !snap.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val raw = snap.toObject(FirestoreUser::class.java)
                val user = raw?.let { u ->
                    if (u.uid.isBlank()) u.copy(uid = snap.id) else u
                }
                trySend(user)
            }
        awaitClose { reg.remove() }
    }
}
