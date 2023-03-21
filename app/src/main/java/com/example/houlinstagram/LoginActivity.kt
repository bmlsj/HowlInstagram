package com.example.houlinstagram

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        // 이메일 로그인 버튼
        val emailLoginButton: Button = findViewById(R.id.email_login_button)
        emailLoginButton.setOnClickListener {
            signinAndSignup()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        // 사용자 정보를 받아서 다루기
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account)
                    } catch (e: ApiException) {
                        Log.d("googleLogin", "google Login Failed")
                    }

                }
            }


        // 구글 로그인 버튼
        val googleSignInBtn: Button = findViewById(R.id.google_sign_btn)
        googleSignInBtn.setOnClickListener {
            // First step
            // google 로그인 화면으로 이동
            resultLauncher.launch(googleSignInClient!!.signInIntent)

        }

    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    moveMainPage(task.result?.user)
                }
                else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }


    // 회원 가입 코드
    private fun signinAndSignup() {

        val emailEdittext: EditText = findViewById(R.id.email_edittext)
        val passwordEdittext: EditText = findViewById(R.id.password_edittext)

        auth?.createUserWithEmailAndPassword(
            emailEdittext.text.toString(),
            passwordEdittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) { // id가 생성되었을 때
                    moveMainPage(task.result?.user)
                } else if (!task.exception?.message.isNullOrEmpty()) {
                    // id 생성에 실패했을 때(로그인 에러), error message를 생성
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // 회원가입이나 에러가 아닐 경우, 로그인
                    singinEmail()
                }
            }
    }

    // 로그인
    fun singinEmail() {

        val emailEdittext: EditText = findViewById(R.id.email_edittext)
        val passwordEdittext: EditText = findViewById(R.id.password_edittext)
        auth?.signInWithEmailAndPassword(
            emailEdittext.text.toString(),
            passwordEdittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) { // id와 pw가 일치해 로그인 되었을 때
                    moveMainPage(task.result?.user)
                } else {
                    // id와 pw를 틀렸을 때
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // 로그인 성공시, 다음 페이지로 넘어가는 함수
    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) { // user가 firebase에 있을 경우, main 페이지로 이동
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


}