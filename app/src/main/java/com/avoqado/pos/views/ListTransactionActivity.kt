package com.avoqado.pos.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.viewmodel.ListTransactionViewModel
import com.avoqado.pos.core.presentation.components.ToolbarWithIcon
import com.avoqado.pos.core.presentation.theme.primary
import com.avoqado.pos.core.presentation.theme.red
import com.menta.android.common_cross.data.datasource.local.model.Transaction
import com.menta.android.core.utils.StringUtils.formatAmount

class ListTransactionActivity : ComponentActivity() {

    lateinit var viewModel: ListTransactionViewModel

    private val transactions: ArrayList<Transaction> by lazy {
        intent.getSerializableExtra("transactionList") as ArrayList<Transaction>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ListTransactionViewModel()
        setContent {
            ViewContainer()
        }
    }

    override fun onBackPressed() {
    }

    @Preview
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun ViewContainer() {
        val selectedTransaction = remember { mutableStateOf<Transaction?>(null) }

        Scaffold(
            topBar = {
                ToolbarWithIcon(
                    "Transacciones",
                    IconAction(
                        flowStep = FlowStep.GO_TO_MENU,
                        context = this,
                        iconType = IconType.BACK
                    )
                )
            },
            content = {
                TransactionList(transactions) { transaction ->
                    selectedTransaction.value = transaction
                }
            }
        )
        selectedTransaction.value?.let { transaction ->
            Log.i(TAG, "transaction selected: $transaction")
            goToReadCard(transaction)
        }
    }

    @Composable
    private fun TransactionList(
        transactions: ArrayList<Transaction>,
        onTransactionSelected: (Transaction) -> Unit
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFF6F7F9)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(56.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions) { transaction ->
                            TransactionItem(transaction, onTransactionSelected)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TransactionItem(
        transaction: Transaction,
        onTransactionSelected: (Transaction) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = 20.dp)
                )
                .padding(14.dp)
                .clickable { onTransactionSelected(transaction) }
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_bag),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(18.dp),
                tint = primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.getTransactionType(transaction.operation.type),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = viewModel.getDateTime(transaction.operation.datetime),
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                when (transaction.operation.type) {
                    "PAYMENT" -> {
                        Text(
                            text = "$${formatAmount(transaction.amount)}",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    else -> {
                        Text(
                            text = "$${formatAmount(transaction.amount)}",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                when (transaction.operation.status) {
                    "APPROVED" -> {
                        Text(
                            text = viewModel.getStatus(transaction.operation.status),
                            style = TextStyle(fontSize = 14.sp, color = primary),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    else -> {
                        Text(
                            text = viewModel.getStatus(transaction.operation.status),
                            style = TextStyle(fontSize = 14.sp, color = red),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

            }
        }
    }

    private fun goToReadCard(transaction: Transaction) {
        val intent =
            Intent(this, CardProcessActivity::class.java)
        intent.putExtra("amount", transaction.operation.amount)
        intent.putExtra("transaction", transaction)
        intent.putExtra("operationType", "REFUND")
        intent.putExtra("currency", CURRENCY_LABEL)
        startActivity(intent)
    }

    companion object {
        const val TAG = "ListTransactionActivity"
    }
}