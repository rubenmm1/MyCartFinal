package com.example.mycartfinal.Activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mycartfinal.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    private lateinit var auth : FirebaseAuth
    private  lateinit var email : EditText
    private  lateinit var password : EditText

    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar_main)

        //asignar un clicklistener al boton de registro
        setup()
    }

    private fun setup(){

        //recojo el los input email y password
        email = findViewById(R.id.input_email)
        password = findViewById(R.id.input_password)


        findViewById<Button>(R.id.bt_register_re).setOnClickListener {
            showRegistration()
        }

        findViewById<Button>(R.id.bt_login).setOnClickListener {

            if(verifyFields()){
                auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {

                    //Muestro la barra de progreso
                    progressBar.visibility= View.VISIBLE

                    if(it.isSuccessful){

                        showHome()
                        progressBar.visibility= View.GONE
                    }else{
                        progressBar.visibility= View.GONE
                        showAlert()
                    }

                }
            }
        }

    }

    private fun verifyFields (): Boolean{

        if(email.text.toString().isEmpty()){
            email.error = getString(R.string.error_field)
            email.requestFocus()
            return false
        }

        if(password.text.toString().isEmpty()){
            password.error = getString(R.string.error_field)
            password.requestFocus()
            return false
        }

        return true
    }

    private fun showAlert(){

        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.error))
        builder.setMessage(getString(R.string.error_user))
        builder.setPositiveButton(getString(R.string.accept),null)
        builder.create().show()

    }

    private fun showHome(){
        val homeIntent = Intent(this, HomeActivity::class.java)

        startActivity(homeIntent)
    }

    private fun showRegistration(){
        val regisgtrationIntent = Intent(this, RegisterActivity::class.java)

        startActivity(regisgtrationIntent)
    }
}