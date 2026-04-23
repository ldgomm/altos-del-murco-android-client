package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.premierdarkcoffee.tourism.altosdelmurco.R
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel.AuthenticationViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.clientId
import kotlinx.coroutines.launch

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current.findActivityOrNull()

    LaunchedEffect(activity) {
        val currentActivity = activity ?: return@LaunchedEffect
        viewModel.beginAuthorizedAccountsAttempt()
        runGoogleSignIn(
            activity = currentActivity,
            filterByAuthorizedAccounts = true,
            autoSelect = true,
            onToken = viewModel::onGoogleIdTokenReceived,
            onNoCredential = viewModel::finishAuthorizedAccountsAttempt,
            onError = viewModel::onSignInError,
        )
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Bienvenido a Altos del Murco",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Inicia sesión con tu cuenta de Google para continuar con pedidos, reservas y tu perfil.",
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val currentActivity = activity ?: return@Button
                    scope.launch {
                        runGoogleSignIn(
                            activity = currentActivity,
                            filterByAuthorizedAccounts = false,
                            autoSelect = false,
                            onToken = viewModel::onGoogleIdTokenReceived,
                            onNoCredential = viewModel::finishAuthorizedAccountsAttempt,
                            onError = viewModel::onSignInError,
                        )
                    }
                },
                enabled = activity != null && !uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator()
                } else {
                    Text("Continuar con Google")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = viewModel::clearError,
                enabled = uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Limpiar mensaje")
            }

            if (uiState.isTryingAuthorizedAccounts && !uiState.isSubmitting) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Buscando una cuenta ya autorizada...",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private suspend fun runGoogleSignIn(
    activity: Activity,
    filterByAuthorizedAccounts: Boolean,
    autoSelect: Boolean,
    onToken: (String) -> Unit,
    onNoCredential: () -> Unit,
    onError: (String) -> Unit,
) {
    val credentialManager = CredentialManager.create(activity)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(clientId)
        .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
        .setAutoSelectEnabled(autoSelect)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCredential.idToken)
            return
        }

        onError("Unsupported credential returned by Credential Manager.")
    } catch (_: GetCredentialCancellationException) {
        onNoCredential()
    } catch (error: GetCredentialException) {
        if (filterByAuthorizedAccounts) {
            onNoCredential()
        } else {
            onError(error.message ?: "Google sign-in failed.")
        }
    } catch (error: Exception) {
        onError(error.message ?: "Google sign-in failed.")
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}
