package com.woocommerce.android.ui.prefs

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.woocommerce.android.R
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground

@Composable
fun PrivacySettingsScreen(
    viewModel: PrivacySettingsViewModel
) {
    val state: PrivacySettingsViewModel.State by viewModel.state.observeAsState(
        PrivacySettingsViewModel.State(sendUsageStats = false, crashReportingEnabled = false)
    )
    PrivacySettingsScreen(
        state,
        onAnalyticsSettingChanged = viewModel::onSendStatsSettingChanged,
        onReportCrashesChanged = viewModel::onCrashReportingSettingChanged,
    )
}

@Composable
fun PrivacySettingsScreen(
    state: PrivacySettingsViewModel.State,
    onAnalyticsSettingChanged: (Boolean) -> Unit,
    onReportCrashesChanged: (Boolean) -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                style = MaterialTheme.typography.caption,
                text = stringResource(R.string.settings_privacy_statement),
                modifier = Modifier.padding(16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                content = {
                    Column {
                        OptionRow(
                            switchChecked = state.sendUsageStats,
                            onAnalyticsSettingChanged,
                            sectionHeader = stringResource(R.string.settings_tracking_header),
                            sectionTitle = stringResource(R.string.settings_tracking_analytics),
                            sectionDescription = stringResource(R.string.settings_tracking_analytics_description),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OptionRow(
                            switchChecked = state.crashReportingEnabled,
                            onReportCrashesChanged,
                            sectionHeader = stringResource(R.string.settings_reports_header),
                            sectionTitle = stringResource(R.string.settings_reports_report_crashes),
                            sectionDescription = stringResource(R.string.settings_reports_report_crashes_description),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun OptionRow(
    switchChecked: Boolean,
    onSwitchChanged: (Boolean) -> Unit,
    sectionHeader: String,
    sectionTitle: String,
    sectionDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = sectionHeader,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            color = MaterialTheme.colors.primary,
        )
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .clickable {
                    onSwitchChanged(!switchChecked)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.subtitle1,
                )
                Text(
                    style = textAppearanceWooBody2(),
                    text = sectionDescription,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .width(1.dp)
            )
            Switch(
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary
                ),
                modifier = Modifier.padding(start = 8.dp),
                checked = switchChecked,
                onCheckedChange = onSwitchChanged,
            )
        }
    }
}

@Composable
// Style of TextAppearance.Woo.Body2
private fun textAppearanceWooBody2() = TextStyle(
    lineHeight = 20.sp,
    color = MaterialTheme.colors.onSurface.copy(
        alpha = 0.60f
    ),
    fontSize = 14.sp,
)

@Preview(name = "Light mode")
@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "RTL mode", locale = "ar")
@Composable
private fun Default() {
    WooThemeWithBackground {
        PrivacySettingsScreen(
            state = PrivacySettingsViewModel.State(
                sendUsageStats = true,
                crashReportingEnabled = false
            ),
            {}, {}
        )
    }
}
