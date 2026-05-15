package com.kaushalya.karnataka.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.kaushalya.karnataka.data.model.*
import com.kaushalya.karnataka.data.repository.AuthRepository
import com.kaushalya.karnataka.data.repository.DummyDataRepository
import com.kaushalya.karnataka.data.repository.StorageRepository
import com.kaushalya.karnataka.data.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import java.util.UUID

// ─── Role Enum ────────────────────────────────────────────────────────────────

enum class AppRole { CUSTOMER, WORKER }

// ─── Auth ViewModel ───────────────────────────────────────────────────────────

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("kaushalya_prefs", Context.MODE_PRIVATE)

    private val authRepo = AuthRepository()

    private var userDocumentListenerJob: Job? = null

    var isLoggedIn by mutableStateOf(false)
        private set
    var currentUser by mutableStateOf<FirestoreUser?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var activeRole by mutableStateOf(AppRole.CUSTOMER)
        private set
    var hasWorkerProfile by mutableStateOf(false)
        private set

    /** False until cold-start Firebase Auth + optional Firestore restore has finished (Splash waits on this). */
    var sessionReady by mutableStateOf(false)
        private set

    // Local User model still used by screens that reference it
    val localUser: User?
        get() = currentUser?.let {
            User(
                id = it.uid,
                name = it.name,
                email = it.email,
                phone = it.phone,
                isWorker = it.role == "worker",
                profileImageUrl = it.profileImageUrl,
                address = it.address
            )
        }

    init {
        // Load session from local storage
        val savedUid = prefs.getString("uid", null)
        if (savedUid != null) {
            currentUser = FirestoreUser(
                uid = savedUid,
                name = prefs.getString("name", "") ?: "",
                email = prefs.getString("email", "") ?: "",
                phone = prefs.getString("phone", "") ?: "",
                role = prefs.getString("role", "customer") ?: "customer",
                hasWorkerProfile = prefs.getBoolean("hasWorkerProfile", false)
            )
            isLoggedIn = true
            hasWorkerProfile = currentUser?.hasWorkerProfile ?: false
            activeRole = if (prefs.getString("activeRole", "customer") == "worker") AppRole.WORKER else AppRole.CUSTOMER
        }
        
        sessionReady = true
    }

    private fun saveSession(user: FirestoreUser) {
        prefs.edit().apply {
            putString("uid", user.uid)
            putString("name", user.name)
            putString("email", user.email)
            putString("phone", user.phone)
            putString("role", user.role)
            putBoolean("hasWorkerProfile", user.hasWorkerProfile)
            putString("activeRole", if (activeRole == AppRole.WORKER) "worker" else "customer")
            apply()
        }
    }

    private fun clearSession() {
        prefs.edit().clear().apply()
    }

    private fun startUserDocumentListener(uid: String) {
        userDocumentListenerJob?.cancel()
        userDocumentListenerJob = viewModelScope.launch {
            runCatching {
                authRepo.observeUser(uid)
                    .catch {
                        // Permission/network snapshot errors must not clear session or crash the app.
                    }
                    .collect { doc ->
                        if (doc != null) {
                            val safe = if (doc.uid.isBlank()) doc.copy(uid = uid) else doc
                            currentUser = safe
                            hasWorkerProfile = safe.hasWorkerProfile
                            activeRole = if (safe.role == "worker") AppRole.WORKER else AppRole.CUSTOMER
                        }
                    }
            }
        }
    }

    private fun stopUserDocumentListener() {
        userDocumentListenerJob?.cancel()
        userDocumentListenerJob = null
    }

    override fun onCleared() {
        stopUserDocumentListener()
        super.onCleared()
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            delay(1000) // Mock delay
            val nameFromEmail = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            val user = FirestoreUser(
                uid = "dummy_user_123",
                name = nameFromEmail,
                email = email,
                phone = "+91 9999999999",
                role = "customer",
                hasWorkerProfile = true
            )
            currentUser = user
            isLoggedIn = true
            hasWorkerProfile = true
            saveSession(user)
            isLoading = false
            onSuccess()
        }
    }

    fun register(name: String, email: String, phone: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            delay(1000) // Mock delay
            val user = FirestoreUser(
                uid = "dummy_user_${System.currentTimeMillis()}",
                name = name,
                email = email,
                phone = phone,
                role = "customer",
                hasWorkerProfile = false
            )
            currentUser = user
            isLoggedIn = true
            hasWorkerProfile = false
            saveSession(user)
            isLoading = false
            onSuccess()
        }
    }

    fun selectRole(role: AppRole, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            activeRole = role
            currentUser?.let { saveSession(it) }
            delay(500)
            isLoading = false
            onSuccess()
        }
    }

    fun switchRole() {
        activeRole = if (activeRole == AppRole.CUSTOMER) AppRole.WORKER else AppRole.CUSTOMER
        currentUser?.let { saveSession(it) }
    }

    fun completeWorkerRegistration() {
        hasWorkerProfile = true
        activeRole = AppRole.WORKER
        currentUser = currentUser?.copy(hasWorkerProfile = true)
        currentUser?.let { saveSession(it) }
        val uid = currentUser?.uid?.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch {
            runCatching { authRepo.markHasWorkerProfile(uid, true) }
        }
    }

    fun logout() {
        isLoggedIn = false
        currentUser = null
        activeRole = AppRole.CUSTOMER
        hasWorkerProfile = false
        errorMessage = null
        clearSession()
    }

    fun clearError() { errorMessage = null }

    private fun friendlyAuthError(t: Throwable): String = when (t) {
        is FirebaseAuthInvalidCredentialsException ->
            "Incorrect email or password. Please try again."
        is FirebaseAuthInvalidUserException ->
            "No account found with this email."
        is FirebaseAuthUserCollisionException ->
            "This email is already registered."
        is FirebaseAuthWeakPasswordException ->
            "Password must be at least 6 characters."
        else -> friendlyAuthErrorFromMessage(t.message)
    }

    private fun friendlyAuthErrorFromMessage(msg: String?): String = when {
        msg == null -> "An unexpected error occurred"
        msg.contains("password", ignoreCase = true) ||
            msg.contains("wrong-password", ignoreCase = true) ->
            "Incorrect email or password. Please try again."
        msg.contains("no user", ignoreCase = true) ||
            msg.contains("user-not-found", ignoreCase = true) -> "No account found with this email."
        msg.contains("invalid-credential", ignoreCase = true) ->
            "Incorrect email or password. Please try again."
        msg.contains("email", ignoreCase = true) -> "Invalid email address."
        msg.contains("network", ignoreCase = true) -> "Network error. Check your internet connection."
        msg.contains("already in use", ignoreCase = true) -> "This email is already registered."
        msg.contains("weak-password", ignoreCase = true) -> "Password must be at least 6 characters."
        else -> msg
    }
}

