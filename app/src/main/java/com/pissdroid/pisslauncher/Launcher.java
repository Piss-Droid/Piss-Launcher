package com.pissdroid.pisslauncher;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.view.*;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetHost;

import java.util.ArrayList;

public class Launcher extends AppCompatActivity {

    AppWidgetManager billy;
    AppWidgetHost bob;
    GestureDetector gestureDetector;

    ViewGroup layout;

    //Does stuff such as creating the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        layout = findViewById(R.id.activity_launcher);
        billy = AppWidgetManager.getInstance(this);
        bob = new AppWidgetHost(this, R.id.BOB_ID);
        gestureDetector = new GestureDetector(this, new GestureListener());
    }

    //Launches Widget Menu
    void selectWidget() {
        int bob_Id  = this.bob.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, bob_Id);
        addEmptyData(pickIntent);
        meGustaface.launch(pickIntent);
    }

    void addEmptyData(Intent pickIntent) {
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, new ArrayList<>());
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, new ArrayList<>());
    };

    ActivityResultLauncher<Intent> meGustaface = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent stuff = result.getData();
                    switch (result.getResultCode()) {
                        case RESULT_OK:
                            configstuff(stuff);
                        case RESULT_CANCELED:
                            if (stuff != null) {
                                int appWidgetId = stuff.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                                if (appWidgetId != -1) bob.deleteAppWidgetId(appWidgetId);
                            }
                    }
                }
            });

                private void configstuff(Intent stuff) {
                    int appWidgetId = stuff.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                    AppWidgetProviderInfo appWidgetInfo = billy.getAppWidgetInfo(appWidgetId);
                    if (appWidgetInfo.configure != null) {
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
                                .setComponent(appWidgetInfo.configure)
                                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, R.id.PICK_WIDGET);
                        leTrollface.launch(intent);
                    } else {
                        makething(stuff);
                    }
                }

                private void makething(Intent stuff) {
                    int appWidgetId = stuff.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                    AppWidgetProviderInfo appWidgetInfo = billy.getAppWidgetInfo(appWidgetId);
                    AppWidgetHostView hostView = bob.createView(this, appWidgetId, appWidgetInfo);
                    hostView.setAppWidget(appWidgetId, appWidgetInfo);
                    layout.addView(hostView);
                }

    ActivityResultLauncher<Intent> leTrollface = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent stuff = result.getData();
                    switch (result.getResultCode()) {
                        case RESULT_OK:
                            assert stuff != null;
                            makething(stuff);
                        case RESULT_CANCELED:
                            if (stuff != null) {
                                int appWidgetId = stuff.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                                if (appWidgetId != -1) bob.deleteAppWidgetId(appWidgetId);
                            }
                    }
                }
            });

    public void removeWidget(AppWidgetHostView hostView) {
        bob.deleteAppWidgetId(hostView.getAppWidgetId());
        layout.removeView(hostView);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            registerForContextMenu(findViewById(R.id.activity_launcher));
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.widgetsetting:
                selectWidget();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bob.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        bob.stopListening();
    }
}