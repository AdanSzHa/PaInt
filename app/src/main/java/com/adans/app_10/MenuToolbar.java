package com.adans.app_10;

/**
 * Created by Wily on 27/04/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.adans.app_10.Cowtech54.CowTabActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialize.color.Material;
import com.squareup.picasso.Picasso;


public class MenuToolbar extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private String titleToolbar;
    private DrawerBuilder drawerBuilder;
    private Context context;

    public MenuToolbar() {
    }

    public MenuToolbar(Toolbar toolbar, String titleToolbar, Context context) {
        this.toolbar = toolbar;
        this.titleToolbar = titleToolbar;
        this.context = context;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public String getTitleToolbar() {
        return titleToolbar;
    }

    public void setTitleToolbar(String title) {
        this.titleToolbar = title;
    }

    public DrawerBuilder getDrawerBuilder() {
        return drawerBuilder;
    }

    public void setDrawerBuilder(DrawerBuilder drawerBuilder) {
        this.drawerBuilder = drawerBuilder;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void loadToolbar(){
       // android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        toolbar.setTitle(titleToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //GlobalVars g = (GlobalVars) getApplication();
      //  GlobalVars g = (GlobalVars) getApplication();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("wp").//g.getName()).
                                //withEmail(g.getEmail()).withIcon(getDrawable(R.drawable.profile)))
                                withEmail("wp@gm.com").withIcon(getDrawable(R.drawable.profile)))

                .withSelectionListEnabledForSingleProfile(false).withHeaderBackground(R.drawable.header)
                .build();
        //if you want to update the items at a later time it is recommended to keep it in a variable
        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_main).withIcon(GoogleMaterial.Icon.gmd_accessibility);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_profile).withIcon(GoogleMaterial.Icon.gmd_account_balance);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_records).withIcon(GoogleMaterial.Icon.gmd_add_to_photos);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_statistics).withIcon(GoogleMaterial.Icon.gmd_adb);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_bluetooth).withIcon(GoogleMaterial.Icon.gmd_attach_file);
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName(R.string.drawer_item_settinds).withIcon(GoogleMaterial.Icon.gmd_settings);
        PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(7).withName(R.string.drawer_item_about).withIcon(GoogleMaterial.Icon.gmd_terrain);
        PrimaryDrawerItem item8 = new PrimaryDrawerItem().withIdentifier(8).withName("Cowtech54").withIcon(R.mipmap.ic_cow_black_nbg);

        new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar).withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4,
                        item5,
                        new DividerDrawerItem(),
                        item6,
                        item7,
                        item8
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (position == 1) {
                            Intent intent = new Intent(context, CowTabActivity.class);//MainScreen.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }
                        if (position == 2) {
                            Intent intent = new Intent(context, PerfilAct.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }
                        if (position == 4){
                            Intent intent = new Intent(context, PruebaAct.class);
                            startActivity(intent);
                            Log.d(" TAG" , "Statistics opened");

                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }
                        if (position == 5){
                            Intent intent = new Intent(context, Bluetooth.class);
                            startActivity(intent);
                            Log.d(" TAG" , "BT opened");

                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }
                        if(position == 6 + 1){
                            Intent intent = new Intent(context, SettingsActivity.class);
                            startActivity(intent);
                            Log.d("TAG" , "Settings opened");

                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }
                        if(position == 8 + 1){
                            Intent intent = new Intent(context, CowTabActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityIfNeeded(intent, 0);

                            Log.d("TAG" , "CowMain opened");

                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }

                        return false;
                    }
                })
                .build();

        //Image Menu
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
           // GlobalVars g = (GlobalVars) getApplication();
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load("http://meddata.sytes.net/newuser/profileImg/" + "profile.jpg") //g.getImage())
                        .placeholder(placeholder).into(imageView);
            }
            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

    }

}
