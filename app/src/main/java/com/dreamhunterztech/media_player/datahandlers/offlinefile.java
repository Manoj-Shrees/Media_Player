/*
package com.dreamhunterztech.media_player.datahandlers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

*/
/**
 * Created by Dreamer on 05-11-2017.
 *//*


public class offlinefile extends AppCompatActivity {

    ListView fileslist;
    ArrayList<String> filename , fileurl;
    int position;
    SwipeRefreshLayout filerefresh;
    private offlinefiles adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.offlinelibfilelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.offlinetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Offline Files");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        filename = new ArrayList<String>();
        fileurl = new ArrayList<String>();

        filerefresh = findViewById(R.id.offlinefileswipe);

       TextView head = (TextView) findViewById(R.id.offlinefilehead);
        head.setSelected(true);
       fileslist = findViewById(R.id.libofflinelist);

        filerefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        filerefresh.setSoundEffectsEnabled(true);
        filerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getofffiledata().execute();
            }
        });



    }


    private void getofffiledata()
    {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), getApplicationContext().getString(R.string.libfileurl));
                File filepath = new File(root ,getApplicationContext().getString(R.string.filename));
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(filepath);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("File");

                filename.clear();
                fileurl.clear();

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        filename.add( eElement
                                .getElementsByTagName("filename")
                                .item(0)
                                .getTextContent());


                        fileurl.add(eElement
                                .getElementsByTagName("fileurl")
                                .item(0)
                                .getTextContent());

                    }
                }
            }

            catch (Exception e) {

                Log.e("Error",": file  not found");
            }
    }

    public class offlinefiles extends BaseAdapter {

        ArrayList<String> filenamelist , fileurl;
        LayoutInflater inflater;

        offlinefiles(Activity activity , ArrayList<String> filenamelist , ArrayList<String> fileurl)
        {
            this.filenamelist = filenamelist;
            this.fileurl = fileurl;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return filenamelist.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vi=view;
            if(view==null)
                position=i;
                vi = inflater.inflate(R.layout.liboffbooklayout, viewGroup, false);
            final TextView filename = vi.findViewById(R.id.filename);
            Button viewbtn = vi.findViewById(R.id.viewbtn);
            Button delbtn = vi.findViewById(R.id.delbtn);
            CardView filecardView = vi.findViewById(R.id.filecard);


            filename.setSelected(true);
            filename.setText(filenamelist.get(i).toString());

            YoYo.with(Techniques.Landing).playOn(filecardView);


            viewbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Intent openpdf = new Intent(getApplicationContext(),PDFviewer.class);
                    openpdf.putExtra("pdf_url",fileurl.get(position));
                    openpdf.putExtra("datatype","file");
                    startActivity(openpdf);
                }
            });

            delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder deletefiledialog= new AlertDialog.Builder(offlinefile.this);
                    deletefiledialog.setTitle(filename.getText().toString());
                    deletefiledialog.setMessage("Do you want to Delete this file ?");
                    deletefiledialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                new offlinedatahandler(getApplicationContext().getString(R.string.libfileurl),getApplicationContext().getString(R.string.filename),filename.getText().toString().trim()).deletefile(position);
                                new getofffiledata().execute();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (TransformerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    deletefiledialog.setNeutralButton("Cancel",null);
                    deletefiledialog.show();

                }
            });

            return vi;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        new getofffiledata().execute();
    }



    public class getofffiledata extends AsyncTask
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            filerefresh.setRefreshing(true);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            getofffiledata();
             adapter = new offlinefiles(offlinefile.this,filename,fileurl);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            fileslist.setAdapter(adapter);
            filerefresh.setRefreshing(false);
        }
    }



}



*/
