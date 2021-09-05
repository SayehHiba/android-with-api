package com.example.isidoc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.isidoc.RestAPI.ApiClient
import com.example.isidoc.RestAPI.ApiService
import com.example.isidoc.model.UserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs


class MainActivity : AppCompatActivity(),GestureDetector.OnGestureListener {


    lateinit var  gestureDetector: GestureDetector
    var x0:Float=0.0f
    var x1:Float=0.0f
    lateinit var imageView: ImageView
    lateinit var textView: TextView
    lateinit var buttonLogin:Button
    lateinit var username:String
    lateinit var password:String
    lateinit var pref:SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    companion object{
        const val MIN_DISTANCE=150
        var count:Int=0
        var builder:Retrofit.Builder=Retrofit.Builder().baseUrl("http://digitalisi.tn:8080/bonita/").addConverterFactory(
                GsonConverterFactory.create()
        )
        var retrofit:Retrofit= builder.build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        imageView = findViewById<ImageView>(R.id.imageView)
        textView=findViewById<TextView>(R.id.textView)
        pref = applicationContext.getSharedPreferences("MyPref", MODE_PRIVATE)
        editor= pref.edit()
        gestureDetector = GestureDetector(this, this)
        // get reference to button
        buttonLogin = findViewById<Button>(R.id.Login)

        // set on-click listener
        buttonLogin.setOnClickListener {
            username = findViewById<EditText>(R.id.username).text.toString()

            password = findViewById<EditText>(R.id.pass).text.toString()

            GlobalScope.launch(Dispatchers.Main) { authentification(username, password) }

        }

    }

    private suspend fun authentification(username: String, password: String){
        var apiService: ApiService? = ApiClient.RetrofitSingleton()?.create(ApiService::class.java)
        var authentif: Response<ResponseBody>? = apiService?.Login(username, password, false)

        if (authentif != null) {
            if(authentif.code()==204){
                var Set_CookieList:List<String> =authentif.headers().values("Set-Cookie")
                var JSESSIONID= Set_CookieList[1].split(";")[0]
                var Token = Set_CookieList[2].split(";")[0]

                editor.putString("JSESSIONID", JSESSIONID);
                editor.putString("Token", Token);
                editor.commit();

                Log.e("MainActivity",pref.getString("JSESSIONID",null).toString())


                var GetUser:Response<UserId>?= apiService?.getId(pref.getString("JSESSIONID",null).toString()+";"+pref.getString("Token",null).toString())
                if (GetUser != null) {
                    if(GetUser.code()==200){
                    Log.e("coooode",GetUser.code().toString())
                    Log.e("MainActivity",GetUser.code().toString())
                    Log.e("MainActivity", GetUser.body()?.user_name.toString())
                    editor.putString("UserId",GetUser.body()?.user_id)
                    editor.putString("UserName",GetUser.body()?.user_name)
                    editor.commit();
                    val intent = Intent(this,ListProcess::class.java)
                    intent.putExtra("mode",count)
                    startActivity(intent)
                    }
                    else {
                        toastMethod("Probléme de récuperation de userID")
                    }
                }else{
                    toastMethod("Probléme de connexion")
                }


            }else {
                toastMethod("verifier votre données")
            }
        }else{
            toastMethod("Probléme de connexion")
        }

    }

    fun Context.toastMethod(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event?.action){

            //quand on commence à glisser
            0 -> {
                x0 = event.x
            }

            //quand nous terminons le glissage
            1 -> {
                x1 = event.x

                val X: Float = x1 - x0

                if (abs(X) > MIN_DISTANCE) {

                    if (count == 0) {
                        imageView.setImageResource(R.drawable.dark)
                        textView.text = "Dark"
                        count = 1
                    } else {
                        imageView.setImageResource(R.drawable.ligth)
                        textView.text = "Light"
                        count = 0
                    }

                }
            }
        }

        return super.onTouchEvent(event)
    }





    override fun onDown(e: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    //je n'utilise pas
    override fun onShowPress(e: MotionEvent?) {
        //TODO("Not yet implemented")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        //TODO("Not yet implemented")
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        //TODO("Not yet implemented")
        return false
    }




}