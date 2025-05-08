package com.avoqado.pos.features.payment.presentation.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.features.payment.presentation.review.components.ReviewToolbar
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview

@Composable
fun LeaveReviewScreen(leaveReviewViewModel: LeaveReviewViewModel) {
    val rating by leaveReviewViewModel.rating.collectAsStateWithLifecycle()

    LeaveReviewContent(
        venueName = leaveReviewViewModel.venueName,
        subtotal = leaveReviewViewModel.subtotal,
        onRatingSelected = leaveReviewViewModel::setRating,
        onSkipReview = leaveReviewViewModel::skipReview,
        onNavigateBack = leaveReviewViewModel::navigateBack
    )
}

@Composable
fun LeaveReviewContent(
    venueName: String,
    subtotal: String,
    onRatingSelected: (ReviewRating) -> Unit,
    onSkipReview: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        ReviewToolbar(
            subtotal = subtotal,
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.CANCEL,
            ),
            onAction = {
                onNavigateBack()
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Payment amount is now displayed in the toolbar
            
            // Restaurant logo
            Text(
                text = venueName,
                color = Color(0xFFAA3333),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
//            Text(
//                text = venueName,
//                color = Color(0xFFAA3333),
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )

            // Question text
            Text(
                text = "como fue",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            
            Text(
                text = "tu experiencia con",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            
            Text(
                text = venueName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Spacer(modifier = Modifier.height(26.dp))
            // Rating options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Poor rating
                RatingOption(
                    emoji = "🤔",
                    label = "mala",
                    onClick = { onRatingSelected(ReviewRating.POOR) }
                )
                
                // Good rating
                RatingOption(
                    emoji = "🙂",
                    label = "super",
                    onClick = { onRatingSelected(ReviewRating.GOOD) }
                )
                
                // Excellent rating
                RatingOption(
                    emoji = "🥰",
                    label = "excelente",
                    onClick = { onRatingSelected(ReviewRating.EXCELLENT) }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Skip button
            TextButton(
                onClick = { onSkipReview() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "Skip",
                        tint = Color.Gray
                    )
                    
                    Text(
                        text = "saltar esta etapa",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingOption(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun LeaveReviewScreenPreview() {
    AvoqadoTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

            
            LeaveReviewContent(
                venueName = "Bouillon Pigalle",
                subtotal = "299.99",
                onRatingSelected = {},
                onSkipReview = {},
                onNavigateBack = {}
            )
        }
    }
}
