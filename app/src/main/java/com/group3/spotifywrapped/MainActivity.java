package com.group3.spotifywrapped;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group3.spotifywrapped.utils.SpotifyApiHelper;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import database.AppDatabase;
import database.User;
import database.UserDao;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "cd5187268d4a421cbfda59e5c697e429";
    public static final String REDIRECT_URI = "spotifywrapped://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private String mAccessToken, mAccessCode;

    private TextView tokenTextView, codeTextView, profileTextView;

    public AppDatabase db;
    public static UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // Initialize the views
//        TODO: ADD THE ConstraintLayout FROM THE TUTORIAL
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        profileTextView = (TextView) findViewById(R.id.response_text_view);

        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        ImageView profileImageView = (ImageView) findViewById(R.id.mainMenuImageView);

        // Init Databae
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "local-database").build();
        userDao = db.userDao();

        // Query Call
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.insert(new User(
                        "Parker1234",
                        "Poiu1234",
                        "Parker.arneson@gmail.com",
                        "Parker Arneson",
                        "lakwndlkand",
                        "985690493"
                ));
                // Set the click listeners for the buttons

                tokenBtn.setOnClickListener((v) -> {
                    getToken();
                });

                codeBtn.setOnClickListener((v) -> {
                    getCode();
                });

                profileBtn.setOnClickListener((v) -> {
                    onGetUserProfileClicked();
                });
            }
        });
        thread.start();

                // Set the click listeners for the buttons
    }


    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        SpotifyApiHelper spotifyApiHelper = new SpotifyApiHelper(mAccessToken, mAccessCode);
        JSONObject test = spotifyApiHelper.callSpotifyApi("/me/top/tracks?time_range=long_term&limit=1", "GET");
            try {
                test = test.getJSONArray("items").getJSONObject(0).getJSONObject("album");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                Log.d("JSON", "FORMATTED DATA: " + test.toString(3));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read"}) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

}