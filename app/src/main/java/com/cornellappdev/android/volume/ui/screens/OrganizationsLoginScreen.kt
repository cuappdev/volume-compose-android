package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.ui.components.general.ErrorMessage
import com.cornellappdev.android.volume.ui.components.general.VolumeButton
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.components.general.VolumeTextField
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.ui.theme.GrayFive
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.OrganizationLoginViewModel

@Composable
fun OrganizationsLoginScreen(
    onSuccessfulLogin: (orgId: String) -> Unit,
    organizationLoginViewModel: OrganizationLoginViewModel = hiltViewModel(),
) {
    var slug by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var hasTriedLogin by remember { mutableStateOf(false) }
    var shouldSaveLogin by remember {
        mutableStateOf(false)
    }

    var authenticateEnabled by remember { mutableStateOf(false) }

    DisposableEffect(
        key1 = arrayOf(slug, accessCode),
        effect = {
            authenticateEnabled = slug.isNotBlank() && accessCode.isNotBlank()
            onDispose { }
        },
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Settings",
            fontSize = 20.sp,
            fontFamily = notoserif,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Organization Slug",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 38.dp),
            fontFamily = notoserif,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        VolumeTextField(
            value = slug, onValueChange = { slug = it }, modifier = Modifier.height(50.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Access code",
            modifier = Modifier
                .fillMaxWidth(),
            fontFamily = notoserif,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        VolumeTextField(
            value = accessCode,
            onValueChange = { accessCode = it },
            modifier = Modifier.height(50.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = shouldSaveLogin,
                onCheckedChange = { shouldSaveLogin = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = VolumeOrange,
                    checkmarkColor = White,
                    uncheckedColor = GrayFive,
                ),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Save login info", fontFamily = lato, color = GrayFive)
        }
        Spacer(modifier = Modifier.height(32.dp))

        VolumeButton(
            text = "Authenticate",
            onClick = {
                hasTriedLogin = true
                organizationLoginViewModel.checkAccessCode(
                    accessCode.trim(),
                    slug.trim(),
                    shouldSaveLogin
                )
            },
            enabled = authenticateEnabled,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        when (val res =
            organizationLoginViewModel.organizationsLoginUiState.checkAccessCodeResult) {
            is ResponseState.Success -> {
                onSuccessfulLogin(res.data.slug)
            }

            is ResponseState.Error -> {
                if (hasTriedLogin) {
                    ErrorMessage(
                        message = res.errors.firstOrNull()?.message
                            ?: "Incorrect slug or access code"
                    )
                }
            }

            ResponseState.Loading -> {
                if (hasTriedLogin) {
                    VolumeLoading()
                }
            }
        }
    }
}