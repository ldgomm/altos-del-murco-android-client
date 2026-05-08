package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyLevel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyWalletEvent
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyWalletEventStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStats
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel.EditProfileUiState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel.ProfileMessage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel.ProfileUiState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel.ProfileViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel.displayTitle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel.formatDateLong
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.clientId
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.SeasonalCardContainer
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.ThemeMode
import kotlinx.coroutines.launch
import java.util.Calendar
import android.provider.Settings as AndroidSettings

private enum class ProfileRoute {
    ROOT,
    EDIT,
    LOYALTY,
    PREFERENCES,
    SUPPORT,
    ACCOUNT,
}

private object ProfileLinks {
    const val instagram = "https://instagram.com/altosdelmurco"
    const val tiktok = "https://www.tiktok.com/@altosdelmurco"
    const val facebook = "https://www.facebook.com/altosdelmurco"
    const val whatsapp = "https://wa.me/593000000000"
    const val maps = "https://maps.google.com/?q=Altos+del+Murco"
    const val supportEmail = "mailto:soporte@altosdelmurco.com"
    const val privacyPolicy = "https://altosdelmurco.com/privacy"
    const val terms = "https://altosdelmurco.com/terms"
}

@Composable
fun ProfileScreen(
    sessionState: SessionState.Authenticated,
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var route by rememberSaveable { mutableStateOf(ProfileRoute.ROOT) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSignOutConfirmation by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        viewModel.onAppear(sessionState.profile)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        val selectedUri = uri ?: return@rememberLauncherForActivityResult
        val bytes = context.readBytes(selectedUri) ?: return@rememberLauncherForActivityResult
        viewModel.uploadProfileImage(bytes)
    }

    BackHandler(enabled = route != ProfileRoute.ROOT) {
        route = ProfileRoute.ROOT
    }

    state.message?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            confirmButton = {
                TextButton(onClick = viewModel::clearMessage) { Text("Aceptar") }
            },
            title = {
                Text(
                    when (message) {
                        is ProfileMessage.Error -> "Algo salió mal"
                        is ProfileMessage.Success -> "Listo"
                    },
                )
            },
            text = {
                Text(
                    when (message) {
                        is ProfileMessage.Error -> message.message
                        is ProfileMessage.Success -> message.message
                    },
                )
            },
        )
    }

    if (showSignOutConfirmation) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmation = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirmation = false
                        viewModel.signOut()
                    },
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmation = false }) { Text("Volver") }
            },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tu cuenta seguirá existiendo. Solo se cerrará la sesión en este dispositivo.") },
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            confirmButton = {
                TextButton(
                    enabled = !state.isDeletingAccount,
                    onClick = {
                        showDeleteConfirmation = false
                        val activity = context.findActivityOrNull() ?: return@TextButton
                        scope.launch {
                            runGoogleReauthentication(
                                activity = activity,
                                onToken = viewModel::deleteAccount,
                                onError = viewModel::presentError,
                            )
                        }
                    },
                ) { Text("Eliminar cuenta") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancelar") }
            },
            title = { Text("Eliminar cuenta definitivamente") },
            text = {
                Text("Se eliminará tu perfil de cliente y Firebase pedirá una credencial reciente de Google antes de borrar la cuenta.")
            },
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (route) {
            ProfileRoute.ROOT -> ProfileHomeScreen(
                state = state,
                onRefresh = viewModel::refresh,
                onPickImage = { imagePicker.launch("image/*") },
                onRemoveImage = viewModel::removeProfileImage,
                onOpenEdit = {
                    viewModel.beginEditProfile()
                    route = ProfileRoute.EDIT
                },
                onOpenLoyalty = { route = ProfileRoute.LOYALTY },
                onOpenPreferences = { route = ProfileRoute.PREFERENCES },
                onOpenSupport = { route = ProfileRoute.SUPPORT },
                onOpenAccount = { route = ProfileRoute.ACCOUNT },
            )

            ProfileRoute.EDIT -> EditProfileScreen(
                state = state,
                onBack = {
                    viewModel.cancelEditProfile()
                    route = ProfileRoute.ROOT
                },
                onFullNameChanged = viewModel::onEditFullNameChanged,
                onPhoneChanged = viewModel::onEditPhoneChanged,
                onBirthdayChanged = viewModel::onEditBirthdayChanged,
                onAddressChanged = viewModel::onEditAddressChanged,
                onEmergencyNameChanged = viewModel::onEditEmergencyNameChanged,
                onEmergencyPhoneChanged = viewModel::onEditEmergencyPhoneChanged,
                onSave = viewModel::saveEditedProfile,
            )

            ProfileRoute.LOYALTY -> LoyaltyProgramScreen(
                stats = state.stats,
                onBack = { route = ProfileRoute.ROOT },
            )

            ProfileRoute.PREFERENCES -> PreferencesScreen(
                currentThemeMode = currentThemeMode,
                onThemeModeSelected = onThemeModeSelected,
                onBack = { route = ProfileRoute.ROOT },
            )

            ProfileRoute.SUPPORT -> SupportScreen(
                onBack = { route = ProfileRoute.ROOT },
            )

            ProfileRoute.ACCOUNT -> AccountActionsScreen(
                state = state,
                onBack = { route = ProfileRoute.ROOT },
                onSignOut = { showSignOutConfirmation = true },
                onDeleteAccount = { showDeleteConfirmation = true },
            )
        }
    }
}

