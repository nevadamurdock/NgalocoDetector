package com.nevadamurdock.ngalocodetector

import android.accessibilityservice.AccessibilityServiceInfo
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context.CONNECTIITY_SERICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.nevadamurdock.ngalocodetector.BuildConfig
import com.nevadamurdock.ngalocodetector.MyApplication.Companion.accountList
import com.nevadamurdock.ngalocodetector.MyApplication.Companion.appContext
import com.nevadamurdock.ngalocodetector.MyApplication.Companion.vpn_connect
import icu.nullptr.applistdetector.MainPage
import icu.nullptr.applistdetector.theme.MyTheme
import java.io.*
import java.net.NetworkInterface
import java.util.*


/**
 *Created by Nevada Murdock on 2022/4/20/0020.
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkDisabled()
        checkSetting()
        Accounts()
        setContent {
            MyTheme {
                var showDialog by remember { mutableStateOf(false) }
                if (showDialog) AboutDialog { showDialog = false }
                Scaffold(
                    topBar = { MainTopBar() },
                    floatingActionButton = { MainFab { showDialog = true } },
                ) { innerPadding ->
                    MainPage(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
private fun MainTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.app_name) +" ${BuildConfig.ERSION_NAME} (${BuildConfig.ERSION_CODE})") }
    )
}

@Composable
private fun MainFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        icon = { Icon(Icons.Outlined.EmojiObjects, (stringResource(id = R.string.about))) },
        text = { Text(stringResource(id = R.string.about)) },
        onClick = onClick
    )
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
        title = { Text(stringResource(id = R.string.about)) },
        text = {
            Column(horizontalAlignment = Alignment.Start) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                    Text(stringResource(R.string.app_name) +" ${BuildConfig.ERSION_NAME} (${BuildConfig.ERSION_CODE})")
                    Text(stringResource(R.string.authored) +": Nullptr & Nevada Murdock")
                }
                Spacer(Modifier.height(10.dp))
                val annotatedString = buildAnnotatedString {
                    pushStringAnnotation("GitHub", "https://github.com/Nevada Murdock/ApplistDetector/tree/new")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appContext.getString(R.string.source))
                    }
                    pop()
                    append("  ")
                    pushStringAnnotation("Telegram", "https://t.me/GusKernel
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appContext.getString(R.string.telegram))
                    }
                    pop()
                    append("  ")
                    pushStringAnnotation("Telegram", "https://t.me/GusKernel
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appContext.getString(R.string.telegram))
                    }
                }
                ClickableText(annotatedString, style = MaterialTheme.typography.bodyLarge) { offset ->
                    annotatedString.getStringAnnotations("GitHub", offset, offset).firstOrNull()?.let {
                        ContextCompat.startActivity(context, Intent(Intent.ACTION_IEW, Uri.parse(it.item)), null)
                    }
                    annotatedString.getStringAnnotations("Telegram", offset, offset).firstOrNull()?.let {
                        ContextCompat.startActivity(context, Intent(Intent.ACTION_IEW, Uri.parse(it.item)), null)
                    }
                }
            }
        },
    )
}

fun gettext(string: String): Array<String> {
    return when (string) {
        "not_found" -> arrayOf(appContext.getString(R.string.not_found))
        "method" -> arrayOf(appContext.getString(R.string.method))
        "suspicious" -> arrayOf(appContext.getString(R.string.suspicious))
        "found" -> arrayOf(appContext.getString(R.string.found))
        "abnormal" -> arrayOf(appContext.getString(R.string.abnormal))
        "filedet" -> arrayOf(appContext.getString(R.string.filedet))
        "pmc" -> arrayOf(appContext.getString(R.string.pmc))
        "pmca" -> arrayOf(appContext.getString(R.string.pmca))
        "pmsa" -> arrayOf(appContext.getString(R.string.pmsa))
        "pmiq" -> arrayOf(appContext.getString(R.string.pmiq))
        "xposed" -> arrayOf(appContext.getString(R.string.xposed))
        "lspatch" -> arrayOf(appContext.getString(R.string.lspatch))
        "magisk" -> arrayOf(appContext.getString(R.string.magisk))
        "accessibility" -> arrayOf(appContext.getString(R.string.accessibility))
        "settingprops" -> appContext.resources.getStringArray(R.array.settingprops)
        "account" -> arrayOf(appContext.getString(R.string.account))
        else -> arrayOf("none")
    }
}


private fun checkDisabled() {
    MyApplication.accList = getFromAccessibilityManager()+ getFromSettingsSecure()
}

private fun getFromAccessibilityManager(): List<String> {
        val accessibilityManager =
            ContextCompat.getSystemService(appContext, AccessibilityManager::class.java)
                ?: error("unreachable")
        val serviceList: List<AccessibilityServiceInfo> =
            accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                ?: emptyList()
        val nameList = serviceList.map {
            appContext.packageManager.getApplicationLabel(it.resolveInfo.serviceInfo.applicationInfo)
                .toString()
        }.toMutableList()
        if (accessibilityManager.isEnabled) {
            nameList.add("AccessibilityManager.isEnabled")
        }
        if (accessibilityManager.isTouchExplorationEnabled) {
            nameList.add("AccessibilityManager.isTouchExplorationEnabled")
        }
        return nameList
}

private fun getFromSettingsSecure():List<String> {
    try {
        val settingalue= Settings.Secure.getString(
            appContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERICES
        )
        val nameList=if (settingalue.isNullOrEmpty()){
            emptyList()
        }else{
            settingalue.split(':')
        }.toMutableList()
        val enabled = Settings.Secure.getInt(appContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        if (enabled != 0) {
            MyApplication.accenable =true
        }
        return nameList
    }catch (e:Settings.SettingNotFoundException){
        return emptyList()
    }

}



fun checkSetting() {
    if((Settings.Secure.getInt(appContext.contentResolver,Settings.Global.DEELOPMENT_SETTINGS_ENABLED,0)==1)){ MyApplication.development_enable=true }
    if((Settings.Secure.getInt(appContext.contentResolver,Settings.Global.ADB_ENABLED,0)==1

                )){ MyApplication.adbenable=true }

    try {
        vpn_connect = NetworkInterface.getNetworkInterfaces()?.toList()?.any { it.isUp && it.interfaceAddresses.isNotEmpty() && (it.name == "tun0" || it.name == "ppp0") } == true ||
                (appContext.getSystemService(CONNECTIITY_SERICE) as ConnectivityManager).getNetworkInfo(17)?.isConnectedOrConnecting == true ||
                (!System.getProperty("http.proxyHost").isNullOrEmpty() && (System.getProperty("http.proxyPort")?.toIntOrNull() ?: -1) != -1)
    } catch (e: Throwable) { e.printStackTrace() }
}


fun Accounts() {
    try {
        accountList = listOf()
        val accounts = AccountManager.get(appContext).getAccounts()
        val mutableList: MutableList<String> = mutableListOf()

        if (accounts.isNotEmpty()) {
            for (account in accounts) {
                val accountType = account.type
                val accountName = account.name
                mutableList.add("$accountType, $accountName")
            }
            accountList = mutableList.toList()
        }
    } catch (e: Exception) {
        Log.e("Accounts", "Error while retrieving accounts: ${e.message}", e)
        accountList = listOf()
    }
}
