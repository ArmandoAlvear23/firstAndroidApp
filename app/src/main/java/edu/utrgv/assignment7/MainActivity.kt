package edu.utrgv.assignment7
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.SwitchCompat
import android.widget.CompoundButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
class MainActivity : AppCompatActivity(),
        SwipeRefreshLayout.OnRefreshListener {
    private lateinit var ignitionSwitch: SwitchCompat
    private lateinit var intrusionSwitch: SwitchCompat
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var refreshSwitch: Boolean = true
    private val username: String = "username"
    private val password: String = "password"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ignitionSwitch = switchLed2 as SwitchCompat
        intrusionSwitch = switchLed as SwitchCompat
        swipeRefresh = swipeLayout as SwipeRefreshLayout
        swipeRefresh.setOnRefreshListener(this)

        intrusionSwitch.isClickable = false
        ignitionSwitch.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            if (refreshSwitch) update()
        }
        update()
    }
    override fun onRefresh() {
        update()
        swipeRefresh.isRefreshing = false
    }

    private fun update() {
        // Get status of SW1 and LED1
        val SW1_status = if (intrusionSwitch.isChecked) 1 else 0
        val LED1_status = if (ignitionSwitch.isChecked) 1 else 0
        val url =
                "https://0254934.000webhostapp.com/scripts/sync_app_data.php"
        val jsonObject = JSONObject().apply {
            put("username", username)
            put("password", password)
            put("SW1", SW1_status)
            put("LED1", LED1_status)
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                Response.Listener {
                    val success = it.get("success")
                    if (success == 1) {
                        // Set the statuses to the ones received
                        refreshSwitch = false
                        intrusionSwitch.isChecked = (it.get("SW1") == 1)
                        ignitionSwitch.isChecked = (it.get("LED1") == 1)
                        refreshSwitch = true
                        Toast.makeText(this, "It succeeded!",
                                Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "It failed...",
                                Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this, it.message,
                            Toast.LENGTH_LONG).show()
                })
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}
