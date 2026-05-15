package com.kaushalya.karnataka.data.repository

import com.kaushalya.karnataka.data.model.*

object DummyDataRepository {

    val workers = listOf(
        Worker(
            id = "w1",
            name = "Ravi Kumar",
            category = WorkerCategory.ELECTRICIAN,
            rating = 4.9f,
            reviewCount = 312,
            experience = 10,
            pricePerHour = 400,
            location = "JP Nagar, Bangalore",
            phone = "+91 9845012345",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=11",
            bio = "Certified electrician with a decade of experience in residential and commercial wiring, MCB replacements, and inverter setups.",
            skills = listOf("Wiring", "MCB Repair", "Inverter Setup", "CCTV Installation"),
            completedJobs = 1450,
            reviews = listOf(
                Review("r1", "Sanjay Patil", 5.0f, "Very fast and clean work. Highly recommended.", "12 May 2026", "https://i.pravatar.cc/50?img=1"),
                Review("r1_2", "Meena Iyer", 4.8f, "Ravi was very professional. He fixed our inverter problem quickly.", "05 May 2026", "https://i.pravatar.cc/50?img=2"),
                Review("r1_3", "Arun Varma", 5.0f, "Excellent service! Best electrician in JP Nagar.", "28 Apr 2026", "https://i.pravatar.cc/50?img=3")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500&auto=format",
                "https://images.unsplash.com/photo-1558211583-d26f610c1eb1?w=500&auto=format",
                "https://images.unsplash.com/photo-1544724569-5f546fd6f2b5?w=500&auto=format"
            )
        ),
        Worker(
            id = "w2",
            name = "Naveen Kumar",
            category = WorkerCategory.ELECTRICIAN,
            rating = 4.8f,
            reviewCount = 189,
            experience = 8,
            pricePerHour = 350,
            location = "Indiranagar, Bangalore",
            phone = "+91 9876543210",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=12",
            bio = "Expert electrician serving Indiranagar. Specializing in leak detection and wiring.",
            skills = listOf("Wiring", "Repair", "Installation"),
            completedJobs = 820,
            reviews = listOf(
                Review("r2_1", "Priya Das", 4.5f, "Great work on the lighting installation.", "10 May 2026", "https://i.pravatar.cc/50?img=5"),
                Review("r2_2", "Karthik S", 5.0f, "Very punctual and knowledgeable.", "02 May 2026", "https://i.pravatar.cc/50?img=6")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1555529669-e69e730f162b?w=500&auto=format",
                "https://images.unsplash.com/photo-1590644365607-1c5a519a9a37?w=500&auto=format"
            )
        ),
        Worker(
            id = "w3",
            name = "Suresh Gowda",
            category = WorkerCategory.CARPENTER,
            rating = 4.7f,
            reviewCount = 256,
            experience = 15,
            pricePerHour = 500,
            location = "Whitefield, Bangalore",
            phone = "+91 9900112233",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=13",
            bio = "Master carpenter with 15 years of woodworking experience.",
            skills = listOf("Modular Kitchen", "Wardrobes", "Door Fitting"),
            completedJobs = 1120,
            reviews = listOf(
                Review("r3_1", "Lokesh M", 4.9f, "The modular kitchen design is fantastic!", "15 May 2026", "https://i.pravatar.cc/50?img=8"),
                Review("r3_2", "Deepa Rani", 4.5f, "Beautiful wardrobe finish. A bit expensive but worth it.", "08 May 2026", "https://i.pravatar.cc/50?img=9")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=500&auto=format",
                "https://images.unsplash.com/photo-1505691938895-1758d7eaa511?w=500&auto=format",
                "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=500&auto=format"
            )
        ),
        Worker(
            id = "w4",
            name = "Arjun Patel",
            category = WorkerCategory.PLUMBER,
            rating = 4.6f,
            reviewCount = 95,
            experience = 5,
            pricePerHour = 300,
            location = "HSR Layout, Bangalore",
            phone = "+91 9844223344",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=14",
            bio = "Professional plumber offering pipe fitting and leak detection.",
            skills = listOf("Pipe Fitting", "Leak Detection", "Taps"),
            completedJobs = 450,
            reviews = listOf(
                Review("r4_1", "Vikram Singh", 4.7f, "Fixed the leak in no time. Good behavior.", "12 May 2026", "https://i.pravatar.cc/50?img=10"),
                Review("r4_2", "Sneha Rao", 4.5f, "Reliable service for bathroom repairs.", "01 May 2026", "https://i.pravatar.cc/50?img=11")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500&auto=format",
                "https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?w=500&auto=format"
            )
        ),
        Worker(
            id = "w5",
            name = "Imran Shaik",
            category = WorkerCategory.CARPENTER,
            rating = 4.5f,
            reviewCount = 110,
            experience = 7,
            pricePerHour = 450,
            location = "Koramangala, Bangalore",
            phone = "+91 9988776655",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=15",
            bio = "Skilled carpenter for all furniture repairs and new fittings.",
            skills = listOf("Furniture Repair", "Polishing", "Assembly"),
            completedJobs = 600,
            reviews = listOf(
                Review("r5_1", "Zeeshan A", 4.8f, "Great polishing work on our dining table.", "14 May 2026", "https://i.pravatar.cc/50?img=15"),
                Review("r5_2", "Anita Nair", 4.2f, "Good work, but arrived 30 mins late.", "03 May 2026", "https://i.pravatar.cc/50?img=16")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1618220179428-22790b461013?w=500&auto=format",
                "https://images.unsplash.com/photo-1538688525198-9b88f6f5012a?w=500&auto=format"
            )
        ),
        Worker(
            id = "w6",
            name = "Kiran Reddy",
            category = WorkerCategory.PAINTER,
            rating = 4.4f,
            reviewCount = 82,
            experience = 6,
            pricePerHour = 300,
            location = "BTM Layout, Bangalore",
            phone = "+91 9741234567",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=16",
            bio = "Interior and exterior painting specialist.",
            skills = listOf("Wall Painting", "Stencils", "Texture"),
            completedJobs = 340,
            reviews = listOf(
                Review("r6_1", "Manjunath K", 4.5f, "Transformed our living room with texture paint.", "18 May 2026", "https://i.pravatar.cc/50?img=20"),
                Review("r6_2", "Saritha V", 4.3f, "Clean work, minimal mess.", "09 May 2026", "https://i.pravatar.cc/50?img=21")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500&auto=format",
                "https://images.unsplash.com/photo-1562591176-329309ecac5f?w=500&auto=format"
            )
        ),
        Worker(
            id = "w7",
            name = "Amit Singh",
            category = WorkerCategory.MECHANIC,
            rating = 4.8f,
            reviewCount = 145,
            experience = 12,
            pricePerHour = 600,
            location = "Hebbal, Bangalore",
            phone = "+91 9822334455",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=33",
            bio = "Expert mechanic for luxury cars and regular maintenance.",
            skills = listOf("Engine Tuning", "Brake Service", "AC Repair"),
            completedJobs = 900,
            reviews = listOf(
                Review("r7_1", "Sahil K", 5.0f, "Best mechanic in Hebbal. Very honest.", "20 May 2026", "https://i.pravatar.cc/50?img=30"),
                Review("r7_2", "Ritu M", 4.7f, "Fixed my car's AC perfectly.", "15 May 2026", "https://i.pravatar.cc/50?img=31")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=500&auto=format",
                "https://images.unsplash.com/photo-1487754180451-c456f719c141?w=500&auto=format"
            )
        ),
        Worker(
            id = "w8",
            name = "Rajesh Kumar",
            category = WorkerCategory.PLUMBER,
            rating = 4.6f,
            reviewCount = 67,
            experience = 4,
            pricePerHour = 250,
            location = "Marathahalli, Bangalore",
            phone = "+91 9733445566",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=44",
            bio = "Quick and reliable plumbing services for households.",
            skills = listOf("Leakage Fix", "Tap Install", "Drain Cleaning"),
            completedJobs = 210,
            reviews = listOf(
                Review("r8_1", "Nikhil T", 4.5f, "Came on short notice and fixed the pipe.", "22 May 2026", "https://i.pravatar.cc/50?img=40")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=500&auto=format",
                "https://images.unsplash.com/photo-1542013936693-884638332954?w=500&auto=format"
            )
        ),
        Worker(
            id = "w9",
            name = "Vijay Patil",
            category = WorkerCategory.ELECTRICIAN,
            rating = 4.7f,
            reviewCount = 112,
            experience = 9,
            pricePerHour = 450,
            location = "Banashankari, Bangalore",
            phone = "+91 9122334455",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=55",
            bio = "Industrial and residential electrician with high safety standards.",
            skills = listOf("Panel Wiring", "Solar Setup", "UPS Repair"),
            completedJobs = 650,
            reviews = listOf(
                Review("r9_1", "Suresh P", 4.9f, "Very professional solar installation.", "19 May 2026", "https://i.pravatar.cc/50?img=50")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500&auto=format",
                "https://images.unsplash.com/photo-1558211583-d26f610c1eb1?w=500&auto=format"
            )
        ),
        Worker(
            id = "w10",
            name = "Sonal Sharma",
            category = WorkerCategory.PAINTER,
            rating = 4.9f,
            reviewCount = 203,
            experience = 8,
            pricePerHour = 400,
            location = "Malleshwaram, Bangalore",
            phone = "+91 9655443322",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=45",
            bio = "Specialist in decorative paints and luxury wall finishes.",
            skills = listOf("Royale Play", "Metallic Finish", "Wall Art"),
            completedJobs = 480,
            reviews = listOf(
                Review("r10_1", "Kavya R", 5.0f, "Her wall art is simply amazing!", "25 May 2026", "https://i.pravatar.cc/50?img=45")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1562591176-329309ecac5f?w=500&auto=format",
                "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=500&auto=format"
            )
        ),
        Worker(
            id = "w11",
            name = "Raghu Ram",
            category = WorkerCategory.CARPENTER,
            rating = 4.3f,
            reviewCount = 56,
            experience = 10,
            pricePerHour = 550,
            location = "Yelahanka, Bangalore",
            phone = "+91 9344556677",
            isAvailable = true,
            profileImageUrl = "https://i.pravatar.cc/150?img=59",
            bio = "Furniture restoration and vintage woodwork specialist.",
            skills = listOf("Restoration", "Antique Repair", "Varnish"),
            completedJobs = 290,
            reviews = listOf(
                Review("r11_1", "Prakash J", 4.5f, "Restored my grandfather's chair beautifully.", "24 May 2026", "https://i.pravatar.cc/50?img=59")
            ),
            workGalleryUrls = listOf(
                "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=500&auto=format",
                "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=500&auto=format"
            )
        )
    )

