package com.example.mycartfinal.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.mycartfinal.Model.User
import com.example.mycartfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditProfileActivity : AppCompatActivity() {

    //textos
    private lateinit var txtName : EditText
    private lateinit var txtSurname : EditText
    private lateinit var txtAge : EditText
    private lateinit var txtEmail : EditText

    //progress bar
    private lateinit var progressBar : ProgressBar

    //botón
    private lateinit var btEdit : Button

    //base de datos
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseDatabase

    private var user : User? = User()

    //bandera para botón editar
    private var flagEdit : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //titulo del activity
        this.title = getString(R.string.it_config_profile)

        //inicializo el progress bar
        progressBar = findViewById(R.id.progressBarEdit)

        //recojo la sesión
        auth = FirebaseAuth.getInstance()

        //conexión bd
        db = FirebaseDatabase.getInstance()

        //inicializo todos los Edit text
        txtName = findViewById(R.id.edit_name)
        txtSurname = findViewById(R.id.edit_surname)
        txtAge = findViewById(R.id.edit_age)
        txtEmail = findViewById(R.id.edit_email)

        //inicializo el botón
        btEdit = findViewById(R.id.bt_edit)

        btEdit.setOnClickListener {

            modififyView(flagEdit)


            //si el botón está en modo guardar
            if (!flagEdit)
                editUser()

            flagEdit = !flagEdit
        }


        getUser()

    }

    private fun editUser() {

        progressBar.visibility = ProgressBar.VISIBLE

        //modifico el objeto user

        val name = txtName.text.toString()
        val surname = txtSurname.text.toString()
        val age = txtAge.text.toString().toInt()
        val email = txtEmail.text.toString()

        user = User(name= name, age = age, surname = surname, email = email)

        auth.uid?.let {

            Log.w("user12",""+user)

            db.getReference("Users").child(it).setValue(user).addOnSuccessListener {
                //mensaje de confirmacion
                Toast.makeText(this, R.string.toast_editComplete, Toast.LENGTH_LONG).show()


                progressBar.visibility = ProgressBar.GONE


            }.addOnFailureListener{

                Toast.makeText(this, R.string.toast_editFailure, Toast.LENGTH_LONG).show()
                progressBar.visibility = ProgressBar.GONE
            }
        }
    }

    private fun fillTexts() {

        user?.let {
            txtName.setText(it.name)
            txtAge.setText(it.age.toString())
            txtEmail.setText(it.email)
            txtSurname.setText(it.surname)
        }


    }

    private fun getUser() {

        //recojo el usuario
        auth.uid?.let {
            db.getReference("Users").child(it).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    user = snapshot.getValue(User::class.java)
                    //relleno los textos con los datos
                    fillTexts()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun modififyView(b: Boolean) {

        txtName.isEnabled = b
        txtSurname.isEnabled = b
        txtAge.isEnabled = b

        if (flagEdit){
            btEdit.text = getString(R.string.btSaveEdit)
        }else{
            btEdit.text = getString(R.string.btEditProfile)
        }


    }
}