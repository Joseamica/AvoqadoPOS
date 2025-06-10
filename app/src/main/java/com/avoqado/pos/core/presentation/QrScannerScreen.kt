package com.avoqado.pos.core.presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.R
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import timber.log.Timber
import java.time.LocalDateTime
import java.util.concurrent.Executors

// Constantes para valores por defecto en la navegación desde QR
private const val DEFAULT_QR_WAITER = "QR_USER"
private const val DEFAULT_QR_SPLIT_TYPE = "FULLPAYMENT" // O el valor que tu sistema espere para un pago completo
private const val DEFAULT_QR_VENUE_NAME = "Establecimiento QR"

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    navigationDispatcher: NavigationDispatcher, // Para navegar hacia atrás o al link del QR
    snackbarDelegate: SnackbarDelegate, // Para mostrar mensajes
    sessionManager: SessionManager = AvoqadoApp.sessionManager,
    paymentRepository: PaymentRepository = AvoqadoApp.paymentRepository
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasScanned by remember { mutableStateOf(false) } // Para evitar múltiples escaneos

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Solicitar permiso si aún no se ha hecho o si fue denegado y no se debe mostrar justificación
    LaunchedEffect(key1 = cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear Código QR") },
                navigationIcon = {
                    IconButton(onClick = { navigationDispatcher.navigateBack() }) {
                        // Asegúrate de que este recurso exista o cámbialo
                        Icon(painterResource(id = R.drawable.baseline_close_24), contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (cameraPermissionState.status) {
                PermissionStatus.Granted -> {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraExecutor = Executors.newSingleThreadExecutor()

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageAnalyzer = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { qrCodeValue ->
                                            if (!hasScanned) {
                                                hasScanned = true // Prevenir múltiples escaneos/navegaciones
                                                Timber.d("QR Scanned: $qrCodeValue")
                                                try {
                                                    // Intentar convertir el valor del QR a Double para validar que es un número
                                                    val amount = qrCodeValue.toDouble()
                                                    // Format the numeric QR value to a string with two decimal places (e.g., 100 -> "100.00")
                                                    val formattedQrAmount = String.format("%.2f", amount)
                                                    
                                                    // Get current user information
                                                    val currentUser = sessionManager.getAvoqadoSession()
                                                    val venueName = sessionManager.getVenueInfo()?.name ?: DEFAULT_QR_VENUE_NAME
                                                    val waiterName = currentUser?.name ?: DEFAULT_QR_WAITER
                                                    
                                                    // Store payment information in cache (just like QuickPaymentViewModel does)
                                                    paymentRepository.setCachePaymentInfo(
                                                        PaymentInfoResult(
                                                            paymentId = "",
                                                            tipAmount = 0.0,
                                                            subtotal = amount,
                                                            rootData = "",
                                                            date = LocalDateTime.now(),
                                                            waiterName = waiterName,
                                                            tableNumber = "",  // Empty table number for QR payment
                                                            venueId = currentUser?.venueId ?: "",
                                                            splitType = SplitType.FULLPAYMENT,
                                                            billId = "",
                                                        )
                                                    )
                                                    
                                                    // Navigate to LeaveReview screen with proper arguments
                                                    navigationDispatcher.navigateWithArgs(
                                                        PaymentDests.LeaveReview,
                                                        NavigationArg.StringArg(
                                                            PaymentDests.LeaveReview.ARG_SUBTOTAL,
                                                            formattedQrAmount
                                                        ),
                                                        NavigationArg.StringArg(
                                                            PaymentDests.LeaveReview.ARG_WAITER,
                                                            waiterName
                                                        ),
                                                        NavigationArg.StringArg(
                                                            PaymentDests.LeaveReview.ARG_SPLIT_TYPE,
                                                            SplitType.FULLPAYMENT.value
                                                        ),
                                                        NavigationArg.StringArg(
                                                            PaymentDests.LeaveReview.ARG_VENUE_NAME,
                                                            venueName
                                                        )
                                                    )
                                                } catch (e: NumberFormatException) {
                                                    // Si qrCodeValue no es un número válido
                                                    Timber.e(e, "QR code content is not a valid amount: $qrCodeValue")
                                                    snackbarDelegate.showSnackbar(message = "QR inválido. Se esperaba un monto.")
                                                    // Permitir otro escaneo o navegar hacia atrás. Por ahora, navegar atrás.
                                                    // Para permitir otro escaneo, podrías hacer: hasScanned = false después de un delay.
                                                    navigationDispatcher.navigateBack()
                                                } catch (e: Exception) {
                                                    // Capturar otros errores inesperados durante el procesamiento o navegación
                                                    Timber.e(e, "Error processing QR code or navigating: $qrCodeValue")
                                                    snackbarDelegate.showSnackbar(message = "Error al procesar el QR.")
                                                    navigationDispatcher.navigateBack()
                                                }
                                            }
                                        })
                                    }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalyzer
                                    )
                                } catch (exc: Exception) {
                                    Timber.e(exc, "Error al vincular casos de uso de la cámara")
                                    snackbarDelegate.showSnackbar(message = "Error al iniciar la cámara.")
                                    navigationDispatcher.navigateBack()
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is PermissionStatus.Denied -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                            "Para escanear códigos QR, necesitamos acceso a tu cámara. Por favor, concede el permiso."
                        } else {
                            "El permiso de cámara fue denegado. Para usar esta función, necesitas habilitarlo desde los ajustes de la aplicación."
                        }
                        Text(textToShow, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Otorgar Permiso")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navigationDispatcher.navigateBack() }) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

private class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        // Tomar el primer código de barras detectado
                        barcodes.first()?.rawValue?.let {
                            onQrCodeScanned(it)
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.e(it, "Error en el escaneo de código de barras")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
