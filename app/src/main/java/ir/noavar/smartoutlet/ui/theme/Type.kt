package ir.noavar.smartoutlet.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.noavar.smartoutlet.R

// Set of Material typography styles to start with
val Typography = Typography(

    body1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    defaultFontFamily = FontFamily(Font(R.font.sans_regular)),

    button = TextStyle(
        fontFamily = FontFamily(Font(R.font.sans_regular)),
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily(Font(R.font.sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
)