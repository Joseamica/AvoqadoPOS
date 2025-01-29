package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.viewmodel.InstallmentViewModel
import com.avoqado.pos.ui.screen.PrimaryButton
import com.avoqado.pos.core.presentation.theme.primary
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.model.bin.InstallmentsApp

class InstallmentsActivity : ComponentActivity() {
    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    private val viewmodel = InstallmentViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Screen {
                val installments: ArrayList<InstallmentsApp>? =
                    intent.getParcelableArrayListExtra("installment_list")

                InstallmentScreen(installments!!)
            }
        }

        viewmodel.installmentSelected.observe(this) {
            Log.i(TAG, "Ir a procesar el pago")
            val intent = Intent(this, DoPaymentActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Composable
    fun Screen(content: @Composable () -> Unit) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column {
                    TopAppBar(
                        title = { Text(text = "Cuotas disponibles") },
                        backgroundColor = primary
                    )
                    content()
                }
            }
        }
    }

    @Composable
    fun InstallmentScreen(installmentWithTotals: List<InstallmentsApp>) {
        var selectedInstallment by remember { mutableStateOf<InstallmentsApp?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(installmentWithTotals) { installment ->
                    val isSelected = installment == selectedInstallment
                    val backgroundColor = if (isSelected) Color.LightGray else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(backgroundColor)
                            .clickable { selectedInstallment = installment },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${installment.installmentLabel} " +
                                    "${when (installment.type) {
                                        "Planes estandar" -> "ESTÁNDAR"
                                        "Planes especiales" -> "CUOTA SIMPLE"
                                        else -> ""
                                    }} " +
                                    installment.installmentAmountLabel,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text =installment.totalAmountLabel,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Divider()
                }
            }

            PrimaryButton(
                text = "Continuar",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(57.dp)
                    .align(Alignment.End),
                onClick = {
                    Log.i("", "Selección: ${selectedInstallment?.installmentLabel}")
                    selectedInstallment?.let {
                        viewmodel.setInstallmentSelected(selectedInstallment!!)
                    }
                }
            )
        }
    }


    companion object {
        const val TAG = "InstallmentsActivity"
    }
}