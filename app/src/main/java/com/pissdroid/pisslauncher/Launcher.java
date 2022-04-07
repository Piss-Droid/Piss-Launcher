package com.pissdroid.pisslauncher;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.view.*;
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

        layout = (ViewGroup) findViewById(R.id.activity_launcher);
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
        startActivityForResult(pickIntent, R.id.PICK_WIDGET);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    };

    @Override
    protected void onActivityResult(int request, int result, Intent stuff) {
        super.onActivityResult(request, result, stuff);
        if (result == RESULT_OK && (request == R.id.PICK_WIDGET)) { // TODO Change this to a switch case later maybe. :)
            configstuff(stuff);
        }
        if (result == RESULT_OK && (request == R.id.CREATE_WIDGET)) {
            makething(stuff);
        }
        if (result == RESULT_CANCELED && stuff != null) {
            int appWidgetId = stuff.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                bob.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    //Checks for config stuff for the widget
    private void configstuff(Intent stuff) {
        int appWidgetId = stuff.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = billy.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, R.id.CREATE_WIDGET);
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