@Composable
private fun ProfileHomeScreen(
    state: ProfileUiState,
    onRefresh: () -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onOpenEdit: () -> Unit,
    onOpenLoyalty: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAccount: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Refrescar")
            }
        }

        ProfileHeaderCard(
            state = state,
            onPickImage = onPickImage,
            onRemoveImage = onRemoveImage,
        )

        ProfileStatsSection(
            state = state,
            onOpenLoyalty = onOpenLoyalty,
        )

        ProfileCard {
            SectionTitle(
                title = "Tu cuenta",
                subtitle = "Datos personales, preferencias y seguridad.",
            )
            ProfileMenuRow(
                title = "Editar perfil",
                subtitle = "Nombre, cuenta, teléfono, dirección y contacto de emergencia",
                icon = Icons.Rounded.Edit,
                onClick = onOpenEdit,
            )
            ProfileMenuRow(
                title = "Murco Loyalty",
                subtitle = "Niveles, puntos y premios disponibles",
                icon = Icons.Rounded.EmojiEvents,
                onClick = onOpenLoyalty,
            )
            ProfileMenuRow(
                title = "Preferencias",
                subtitle = "Tema visual y permisos de la app",
                icon = Icons.Rounded.Settings,
                onClick = onOpenPreferences,
            )
            ProfileMenuRow(
                title = "Soporte",
                subtitle = "WhatsApp, redes, ubicación y políticas",
                icon = Icons.Rounded.SupportAgent,
                onClick = onOpenSupport,
            )
            ProfileMenuRow(
                title = "Acciones de la cuenta",
                subtitle = "Cerrar sesión o eliminar cuenta",
                icon = Icons.Rounded.Security,
                destructive = true,
                onClick = onOpenAccount,
            )
        }

        ProfileCard {
            SectionTitle(
                title = "Altos del Murco",
                subtitle = "Síguenos o encuentra la ubicación del restaurante.",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { uriHandler.openUri(ProfileLinks.instagram) },
                    label = { Text("Instagram") },
                    leadingIcon = { Icon(Icons.Rounded.OpenInNew, contentDescription = null) },
                )
                AssistChip(
                    onClick = { uriHandler.openUri(ProfileLinks.tiktok) },
                    label = { Text("TikTok") },
                    leadingIcon = { Icon(Icons.Rounded.OpenInNew, contentDescription = null) },
                )
            }
            OutlinedButton(
                onClick = { uriHandler.openUri(ProfileLinks.maps) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.Map, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Abrir ubicación")
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ProfileHeaderCard(
    state: ProfileUiState,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
) {
    ProfileCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileAvatar(
                    avatarBytes = state.avatarBytes,
                    initials = state.initials,
                    isLoading = state.isLoadingAvatar || state.isUploadingProfileImage,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.hasProfileImage) {
                        IconButton(
                            onClick = onRemoveImage,
                            enabled = !state.isUploadingProfileImage,
                            modifier = Modifier
                                .size(42.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape),
                        ) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = "Eliminar foto",
                                tint = MaterialTheme.colorScheme.onError,
                            )
                        }
                    }

                    IconButton(
                        onClick = onPickImage,
                        enabled = !state.isUploadingProfileImage,
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                    ) {
                        Icon(
                            Icons.Rounded.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.emailText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Miembro desde ${state.memberSinceText}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Teléfono",
                    value = state.phoneText,
                    icon = Icons.Rounded.Phone,
                )
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Cumpleaños",
                    value = state.birthdayText,
                    icon = Icons.Rounded.Cake,
                )
            }

            InfoRow(
                title = "Dirección",
                value = state.addressText,
                icon = Icons.Rounded.Home,
            )
            InfoRow(
                title = "Contacto de emergencia",
                value = state.emergencyContactText,
                icon = Icons.Rounded.AccountCircle,
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    avatarBytes: ByteArray?,
    initials: String,
    isLoading: Boolean,
) {
    val imageBitmap = remember(avatarBytes) {
        avatarBytes?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), CircleShape),
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.34f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.5.dp,
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(
    state: ProfileUiState,
    onOpenLoyalty: () -> Unit,
) {
    ProfileCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(
                title = "Resumen",
                subtitle = "Solo cuentan pedidos y reservas completadas.",
                modifier = Modifier.weight(1f),
            )
            if (state.isLoadingStats) CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
        }

        LevelSummaryCard(stats = state.stats, onClick = onOpenLoyalty)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Puntos",
                value = state.stats.points.toString(),
                icon = Icons.Rounded.Star,
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Pedidos",
                value = state.stats.completedOrders.toString(),
                icon = Icons.Rounded.Restaurant,
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Reservas",
                value = state.stats.completedBookings.toString(),
                icon = Icons.Rounded.Explore,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Restaurante",
                value = state.stats.restaurantSpent.priceText(),
                icon = Icons.Rounded.ShoppingBag,
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Aventura",
                value = state.stats.adventureSpent.priceText(),
                icon = Icons.Rounded.EventAvailable,
            )
        }
    }
}

