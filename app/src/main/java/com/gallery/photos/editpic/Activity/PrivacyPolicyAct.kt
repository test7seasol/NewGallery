package com.gallery.photos.editpic.Activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.gallery.photos.editpic.Extensions.html
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyAct : AppCompatActivity() {
    lateinit var bind: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.toolid.backid.onClick { finish() }
        bind.toolid.tvtoolname.text = getString(R.string.privacy_policy)

        applyStatusBarColor(this)

        val bodyData =
            "    <body>\n" + "    <strong>Privacy Policy</strong><p>This privacy policy applies to the Android ${
                getString(
                    R.string.app_name
                )
            } (hereby referred to as \"Application\") for mobile devices that was created by Kinjal (hereby referred to as \"Service Provider\") as a Free service. This service is intended for use \"AS IS\".</p><br><strong>Information Collection and Use</strong><p>The Application collects information when you download and use it. This information may include information such as </p><ul><li>Your device's Internet Protocol address (e.g. IP address)</li><li>The pages of the Application that you visit, the time and date of your visit, the time spent on those pages</li><li>The time spent on the Application</li><li>The operating system you use on your mobile device</li></ul><p></p><br><p>The Application does not gather precise information about the location of your mobile device.</p><div style=\"display: none;\"><p>The Application collects your device's location, which helps the Service Provider determine your approximate geographical location and make use of in below ways:</p><ul><li>Geolocation Services: The Service Provider utilizes location data to provide features such as personalized content, relevant recommendations, and location-based services.</li><li>Analytics and Improvements: Aggregated and anonymized location data helps the Service Provider to analyze user behavior, identify trends, and improve the overall performance and functionality of the Application.</li><li>Third-Party Services: Periodically, the Service Provider may transmit anonymized location data to external services. These services assist them in enhancing the Application and optimizing their offerings.</li></ul></div><br><p>The Service Provider may use the information you provided to contact you from time to time to provide you with important information, required notices and marketing promotions.</p><br><p>For a better experience, while using the Application, the Service Provider may require you to provide us with certain personally identifiable information. The information that the Service Provider request will be retained by them and used as described in this privacy policy.</p><br><strong>Third Party Access</strong><p>Only aggregated, anonymized data is periodically transmitted to external services to aid the Service Provider in improving the Application and their service. The Service Provider may share your information with third parties in the ways that are described in this privacy statement.</p><div><br><p>Please note that the Application utilizes third-party services that have their own Privacy Policy about handling data. Below are the links to the Privacy Policy of the third-party service providers used by the Application:</p><ul><li><a href=\"https://www.google.com/policies/privacy/\" target=\"_blank\" rel=\"noopener noreferrer\">Google Play Services</a></li><li><a href=\"https://support.google.com/admob/answer/6128543?hl=en\" target=\"_blank\" rel=\"noopener noreferrer\">AdMob</a></li><li><a href=\"https://firebase.google.com/support/privacy\" target=\"_blank\" rel=\"noopener noreferrer\">Google Analytics for Firebase</a></li><li><a href=\"https://firebase.google.com/support/privacy/\" target=\"_blank\" rel=\"noopener noreferrer\">Firebase Crashlytics</a></li><li><a href=\"https://www.facebook.com/about/privacy/update/printable\" target=\"_blank\" rel=\"noopener noreferrer\">Facebook</a></li><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----></ul></div><br><p>The Service Provider may disclose User Provided and Automatically Collected Information:</p><ul><li>as required by law, such as to comply with a subpoena, or similar legal process;</li><li>when they believe in good faith that disclosure is necessary to protect their rights, protect your safety or the safety of others, investigate fraud, or respond to a government request;</li><li>with their trusted services providers who work on their behalf, do not have an independent use of the information we disclose to them, and have agreed to adhere to the rules set forth in this privacy statement.</li></ul><p></p><br><strong>Opt-Out Rights</strong><p>You can stop all collection of information by the Application easily by uninstalling it. You may use the standard uninstall processes as may be available as part of your mobile device or via the mobile application marketplace or network.</p><br><strong>Data Retention Policy</strong><p>The Service Provider will retain User Provided data for as long as you use the Application and for a reasonable time thereafter. If you'd like them to delete User Provided Data that you have provided via the Application, please contact them at jinacongtact@gmail.com and they will respond in a reasonable time.</p><br><strong>Children</strong><p>The Service Provider does not use the Application to knowingly solicit data from or market to children under the age of 13.</p><div><br><p>The Application does not address anyone under the age of 13.\n" + "The Service Provider does not knowingly collect personally\n" + "identifiable information from children under 13 years of age. In the case\n" + "the Service Provider discover that a child under 13 has provided\n" + "personal information, the Service Provider will immediately\n" + "delete this from their servers. If you are a parent or guardian\n" + "and you are aware that your child has provided us with\n" + "personal information, please contact the Service Provider (jinacongtact@gmail.com) so that\n" + "they will be able to take the necessary actions.</p></div><!----><br><strong>Security</strong><p>The Service Provider is concerned about safeguarding the confidentiality of your information. The Service Provider provides physical, electronic, and procedural safeguards to protect information the Service Provider processes and maintains.</p><br><strong>Changes</strong><p>This Privacy Policy may be updated from time to time for any reason. The Service Provider will notify you of any changes to the Privacy Policy by updating this page with the new Privacy Policy. You are advised to consult this Privacy Policy regularly for any changes, as continued use is deemed approval of all changes.</p><br><p>This privacy policy is effective as of 2024-10-16</p><br><strong>Your Consent</strong><p>By using the Application, you are consenting to the processing of your information as set forth in this Privacy Policy now and as amended by us.</p><br><strong>Contact Us</strong><p>If you have any questions regarding privacy while using the Application, or have questions about the practices, please contact the Service Provider via email at kinjal@gmail.com.</p>\n" + "    </body>\n"
        bind.tvprivacyid.text = bodyData.html()
    }

    fun applyStatusBarColor(activity: Activity) {
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = activity.window.insetsController
            if (controller != null) {
                if (isDarkMode) {
                    controller.setSystemBarsAppearance(
                        0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                } else {
                    controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }
        } else {
            @Suppress("DEPRECATION") if (isDarkMode) {
                activity.window.decorView.systemUiVisibility = 0
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
        // Set the status bar color
        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
    }
}