    val customerBookings = listOf(
        Booking(
            id = "db1",
            workerId = "w1",
            workerName = "Ravi Kumar",
            workerCategory = WorkerCategory.ELECTRICIAN,
            customerName = "Rahul Sharma",
            date = "06 May 2026",
            time = "10:00 AM",
            status = BookingStatus.ACCEPTED,
            address = "JP Nagar, Bangalore",
            totalAmount = 800,
            notes = "Electrician Service"
        ),
        Booking(
            id = "db2",
            workerId = "w2",
            workerName = "Naveen Kumar",
            workerCategory = WorkerCategory.ELECTRICIAN,
            customerName = "Rahul Sharma",
            date = "08 May 2026",
            time = "02:00 PM",
            status = BookingStatus.PENDING,
            address = "Indiranagar, Bangalore",
            totalAmount = 700,
            notes = "Plumbing Repair"
        ),
        Booking(
            id = "db3",
            workerId = "w3",
            workerName = "Suresh Gowda",
            workerCategory = WorkerCategory.CARPENTER,
            customerName = "Rahul Sharma",
            date = "10 May 2026",
            time = "11:30 AM",
            status = BookingStatus.COMPLETED,
            address = "Whitefield, Bangalore",
            totalAmount = 1500,
            notes = "Carpenter Visit"
        )
    )

