package android.mangaliso_elvynn.practical5;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DiscoverActivity extends AppCompatActivity {

    public static String urlGetString = "https://wheatley.cs.up.ac.za/u15207715/Practical5/Practical3/api.php?type=info&api_key=076cb7817974d&title=John&chk_title=title&chk_imdbID=id&chk_release_date=release_date&chk_overview=overview&action=Get+Results",
     urlForImages = "http://www.omdbapi.com/?i=tt0076759&apikey=eaafa32e",posterPath,
    movieName,API_KEY,USERNAME,NAME;
    String IMAGE_PATH = "https://image.tmdb.org/t/p/original";
    String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie?page=1&include_video=false&include_adult=false&sort_by=popularity.desc&language=en-US&api_key=54da015ba64ca22427662da2d6e4819d";
    private MovieAdapter movieAdapter;
    private Button searchButton;
    private EditText movieSearchField;
    private ImageView imageView;
    private boolean isEmpty;
    ArrayList<Movie> movies;
    ArrayList<Bitmap> imagesList;
    ArrayList<String>titleList,releaseList,imdbidList,overviewList;
    public ProgressDialog p;
    //ADDITIONS
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    ActionBar toolbar;
    BottomNavigationView bottomNavigationView;
    BottomNavigationItemView item1, item2,item3;
    public static  Context mContext;

    public void setContext(Context mContext)
    {
        this.mContext = mContext;
    }

    public static Context getContext()
    {
        return mContext;
    }

    public boolean isEmptyField()
    {
        movieSearchField = findViewById(R.id.movieEditText);
        movieName = movieSearchField.getText().toString();
        isEmpty = !TextUtils.isEmpty(movieName.trim());

        return isEmpty;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        setContext(getContext());

        p = new ProgressDialog(DiscoverActivity.this);
        p.setMessage("Please wait... while fetching movies");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();

        imagesList = new ArrayList<>();

        titleList = new ArrayList<>();
        releaseList = new ArrayList<>();
        imdbidList = new ArrayList<>();
        overviewList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setSelectedItemId(R.id.nav_discover);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                switch (menuItem.getItemId()){
                    case R.id.nav_discover:
                        if(getContext() == DiscoverActivity.getContext())
                            Toast.makeText(getApplicationContext(),"In Discover",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_Login:
                        if(getContext() == DiscoverActivity.getContext()) {
                            Toast.makeText(getApplicationContext(), "You're already logged In", Toast.LENGTH_SHORT).show();
                            //bottomNavigationView.setSelected(false);
//                            Toast.makeText(DiscoverActivity.this,"Cached data: "+ MainActivity.cacheEntry,Toast.LENGTH_SHORT).show();
                            bottomNavigationView.setSelectedItemId(R.id.nav_discover);
                            bottomNavigationView.refreshDrawableState();
                        }
                        break;

                    case R.id.nav_Logout:


                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiscoverActivity.this); //Read Update
                        alertDialogBuilder.setMessage("Are you sure you want to log out?");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                bottomNavigationView.setSelectedItemId(R.id.nav_discover);
                                dialog.dismiss();
                            }
                        });

                        alertDialogBuilder.show();
                        break;
                }

                return true;
            }
        });


        API_KEY = getIntent().getStringExtra("api_key");
        USERNAME = getIntent().getStringExtra("username");
        NAME = getIntent().getStringExtra("name");

        Toast.makeText(this,"Welcome: "+NAME,Toast.LENGTH_LONG).show();
        this.setTitle("Discover - api key:"+API_KEY);

        fetchDiscover(DISCOVER_URL);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        ((ShakeEventListener) mSensorListener).setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {

                p = new ProgressDialog(DiscoverActivity.this);
                p.setMessage("Please wait... while fetching movies");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();

                fetchDiscover(DISCOVER_URL);


                Toast.makeText(DiscoverActivity.this,"Discover has been refreshed",Toast.LENGTH_SHORT).show();

                p.dismiss();
                p.cancel();
            }
        });

        imageView = findViewById(R.id.movieImageView);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEmptyField();
                if(isEmpty) {

                    p = new ProgressDialog(DiscoverActivity.this);
                    p.setMessage("Please wait... while fetching movies");
                    p.setIndeterminate(false);
                    p.setCancelable(false);
                    p.show();

                    fetchMovieInfo("https://wheatley.cs.up.ac.za/u15207715/Practical5/Practical3/api.php?type=info&api_key=" + API_KEY + "&title=" + movieName + "&chk_title=title&chk_imdbID=id&chk_release_date=release_date&chk_overview=overview&action=Get+Results");
                }
                else
                    Toast.makeText(v.getContext(),"Please enter movie name",Toast.LENGTH_LONG).show();
            }
        });
    }

    //These function are for the Shake EventHandlers
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    public void fetchDiscover(String DISCOVER_URL)
    {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, DISCOVER_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        p.dismiss();
                        p.cancel();

                        if(response.length()==0) {
                            Toast.makeText(DiscoverActivity.this,"No movies found",Toast.LENGTH_LONG).show();
                            return;
                        }

                        movies = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            for(int x =0;x<jsonArray.length();x++)
                            {
                                JSONObject jsonResultObject = jsonArray.getJSONObject(x);
                                String title;
                                String imdbid;
                                String releaseDate;
                                String overview;
                                String imagePath;

                                if(jsonResultObject.get("title") != null) {
                                    title = jsonResultObject.get("title").toString();
//                                    Toast.makeText(DiscoverActivity.this,"Title of the movies: "+title,Toast.LENGTH_LONG).show();
                                }
                                else
                                    title = "N/A";

                                if(jsonResultObject.get("vote_average") != null) {
                                    imdbid = jsonResultObject.get("vote_average").toString();
                                }
                                else
                                    imdbid ="N/A";

                                if(jsonResultObject.get("release_date") != null) {
                                    releaseDate = jsonResultObject.get("release_date").toString();
                                }
                                else
                                    releaseDate ="N/A";

                                if(jsonResultObject.get("overview") != null ){
                                    overview = jsonResultObject.get("overview").toString();
                                }
                                else
                                    overview = "N/A";

                                if(jsonResultObject.get("poster_path")!=null)
                                    imagePath = IMAGE_PATH+jsonResultObject.get("poster_path").toString();
                                else
                                    imagePath = "N/A";

                                movies.add(new Movie(title,releaseDate,imdbid,overview, imagePath));
                            }
                        } catch (JSONException e) {

                            Toast.makeText(DiscoverActivity.this, "Error is: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        movieAdapter = new MovieAdapter(DiscoverActivity.this, movies);
                        ListView movieListView = findViewById(R.id.movieList);
                        movieListView.setAdapter(movieAdapter);

                        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final AlertDialog alertDialog = new AlertDialog.Builder(DiscoverActivity.this).create(); //Read Update
                                final Movie currentMovie = movieAdapter.getItem(position);
                                assert currentMovie != null;
                                alertDialog.setTitle("Movie overview => "+currentMovie.getTitle());
                                alertDialog.setMessage(currentMovie.getSynopsis());

                                alertDialog.setButton("See movie image", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        final Dialog builder = new Dialog(DiscoverActivity.this);
                                        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(
                                                new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {
                                                //nothing;
                                            }
                                        });

                                        final NetworkImageView imageView = new NetworkImageView(DiscoverActivity.this);
                                        imageView.setImageUrl(currentMovie.getImage(),VolleySingleton.getInstance(DiscoverActivity.this).getImageLoader());
                                        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        builder.show();

                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                builder.dismiss();
                                            }
                                        });
