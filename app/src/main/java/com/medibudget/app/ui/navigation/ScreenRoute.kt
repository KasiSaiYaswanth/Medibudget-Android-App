package com.medibudget.app.ui.navigation

sealed class ScreenRoute(val route: String) {
    // Auth & Onboarding
    object Splash : ScreenRoute("splash")
    object Onboarding : ScreenRoute("onboarding")
    object Login : ScreenRoute("login")
    object Signup : ScreenRoute("signup")
    object ForgotPassword : ScreenRoute("forgot_password")
    object OtpVerification : ScreenRoute("otp_verify")
    object ResetPassword : ScreenRoute("reset_password")
    object BiometricsSetup : ScreenRoute("biometrics_setup")

    // Core Pages
    object Dashboard : ScreenRoute("dashboard")
    object HealthDashboard : ScreenRoute("health_dashboard")
    object Profile : ScreenRoute("profile")
    object EditProfile : ScreenRoute("edit_profile")
    object SecuritySettings : ScreenRoute("security_settings")
    object Settings : ScreenRoute("settings")
    object HelpFAQ : ScreenRoute("help_faq")

    // Symptom AI Assistant
    object SymptomInput : ScreenRoute("symptom_input")
    object AIChat : ScreenRoute("ai_chat")
    object SymptomHistory : ScreenRoute("symptom_history")

    // Cost Estimation
    object CostEstimator : ScreenRoute("cost_estimator")
    object CostBreakdown : ScreenRoute("cost_breakdown/{condition}/{city}/{facility}") {
        fun createRoute(cond: String, city: String, fac: String) = "cost_breakdown/$cond/$city/$fac"
    }
    object CostComparison : ScreenRoute("cost_comparison")

    // OCR Scanner & Medicines
    object OCRScanner : ScreenRoute("ocr_scanner")
    object GenericFinder : ScreenRoute("generic_finder/{query}") {
        fun createRoute(q: String) = "generic_finder/$q"
    }
    object MedicineDetail : ScreenRoute("medicine_detail/{id}") {
        fun createRoute(id: String) = "medicine_detail/$id"
    }

    // Hospital Finder
    object MapFinder : ScreenRoute("map_finder")
    object HospitalCompare : ScreenRoute("hospital_compare")

    // Insurance & Schemes
    object InsuranceCalc : ScreenRoute("insurance_calc")
    object SchemeChecker : ScreenRoute("scheme_checker")

    // Emergency SOS
    object SOSMode : ScreenRoute("sos_mode")

    // Admin & Reports
    object AdminDashboard : ScreenRoute("admin_dashboard")
    object ReportsList : ScreenRoute("reports_list")
}