    val workerBookingRequests = listOf(
        Booking(
            id = "wr1",
            workerId = "me",
            workerName = "Myself",
            workerCategory = WorkerCategory.ELECTRICIAN,
            customerName = "Ravi Kumar",
            date = "06 May 2026",
            time = "10:00 AM",
            status = BookingStatus.PENDING,
            address = "JP Nagar, Bangalore",
            totalAmount = 600,
            notes = "Electrician Repair"
        ),
        Booking(
            id = "wr2",
            workerId = "me",
            workerName = "Myself",
            workerCategory = WorkerCategory.PLUMBER,
            customerName = "Arjun Patel",
            date = "07 May 2026",
            time = "01:00 PM",
            status = BookingStatus.PENDING,
            address = "HSR Layout, Bangalore",
            totalAmount = 900,
            notes = "Plumbing Work"
        )
    )

    val customerNotifications = listOf(
        Notification(
            id = "cn1",
            title = "Booking Confirmed ✅",
            message = "Your booking with Naveen Kumar (Electrician) on 15 June 2025 has been confirmed.",
            time = "10 mins ago",
            type = NotificationType.BOOKING_UPDATE,
            isRead = false
        ),
        Notification(
            id = "cn2",
            title = "Worker Accepted Your Request 🤝",
            message = "Suresh Gowda (Plumber) has accepted your service request.",
            time = "1 hour ago",
            type = NotificationType.BOOKING_UPDATE,
            isRead = false
        ),
        Notification(
            id = "cn3",
            title = "Worker Arriving Soon 🚗",
            message = "Your worker Manjunath is on his way and will arrive in 15 mins.",
            time = "2 hours ago",
            type = NotificationType.ALERT,
            isRead = true
        ),
        Notification(
            id = "cn4",
            title = "Service Completed ⭐",
            message = "The shelf repair job is complete. Please rate your experience.",
            time = "1 day ago",
            type = NotificationType.BOOKING_UPDATE,
            isRead = true
        ),
        Notification(
            id = "cn5",
            title = "New Electrician Available Nearby 📍",
            message = "Girish Shetty is now available in your area Indiranagar.",
            time = "2 days ago",
            type = NotificationType.PROMOTION,
            isRead = true
        )
    )

