package com.cornellappdev.android.volume.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.OutlinedFlag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.BuildConfig
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onAboutUsClicked: () -> Unit, onOrganizationLoginClicked: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    fontSize = 20.sp,
                    fontFamily = notoserif,
                    fontWeight = FontWeight.Medium,
                    text = "Settings"
                )
            })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = innerPadding.calculateTopPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .height(30.dp)
                        .clickable
                        {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(BuildConfig.FEEDBACK_FORM)
                                )
                            )
                        },
                ) {
                    Icon(
                        Icons.Outlined.OutlinedFlag,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Send Feedback",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.weight(1f, true))
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .height(30.dp)
                        .clickable
                        {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(BuildConfig.WEBSITE)
                                )
                            )
                        },
                ) {
                    Icon(
                        Icons.Outlined.Language,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Visit Our Website",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.weight(1f, true))
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .height(30.dp)
                        .clickable { onAboutUsClicked.invoke() }
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "About Us",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.weight(1f, true))
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .height(30.dp)
                        .clickable { onOrganizationLoginClicked() }
                ) {
                    Icon(
                        Icons.Outlined.Lock,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Organization login",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.weight(1f, true))
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                    )
                }
            }
        }
    )
}
