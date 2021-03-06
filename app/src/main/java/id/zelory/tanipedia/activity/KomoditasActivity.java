/*
 * Copyright (c) 2015 Zetra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.zelory.tanipedia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import id.zelory.tanipedia.R;
import id.zelory.tanipedia.adapter.KomoditasAdapter;
import id.zelory.tanipedia.model.Komoditas;
import id.zelory.tanipedia.util.PrefUtils;
import mbanje.kurt.fabbutton.FabButton;

public class KomoditasActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Komoditas> komoditasArrayList;
    private FabButton fabButton;
    private ImageView imageHeader;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komoditas);
        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Harga Komoditas");

        animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);

        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        setUpNavDrawer();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);
        TextView nama = (TextView) navigationView.findViewById(R.id.nama);
        nama.setText(PrefUtils.ambilString(this, "nama"));
        TextView email = (TextView) navigationView.findViewById(R.id.email);
        email.setText(PrefUtils.ambilString(this, "email"));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                Intent intent;
                switch (menuItem.getItemId())
                {
                    case R.id.cuaca:
                        intent = new Intent(KomoditasActivity.this, CuacaActivity.class);
                        break;
                    case R.id.berita:
                        intent = new Intent(KomoditasActivity.this, BeritaActivity.class);
                        break;
                    case R.id.tanya:
                        intent = new Intent(KomoditasActivity.this, TanyaActivity.class);
                        break;
                    case R.id.harga:
                        return true;
                    case R.id.logout:
                        PrefUtils.simpanString(KomoditasActivity.this, "nama", null);
                        intent = new Intent(KomoditasActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.tentang:
                        intent = new Intent(KomoditasActivity.this, TentangActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return true;
                }
                startActivity(intent);
                finish();
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageHeader = (ImageView) findViewById(R.id.header);
        imageHeader.setVisibility(View.GONE);

        fabButton = (FabButton) findViewById(R.id.determinate);
        fabButton.showProgress(true);
        new DownloadData().execute();
        fabButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fabButton.showProgress(true);
                new DownloadData().execute();
            }
        });
    }

    private void setUpNavDrawer()
    {
        if (toolbar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_drawer);
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private class DownloadData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            ObjectMapper mapper = new ObjectMapper();
            komoditasArrayList = null;
            int i = 0;
            while (komoditasArrayList == null)
            {
                i++;
                try
                {
                    komoditasArrayList = mapper.readValue(new URL(Komoditas.API),
                            mapper.getTypeFactory().constructCollectionType(ArrayList.class, Komoditas.class));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (i >= 5)
                {
                    try
                    {
                        komoditasArrayList = mapper.readValue(PrefUtils.ambilString(KomoditasActivity.this, "komoditas"),
                                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Komoditas.class));
                    } catch (IOException e)
                    {
                        Snackbar.make(fabButton, "Terjadi kesalahan silahkan coba lagi!", Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                }
            }
            try
            {
                PrefUtils.simpanString(KomoditasActivity.this, "komoditas", mapper.writeValueAsString(komoditasArrayList));
            } catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (komoditasArrayList != null && !komoditasArrayList.isEmpty())
            {
                KomoditasAdapter adapter = new KomoditasAdapter(KomoditasActivity.this, komoditasArrayList);
                adapter.SetOnItemClickListener(new KomoditasAdapter.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        Komoditas komoditas = komoditasArrayList.get(position);
                        Snackbar.make(view, "Harga " + komoditas.getNama().toLowerCase() + " adalah Rp. " + komoditas.getHarga() + ",00 per Kg.", Snackbar.LENGTH_LONG).show();
                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.startAnimation(animation);
                imageHeader.setVisibility(View.VISIBLE);
                imageHeader.startAnimation(animation);
            } else
            {
                Snackbar.make(fabButton, "Mohon periksa koneksi internet anda!", Snackbar.LENGTH_LONG).show();
            }

            fabButton.onProgressCompleted();
            fabButton.showProgress(false);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    fabButton.resetIcon();
                }
            }, 2500);

        }
    }
}