@Composable
private fun LevelSummaryCard(
    stats: ProfileStats,
    onClick: () -> Unit,
) {
    val level = stats.level
    val next = level.nextLevel
    val progress = LoyaltyLevel.progress(stats.totalSpent).toFloat()
    val remaining = next?.let { (it.minimumSpent - stats.totalSpent).coerceAtLeast(0.0) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
//        colors = CardDefaults.elevatedCardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nivel ${level.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = level.badgeSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                    )
                }
                Icon(Icons.Rounded.OpenInNew, contentDescription = null)
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = if (next == null) {
                    "Ya estás en el máximo nivel. Total acumulado: ${stats.totalSpent.priceText()}"
                } else {
                    "Te faltan ${remaining?.priceText()} para llegar a ${next.title}."
                },
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun EditProfileScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onBirthdayChanged: (java.util.Date) -> Unit,
    onAddressChanged: (String) -> Unit,
    onEmergencyNameChanged: (String) -> Unit,
    onEmergencyPhoneChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    val edit = state.editState ?: return
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    ) { Text("Cancelar") }
                    Button(
                        onClick = onSave,
                        enabled = edit.canSave && !state.isSavingProfile,
                        modifier = Modifier.weight(1.4f),
                    ) {
                        if (state.isSavingProfile) CircularProgressIndicator(
                            modifier = Modifier.size(
                                18.dp
                            ), strokeWidth = 2.dp
                        )
                        else Text("Guardar")
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScreenHeader(
                title = "Editar perfil",
                subtitle = "Esta información se usa para pedidos, reservas, beneficios y contacto.",
                onBack = onBack,
            )

            ProfileCard {
                SectionTitle("Datos personales", "Los campos obligatorios deben estar completos.")
                OutlinedTextField(
                    value = edit.fullName,
                    onValueChange = onFullNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                )
                OutlinedTextField(
                    value = state.emailText,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    singleLine = true,
                    label = { Text("Correo") },
                    leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.phoneNumber,
                    onValueChange = onPhoneChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("WhatsApp") },
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                )
                OutlinedButton(
                    onClick = { showBirthdayPicker(context, edit, onBirthdayChanged) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(edit.birthday.formatDateLong())
                }
            }

            ProfileCard {
                SectionTitle(
                    "Ubicación y emergencia",
                    "Ayuda al equipo a coordinar mejor cualquier visita."
                )
                OutlinedTextField(
                    value = edit.address,
                    onValueChange = onAddressChanged,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    label = { Text("Dirección") },
                    leadingIcon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.emergencyContactName,
                    onValueChange = onEmergencyNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Contacto de emergencia") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.emergencyContactPhone,
                    onValueChange = onEmergencyPhoneChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Teléfono de emergencia") },
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                )
            }

            Spacer(Modifier.height(86.dp))
        }
    }
}

