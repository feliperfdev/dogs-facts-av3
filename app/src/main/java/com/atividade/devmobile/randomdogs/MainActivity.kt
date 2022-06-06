package com.atividade.devmobile.randomdogs
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.atividade.devmobile.randomdogs.data.SQLiteHelper
import com.atividade.devmobile.randomdogs.databinding.ActivityMainBinding
import com.atividade.devmobile.randomdogs.models.FactModel
import com.atividade.devmobile.randomdogs.repository.DogsClient
import com.atividade.devmobile.randomdogs.repository.Endpoint
import com.atividade.devmobile.randomdogs.utils.AppConsts
import com.atividade.devmobile.randomdogs.utils.AppFunctions
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var sqlite: SQLiteHelper

    private lateinit var binding: ActivityMainBinding

    private lateinit var centerText: TextView

    private lateinit var botao: Button
    private lateinit var botaoSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        initView()
        initStorage()

        botao.setOnClickListener {
            getRandom()
        }

        botaoSalvar.setOnClickListener {
            saveFact()
        }
    }

    private fun getRandom() {
        val retrofitClient = DogsClient.get(AppConsts.baseURL)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getRandomFact().enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var body = response.body()

                var facts = body?.get("facts")?.asJsonArray

                var fact = facts?.get(0).toString()

                centerText.text = fact
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Falhou")
            }
        })
    }

    private fun saveFact() {

        var message = centerText.text.toString()

        var exists: Boolean = sqlite.existsAtStorage(message)

        if (!exists) {
            var fact = FactModel(AppFunctions.randomID(message), message)
            sqlite.insertFact(fact)
        }
    }

    private fun initView() {
        botao = binding.rdnBtn
        centerText = binding.factText
        botaoSalvar = binding.saveBtn
    }

    private fun initStorage() {
        sqlite = SQLiteHelper(context = this)
    }

    private fun navToList() {
        startActivity(Intent(this, FactListActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.menu_item, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.factListMenu -> {
                navToList()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

