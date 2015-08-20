/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Raphaël Bussa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.andres.chatandroid.navigationDrawer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.andres.chatandroid.FragmentContactos;
import com.example.andres.chatandroid.Contactos;
import com.example.andres.chatandroid.FragmentMensages;
import com.example.andres.chatandroid.R;
import com.example.andres.chatandroid.chat.BlankFragment2;
import com.example.andres.chatandroid.chat.Constantes;
import com.example.andres.chatandroid.chat.chackListGrupos;
import com.example.andres.chatandroid.headerview.HeaderInterface;
import com.example.andres.chatandroid.headerview.HeaderView;
import com.squareup.picasso.Picasso;

/**
 * clase encargada de generar y mostrar el navigation drawer de la pantalla principal y realiza la tarea de redireccionamiento en la aplicacion
 */

public class HeaderActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private android.support.v4.app.Fragment fragment;
    private int menu;
    private SharedPreferences preferences;
    private TabLayout pestañas;
    private String Title = "Home";
    private String SubTitle = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);
        pestañas = (TabLayout) findViewById(R.id.appbartabs);
        pestañas.addTab(pestañas.newTab().setText("Chats"));
        pestañas.addTab(pestañas.newTab().setText("Contactos"));
        pestañas.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        ActualizarToolbar(R.menu.vacio, "Home", "", new BlankFragment2());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        preferences = getSharedPreferences(Constantes.sharePreference, Context.MODE_PRIVATE);
        navigationView.addHeaderView(headerView());


        /**
         * En este punto del codigo se relaiza el redireccionamiento del navigation drawer
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                BlankFragment2 f1;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        f1 = new BlankFragment2();
                        f1.SetUrl("http://proyectopgr.herokuapp.com/index3.html#/");
                        fragment = f1;
                        Title = "Home";
                        SubTitle = "";
                        menu = R.menu.vacio2;
                        pestañas.setVisibility(View.GONE);
                        break;
                    case R.id.mensaje:
                        fragment = new FragmentMensages();
                        Title = "Comunidad";
                        SubTitle = "";
                        menu = R.menu.main2;
                        pestañas.setVisibility(View.VISIBLE);
                        break;
                    case R.id.noticia:
                        f1 = new BlankFragment2();
                        f1.SetUrl("http://proyectopgr.herokuapp.com/index3.html#/nev/noticias");
                        fragment = f1;
                        Title = "Noticias";
                        SubTitle = "";
                        menu = R.menu.vacio;
                        pestañas.setVisibility(View.GONE);
                        break;
                    case R.id.eventos:
                        f1 = new BlankFragment2();
                        f1.SetUrl("http://proyectopgr.herokuapp.com/index3.html#/nev/eventos");
                        fragment = f1;
                        Title = "Eventos";
                        SubTitle = "";
                        menu = R.menu.vacio;
                        pestañas.setVisibility(View.GONE);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "caso no visto", Toast.LENGTH_SHORT).show();
                }
                ActualizarToolbar(menu, Title, SubTitle, fragment);

                return true;
            }
        });

        /**
         * este parte del codigo realiza el redireccionamiento en las pestañas de la parte d comunidad entre contactos y chat
         */
        pestañas.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        fragment = new FragmentMensages();
                        Title = "Comunidad";
                        SubTitle = "";
                        menu = R.menu.vacio2;
                        break;
                    case 1:
                        fragment = new FragmentContactos();
                        Title = "Comunidad";
                        SubTitle = "";
                        menu = R.menu.main;
                        break;
                }


                ActualizarToolbar(menu, Title, SubTitle, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /**
         * En esta parte del codigo se activa y se coloca el icono del despliegue del menu izquierdo
          */
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

    }
    

    private HeaderView headerView() {
        HeaderView headerView = new HeaderView(HeaderActivity.this);
        headerView.background().setBackgroundColor(getResources().getColor(R.color.primary_dark));
        Picasso.with(HeaderActivity.this)
                .load("http://www.nexus-lab.com/wp-content/uploads/2014/08/image_new-material.jpeg")
                .into(headerView.background());
        Picasso.with(HeaderActivity.this)
                .load(R.drawable.ic_contact_picture)
                .into(headerView.avatar());
        headerView.username(preferences.getString(Constantes.PROPERTY_NAME, ""));
        headerView.email(preferences.getString(Constantes.PROPERTY_USER, ""));
        headerView.setOnHeaderClickListener(new HeaderInterface.OnHeaderClickListener() {
            @Override
            public void onClick() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        return headerView;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * en este metodo se realiza la actuliacion de los menus y nombres del toolbar cada vez que se cambia de elemento
     * @param idMenu
     * @param title
     * @param subTitle
     * @param fragment
     */
    public void ActualizarToolbar(int idMenu, final String title, String subTitle, Fragment fragment){

            final Fragment fragm = fragment;

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
        toolbar.getMenu().clear();
        ActualizarMenu(idMenu);
        toolbar.setTitle(title);
        toolbar.setSubtitle(subTitle);
        toolbar.refreshDrawableState();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.action_add:
                        Intent add = new Intent(HeaderActivity.this, Contactos.class);
                        startActivity(add);
                        break;
                    case R.id.action_group:
                        Intent intentgroup = new Intent(HeaderActivity.this, chackListGrupos.class);
                        startActivity(intentgroup);
                        break;
                    case R.id.actualizar:
                            if(title != "Comunidad") {
                                final BlankFragment2 frag = (BlankFragment2) fragm;
                                frag.recargar();
                            }
                        break;
                }

                return true;
            }
        });
    }

    public void ActualizarMenu(int menu){
        toolbar.getMenu().clear();
        toolbar.inflateMenu(menu);
    }
}
