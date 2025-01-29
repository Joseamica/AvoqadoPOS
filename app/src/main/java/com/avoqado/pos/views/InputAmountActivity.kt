package com.avoqado.pos.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.core.viewmodel.InputAmountViewModel
import com.avoqado.pos.ui.screen.AmountScreen
import com.avoqado.pos.ui.screen.ToolbarWithIcon
import com.menta.android.core.model.OperationType
import com.urovo.i9000s.api.emv.ContantPara

class InputAmountActivity : ComponentActivity() {
    private lateinit var viewModel: InputAmountViewModel

    private val operationType: String by lazy {
        (intent.getStringExtra("operationType") ?: OperationType.PAYMENT.name).toString()
    }
    private val currency: String by lazy {
        (intent.getStringExtra("currency") ?: CURRENCY_LABEL).toString()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewModel = InputAmountViewModel(this, ValidateAmountUseCase())

        setContent {
            Scaffold(
                topBar = {
                    ToolbarWithIcon(
                        "Importe"
                    )
                },
                content = { AmountScreen(viewModel) }
            )

        }
        observer()

    }

    private fun observer() {
        viewModel.isValidAmount.observe(this) { amount ->
            if (amount != null) {
                Log.i(TAG, "Ready to read card")
                val intent = Intent(this, CardProcessActivity::class.java)
                intent.putExtra("amount", amount)
                intent.putExtra("currency", currency)
                intent.putExtra("operationType", operationType)
                startActivity(intent)
            }
        }

    }

    companion object {
        const val TAG = "InputAmountActivity"
    }
}