// ─── My Worker Profile ViewModel ─────────────────────────────────────────────

class MyWorkerProfileViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()
    private val storageRepo = StorageRepository()
    private val authRepo = AuthRepository()

    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var category by mutableStateOf(WorkerCategory.ELECTRICIAN)
    var skillsText by mutableStateOf("")
    var experience by mutableStateOf("")
    var pricePerHour by mutableStateOf("")
    var location by mutableStateOf("")
    var bio by mutableStateOf("")
    var isAvailable by mutableStateOf(true)
    var profilePhotoUri by mutableStateOf<Uri?>(null)
    var workPhotoUris by mutableStateOf<List<Uri>>(emptyList())
    var profileImageUrl by mutableStateOf("")
    var workGalleryUrls by mutableStateOf<List<String>>(emptyList())

    var isEditMode by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var profileSaveSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var editName by mutableStateOf("")
    var editPhone by mutableStateOf("")
    var editSkillsText by mutableStateOf("")
    var editExperience by mutableStateOf("")
    var editPricePerHour by mutableStateOf("")
    var editLocation by mutableStateOf("")
    var editBio by mutableStateOf("")
    var editIsAvailable by mutableStateOf(true)
    var editCategory by mutableStateOf(WorkerCategory.ELECTRICIAN)

    val hasProfile get() = name.isNotBlank()

    private var profileObserveJob: Job? = null

    /** Realtime worker profile from `workers/{uid}`. */
    fun observeProfile(uid: String) {
        isLoading = false
        val worker = DummyDataRepository.workers.find { it.id == uid } ?: DummyDataRepository.workers.first()
        name = worker.name
        phone = worker.phone
        email = worker.email
        category = worker.category
        skillsText = worker.skills.joinToString(", ")
        experience = worker.experience.toString()
        pricePerHour = worker.pricePerHour.toString()
        location = worker.location
        bio = worker.bio
        isAvailable = worker.isAvailable
        profileImageUrl = worker.profileImageUrl
        workGalleryUrls = worker.workGalleryUrls
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun saveFromRegistration(
        uid: String,
        name: String, phone: String, email: String,
        category: WorkerCategory, skillsText: String,
        experience: String, pricePerHour: String,
        location: String, bio: String, isAvailable: Boolean,
        profilePhotoUri: Uri?, workPhotoUris: List<Uri>
    ) {
        this.name = name; this.phone = phone; this.email = email
        this.category = category; this.skillsText = skillsText
        this.experience = experience; this.pricePerHour = pricePerHour
        this.location = location; this.bio = bio; this.isAvailable = isAvailable
        
        viewModelScope.launch {
            isSaving = true
            delay(1000)
            isSaving = false
        }
    }

    fun startEdit() {
        editName = name; editPhone = phone; editSkillsText = skillsText
        editExperience = experience; editPricePerHour = pricePerHour
        editLocation = location; editBio = bio
        editIsAvailable = isAvailable; editCategory = category
        isEditMode = true
    }

    fun saveEdits(uid: String) {
        viewModelScope.launch {
            isSaving = true
            delay(1000)
            name = editName; phone = editPhone; skillsText = editSkillsText
            experience = editExperience; pricePerHour = editPricePerHour
            location = editLocation; bio = editBio
            isAvailable = editIsAvailable; category = editCategory
            isEditMode = false
            profileSaveSuccess = true
            delay(2000)
            profileSaveSuccess = false
            isSaving = false
        }
    }

    fun cancelEdit() { isEditMode = false }

    fun addWorkPhotos(uris: List<Uri>) {
        workPhotoUris = (workPhotoUris + uris).distinctBy { it.toString() }
    }

    fun updateProfilePhoto(uri: Uri) { profilePhotoUri = uri }
}

