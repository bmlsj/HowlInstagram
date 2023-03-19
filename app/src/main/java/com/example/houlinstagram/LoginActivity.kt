package com.example.houlinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val emailLoginButton : Button = findViewById(R.id.email_login_button)

        emailLoginButton.setOnClickListener {
            signinAndSignup()
        }
    }


    // 회원 가입 코드
    private fun signinAndSignup(){

        val emailEdittext : EditText = findViewById(R.id.email_edittext)
        val passwordEdittext : EditText = findViewById(R.id.password_edittext)

        auth?.createUserWithEmailAndPassword(emailEdittext.text.toString(), passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful){ // id가 생성되었을 때
                    moveMainPage(task.result?.user)
                }
                else if(!task.exception?.message.isNullOrEmpty()){
                    // id 생성에 실패했을 때(로그인 에러), error message를 생성
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
                else {
                    // 회원가입이나 에러가 아닐 경우, 로그인
                     singinEmail()
                }
            }
    }


    // 로그인
    private fun singinEmail(){

        val emailEdittext : EditText = findViewById(R.id.email_edittext)
        val passwordEdittext : EditText = findViewById(R.id.password_edittext)
        auth?.signInWithEmailAndPassword(emailEdittext.text.toString(), passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful){ // id와 pw가 일치해 로그인 되었을 때
                    moveMainPage(task.result?.user)
                }
                else {
                    // id와 pw를 틀렸을 때
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // 로그인 성공시, 다음 페이지로 넘어가는 함수
    private fun moveMainPage(user:FirebaseUser?){
        if (user != null){ // user가 firebase에 있을 경우, main 페이지로 이동
            startActivity(Intent(this, MainActivity::class.java))
        }
    }



}