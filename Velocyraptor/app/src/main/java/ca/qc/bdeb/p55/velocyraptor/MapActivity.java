package ca.qc.bdeb.p55.velocyraptor;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;
import ca.qc.bdeb.p55.velocyraptor.model.Course;


/**
 * Activité principale : carte et données de la course en cours.
 * VOIR : https://developers.google.com/maps/documentation/android-api/intro
 * https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap
 */
public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String KEY_USER_RACE = "userrace";

    private GoogleMap googleMap;
    private TextView chronometerText;
    private android.support.v7.widget.Toolbar toolbar;
    private Button btnStart;
    private Button btnStop;
    private Button btnResume;
    private Button btnPause;

    private final Runnable onChronometerTick = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chronometerText.setText(course.getFormattedElapsedTime());
                }
            });
        }
    };

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Course course;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase.setApplicationContext(getApplicationContext());
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        chronometerText = (TextView) findViewById(R.id.mapactivity_txt_chronometer);

        initialiserBoutons();

        setSupportActionBar(toolbar);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(800);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (savedInstanceState != null) {
            Object savedRace = savedInstanceState.getSerializable(KEY_USER_RACE);
            if(savedRace != null) {
                course = (Course) savedRace;
                switchButtonsToState(course.getState());
                chronometerText.setText(course.getFormattedElapsedTime());
            }
        }
    }

    private void initialiserBoutons() {
        btnStart = (Button) findViewById(R.id.mapactivity_btn_start);
        btnStop = (Button) findViewById(R.id.mapactivity_btn_Stop);
        btnResume = (Button) findViewById(R.id.mapactivity_btn_resume);
        btnPause = (Button) findViewById(R.id.mapactivity_btn_Pause);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course = new Course(Course.TypeCourse.APIED); // TODO choix type
                course.setOnChronometerTick(onChronometerTick);
                switchButtonsToState(Course.State.STARTED);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.interrompre();
                switchButtonsToState(Course.State.PAUSED);
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.redemarrer();
                switchButtonsToState(Course.State.STARTED);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.endRaceAndSave(50, 50);
                switchButtonsToState(Course.State.STOPPED);
            }
        });
    }

    private void switchButtonsToState(Course.State state){
        switch (course.getState()) {
            case STARTED:
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                break;
            case PAUSED:
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                break;
            case STOPPED:
                btnStart.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if(course != null)
            course.setOnChronometerTick(onChronometerTick);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();

        if(course != null)
            course.removeOnChronometerTick();

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(course != null)
            outState.putSerializable(KEY_USER_RACE, course);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_stats:
                startActivity(new Intent(this, StatsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #googleMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
        if (course != null) {
            for (Location point : course.getPath()) {
                drawLineFromLastLocation(point);
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation == null || lastLocation.distanceTo(location) > 1) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatLng(location), 15));

            if(course != null && course.getState() == Course.State.STARTED) {
                course.addLocation(location);
                drawLineFromLastLocation(location);
            }
        }
    }

    private void drawLineFromLastLocation(Location to) {
        if (lastLocation != null) {
            googleMap.addPolyline(new PolylineOptions()
                    .add(toLatLng(lastLocation), toLatLng(to))
                    .width(5)
                    .color(Color.BLUE));
        }

        lastLocation = to;
    }

    private LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
