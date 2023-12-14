package com.example.mycartfinal.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.mycartfinal.R
import com.example.mycartfinal.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var  email : EditText
    private lateinit var  password : EditText
    private lateinit var progressBar : ProgressBar

    private var  age : Int = 0
    private lateinit var  name : String
    private lateinit var  surname : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //titulo del activity
        this.title = getString(R.string.tittle_register)

        auth = FirebaseAuth.getInstance()

        email = findViewById<EditText>(R.id.input_email_re)
        password = findViewById<EditText>(R.id.input_password_re)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)


        setup()
    }

    private fun verifyFileds(): Boolean {

        val name = findViewById<EditText>(R.id.input_name)
        val surname = findViewById<EditText>(R.id.input_surname)
        val age = findViewById<EditText>(R.id.input_age)

        if(name.text.toString().isEmpty()){
            name.error = getString(R.string.error_field)
            name.requestFocus()
            return false
        }

        if(surname.text.toString().isEmpty()){

            surname.error = getString(R.string.error_field)
            surname.requestFocus()
            return false
        }

        if(age.text.toString().isEmpty()){
            age.error = getString(R.string.error_field)
            age.requestFocus()
            return false
        }

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

        this.age = age.text.toString().toInt()
        this.name = name.text.toString()
        this.surname = surname.text.toString()

        return true

    }

    private fun setup(){
        val bt_registrar = findViewById<Button>(R.id.bt_register_re).setOnClickListener {
            //Muestro la barra de progreso


            //si todos los campos están relleneos creo el usuario
            if(verifyFileds()){

                //verifico que la cuenta no esté registrada anteriormente
                auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {

                    //si ya está registrado, informo y vuelve a la pantalla de inicio
                    if (it.isSuccessful){

                        //mensaje de advertencia
                        Toast.makeText(this, R.string.alert_already_registered,Toast.LENGTH_LONG).show()

                        //vuelve a la pantalla de inicio y cierro sesion
                        auth.signOut()
                        showHome()

                    //si no está registrado, creo el usuario y lo guardo en la BD
                    }else{

                        progressBar.visibility=View.VISIBLE

                        auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {


                            //creo el objeto del nuevo usuario
                            val user = User(age, name , surname, email.text.toString())

                            //cuando creo el usuario, lo añado a la tabla "Users"
                            FirebaseAuth.getInstance().currentUser?.let { it1 ->
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(it1.uid).setValue(user).addOnSuccessListener {

                                        //mensaje de confirmacion
                                        Toast.makeText(this, R.string.alert_registration_txt,Toast.LENGTH_LONG).show()


                                        //vuelve a la pantalla de inicio
                                        showHome()
                                    }.addOnFailureListener {

                                        //mensaje de error
                                        Toast.makeText(this,
                                            R.string.aler_registration_tittle_fail,Toast.LENGTH_LONG).show()
                                        progressBar.visibility=View.GONE
                                    }
                            }

                        }

                    }


                }


            }

        }
    }

    private fun showHome(){
        val homeIntent = Intent(this, MainActivity::class.java)

        startActivity(homeIntent)
    }
}