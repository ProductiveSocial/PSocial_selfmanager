package com.productivesocial.psocial_selfmanager.config

object DotEnvConfig {
    val databaseUrl: String get() = DotEnv.get("DATABASE_URL")
        ?: "jdbc:postgresql://${DotEnv.get("DB_HOST", "localhost")}:${DotEnv.getInt("DB_PORT", 5432)}/${DotEnv.get("DB_NAME", "psocial_selfmanager")}"
    val dbUser: String get() = DotEnv.get("DB_USER", "postgres")
    val dbPassword: String? get() = DotEnv.get("DB_PASSWORD")

    // Central user registry (psocial_user)
    val userRegistryDbHost: String get() = DotEnv.get("USER_REGISTRY_DB_HOST", "localhost")
    val userRegistryDbPort: Int get() = DotEnv.getInt("USER_REGISTRY_DB_PORT", 5432)
    val userRegistryDbName: String get() = DotEnv.get("USER_REGISTRY_DB_NAME", "psocial_user")
    val userRegistryDbUser: String get() = DotEnv.get("USER_REGISTRY_DB_USER", "rcolejnr")
    val userRegistryDbPassword: String? get() = DotEnv.get("USER_REGISTRY_DB_PASSWORD")

    // Server configuration
    val serverPort: Int get() = DotEnv.getInt("PORT", 8080)
    val serverHost: String get() = DotEnv.get("HOST", "localhost")

    // JWT configuration
    val jwtSecret: String get() = DotEnv.get("JWT_SECRET", "zAP5MBA4B4Ijz0MZaS48")
    val jwtIssuer: String get() = DotEnv.get("JWT_ISSUER", "piashcse")
    val jwtAudience: String get() = DotEnv.get("JWT_AUDIENCE", "ktor-psocial")
    val jwtRealm: String get() = DotEnv.get("JWT_REALM", "ktor-psocial")

    // CORS configuration
    val allowedOrigins: String get() = DotEnv.get("ALLOWED_ORIGINS", "http://localhost:3000,http://localhost:8080")

    // Internal service-to-service authentication
    val internalApiKey: String get() = DotEnv.get("INTERNAL_API_KEY", "")

    // URL of the billing service (for ML/LLM prediction calls)
    val billingServiceUrl: String get() = DotEnv.get("BILLING_SERVICE_URL", "http://localhost:8001")

    // URL of the timer service (for pomodoro stats in analysis)
    val timerServiceUrl: String get() = DotEnv.get("TIMER_SERVICE_URL", "http://localhost:8081")

    // Email configuration
    val emailHost: String get() = DotEnv.get("EMAIL_HOST", "smtp.gmail.com")
    val emailPort: Int get() = DotEnv.getInt("EMAIL_PORT", 587)
    val emailUsername: String get() = DotEnv.get("EMAIL_USERNAME", "")
    val emailPassword: String get() = DotEnv.get("EMAIL_PASSWORD", "")
}