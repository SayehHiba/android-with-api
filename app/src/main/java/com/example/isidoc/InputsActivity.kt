package com.example.isidoc

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.iterator
import com.example.isidoc.RestAPI.ApiClient
import com.example.isidoc.RestAPI.ApiService
import com.example.isidoc.model.Contract
import com.example.isidoc.model.Input
import com.example.isidoc.model.Process
import com.example.isidoc.model.submit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class InputsActivity : AppCompatActivity() {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var contract: Contract
    lateinit var Layout:LinearLayout
    lateinit var imageView: pl.droidsonroids.gif.GifImageView
    lateinit var userName: TextView
    lateinit var enrg:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inputs)
        supportActionBar?.hide()
        pref = applicationContext.getSharedPreferences("MyPref", MODE_PRIVATE)
        editor= pref.edit()
        imageView = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.ImgInputs)
        enrg = findViewById<Button>(R.id.enrg)
        userName=findViewById<TextView>(R.id.UserI)
        userName.text=pref.getString("UserName", null)

        val mode:Int =intent.getIntExtra("mode", 0)
        val back: FrameLayout =findViewById(R.id.inputsBackground)
        if(mode==0)
        {
            back.setBackgroundColor(Color.WHITE)
            imageView.setImageResource(R.drawable.ligth)
            enrg.setTextColor(Color.parseColor("#FF000000"))
        }
        else{
            back.setBackgroundColor(Color.BLACK)
            imageView.setImageResource(R.drawable.ligth)
            enrg.setTextColor(Color.parseColor("#FFFFFF"))

        }


        GlobalScope.launch(Dispatchers.Main) { InputsForm() }


        enrg.setOnClickListener {
            val layout: LinearLayout = findViewById(R.id.inputs)
            var BodyJSON:String="{\""+contract.inputs?.get(0)?.name+"\":{"

            for(v: View in layout)
            {

                    val vv = v as EditText
                    //Do your stuff
                    BodyJSON=BodyJSON+"\""+vv.hint.toString()+"\":\""+vv.text.toString()+"\","




            }
            BodyJSON=BodyJSON.substring(0, BodyJSON.length - 1)+"}}"

            GlobalScope.launch(Dispatchers.Main) { executer2(BodyJSON)

                toastMethod("formulaire accepter")
            }
            val intent = Intent(this,ListProcess::class.java)
            intent.putExtra("mode", MainActivity.count)
            startActivity(intent)
        }

    }
    private suspend fun executer2(BodyJSON: String){
        var api: ApiService = MainActivity.retrofit.create(ApiService::class.java)
        var j:JSONObject =JSONObject(BodyJSON)
        Log.e("body",BodyJSON)
        var submitForm: Response<submit> =api.submit(pref.getString("JSESSIONID", null).toString() + ";" + pref.getString("Token", null).toString(), pref.getString("Token", null).toString(), pref.getString("IdProcessCourant", null).toString(), BodyJSON)

        Log.e("InputActivitycode", submitForm.toString())
        Log.e("InputActivitycode", submitForm.code().toString())
        Log.e("InputActivitycodecase", submitForm.body()?.caseId.toString())

    }
    fun Context.toastMethod(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    private suspend fun InputsForm(){

        var apiService: ApiService? = ApiClient.RetrofitSingleton()?.create(ApiService::class.java)
        var getContract: Response<Contract>? =apiService?.getProcessContract(pref.getString("JSESSIONID", null).toString() + ";" + pref.getString("Token", null).toString(), pref.getString("IdProcessCourant", null).toString())
        contract=getContract?.body()!!
        val inputs:ArrayList<Input>?=contract.inputs?.get(0)?.inputs
        Layout=findViewById(R.id.inputs)
        if (inputs != null) {
            for(i:Input in inputs){

                when (i.type) {
                    "TEXT"-> {
                        var e:EditText = EditText(this)
                        e.hint=i.name
                        e.setTextColor(Color.parseColor("#043e61"))
                        e.inputType=InputType.TYPE_CLASS_TEXT
                        Layout.addView(e)
                    }
                    "LOCALDATE" -> {
                        var e:TextView = TextView(this)
                        e.setTextColor(Color.parseColor("#043e61"))
                        e.hint=i.name
                        var calendar:Calendar= Calendar.getInstance()
                        var year:Int=calendar.get(Calendar.YEAR)
                        var month:Int=calendar.get(Calendar.MONTH)
                        var day:Int=calendar.get(Calendar.DAY_OF_MONTH)
                        lateinit var listener:DatePickerDialog.OnDateSetListener
                        e.setOnClickListener{
                            val date:DatePickerDialog =DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,listener,year,month,day)
                            date.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            date.show()
                        }

                        listener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            run {
                                var m = month + 1
                                var d: String =
                                        year.toString()+"-" +  m.toString() + "-"+ dayOfMonth.toString()
                                e.setText(d)

                            }
                        }
                        Layout.addView(e)

                    }
                    "BOOLEAN" ->{var e:EditText = EditText(this)
                        e.hint=i.name
                        e.setTextColor(Color.parseColor("#043e61"))
                        e.inputType=InputType.TYPE_CLASS_NUMBER
                        Layout.addView(e)
                    }
                    "OFFSETDATETIME" -> {
                        var e:EditText = EditText(this)
                        e.hint=i.name
                        e.setTextColor(Color.parseColor("#043e61"))
                        e.inputType=InputType.TYPE_DATETIME_VARIATION_NORMAL
                        Layout.addView(e)
                    }
                    else -> { // Note the block
                        Log.e("errr","type")
                    }
                }

            }
        }
    }
}