// ─── Home ViewModel ───────────────────────────────────────────────────────────

class HomeViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()
    private var workersObserveJob: Job? = null

    private fun computeFeatured(all: List<Worker>): List<Worker> =
        all.filter { it.rating >= 4.5f }
            .ifEmpty { all.take(minOf(5, all.size)) }

    private var allWorkersCache by mutableStateOf(DummyDataRepository.workers)

    var featuredWorkers by mutableStateOf(computeFeatured(DummyDataRepository.workers))
        private set
    var nearbyWorkers by mutableStateOf(DummyDataRepository.workers)
        private set
    var searchQuery by mutableStateOf("")
        private set
    var searchResults by mutableStateOf<List<Worker>>(emptyList())
        private set
    var promoBanners by mutableStateOf(defaultHomePromoBanners)
        private set
    var isSearchActive by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(true)
        private set

    init {
        isLoading = false
        val effective = DummyDataRepository.workers
        allWorkersCache = effective
        featuredWorkers = computeFeatured(effective)
        nearbyWorkers = effective
    }

    private fun filterWorkersBySearch(all: List<Worker>, query: String): List<Worker> =
        all.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.category.displayName.contains(query, ignoreCase = true) ||
                it.location.contains(query, ignoreCase = true) ||
                it.skills.any { s -> s.contains(query, ignoreCase = true) }
        }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        isSearchActive = query.isNotBlank()
        searchResults = if (query.isBlank()) emptyList()
        else filterWorkersBySearch(allWorkersCache, query)
    }

    fun clearSearch() {
        searchQuery = ""; isSearchActive = false; searchResults = emptyList()
    }

    fun refresh() { /* Realtime listener keeps data fresh; no-op for UI compatibility */ }

    override fun onCleared() {
        workersObserveJob?.cancel()
        super.onCleared()
    }
}

// ─── Category ViewModel ───────────────────────────────────────────────────────

class CategoryViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()
    private var categoryObserveJob: Job? = null

    var workers by mutableStateOf<List<Worker>>(emptyList())
        private set
    var selectedCategory by mutableStateOf<WorkerCategory?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun loadCategory(categoryName: String) {
        isLoading = false
        selectedCategory = if (categoryName == "ALL") {
            null
        } else {
            WorkerCategory.entries.find { it.name.equals(categoryName, ignoreCase = true) }
        }
        val sourceList = DummyDataRepository.workers
        val cat = selectedCategory
        workers = if (cat != null) sourceList.filter { w -> w.category == cat } else sourceList
    }

    override fun onCleared() {
        categoryObserveJob?.cancel()
        super.onCleared()
    }
}

// ─── Worker Profile ViewModel ─────────────────────────────────────────────────

class WorkerProfileViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()
    private var workerObserveJob: Job? = null

    var worker by mutableStateOf<Worker?>(null)
        private set
    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun loadWorker(workerId: String) {
        isLoading = false
        val dummy = DummyDataRepository.workers.find { it.id == workerId }
        if (dummy != null) {
            worker = dummy
            reviews = dummy.reviews
        } else {
            worker = null
            reviews = emptyList()
        }
    }

    override fun onCleared() {
        workerObserveJob?.cancel()
        super.onCleared()
    }
}

// ─── Booking ViewModel ────────────────────────────────────────────────────────

class BookingViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()

    var selectedDate by mutableStateOf("")
    var selectedTime by mutableStateOf("")
    var address by mutableStateOf("")
    var notes by mutableStateOf("")
    var isConfirmed by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    val availableTimes = listOf(
        "9:00 AM","10:00 AM","11:00 AM","12:00 PM",
        "1:00 PM","2:00 PM","3:00 PM","4:00 PM","5:00 PM"
    )

    fun confirmBooking(
        workerId: String,
        workerName: String,
        workerCategory: WorkerCategory,
        customerId: String,
        customerName: String,
        pricePerHour: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            delay(1000) // Mock delay
            isConfirmed = true
            isLoading = false
            onSuccess()
        }
    }

    fun reset() {
        selectedDate = ""; selectedTime = ""; address = ""
        notes = ""; isConfirmed = false; errorMessage = null
    }

    fun clearBookingError() {
        errorMessage = null
    }
}

// ─── Booking History ViewModel ────────────────────────────────────────────────

class BookingHistoryViewModel : ViewModel() {
    private val workerRepo = WorkerRepository()
    private var bookingsObserveJob: Job? = null

    var upcomingBookings by mutableStateOf<List<Booking>>(emptyList())
        private set
    var completedBookings by mutableStateOf<List<Booking>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun loadBookings(userId: String, isWorker: Boolean) {
        isLoading = false
        val all = if (isWorker) DummyDataRepository.workerBookingRequests else DummyDataRepository.customerBookings
        
        upcomingBookings = all.filter {
            it.status == BookingStatus.CONFIRMED || 
            it.status == BookingStatus.PENDING ||
            it.status == BookingStatus.ACCEPTED
        }
        completedBookings = all.filter {
            it.status == BookingStatus.COMPLETED || 
            it.status == BookingStatus.CANCELLED ||
            it.status == BookingStatus.REJECTED
        }
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            runCatching { workerRepo.updateBookingStatus(bookingId, status.name) }
        }
    }

    override fun onCleared() {
        bookingsObserveJob?.cancel()
        super.onCleared()
    }
}

// ─── Worker Dashboard ViewModel ───────────────────────────────────────────────

class WorkerDashboardViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()
    private val storageRepo = StorageRepository()
    private val authRepo = AuthRepository()
    private var dashboardObserveJob: Job? = null

    var dashboardUid by mutableStateOf("")
        private set

    var workerProfile by mutableStateOf<FirestoreWorker?>(null)
        private set
    var bookingRequests by mutableStateOf<List<Booking>>(emptyList())
        private set
    var upcomingBookings by mutableStateOf<List<Booking>>(emptyList())
        private set
    var totalEarnings by mutableStateOf(0)
        private set
    var totalJobs by mutableStateOf(0)
        private set
    var earnings by mutableStateOf<List<Earning>>(emptyList())
        private set
    var recentPayments by mutableStateOf<List<Booking>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var profileSaved by mutableStateOf(false)
        private set
    var profileSaveError by mutableStateOf<String?>(null)
        private set

    private fun shellWorker(uid: String): Worker {
        val safeId = uid.ifBlank { "me" }
        return Worker(
            id = safeId,
            name = "",
            category = WorkerCategory.ELECTRICIAN,
            rating = 0f,
            reviewCount = 0,
            experience = 0,
            pricePerHour = 0,
            location = "",
            phone = "",
            isAvailable = true,
            profileImageUrl = "",
            bio = "",
            skills = emptyList(),
            completedJobs = 0,
            reviews = emptyList(),
            workGalleryUrls = emptyList()
        )
    }

    // Edit fields (kept compatible with WorkerDashboardScreen)
    val baseWorker get() = workerProfile?.toWorker() ?: shellWorker(dashboardUid)
    var editName by mutableStateOf("")
    var editSkill by mutableStateOf("")
    var editExperience by mutableStateOf("")
    var editPricePerHour by mutableStateOf("")
    var editBio by mutableStateOf("")
    var editPhone by mutableStateOf("")
    var editLocation by mutableStateOf("")
    var editAvailable by mutableStateOf(true)
    var pendingProfilePhotoUri by mutableStateOf<Uri?>(null)
    val currentWorker get() = baseWorker

    fun loadDashboard(uid: String) {
        dashboardUid = uid
        isLoading = false
        
        // Use dummy profile
        val dummyWorker = DummyDataRepository.workers.find { it.id == uid } ?: DummyDataRepository.workers.first()
        editName = dummyWorker.name
        editBio = dummyWorker.bio
        editPhone = dummyWorker.phone
        editLocation = dummyWorker.location
        editExperience = dummyWorker.experience.toString()
        editPricePerHour = dummyWorker.pricePerHour.toString()
        editAvailable = dummyWorker.isAvailable
        
        val all = DummyDataRepository.workerBookingRequests
        bookingRequests = all.filter { it.status == BookingStatus.PENDING }
        upcomingBookings = all.filter { 
            it.status == BookingStatus.CONFIRMED || it.status == BookingStatus.ACCEPTED 
        }
        totalJobs = 24
        totalEarnings = 12500
        earnings = DummyDataRepository.earnings
        recentPayments = DummyDataRepository.recentPayments
    }

    fun saveProfile(uid: String) {
        viewModelScope.launch {
            isLoading = true
            delay(1000)
            profileSaved = true
            isLoading = false
            delay(2000)
            profileSaved = false
        }
    }

    fun saveProfile() {
        val uid = dashboardUid
        if (uid.isNotBlank()) saveProfile(uid)
    }

    fun clearProfileSaveError() {
        profileSaveError = null
    }

    fun acceptBooking(bookingId: String) {
        bookingRequests = bookingRequests.filter { it.id != bookingId }
    }

    fun rejectBooking(bookingId: String) {
        bookingRequests = bookingRequests.filter { it.id != bookingId }
    }

    override fun onCleared() {
        dashboardObserveJob?.cancel()
        super.onCleared()
    }
}

// ─── Notifications ViewModel ──────────────────────────────────────────────────

class NotificationsViewModel : ViewModel() {

    private val workerRepo = WorkerRepository()

    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    val unreadCount get() = notifications.count { !it.isRead }

    fun loadNotifications(uid: String, isWorker: Boolean = false) {
        isLoading = false
        notifications = if (isWorker) DummyDataRepository.workerNotifications else DummyDataRepository.customerNotifications
    }

    fun markAsRead(id: String, uid: String? = null) {
        notifications = notifications.map { if (it.id == id) it.copy(isRead = true) else it }
        if (uid != null) {
            viewModelScope.launch { runCatching { workerRepo.markNotificationRead(uid, id) } }
        }
    }

    fun markAllRead(uid: String? = null) {
        notifications = notifications.map { it.copy(isRead = true) }
        if (uid != null) {
            viewModelScope.launch { runCatching { workerRepo.markAllNotificationsRead(uid) } }
        }
    }
}

// ─── Settings ViewModel ───────────────────────────────────────────────────────

class SettingsViewModel : ViewModel() {
    var isDarkMode by mutableStateOf(false)
    var selectedLanguage by mutableStateOf("English")
    val languages = listOf("English", "Kannada", "Hindi", "Telugu", "Tamil")

    fun toggleDarkMode() { isDarkMode = !isDarkMode }
    fun setLanguage(lang: String) { selectedLanguage = lang }
}