@Composable
private fun LoyaltyProgramScreen(
    stats: ProfileStats,
    onBack: () -> Unit,
) {
    val wallet = stats.wallet
    val level = stats.level
    val next = level.nextLevel
    val progress = LoyaltyLevel.progress(stats.totalSpent).toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Murco Loyalty",
            subtitle = "Niveles, puntos y premios automáticos del restaurante y aventura.",
            onBack = onBack,
        )

        ProfileCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Nivel ${level.title}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(level.badgeSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider()
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    Modifier.weight(1f),
                    "Total",
                    stats.totalSpent.priceText(),
                    Icons.Rounded.Star
                )
                StatTile(
                    Modifier.weight(1f),
                    "Puntos",
                    stats.points.toString(),
                    Icons.Rounded.EmojiEvents
                )
            }

            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            Text(
                text = if (next == null) {
                    "Ya estás en la cima del programa."
                } else {
                    "Próximo nivel: ${next.title}. Te faltan ${
                        (next.minimumSpent - stats.totalSpent).coerceAtLeast(
                            0.0
                        ).priceText()
                    }."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ProfileCard {
            SectionTitle(
                "Beneficios actuales",
                "Esto es lo que representa tu nivel ${level.title}."
            )
            level.benefits.forEach { benefit ->
                InfoRow(
                    title = benefit,
                    value = level.spendRangeText,
                    icon = Icons.Rounded.CheckCircle
                )
            }
        }

        RewardTemplateSection(
            title = "Premios disponibles",
            subtitle = "Se aplican automáticamente cuando tu pedido o reserva cumple la regla.",
            emptyText = "Todavía no tienes premios automáticos disponibles para tu nivel.",
            rows = wallet.availableTemplates.map { template ->
                RewardRowData(
                    title = template.title,
                    subtitle = template.subtitle,
                    value = template.displaySummary,
                )
            },
        )

        RewardEventSection(
            title = "Premios reservados",
            subtitle = "Ya están apartados en pedidos o reservas pendientes.",
            emptyText = "No tienes premios reservados ahora mismo.",
            events = wallet.reservedEvents,
            status = LoyaltyWalletEventStatus.RESERVED,
        )

        RewardEventSection(
            title = "Historial de premios usados",
            subtitle = "Beneficios ya consumidos.",
            emptyText = "Todavía no has usado premios.",
            events = wallet.consumedEvents,
            status = LoyaltyWalletEventStatus.CONSUMED,
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun PreferencesScreen(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Preferencias",
            subtitle = "Apariencia y ajustes del dispositivo.",
            onBack = onBack,
        )

        ProfileCard {
            SectionTitle("Apariencia", "Elige cómo quieres ver Altos del Murco.")
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = currentThemeMode == mode,
                    onClick = { onThemeModeSelected(mode) },
                    label = { Text(mode.displayTitle()) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (mode) {
                                ThemeMode.SYSTEM -> Icons.Rounded.AutoMode
                                ThemeMode.LIGHT -> Icons.Rounded.LightMode
                                ThemeMode.DARK -> Icons.Rounded.DarkMode
                            },
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        ProfileCard {
            ProfileMenuRow(
                title = "Permisos de la app",
                subtitle = "Notificaciones, cámara, imágenes y ajustes del dispositivo",
                icon = Icons.Rounded.Palette,
                onClick = {
                    val intent = Intent(
                        AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )

                    context.startActivity(intent)
                },
            )
            Text(
                text = "Para cambiar permisos, abre Ajustes > Apps > Altos del Murco en tu dispositivo Android.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SupportScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Soporte",
            subtitle = "Contactos, redes sociales y documentos de la app.",
            onBack = onBack,
        )
        ProfileCard {
            ProfileMenuRow(
                "WhatsApp",
                "Escríbenos para reservas o ayuda",
                Icons.Rounded.Phone
            ) { uriHandler.openUri(ProfileLinks.whatsapp) }
            ProfileMenuRow(
                "Instagram",
                "@altosdelmurco",
                Icons.Rounded.OpenInNew
            ) { uriHandler.openUri(ProfileLinks.instagram) }
            ProfileMenuRow(
                "TikTok",
                "Videos, promociones y experiencias",
                Icons.Rounded.OpenInNew
            ) { uriHandler.openUri(ProfileLinks.tiktok) }
            ProfileMenuRow(
                "Facebook",
                "Comunidad y novedades",
                Icons.Rounded.OpenInNew
            ) { uriHandler.openUri(ProfileLinks.facebook) }
            ProfileMenuRow(
                "Ubicación",
                "Abrir en Google Maps",
                Icons.Rounded.Map
            ) { uriHandler.openUri(ProfileLinks.maps) }
        }
        ProfileCard {
            ProfileMenuRow(
                "Correo de soporte",
                "soporte@altosdelmurco.com",
                Icons.Rounded.Email
            ) { uriHandler.openUri(ProfileLinks.supportEmail) }
            ProfileMenuRow(
                "Privacidad",
                "Política de privacidad",
                Icons.Rounded.Security
            ) { uriHandler.openUri(ProfileLinks.privacyPolicy) }
            ProfileMenuRow(
                "Términos",
                "Términos y condiciones",
                Icons.Rounded.Info
            ) { uriHandler.openUri(ProfileLinks.terms) }
        }
    }
}

@Composable
private fun AccountActionsScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Acciones de la cuenta",
            subtitle = "Estas acciones afectan tu sesión y tu perfil.",
            onBack = onBack,
        )

        ProfileCard {
            DangerRow(
                title = "Cerrar sesión",
                subtitle = "Cierra tu sesión actual en este dispositivo",
                icon = Icons.Rounded.Logout,
                enabled = !state.isSigningOut && !state.isDeletingAccount,
                onClick = onSignOut,
            )
            DangerRow(
                title = "Eliminar cuenta",
                subtitle = "Elimina permanentemente tu cuenta y perfil",
                icon = Icons.Rounded.Delete,
                enabled = !state.isSigningOut && !state.isDeletingAccount,
                onClick = onDeleteAccount,
            )

            AnimatedVisibility(state.isSigningOut || state.isDeletingAccount) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun RewardTemplateSection(
    title: String,
    subtitle: String,
    emptyText: String,
    rows: List<RewardRowData>,
) {
    ProfileCard {
        SectionTitle(title, subtitle)
        if (rows.isEmpty()) {
            Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            rows.forEach { row ->
                InfoRow(
                    title = row.title,
                    value = "${row.subtitle}\n${row.value}",
                    icon = Icons.Rounded.EmojiEvents
                )
            }
        }
    }
}

@Composable
private fun RewardEventSection(
    title: String,
    subtitle: String,
    emptyText: String,
    events: List<LoyaltyWalletEvent>,
    status: LoyaltyWalletEventStatus,
) {
    ProfileCard {
        SectionTitle(title, subtitle)
        if (events.isEmpty()) {
            Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            events.forEach { event ->
                val referenceLabel = when (event.referenceType) {
                    LoyaltyRewardReferenceType.ORDER -> "Pedido"
                    LoyaltyRewardReferenceType.BOOKING -> "Reserva"
                }
                InfoRow(
                    title = event.templateTitle,
                    value = "$referenceLabel ${event.referenceId.take(8)} • ${event.amount.priceText()}",
                    icon = if (status == LoyaltyWalletEventStatus.RESERVED) Icons.Rounded.EventAvailable else Icons.Rounded.CheckCircle,
                )
            }
        }
    }
}

private data class RewardRowData(
    val title: String,
    val subtitle: String,
    val value: String,
)

@Composable
private fun ScreenHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    SeasonalCardContainer(
        sectionTheme = AppSectionTheme.Neutral,
        modifier = modifier.fillMaxWidth(),
        minHeightDp = 0,
        content = content,
    )
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompactInfoCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
//        colors = CardDefaults.elevatedCardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
//                alpha = 0.5f
//            )
//        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String,
    icon: ImageVector,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconBubble(icon)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
) {
    ElevatedCard(
        modifier = modifier.aspectRatio(1.15f),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBubble(icon, destructive)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Rounded.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DangerRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    destructive: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (destructive) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
    }
}

private fun showBirthdayPicker(
    context: Context,
    edit: EditProfileUiState,
    onPicked: (java.util.Date) -> Unit,
) {
    val calendar = Calendar.getInstance().apply { time = edit.birthday }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val picked = Calendar.getInstance().apply {
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onPicked(picked.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private fun Context.readBytes(uri: Uri): ByteArray? = runCatching {
    contentResolver.openInputStream(uri)?.use { it.readBytes() }
}.getOrNull()

private suspend fun runGoogleReauthentication(
    activity: Activity,
    onToken: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val credentialManager = CredentialManager.create(activity)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(clientId)
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result: GetCredentialResponse = credentialManager.getCredential(activity, request)
        val credential = result.credential
        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCredential.idToken)
        } else {
            onError("Credential Manager devolvió una credencial no compatible.")
        }
    } catch (_: GetCredentialCancellationException) {
        onError("Reautenticación cancelada.")
    } catch (_: NoCredentialException) {
        onError("No se encontró una cuenta de Google disponible.")
    } catch (error: GetCredentialException) {
        onError(error.message ?: "No se pudo reautenticar con Google.")
    } catch (error: Exception) {
        onError(error.message ?: "No se pudo reautenticar con Google.")
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}
