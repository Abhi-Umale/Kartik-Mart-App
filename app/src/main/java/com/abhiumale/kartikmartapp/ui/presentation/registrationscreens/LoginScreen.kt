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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSelected by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginSuccess = viewModel.loginSuccess
    val userRole = viewModel.userRole

    LaunchedEffect(loginSuccess, userRole) { // Dono dependencies add karein
        if (loginSuccess && userRole != null) {
            Toast.makeText(context, "Login Success as $userRole ✅", Toast.LENGTH_SHORT).show()

            if (userRole == "ADMIN") {
                navController.navigate(Routs.AdminHomeRouts) {
                    popUpTo(Routs.LoginRouts) { inclusive = true }
                }
            } else {
                navController.navigate(Routs.HomeRouts) {
                    popUpTo(Routs.LoginRouts) { inclusive = true }
                }
            }
            viewModel.clearState()
        }
    }

    LaunchedEffect(viewModel.loginError) {
        viewModel.loginError?.let { error ->
            Toast.makeText(context, "Login Failed: $error ❌", Toast.LENGTH_LONG).show()
            viewModel.clearState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                Text(
                    "Welcome, Login Here!!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                    trailingIcon = {
                        if (email.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Valid",
                                tint =
                                    if (isEmailFocused && email.contains("@")) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
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

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .onFocusChanged {
                            isPasswordFocused = it.isFocused
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    label = { Text("Password", fontSize = 14.sp) },
                    placeholder = { Text("Enter Your Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
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
                                    else MaterialTheme.colorScheme.surface
                            )
                        }
                    },
                    singleLine = true,
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

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        " FORGOT PASSWORD ",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(end = 25.dp, top = 10.dp)
                            .clickable(onClick = { showDialog = true }),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text("Reset Password",
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        },
                        text = {
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                },
                                textStyle = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                singleLine = true,
                                label = {
                                    Text("Enter your email")
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.forgotPassword(email)
                                    showDialog = false
                                    Toast.makeText(context,"Reset Link Sent to your mail-📩",Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Send Reset Link",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                }
                            ) {
                                Text("Cancel",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.clearState() // Pehle purani errors hatao
                        when {
                            email.isEmpty() -> {
                                Toast.makeText(context, "Email cannot be empty ❌", Toast.LENGTH_SHORT).show()
                            }
                            password.isEmpty() -> {
                                Toast.makeText(context, "Password cannot be empty ❌", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.login(email, password)
                            }
                        }
                    },
                    enabled = !viewModel.isLoading && email.isNotEmpty() && password.length >= 6,
                    modifier = Modifier.width(150.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (viewModel.isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Login", fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row() {
                    Text(
                        "Don't have an account, ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Create Account",
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate(Routs.RegisterRouts)
                        }),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
    }
}