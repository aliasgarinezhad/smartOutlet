package ir.noavar.smartoutlet.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

/*private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)*/

private val LightColorPalette = lightColors(
    primary = Blue,
    background = Background,
    secondaryVariant = Blue,
    error = errorColor,

    //primaryVariant = Purple700,
    //secondary = Teal200

/*    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,*/

)

@Composable
fun MyApplicationTheme(darkTheme: Boolean = false/*isSystemInDarkTheme()*/, content: @Composable () -> Unit) {
    /*val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }*/

    val colors = LightColorPalette
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}