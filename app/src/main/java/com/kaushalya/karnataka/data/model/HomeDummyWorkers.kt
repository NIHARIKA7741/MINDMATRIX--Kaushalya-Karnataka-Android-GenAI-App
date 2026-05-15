package com.kaushalya.karnataka.data.model

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Temporary hardcoded workers when Firestore `workers` is empty, has no
 * documents, or the list is still loading. Replaced as soon as Firestore
 * returns at least one worker.
 */
object HomeDummyWorkers {

    private fun avatarUrl(name: String): String {
        val enc = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
        return "https://ui-avatars.com/api/?name=$enc&size=256&background=1B7A3E&color=fff"
    }

    private fun worker(
        id: String,
        name: String,
        category: WorkerCategory,
        rating: Float,
        reviewCount: Int,
        experience: Int,
        pricePerHour: Int,
        location: String,
        phone: String,
        isAvailable: Boolean,
        skills: List<String>,
        bio: String,
        completedJobs: Int
    ): Worker = Worker(
        id = id,
        name = name,
        category = category,
        rating = rating,
        reviewCount = reviewCount,
        experience = experience,
        pricePerHour = pricePerHour,
        location = location,
        phone = phone,
        isAvailable = isAvailable,
        profileImageUrl = avatarUrl(name),
        bio = bio,
        skills = skills,
        completedJobs = completedJobs,
        reviews = emptyList(),
        workGalleryUrls = emptyList()
    )

    /** Exactly 10 fallback workers for home lists. */
    val workers: List<Worker> = listOf(
        worker(
            id = "dummy_ravi_kumar",
            name = "Ravi Kumar",
            category = WorkerCategory.ELECTRICIAN,
            rating = 4.8f,
            reviewCount = 42,
            experience = 8,
            pricePerHour = 450,
            location = "Indiranagar, Bengaluru",
            phone = "+91 98765 43210",
            isAvailable = true,
            skills = listOf("House wiring", "MCB panels"),
            bio = "Licensed electrician for homes.",
            completedJobs = 120
        ),
        worker(
            id = "dummy_naveen_kumar",
            name = "Naveen Kumar",
            category = WorkerCategory.PLUMBER,
            rating = 4.6f,
            reviewCount = 31,
            experience = 6,
            pricePerHour = 400,
            location = "Jayanagar, Bengaluru",
            phone = "+91 98765 43211",
            isAvailable = true,
            skills = listOf("Leak repair", "Bathroom fittings"),
            bio = "Same-day residential plumbing.",
            completedJobs = 89
        ),
        worker(
            id = "dummy_suresh_gowda",
            name = "Suresh Gowda",
            category = WorkerCategory.CARPENTER,
            rating = 4.9f,
            reviewCount = 56,
            experience = 12,
            pricePerHour = 500,
            location = "Mysuru",
            phone = "+91 98765 43212",
            isAvailable = true,
            skills = listOf("Modular kitchen", "Doors"),
            bio = "Woodwork and furniture repair.",
            completedJobs = 210
        ),
        worker(
            id = "dummy_arjun_patel",
            name = "Arjun Patel",
            category = WorkerCategory.PAINTER,
            rating = 4.5f,
            reviewCount = 24,
            experience = 5,
            pricePerHour = 350,
            location = "Whitefield, Bengaluru",
            phone = "+91 98765 43213",
            isAvailable = true,
            skills = listOf("Interior emulsion", "Texture"),
            bio = "Clean finishing, on-time handover.",
            completedJobs = 67
        ),
        worker(
            id = "dummy_imran_shaik",
            name = "Imran Shaik",
            category = WorkerCategory.ELECTRICIAN,
            rating = 4.7f,
            reviewCount = 38,
            experience = 7,
            pricePerHour = 420,
            location = "Koramangala, Bengaluru",
            phone = "+91 98765 43214",
            isAvailable = true,
            skills = listOf("Industrial wiring", "LED retrofit"),
            bio = "Commercial electrical maintenance.",
            completedJobs = 95
        ),
        worker(
            id = "dummy_kiran_reddy",
            name = "Kiran Reddy",
            category = WorkerCategory.PLUMBER,
            rating = 4.4f,
            reviewCount = 22,
            experience = 5,
            pricePerHour = 380,
            location = "HSR Layout, Bengaluru",
            phone = "+91 98765 43215",
            isAvailable = true,
            skills = listOf("CPVC", "Drain cleaning"),
            bio = "Apartment plumbing specialist.",
            completedJobs = 61
        ),
        worker(
            id = "dummy_manoj_singh",
            name = "Manoj Singh",
            category = WorkerCategory.CARPENTER,
            rating = 4.6f,
            reviewCount = 28,
            experience = 9,
            pricePerHour = 470,
            location = "Hubballi",
            phone = "+91 98765 43216",
            isAvailable = true,
            skills = listOf("Partitions", "Custom shelves"),
            bio = "Site measurements and woodwork.",
            completedJobs = 98
        ),
        worker(
            id = "dummy_akash_jain",
            name = "Akash Jain",
            category = WorkerCategory.PAINTER,
            rating = 4.5f,
            reviewCount = 20,
            experience = 4,
            pricePerHour = 340,
            location = "Rajajinagar, Bengaluru",
            phone = "+91 98765 43217",
            isAvailable = false,
            skills = listOf("Enamel", "Putty"),
            bio = "Interior and exterior packages.",
            completedJobs = 55
        ),
        worker(
            id = "dummy_ramesh_babu",
            name = "Ramesh Babu",
            category = WorkerCategory.ELECTRICIAN,
            rating = 4.9f,
            reviewCount = 51,
            experience = 10,
            pricePerHour = 460,
            location = "Electronic City, Bengaluru",
            phone = "+91 98765 43218",
            isAvailable = true,
            skills = listOf("Earthing", "Appliance install"),
            bio = "Residential and small office work.",
            completedJobs = 142
        ),
        worker(
            id = "dummy_salman_khan",
            name = "Salman Khan",
            category = WorkerCategory.PLUMBER,
            rating = 4.3f,
            reviewCount = 18,
            experience = 4,
            pricePerHour = 360,
            location = "Mangaluru",
            phone = "+91 98765 43219",
            isAvailable = true,
            skills = listOf("Water tanks", "Motor pumps"),
            bio = "Coastal plumbing repairs.",
            completedJobs = 48
        )
    )
}
