package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel.CompleteProfileViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CompleteProfileScreen(
    state: SessionState.NeedsProfileCompletion,
    modifier: Modifier = Modifier,
    viewModel: CompleteProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

    LaunchedEffect(state.user.uid) {
        viewModel.initialize(
            user = state.user,
            existingProfile = state.existingProfile,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Completa tu perfil",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Necesitamos tus datos básicos para pedidos, reservas y beneficios.",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = {},
            label = { Text("Correo") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = viewModel::onFullNameChanged,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.nationalId,
            onValueChange = viewModel::onNationalIdChanged,
            label = { Text("Cédula") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = viewModel::onPhoneNumberChanged,
            label = { Text("WhatsApp") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val current = Calendar.getInstance().apply { time = uiState.birthday }
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val picked = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        viewModel.onBirthdayChanged(picked.time)
                    },
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH),
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Fecha de nacimiento: ${dateFormatter.format(uiState.birthday)}")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.address,
            onValueChange = viewModel::onAddressChanged,
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.emergencyContactName,
            onValueChange = viewModel::onEmergencyContactNameChanged,
            label = { Text("Contacto de emergencia") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.emergencyContactPhone,
            onValueChange = viewModel::onEmergencyContactPhoneChanged,
            label = { Text("Teléfono de emergencia") },
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = viewModel::saveProfile,
            enabled = uiState.canSubmit && !uiState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator()
            } else {
                Text("Guardar perfil")
            }
        }
    }
}
