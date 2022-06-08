package com.pole.digitaldigging.screens.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pole.digitaldigging.R

@Composable
fun ErrorMessage() {
    Message(
        text = stringResource(id = R.string.no_internet_connection_error),
        resId = R.drawable.ic_baseline_signal_wifi_connected_no_internet_4_24
    )
}