    val workerNotifications = listOf(
        Notification(
            id = "wn1",
            title = "New Booking Request 🔔",
            message = "Anita Singh requested a fan regulator replacement for 20 June.",
            time = "5 mins ago",
            type = NotificationType.NEW_REQUEST,
            isRead = false
        ),
        Notification(
            id = "wn2",
            title = "Customer Added Review ⭐",
            message = "Rahul Sharma left a 5-star review: 'Excellent work!'",
            time = "3 hours ago",
            type = NotificationType.ALERT,
            isRead = false
        ),
        Notification(
            id = "wn3",
            title = "Booking Cancelled ❌",
            message = "The booking request from Koramangala has been cancelled by customer.",
            time = "6 hours ago",
            type = NotificationType.ALERT,
            isRead = true
        ),
        Notification(
            id = "wn4",
            title = "Upcoming Service Reminder ⏰",
            message = "Reminder: You have a booking tomorrow at 09:00 AM.",
            time = "1 day ago",
            type = NotificationType.ALERT,
            isRead = true
        ),
        Notification(
            id = "wn5",
            title = "Profile Viewed by Customer 👀",
            message = "15 new customers viewed your profile in the last 24 hours.",
            time = "2 days ago",
            type = NotificationType.PROMOTION,
            isRead = true
        )
    )

    val bookings = customerBookings // Fallback for old references
    val notifications = customerNotifications // Fallback for old references

    val promoBanners = listOf(
        PromoBanner(
            id = "pb1",
            title = "First Booking Free!",
            subtitle = "Get your first electrical service at no consultation charge",
            backgroundColor = 0xFF1B7A3E,
            imageDescription = "Electrician at work"
        ),
        PromoBanner(
            id = "pb2",
            title = "20% OFF This Weekend",
            subtitle = "Book any plumbing service and save big!",
            backgroundColor = 0xFF1A6B5A,
            imageDescription = "Plumber fixing pipes"
        ),
        PromoBanner(
            id = "pb3",
            title = "Premium Painting Package",
            subtitle = "Full house painting starting at ₹999/room",
            backgroundColor = 0xFF4E342E,
            imageDescription = "Painter at work"
        )
    )

    val earnings = listOf(
        Earning("January", 5000, 10),
        Earning("February", 7200, 14),
        Earning("March", 8500, 16),
        Earning("April", 9600, 18),
        Earning("May", 12500, 24),
        Earning("June", 14200, 28),
        Earning("July", 13800, 26),
        Earning("August", 15500, 31),
        Earning("September", 14900, 29),
        Earning("October", 16800, 33),
        Earning("November", 17200, 35),
        Earning("December", 19000, 40)
    )

    val recentPayments = listOf(
        Booking(
            id = "rp1",
            workerId = "me",
            workerName = "Myself",
            workerCategory = WorkerCategory.ELECTRICIAN,
            customerName = "Ravi Kumar",
            date = "04 May 2026",
            time = "10:00 AM",
            status = BookingStatus.COMPLETED,
            address = "JP Nagar, Bangalore",
            totalAmount = 450,
            notes = "Electrical Repair"
        ),
        Booking(
            id = "rp2",
            workerId = "me",
            workerName = "Myself",
            workerCategory = WorkerCategory.PLUMBER,
            customerName = "Naveen Kumar",
            date = "03 May 2026",
            time = "02:30 PM",
            status = BookingStatus.COMPLETED,
            address = "Indiranagar, Bangalore",
            totalAmount = 800,
            notes = "Plumbing Service"
        ),
        Booking(
            id = "rp3",
            workerId = "me",
            workerName = "Myself",
            workerCategory = WorkerCategory.PAINTER,
            customerName = "Arjun Patel",
            date = "02 May 2026",
            time = "11:00 AM",
            status = BookingStatus.COMPLETED,
            address = "HSR Layout, Bangalore",
            totalAmount = 1200,
            notes = "Painting Work"
        )
    )

    fun getWorkerById(id: String): Worker? = workers.find { it.id == id }

    fun getWorkersByCategory(category: WorkerCategory): List<Worker> =
        workers.filter { it.category == category }

    fun getFeaturedWorkers(): List<Worker> = workers.filter { it.rating >= 4.6f }

    fun getNearbyWorkers(): List<Worker> =
        workers.filter { it.location.contains("Bangalore") }

    fun searchWorkers(query: String): List<Worker> =
        workers.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.category.displayName.contains(query, ignoreCase = true) ||
            it.location.contains(query, ignoreCase = true)
        }
}
