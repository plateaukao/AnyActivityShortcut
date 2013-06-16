package com.danielkao.aashortcut;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ListActivity {
    // Buffer used to store package and class information, and also determine the number of installed activities
    private ArrayList<String[]> _activitiesBuffer = null;

    // Buffers for package and class information
    private String[] _packages = null;
    private String[] _classes = null;
    //private String[] _icons = null;
    private List<Drawable> _icons = null;

    // Index used to fill buffers
    private int _index = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get all installed activities (package and class information for every activity)
        getAllInstalledActivities();

        // Set content to GUI
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, _classes));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        // Add listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // When clicked, show a toast with the selected activity
                Toast.makeText(
                        getApplicationContext(),
                        ((TextView) view).getText(),
                        Toast.LENGTH_SHORT).show();

                createShortcut(_packages[position],_classes[position],_icons.get(position));
                finish();

            } // public void onItemClick(AdapterView<?> parent, View view, int position, long id)

        });

    } // public void onCreate(Bundle savedInstanceState)

    /**
     * create shortcut for the pressed item
     * @param packageName
     * @param className
     */
    private void createShortcut(String packageName, String className, Drawable d){

        //Intent.ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);

        Intent intent = new Intent();
        Intent launchIntent = new Intent().setClassName(
                packageName, // package
                className); // class

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, className);
        BitmapDrawable bd = (BitmapDrawable)d;
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bd.getBitmap());

        setResult(RESULT_OK, intent);
    }

    /*
     * Get all installed activities
     */
    private void getAllInstalledActivities() {

        // Initialize activities buffer
        _activitiesBuffer = new ArrayList<String[]>();
        _icons = new ArrayList<Drawable>();

        // Get installed packages
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);


        // Get activities for every package
        for (PackageInfo packageInfo : installedPackages) {

            // Get activities for current package
            ActivityInfo[] activities = packageInfo.activities;

            // For every activity save package and class information
            if (activities != null)
                for (ActivityInfo activityInfo : activities) {

                String[] buf = new String[2];
                buf[0] = activityInfo.packageName;
                buf[1] = activityInfo.name;

                Resources resources = null;
                    try {
                        resources = getPackageManager().getResourcesForApplication(activityInfo.applicationInfo);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                if(null != resources){
                    try {
                    _icons.add(resources.getDrawable(activityInfo.getIconResource()));
                    } catch (Resources.NotFoundException e) {
                        _icons.add(getResources().getDrawable(R.drawable.ic_launcher));
                    }
                }

                    _activitiesBuffer.add(buf);

            } // for (ActivityInfo activityInfo : activities)

            // if (activities != null)
        } // for (PackageInfo packageInfo : installedPackages)

        _packages = new String[_activitiesBuffer.size()];
        _classes = new String[_activitiesBuffer.size()];

        Iterator<String[]> iterator = _activitiesBuffer.iterator();
        while (iterator.hasNext()) {

            String[] buf = iterator.next();

            // Store package information
            _packages[_index] = buf[0];

            // Store class information
            _classes[_index] = buf[1];

            _index++;

        } // while (iterator.hasNext())

    } // private void getAllInstalledActivities()

}
