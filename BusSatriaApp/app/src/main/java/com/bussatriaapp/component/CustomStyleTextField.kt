package com.bussatriaapp.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomStyleTextField(
    placeHolder: String,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector? = null,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation,
    value: String,
    onValueChange: (String) -> Unit,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    errorColor: Color, // Explicitly specify the type for errorColor
    errorMessage: String? = null, // Explicitly specify the type for errorMessage
    showTrailingIcon: Boolean = true
) {
    val textState = remember { mutableStateOf(TextFieldValue(text = value)) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
        value = textState.value,
        onValueChange = { valueChanged ->
            textState.value = valueChanged
            onValueChange(valueChanged.text)
            isError = keyboardType == KeyboardType.Password && !isValidPassword(valueChanged.text) // Validasi password hanya jika tipe keyboard adalah password
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        placeholder = { Text(text = placeHolder, color = textColor) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
            )
        },
        trailingIcon = {
            if (showTrailingIcon && trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                        .clickable {
                            isPasswordVisible = !isPasswordVisible
                        }
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) errorColor else borderColor,
            unfocusedBorderColor = if (isError) errorColor else borderColor,
            textColor = textColor,
            backgroundColor = Color.Transparent // Set background color to transparent
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(color = textColor, fontSize = 16.sp),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else visualTransformation,
        isError = isError,
        singleLine = true,

        )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = errorColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}
fun isValidPassword(password: String): Boolean {
    val pattern: Regex = Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,}$")
    return pattern.matches(password)
}