//                                        imageView.
//                                        builder.show();
                                    }
                                });
                                alertDialog.show();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                p.dismiss();
                p.cancel();
                Toast.makeText(DiscoverActivity.this,"Error is: "+error,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                String creds = String.format("%s:%s","u15207715","iequocha");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                p.dismiss();
                p.cancel();

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    public void fetchMovieInfo(String url)
    {

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                p.dismiss();
                p.cancel();

                movies = new ArrayList<>();
                JSONArray jsonArray = null;
//                imagesList = new ArrayList<>();
//
//                titleList = new ArrayList<>();
//                releaseList = new ArrayList<>();
//                imdbidList = new ArrayList<>();
//                overviewList = new ArrayList<>();

                try {
                    if(response.getJSONArray("data")== null) {
                        jsonArray = response.getJSONArray("results");
                    }
                    else {
                        jsonArray = response.getJSONArray("data");
                    }

//                    JSONArray jsonArray = response.getJSONArray("data");
//                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    //DEBUGGING
//                    Toast.makeText(DiscoverActivity.this,"First movie title is: "+jsonObject.get("title"),Toast.LENGTH_LONG).show();
//                    Toast.makeText(DiscoverActivity.this,"Response from getting movie data: "+response.getString("status"),Toast.LENGTH_LONG).show();

                    for(int x =0;x<jsonArray.length();x++)
                    {
                        JSONObject jsonResultObject = jsonArray.getJSONObject(x);
                        String title;
                        String imdbid;
                        String releaseDate;
                        String overview;
                        String imagePath;

                        if(jsonResultObject.get("title") != null) {
                            title = jsonResultObject.get("title").toString();
                        }
                        else
                            title = "N/A";

                        if(jsonResultObject.get("imdbid") != null) {
                            imdbid = jsonResultObject.get("imdbid").toString();
//                            imdbidList.add(imdbid);


//                            fetchImages("http://www.omdbapi.com/?i="+imdbid+"&apikey=eaafa32e");

//                            Toast.makeText(DiscoverActivity.this,"The result is: "+fetchImages("http://www.omdbapi.com/?i="+imdbid+"&apikey=eaafa32e"),Toast.LENGTH_SHORT).show();
//                           imageView.setImageBitmap(fetchImages("http://www.omdbapi.com/?i="+imdbid+"&apikey=eaafa32e"));
//                            Toast.makeText(DiscoverActivity.this,"The Image value is: "+image.toString(),Toast.LENGTH_SHORT).show();
                        }
                        else
                            imdbid ="N/A";

                        if(jsonResultObject.get("release_date") != null) {
                            releaseDate = jsonResultObject.get("release_date").toString();
//                            releaseList.add(releaseDate);
                        }
                        else
                            releaseDate ="N/A";

                        if(jsonResultObject.get("overview") != null ){
                            overview = jsonResultObject.get("overview").toString();
                        }
                        else
                            overview = "N/A";

                        if(jsonResultObject.get("poster")!=null)
                            imagePath = IMAGE_PATH+jsonResultObject.get("poster").toString();
                        else if(jsonResultObject.get("poster_path")!=null)
                            imagePath = IMAGE_PATH+jsonResultObject.get("poster").toString();
                        else
                            imagePath = "N/A";

                        movies.add(new Movie(title,releaseDate,imdbid,overview, imagePath));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                movieAdapter = new MovieAdapter(DiscoverActivity.this, movies);
                ListView movieListView = findViewById(R.id.movieList);
                movieListView.setAdapter(movieAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DiscoverActivity.this,"Error is: "+error,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                String creds = String.format("%s:%s","u15207715","iequocha");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
