package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                ),
                            ),
                        )
                        .padding(24.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.18f), CircleShape)
                                .padding(14.dp),
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.Agriculture,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }

                        Text(
                            text = "Bienvenido a Altos del Murco",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                        )

                        Text(
                            text = "Entra con Google para continuar con pedidos, reservas y tu perfil.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.92f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Acceso rápido",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Text(
                        text = if (uiState.isTryingAuthorizedAccounts && !uiState.isSubmitting) {
                            "Buscando una cuenta ya autorizada..."
                        } else {
                            "Usa tu cuenta de Google para mantener sincronizados tus beneficios y reservas."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp),
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp,
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.Login,
                                contentDescription = null,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (uiState.isSubmitting) "Iniciando sesión..." else "Continuar con Google",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
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

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCredential.idToken)
            return
        }

        onError("Credential Manager devolvió una credencial no compatible.")
    } catch (_: GetCredentialCancellationException) {
        onNoCredential()
    } catch (_: NoCredentialException) {
        onNoCredential()
    } catch (error: GetCredentialException) {
        if (filterByAuthorizedAccounts) {
            onNoCredential()
        } else {
            onError(error.message ?: "No se pudo iniciar sesión con Google.")
        }
    } catch (error: Exception) {
        onError(error.message ?: "No se pudo iniciar sesión con Google.")
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}
