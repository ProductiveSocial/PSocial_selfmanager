package com.productivesocial.constants


/**
 * User-facing error messages organized by domain.
 * Industry standard: All error messages centralized here for consistency, i18n, and maintainability.
 *
 * Usage: throw ValidationException(Message.Validation.BLANK_FIELD("User ID"))
 *
 * Based on Stripe, GitHub, OpenAI best practices:
 * - Clear, actionable messages
 * - No technical jargon
 * - Consistent tone across all endpoints
 */
object Messages {

    // ─── Validation Messages (Reusable with field names) ───────────────────
    object Validation {
        // Generic field validators
        fun blankField(fieldName: String) = "$fieldName cannot be blank"
        fun negativeValue(fieldName: String) = "$fieldName cannot be negative"
        fun notPositive(fieldName: String) = "$fieldName must be greater than 0"
        fun tooLong(fieldName: String, maxLength: Int) = "$fieldName cannot exceed $maxLength characters"
        fun invalidFormat(fieldName: String) = "Invalid $fieldName format"

        // Specific validation messages
        const val INVALID_EMAIL = "Invalid email address"
        const val WEAK_PASSWORD = "Password must be at least 8 characters with uppercase, lowercase, digit, and special character"
        const val INVALID_USER_TYPE = "Invalid user type. Must be one of: CUSTOMER, SELLER, ADMIN, SUPER_ADMIN"
        const val EMPTY_PASSWORD = "Password cannot be empty"
        const val INVALID_ORDER_ITEMS = "Order must contain at least one item"
        const val FILE_NAME_REQUIRED = "File name is required"
        const val INVALID_FILE_TYPE = "Invalid file type. Allowed types: jpg, jpeg, png, gif, webp"
        const val FILE_REQUIRED = "No file uploaded"
        fun productNotFound(productId: String) = "Product with ID $productId not found"
        fun insufficientStock(productName: String, available: Int) = "Insufficient stock for $productName. Available: $available"
        fun invalidOperation(operation: String, validOps: String) = "Invalid operation: $operation. Valid operations: $validOps"
    }

    // ─── Auth ──────────────────────────────────────────────────────────────
    object Auth {
        const val USER_EXISTS = "User already exists with this email"
        const val INVALID_CREDENTIALS = "Invalid email or password"
        const val ACCOUNT_NOT_VERIFIED = "Account not verified"
        const val ACCOUNT_DEACTIVATED = "Account has been deactivated"
        const val PASSWORD_CHANGE_SUCCESS = "Password changed successfully"
        const val PASSWORD_SAME = "New password cannot be the same as current password"

        const val OTP_SENT = "Verification code sent to your email"
        const val OTP_ALREADY_SENT = "Verification code already sent. Please wait before requesting a new one"
        const val OTP_INVALID = "Invalid or expired verification code"
        const val PASSWORD_RESET_SUCCESS = "Password reset successful"
        const val INVALID_REFRESH_TOKEN = "Invalid or expired refresh token"
        const val TOKEN_EXPIRED = "Refresh token expired or revoked"

        fun accountLocked(lockoutMinutes: Long) = "Account locked due to too many failed login attempts. Try again in $lockoutMinutes minutes"

        // Permissions
        fun insufficientPermissions(action: String) = "Insufficient permissions to $action"
        fun userNotFoundForRole(email: String, userType: String) = "User with email $email not found for $userType role"
    }

    // ─── General Errors ────────────────────────────────────────────────────
    object Errors {
        const val INTERNAL = "Internal server error"
        const val UNAUTHORIZED = "Authentication required"
        const val FORBIDDEN = "Insufficient permissions"
        const val NOT_FOUND = "Resource not found"
        const val VALIDATION_FAILED = "Validation failed"
        const val EMAIL_FAILED = "Failed to send email"
        const val SELLER_REQUIRED = "User must be registered as a seller"
        const val MISSING_PARAMETER = "Missing required parameter: %s"
    }
}

