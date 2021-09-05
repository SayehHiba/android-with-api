package com.example.isidoc


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isidoc.Extra.RecycleReview.OnRecycleReviewClickListner
import com.example.isidoc.Extra.RecycleReview.RecycleReviewAdapter
import com.example.isidoc.RestAPI.ApiClient
import com.example.isidoc.RestAPI.ApiService
import com.example.isidoc.model.Category
import com.example.isidoc.model.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response


class ListProcess : AppCompatActivity() , OnRecycleReviewClickListner {
    lateinit var imageView: pl.droidsonroids.gif.GifImageView
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var listProcess: ArrayList<Process>
    lateinit var listcategory: ArrayList<Category>
    lateinit var grid:GridLayoutManager
    lateinit var userName: TextView
    lateinit var cat1:TextView
    lateinit var cat2:TextView
    lateinit var cat3:TextView
    lateinit var cat4:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_process)
        supportActionBar?.hide()
        imageView = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.ListImg)
        pref = applicationContext.getSharedPreferences("MyPref", MODE_PRIVATE)
        editor= pref.edit()
        userName=findViewById<TextView>(R.id.User)
       userName.text=pref.getString("UserName", null)
        val mode:Int =intent.getIntExtra("mode", 0)
        val back:FrameLayout=findViewById(R.id.BackgroundList)
         if(mode==0)
         {
             imageView.setImageResource(R.drawable.ligth)
             back.setBackgroundColor(Color.WHITE)

         }
         else{
             imageView.setImageResource(R.drawable.dark)
             back.setBackgroundColor(Color.BLACK)

         }


        GlobalScope.launch(Dispatchers.Main) { GetListProcess() }

    }

    private suspend fun GetListProcess(){
        var apiService: ApiService? = ApiClient.RetrofitSingleton()?.create(ApiService::class.java)
        var GetListProcess: Response<ArrayList<Process>>? = apiService?.getProcessList(pref.getString("JSESSIONID", null).toString() + ";" + pref.getString("Token", null).toString(), "user_id=" + pref.getString("UserId", null).toString())

        listProcess = GetListProcess?.body()!!
        var Names:ArrayList<String> = ArrayList()
        if (listProcess != null) {
            for (p: Process in listProcess){
                Names.add(p.name.toString())
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        grid= GridLayoutManager(applicationContext,2,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager=grid
        recyclerView.setHasFixedSize(true)
        val array = arrayOfNulls<String>(Names.size)
        recyclerView.adapter = RecycleReviewAdapter(Names.toArray(array),this)
        //list des categorie
        cat1=findViewById<TextView>(R.id.cat1)
        cat2=findViewById<TextView>(R.id.cat2)
        cat3=findViewById<TextView>(R.id.cat3)
        cat4=findViewById<TextView>(R.id.cat4)
        var category= apiService?.getProcessCategory(pref.getString("JSESSIONID", null).toString() + ";" + pref.getString("Token", null).toString(), "0")
        listcategory= category?.body()!!
        if (listcategory != null) {

            for (c: Category in listcategory){

                Log.e("ListProcess", c.name.toString())
            }
            cat1.text=listcategory[0].name
            cat2.text=listcategory[1].name
            cat3.text=listcategory[2].name
            cat4.text=listcategory[3].name
        }
        //
    }

    override fun onItemClick(item: String, position: Int) {
        editor.putString("IdProcessCourant",listProcess[position].id)
        editor.commit()
        val intent = Intent(this,InputsActivity::class.java)
        intent.putExtra("mode", MainActivity.count)
        startActivity(intent)
    }
}