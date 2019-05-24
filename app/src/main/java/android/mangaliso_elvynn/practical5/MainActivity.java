package android.mangaliso_elvynn.practical5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Variables
    RequestQueue requestQueue;
    EditText usernameEditText, passwordEditText;
    String username, password;
    Button loginButton;
    Boolean isEmpty = false;
    public String api_key,name;
    public ProgressDialog p;
    JSONObject params = new JSONObject();
    public static Cache.Entry cacheEntry;

String url = "https://wheatley.cs.up.ac.za/u15207715/u15207715/u15207715/u15207715/Practical5/Practical3/login-validate.php?android=true";
//    String url = "http://192.168.42.38/Practical3_updated_28/Practical3_updated_27/Practical3_updated_25/Practical3_updated_23/Practical3/login-validate.php?android=true";
//    String url = "http://137.215.37.211:8080/Practical3_updated_28/Practical3_updated_27/Practical3_updated_25/Practical3_updated_23/Practical3/login-validate.php";
//    C:\xampp\htdocs\Practical3_updated_28\Practical3_updated_27\Practical3_updated_25\Practical3_updated_23
//    public final String url = "http://10.0.2.2:80/Practical3_updated_27/Practical3_updated_25/Practical3_updated_23/Practical3/login-validate.php";
//    public final String url = "http://127.0.0.1:80/Practical3_updated_27/Practical3_updated_25/Practical3_updated_23/Practical3/login-validate.php";
//    static JSONParser jsonParser = new JSONParser();

///https://developer.android.com/training/volley/request
//https://abhiandroid.com/programming/volley

    //https://www.geeksforgeeks.org/volley-library-in-android/
    //https://developer.android.com/training/volley/simple.html#java
    //https://developer.android.com/training/volley/requestqueue.html
    //https://developer.android.com/training/volley/request.html
    //https://developer.android.com/training/volley/request-custom.html
    //https://www.android-examples.com/volley-user-login-system/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        usernameEditText = findViewById(R.id.usernameTextView);
        passwordEditText = findViewById(R.id.passwordTextView);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    isEmptyField();

                    if (isEmpty)Login();
                    else Toast.makeText(MainActivity.this,"Please check if you've entered your credentials",Toast.LENGTH_LONG).show();
            }
        });
    }

public boolean isEmptyField()
{
    username = usernameEditText.getText().toString().trim();
    password = passwordEditText.getText().toString().trim();

    isEmpty = !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);

    return isEmpty;
}

public void Login() {

        p = new ProgressDialog(MainActivity.this);
        p.setMessage("Please wait... while logging in");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();


    final JSONObject response = null;
    RequestQueue requestQueue;

// Instantiate the cache
    final Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
    Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
    requestQueue = new RequestQueue(cache, network);

// Start the queue
    VolleySingleton.getInstance(MainActivity.this).getRequestQueue().start();

    try {
        params.put("email", username.trim());
        params.put("password", password.trim());
    }
    catch (JSONException e)
    {
        Toast.makeText(MainActivity.this,"JsonError: "+e.getMessage(),Toast.LENGTH_LONG).show();
    }

    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,null,
            new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject stringResponse) {
            try {

                p.dismiss();
                p.cancel();

                Toast.makeText(MainActivity.this,"The response is: "+stringResponse.get("status")
                        +"\nAPI_KEY is: "+stringResponse.get("api_key")+"\nName is: "+stringResponse.get("name"),Toast.LENGTH_LONG).show();
                api_key = stringResponse.get("api_key").toString();
                name = stringResponse.get("name").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {

                if (stringResponse.get("status").equals("Successfully logged in")) {

                    Intent i = new Intent(MainActivity.this, DiscoverActivity.class);
                    i.putExtra("username", usernameEditText.getText());
                    i.putExtra("api_key",api_key);
                    i.putExtra("name",name);
                    startActivity(i);
                    finish();
                } else if(stringResponse.get("status").equals("Incorrect password")) {
                    Toast.makeText(MainActivity.this,"Incorrect password, try again", Toast.LENGTH_LONG).show();
                }
                else if(stringResponse.get("status").equals("Please check your credentials,Have you registered?")){
                    Toast.makeText(MainActivity.this,"Please check your credentials,Have you registered?", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(MainActivity.this,"Error occurred while fetching results.", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(final VolleyError error) {

           final NetworkResponse networkResponse = error.networkResponse;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(MainActivity.this,"Seems like the error is: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(MainActivity.this.getClass().getName(),"Value of response: "+ String.valueOf(response));
                    if(networkResponse!=null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED)
                        Toast.makeText(MainActivity.this,"Error error "+networkResponse.statusCode, Toast.LENGTH_LONG).show();
                }
            });
        }
    }) {

        @Override
        public byte[] getBody() {
            final JSONObject body = new JSONObject();
            try {
                body.put("email", username);
                body.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return body.toString().getBytes();
        }

        @Override
        public Map<String, String> getHeaders() {

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type","application/json");
            String creds = String.format("%s:%s","u15207715","iequocha");
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            headers.put("Authorization", auth);
            return headers;
        }

//        @Override
//        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//            try {
//                cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
//                if (cacheEntry == null) {
//                    cacheEntry = new Cache.Entry();
//                }
//
//                final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
//                final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
//                long now = System.currentTimeMillis();
//                final long softExpire = now + cacheHitButRefreshed;
//                final long ttl = now + cacheExpired;
//                cacheEntry.data = response.data;
//                cacheEntry.softTtl = softExpire;
//                cacheEntry.ttl = ttl;
//                String headerValue;
//                headerValue = response.headers.get("Date");
//                if (headerValue != null) {
//                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
//                }
//                headerValue = response.headers.get("Last-Modified");
//                if (headerValue != null) {
//                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
//                }
//                cacheEntry.responseHeaders = response.headers;
//                final String jsonString = new String(response.data,
//                        HttpHeaderParser.parseCharset(response.headers));
//                return Response.success(new JSONObject(jsonString), cacheEntry);
//            } catch (UnsupportedEncodingException | JSONException e) {
//                return Response.error(new ParseError(e));
//            }
//        }
//
//        @Override
//        protected void deliverResponse(JSONObject response) {
//            super.deliverResponse(response);
//        }
//
//        @Override
//        public void deliverError(VolleyError error) {
//            super.deliverError(error);
//        }
//
//        @Override
//        protected VolleyError parseNetworkError(VolleyError volleyError) {
//            return super.parseNetworkError(volleyError);
//        }
    };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}