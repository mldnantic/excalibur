package com.example.mobilnibus.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilnibus.MainActivity
import com.example.mobilnibus.model.UserModel
import com.example.mobilnibus.viemodels.FormViewModel
import com.example.mobilnibus.viemodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest

@Composable
fun StartScreen(auth: FirebaseAuth, mainActivity: MainActivity, formViewModel: FormViewModel,
                userViewModel: UserViewModel, startSvc:()->Unit, navigateToMap:()->Unit) {

    fun createUserWithEmailAndPassword(auth: FirebaseAuth, mainActivity: MainActivity) {
        if(
            formViewModel.email!=""&&
            formViewModel.password!=""&&
            formViewModel.username!=""&&
            formViewModel.ime!=""&&
            formViewModel.prezime!=""&&
            formViewModel.telefon!=""
        )
        {
            auth.createUserWithEmailAndPassword(formViewModel.email,formViewModel.password)
                .addOnCompleteListener(mainActivity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        val profileUpdates = userProfileChangeRequest {
                            displayName = formViewModel.username
                        }

                        user!!.updateProfile(profileUpdates)

                        userViewModel.addUser(user.uid,
                            formViewModel.ime,formViewModel.prezime,formViewModel.telefon)
                        userViewModel.setCurrentUser(
                            UserModel(uid = user.uid,
                                firstName = formViewModel.ime,
                                lastName = formViewModel.prezime,
                                phone = formViewModel.telefon)
                        )
                        formViewModel.reset()
                        navigateToMap()
                    } else {
                        Toast.makeText(
                            mainActivity,
                            "Registration failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        else
        {
            Toast.makeText(
                mainActivity,
                "Please fill in all fields",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun signInWithEmailAndPassword(auth: FirebaseAuth, mainActivity: MainActivity,
        email: String,
        password: String) {
        
        if(formViewModel.email!="" && formViewModel.password!="") {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mainActivity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            userViewModel.getUser(user.uid)
                        }
                        formViewModel.reset()
                        navigateToMap()
                    } else {
                        Toast.makeText(
                            mainActivity,
                            "Login failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
        else
        {
            Toast.makeText(
                mainActivity,
                "Please fill in email and password",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    if (auth.currentUser != null) {
        navigateToMap()
    }

    Surface(color = Color.Black) {
        Surface(
            color = Color.White,
//            modifier = Modifier.padding(0.dp, 72.dp, 0.dp, 72.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = "MobilniBus",
                    fontSize = 64.sp,
                    style = TextStyle(
                        brush = Brush.linearGradient(colors = listOf(Color.Magenta, Color.Cyan),)
                    ),
                    fontFamily = FontFamily.Cursive
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp),
                    value = formViewModel.email,
                    singleLine = true,
                    onValueChange = { formViewModel.email = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp),
                    value = formViewModel.username,
                    singleLine = true,
                    onValueChange = { formViewModel.username = it },
                    label = { Text("Username") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp),
                    value = formViewModel.password,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { formViewModel.password = it },
                    label = { Text("Password") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp),
                    value = formViewModel.ime,
                    singleLine = true,
                    onValueChange = { formViewModel.ime = it },
                    label = { Text("Ime") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp),
                    value = formViewModel.prezime,
                    singleLine = true,
                    onValueChange = { formViewModel.prezime = it },
                    label = { Text("Prezime") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp),
                    value = formViewModel.telefon,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    onValueChange = { formViewModel.telefon = it },
                    label = { Text("Telefon") }
                )
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp, 0.dp)) {
                    ElevatedButton(
                        onClick = {
                            createUserWithEmailAndPassword(
                                auth,
                                mainActivity
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text(text = "Register")
                    }
                    ElevatedButton(
                        onClick = {
                            signInWithEmailAndPassword(
                                auth,
                                mainActivity,
                                formViewModel.email,
                                formViewModel.password
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text(text = "Login")
                    }
                }

                Text(
                    modifier = Modifier.padding(32.dp),
                    text = "Da bi ste mogli da koristite aplikaciju potrebno je da se registrujete",
                    fontSize = 10.sp,
                    color = Color.DarkGray
                )
                Text(text = "©Mladen Antic 2024")
            }
        }
    }
}