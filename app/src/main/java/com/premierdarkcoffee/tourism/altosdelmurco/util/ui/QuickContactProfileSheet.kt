package com.premierdarkcoffee.tourism.altosdelmurco.util.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel.CompleteProfileViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickContactProfileSheet(
    theme: AppSectionTheme,
    profile: ClientProfile,
    viewModel: CompleteProfileViewModel,
    title: String,
    message: String,
    onSaved: (ClientProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    LaunchedEffect(profile.id, profile.updatedAt) {
        viewModel.initialize(
            user = profile.toAuthenticatedUserForQuickEdit(),
            existingProfile = profile,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.profileCompleted.collect { updatedProfile ->
            onSaved(updatedProfile)
            onDismiss()
        }
    }

    val nameMissing = state.fullName.trim().isEmpty()
    val phoneDigits = state.phoneNumber.filter(Char::isDigit)
    val phoneMissing = phoneDigits.isEmpty()
    val phoneInvalid = phoneDigits.isNotEmpty() && phoneDigits.length < 8
    val canSave = !nameMissing && !phoneMissing && !phoneInvalid && !state.isSaving

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = palette.surface,
        contentColor = palette.textPrimary,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = palette.textTertiary)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .appCardStyle(theme = theme, emphasized = true),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BrandIconBubble(
                    theme = theme,
                    icon = Icons.Rounded.Person,
                    size = 52.dp,
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary,
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                )
            }

            OutlinedTextField(
                value = state.fullName,
                onValueChange = viewModel::onFullNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre completo") },
                leadingIcon = {
                    Icon(Icons.Rounded.Person, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(AppTheme.Radius.large),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary,
                    focusedContainerColor = palette.elevatedCard,
                    unfocusedContainerColor = palette.elevatedCard,
                    focusedBorderColor = palette.primary,
                    unfocusedBorderColor = palette.stroke,
                    focusedLabelColor = palette.primary,
                    unfocusedLabelColor = palette.textSecondary,
                    cursorColor = palette.primary,
                ),
            )

            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("WhatsApp") },
                leadingIcon = {
                    Icon(Icons.Rounded.Phone, contentDescription = null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(AppTheme.Radius.large),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary,
                    focusedContainerColor = palette.elevatedCard,
                    unfocusedContainerColor = palette.elevatedCard,
                    focusedBorderColor = palette.primary,
                    unfocusedBorderColor = palette.stroke,
                    focusedLabelColor = palette.primary,
                    unfocusedLabelColor = palette.textSecondary,
                    cursorColor = palette.primary,
                ),
            )

            ContactRequirementCard(
                theme = theme,
                nameMissing = nameMissing,
                phoneMissing = phoneMissing,
                phoneInvalid = phoneInvalid,
                name = state.fullName,
                phone = state.phoneNumber,
            )

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.destructive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .appCardStyle(theme = theme),
                )
            }

            BrandPrimaryButton(
                theme = theme,
                enabled = canSave,
                onClick = viewModel::saveProfile,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = palette.onPrimary,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = palette.onPrimary,
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (state.isSaving) "Guardando..." else "Guardar y continuar",
                    color = palette.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Ahora no",
                    color = palette.textSecondary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ContactRequirementCard(
    theme: AppSectionTheme,
    nameMissing: Boolean,
    phoneMissing: Boolean,
    phoneInvalid: Boolean,
    name: String,
    phone: String,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ContactRequirementRow(
            completed = !nameMissing,
            title = if (nameMissing) "Falta el nombre" else "Nombre listo",
            message = if (nameMissing) {
                "Lo usamos para identificar tu pedido o reserva."
            } else {
                name.trim()
            }
        )

        ContactRequirementRow(
            completed = !phoneMissing && !phoneInvalid, title = when {
                phoneMissing -> "Falta WhatsApp"
                phoneInvalid -> "WhatsApp incompleto"
                else -> "WhatsApp listo"
            }, message = when {
                phoneMissing -> "Nos ayuda a confirmar horarios, disponibilidad o cambios."
                phoneInvalid -> "Revisa que tenga al menos 8 dígitos."
                else -> phone.filter(Char::isDigit)
            }
        )
    }
}

@Composable
private fun ContactRequirementRow(
    completed: Boolean,
    title: String,
    message: String,
) {
    val palette = LocalBrandPalette.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = if (completed) {
                Icons.Rounded.CheckCircle
            } else {
                Icons.Rounded.WarningAmber
            },
            contentDescription = null,
            tint = if (completed) {
                palette.success
            } else {
                palette.destructive
            },
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )
        }
    }
}

fun ClientProfile.toAuthenticatedUserForQuickEdit(): AuthenticatedUser = AuthenticatedUser(
    uid = id,
    email = email,
    displayName = fullName,
    appleUserIdentifier = appleUserIdentifier,
)