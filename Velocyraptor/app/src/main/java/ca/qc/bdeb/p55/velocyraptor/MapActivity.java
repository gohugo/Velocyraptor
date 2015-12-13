package ca.qc.bdeb.p55.velocyraptor;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.common.Formatting;
import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;
import ca.qc.bdeb.p55.velocyraptor.model.Achievement;
import ca.qc.bdeb.p55.velocyraptor.model.Course;
import ca.qc.bdeb.p55.velocyraptor.model.Ghost;
import ca.qc.bdeb.p55.velocyraptor.model.RaceMarker;


/**
 * Activité principale : carte et données de la course en cours.
 */
public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String KEY_USER_RACE = "userrace";
    private static final String KEY_GHOST = "ghost";

    private static final int MAP_LINE_WIDTH = 3;

    private GoogleMap googleMap;
    public TextView chronometerText;
    private TextView distanceText;
    private TextView calorieText;
    private TextView stepText;
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
                    updateDuration();
                    distanceText.setText(Formatting.formatDistance(course.getDistanceInMeters()));
                    calorieText.setText(String.valueOf(course.getCalories()));
                    stepText.setText(String.valueOf(course.getNbCountedSteps()));
                    drawGhostPath(ghost.getNextMarkersAt(course.getElapsedSeconds()));
                }
            });
        }
    };

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    /**
     * Vrai si un zoom s'est fait à l'emplacement de l'utilisateur au moins une fois.
     */
    private boolean hasMovedOnceToUserLocation = false;

    private Course course;
    private Location lastLocation;
    private Ghost ghost;
    private Location lastGhostLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase.setApplicationContext(getApplicationContext());
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        chronometerText = (TextView) findViewById(R.id.mapactivity_txt_chronometer);
        distanceText = (TextView) findViewById(R.id.mapactivity_lbl_distancevalue);
        calorieText = (TextView) findViewById(R.id.mapactivity_lbl_calorievalue);
        stepText = (TextView) findViewById(R.id.mapactivity_lbl_rythmevalue);

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
            if (savedRace != null) {
                course = (Course) savedRace;
                ghost = (Ghost) savedInstanceState.getSerializable(KEY_GHOST);
                switchButtonsToCurrentRaceState();
                updateDuration();

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
                ghost = Ghost.startGhostFromLastRace(Course.TypeCourse.APIED);
                course.setContext(getApplicationContext());
                course.setOnChronometerTick(onChronometerTick);
                course.demarrer();

                if (lastLocation != null)
                    moveUserToOnMap(lastLocation);

                switchButtonsToCurrentRaceState();
                setMapControlsEnabled(false);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.interrompre();
                updateDuration();
                switchButtonsToCurrentRaceState();
                setMapControlsEnabled(false);
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.demarrer();
                switchButtonsToCurrentRaceState();
                setMapControlsEnabled(false);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.endRaceAndSave();
                updateDuration();
                switchButtonsToCurrentRaceState();
                setMapControlsEnabled(true);
            }
        });
    }

    private void switchButtonsToCurrentRaceState() {
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

    private void setMapControlsEnabled(boolean areEnabled) {
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(areEnabled);
        settings.setAllGesturesEnabled(areEnabled);
    }

    private void updateDuration() {
        chronometerText.setText(Formatting.formatExactDuration(course.getElapsedMilliseconds()));
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

        if (course != null) {
            course.setOnChronometerTick(onChronometerTick);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();

        if (course != null)
            course.removeOnChronometerTick();

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (course != null) {
            outState.putSerializable(KEY_USER_RACE, course);
            outState.putSerializable(KEY_GHOST, ghost);
        }
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
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
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
     * <p>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
        if (course != null) {
            for (Location point : course.getPath()) {
                drawLineFromLastLocation(point);
            }

            drawGhostPath(ghost.getReadMarkers());
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
        if (!hasMovedOnceToUserLocation || course != null && course.getState() != Course.State.STOPPED
                && (lastLocation == null || lastLocation.distanceTo(location) > 1)) {
            moveUserToOnMap(location);

            if (course != null && course.getState() == Course.State.STARTED) {
                course.addLocation(location);
                drawLineFromLastLocation(location);
            }

            hasMovedOnceToUserLocation = true;
        }
    }

    private void moveUserToOnMap(Location to) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatLng(to), 16));
    }

    private void drawLineFromLastLocation(Location to) {
        if (lastLocation != null) {
            googleMap.addPolyline(new PolylineOptions()
                    .add(toLatLng(lastLocation), toLatLng(to))
                    .width(MAP_LINE_WIDTH)
                    .color(Color.BLUE));
        }

        lastLocation = to;
    }

    private void drawGhostPath(List<RaceMarker> markers) {
        for (RaceMarker marker : ghost.getReadMarkers()) {
            if (lastGhostLocation != null) {
                drawGhostLine(lastGhostLocation, marker.getLocation());
            }

            lastGhostLocation = marker.getLocation();
        }
    }

    private void drawGhostLine(Location from, Location to) {
        googleMap.addPolyline(new PolylineOptions()
                .add(toLatLng(from), toLatLng(to))
                .width(MAP_LINE_WIDTH)
                .color(Color.GRAY));
    }

    private LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

}
