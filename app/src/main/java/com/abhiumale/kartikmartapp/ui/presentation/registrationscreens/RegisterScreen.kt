package com.abhiumale.kartikmartapp.ui.presentation.registrationscreens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.R
import com.abhiumale.kartikmartapp.ui.navigation.Routs

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    var isUserFocused by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPhoneNumberFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isCPasswordFocused by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LaunchedEffect(viewModel.registerSuccess) {
            if (viewModel.registerSuccess) {
                Toast.makeText(context, "Registration Successful ✅", Toast.LENGTH_SHORT).show()
                navController.navigate(Routs.LoginRouts) {
                    popUpTo(Routs.RegisterRouts) { inclusive = true }
                }
                viewModel.clearState()
            }
        }

        LaunchedEffect(viewModel.registerError) {
            viewModel.registerError?.let { error ->
                Toast.makeText(context, "Error: $error ❌", Toast.LENGTH_LONG).show()
                viewModel.clearState()
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //welcome message
                Text(
                    "Welcome, Register Here!!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                //username
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .onFocusChanged() {
                            isUserFocused = it.isFocused
                        },
                    label = { Text("UserName", fontSize = 14.sp) },
                    placeholder = { Text("Enter UserName") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "UserName"
                        )
                    },
                    trailingIcon = {
                        if (userName.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Valid",
                                tint =
                                    if (isUserFocused && userName.length >= 4) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                //Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .onFocusChanged {
                            isEmailFocused = it.isFocused
                        },
                    label = { Text("E-mail", fontSize = 14.sp) },
                    placeholder = { Text("Enter Your E-mail") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (email.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Valid",
                                tint =
                                    if (isEmailFocused && email.contains("@")) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                //Phone number
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (phoneNumber.length <= 10) phoneNumber = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .onFocusChanged {
                            isPhoneNumberFocused = it.isFocused
                        },
                    label = { Text("Phone Number", fontSize = 14.sp) },
                    placeholder = { Text("Enter Your Phone Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Phone Number"
                        )
                    },
                    trailingIcon = {
                        if (phoneNumber.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Valid",
                                tint =
                                    if (isPhoneNumberFocused && phoneNumber.length == 10) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                //Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .onFocusChanged {
                            isPasswordFocused = it.isFocused
                        },
                    label = { Text("Password", fontSize = 14.sp) },
                    placeholder = { Text("Enter Your Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            },
                        ) {
                            Icon(
                                painter =
                                    if (passwordVisible)
                                        painterResource(R.drawable.outline_blur_on_24)
                                    else
                                        painterResource(R.drawable.outline_blur_off_24),
                                contentDescription = "Toggle Password",
                                tint =
                                    if (isPasswordFocused) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                //Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .onFocusChanged {
                            isCPasswordFocused = it.isFocused
                        },
                    label = { Text("Confirm Password", fontSize = 14.sp) },
                    placeholder = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "CPassword"
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            },
                        ) {
                            Icon(
                                painter =
                                    if (confirmPasswordVisible)
                                        painterResource(R.drawable.outline_blur_on_24)
                                    else
                                        painterResource(R.drawable.outline_blur_off_24),
                                contentDescription = "Toggle Password",
                                tint =
                                    if (isCPasswordFocused) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    visualTransformation =
                        if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        android.util.Log.d("AUTH_DEBUG", "Register Button Clicked") // LOG 4
                        if (userName.isNotEmpty() &&
                            email.isNotEmpty() &&
                            phoneNumber.isNotEmpty() &&
                            password.isNotEmpty() &&
                            confirmPassword.isNotEmpty()
                        ) {
                            android.util.Log.d("AUTH_DEBUG", "Validation Passed, calling ViewModel") // LOG 5
                            viewModel.register(email, password, userName, phoneNumber)
                        } else {
                            android.util.Log.d("AUTH_DEBUG", "Validation Failed") // LOG 6
                            when {
                                userName.isEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        "Please Enter UserName !!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                email.isEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        "Email cannot be empty ❌",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                phoneNumber.isEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        "Please Enter Your Phone number !!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                password.isEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        "Password cannot be empty ❌",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                password.length < 6 -> {
                                    Toast.makeText(
                                        context,
                                        "Password must be at least 6 characters ❌",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                confirmPassword.isEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        "Confirm Password cannot be empty ❌",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                password != confirmPassword -> {
                                    Toast.makeText(
                                        context,
                                        "Passwords not match ❌ \n Please Enter same Password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> { }
                            }
                        }
                    },
                    modifier = Modifier.width(180.dp),
                    enabled = !viewModel.isLoading && email.isNotEmpty() && password.length >= 6,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = Color.Transparent
                    )
                ) {
                    if (viewModel.isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Register", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row {
                    Text(
                        "Already have an account, ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Log-in",
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate(Routs.LoginRouts)
                        }),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}