package com.example.news

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(),NewsItemClicked,NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerlayout: DrawerLayout
    lateinit var actionBarDrawerToggle:ActionBarDrawerToggle
    lateinit var url: String

    private lateinit var mAdapter: NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)

        url = "https://newsapi.org/v2/top-headlines?country=in&apiKey=224c18b2a1d140e4b2ed5ebd81b8df71"
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)


        fetchData(url)
        mAdapter=NewsListAdapter(this)
        recyclerView.adapter=mAdapter
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        swipeRefreshLayout.setOnRefreshListener {
            fetchData(url)
            mAdapter=NewsListAdapter(this)
            recyclerView.adapter=mAdapter
            swipeRefreshLayout.isRefreshing=false
        }

        drawerlayout =findViewById(R.id.my_drawer)
        actionBarDrawerToggle= ActionBarDrawerToggle(this,drawerlayout,R.string.nav_open,R.string.nav_close)
        drawerlayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    private fun fetchData(url: String) {


        val jsonObjectRequest = object :JsonObjectRequest(
            Request.Method.GET, url, null,

            Response.Listener{
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("urlToImage"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("author")
                    )
                    newsArray.add(news)
                }

                mAdapter.updateNews(newsArray)

            },
            Response.ErrorListener{
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
            })

        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId();
        if(id==R.id.All){
            url="https://newsapi.org/v2/top-headlines?country=in&apiKey=224c18b2a1d140e4b2ed5ebd81b8df71"
            fetchData(url)
        }
        else if (id == R.id.buss) {
            url="https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=224c18b2a1d140e4b2ed5ebd81b8df71"
            fetchData(url)
        } else if (id == R.id.sports) {
            url="https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=224c18b2a1d140e4b2ed5ebd81b8df71"
            fetchData(url)
        } else if (id == R.id.tech) {
            url="https://newsapi.org/v2/top-headlines?country=in&category=technology&apiKey=224c18b2a1d140e4b2ed5ebd81b8df71"
            fetchData(url)
        }
        val drawer = findViewById<DrawerLayout>(R.id.my_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}