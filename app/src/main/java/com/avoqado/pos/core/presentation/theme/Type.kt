package com.avoqado.pos.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R

object AppFont {
    val SourceSansProFamily = FontFamily(
        Font(R.font.sourcesanspro_regular),
        Font(R.font.sourcesanspro_bold, FontWeight.Bold),
        Font(R.font.sourcesanspro_semibold, FontWeight.SemiBold)
    )
}
// Set of Material typography styles to start with
private val defaultTypography = Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
)

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.SourceSansProFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.SourceSansProFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.SourceSansProFamily),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.SourceSansProFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.SourceSansProFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.SourceSansProFamily),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.SourceSansProFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.SourceSansProFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.SourceSansProFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.SourceSansProFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.SourceSansProFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.SourceSansProFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.SourceSansProFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.SourceSansProFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.SourceSansProFamily)
)