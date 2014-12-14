package com.sds.ivor.fileexplorer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private RootTools rootUtils;

    private ListView lv;
    private List<FileListEntry> listFiles = new LinkedList<>();
    private static String ROOT_EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String actualPath = "";

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
/*
            Toast.makeText(this,actualPath,Toast.LENGTH_SHORT).show();
*/
            if (actualPath.equalsIgnoreCase("/storage"))
                new AlertDialog.Builder(this).setTitle("Do you want to leave???? ").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Cancel", null).show();
            else
                loadListedFiles(actualPath.substring(0, actualPath.lastIndexOf("/")));
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
    }

    private void start() {
        rootUtils = new RootTools();
        lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File f = ((FileListEntry) lv.getItemAtPosition(position)).getPath();

                if (((FileListEntry) lv.getItemAtPosition(position)).getName().equalsIgnoreCase(".")) {
                    Toast.makeText(MainActivity.this, "Current Directory: " + f.getPath(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (f.isDirectory()) {
                    if (!Util.isProtected(f))
                        loadListedFiles(f.getPath().toString());
                    else
                        Toast.makeText(MainActivity.this, "This Directory is protected\nNeeds Root Permissions", Toast.LENGTH_SHORT).show();
                } else if (f.isFile()) {
                    Util.openFile(MainActivity.this, new File(f.getPath()));
                }
            }
        });
        loadListedFiles(ROOT_EXTERNAL_DIR);
    }

    private List<FileListEntry> loadListedFiles(String path) {
        try {
            listFiles.clear();
            FileListEntry fle;
            for (File f : new File(path).listFiles()) {
                fle = new FileListEntry(f.getAbsolutePath().trim());
                fle.setSize(f.length());
                fle.setLastModified(new Date(f.lastModified()));
                listFiles.add(fle);
            }
            actualPath = path;
            placeAdapter();
            return listFiles;
        } catch (NullPointerException e) {
            Toast.makeText(MainActivity.this, "Cannot load this directory,needs Root Permissions" + actualPath.substring(0, actualPath.lastIndexOf("/")), Toast.LENGTH_SHORT).show();
            loadListedFiles(actualPath);
        }
        return listFiles;
    }

    private void placeAdapter() {
        this.setTitle("File Explorer "+actualPath);
        FileListEntry FlE = new FileListEntry(actualPath);
        FlE.setName(".");
        listFiles.add(0, FlE);
        FlE = new FileListEntry(actualPath.substring(0, actualPath.lastIndexOf("/")));
        FlE.setName("..");
        listFiles.add(1, FlE);
        lv.setAdapter(new FileListAdapter(this, listFiles));
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.action_del)
            new AlertDialog.Builder(this).setTitle(listFiles.get(info.position).getName() + "  " + listFiles.get(info.position).getLastModified()).setMessage("Delete this file?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FileUtils.deleteQuietly(listFiles.get(info.position).getPath());
                    loadListedFiles(actualPath);
                }
            }).setNegativeButton("Cancel", null).show();
        else if (item.getItemId() == R.id.action_properties)
            new AlertDialog.Builder(this).setTitle(listFiles.get(info.position).getName() + "  " + listFiles.get(info.position).getLastModified()).setMessage(Arrays.asList(Util.getFileProperties(listFiles.get(info.position), this)).toString().replaceAll("]","").replaceAll("\\[","").replaceAll(",","\n")).setPositiveButton("OK", null).show();
        return super.onContextItemSelected(item);
    }
}
