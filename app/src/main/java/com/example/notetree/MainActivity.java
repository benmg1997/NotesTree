package com.example.notetree;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    public final static String LOGTAG = "NOTETREELOG";

    public static MainActivity main;

    public static File notesDirectory;
    public static File currentDirectory;
    public static File currentFile;
    public static File selectedFile;

    public int creating = 0; // 0 for folder, 1 for file, 2 for chain

    public RecyclerView rV;
    public FolderAdapter fA;

    public Button yes;
    public Button no;
    public TextView areYouSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        permissions();
    }

    @Override
    public void onBackPressed() {
        if(!currentDirectory.getPath().equals(notesDirectory.getPath())) {
            currentDirectory = currentDirectory.getParentFile();
            createInputVisibility(View.GONE);
            createChoiceVisibility(View.GONE);
            fA.refresh();
        }
    }

    public void permissions() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setupScreen();
                setupFiles();
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                permissions();
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            permissions();
        }
    }

    public void setupScreen() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                FloatingActionButton fabFile = findViewById(R.id.fabFile);
                createInputVisibility(View.GONE);
                if(fabFile.getVisibility() == View.GONE) {
                    createChoiceVisibility(View.VISIBLE);
                }
                else {
                    createChoiceVisibility(View.GONE);
                }
            }
        });

        Button create = findViewById(R.id.nameInputCreate);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
                createInputVisibility(View.GONE);
            }
        });

        Button cancel = findViewById(R.id.nameInputCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputVisibility(View.GONE);
            }
        });

        FloatingActionButton fabFile = findViewById(R.id.fabFile);
        fabFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputVisibility(View.VISIBLE);
                creating = 1;
                ((ImageView) findViewById(R.id.nameInputImage)).setImageResource(R.drawable.textsmall);
                createChoiceVisibility(View.GONE);
            }
        });

        FloatingActionButton fabFolder = findViewById(R.id.fabFolder);
        fabFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputVisibility(View.VISIBLE);
                creating = 0;
                ((ImageView) findViewById(R.id.nameInputImage)).setImageResource(R.drawable.foldersmall);
                createChoiceVisibility(View.GONE);
            }
        });

        FloatingActionButton fabChain = findViewById(R.id.fabChain);
        fabChain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputVisibility(View.VISIBLE);
                creating = 2;
                ((ImageView) findViewById(R.id.nameInputImage)).setImageResource(R.drawable.chainsmall);
                createChoiceVisibility(View.GONE);
            }
        });

        yes = findViewById(R.id.areYouSureYes);
        no = findViewById(R.id.areYouSureNo);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVisibility(View.GONE);
                delete();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVisibility(View.GONE);
            }
        });

        findViewById(R.id.folderCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopups();
            }
        });
        findViewById(R.id.folderRenameCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopups();
            }
        });
        findViewById(R.id.folderRenameRename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename();
            }
        });
        findViewById(R.id.folderRename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopups();
                renameVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.folderDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopups();
                deleteVisibility(View.VISIBLE);
            }
        });
    }

    public void setupFiles() {
        notesDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/NoteTree/Notes");
        currentDirectory = new File(notesDirectory.getPath());
        selectedFile = new File(notesDirectory.getPath());
        if(!notesDirectory.exists()) {
            Log.e(LOGTAG, "Directory not exist");
            if(!notesDirectory.mkdirs()) {
                Log.e(LOGTAG, "Directory not created");
            }
            else {
                Log.i(LOGTAG, "Directory is created");
            }
        }
        rV = findViewById(R.id.folderView);
        fA = new FolderAdapter(this, currentDirectory.listFiles());
        rV.setAdapter(fA);
        rV.setLayoutManager(new LinearLayoutManager(this));
        fA.refresh();
    }





    public void hidePopups() {
        createInputVisibility(View.GONE);
        settingsVisibility(View.GONE);
        renameVisibility(View.GONE);
        deleteVisibility(View.GONE);
        findViewById(R.id.fabFolder).setVisibility(View.GONE);
        findViewById(R.id.fabFile).setVisibility(View.GONE);
        findViewById(R.id.fabChain).setVisibility(View.GONE);
    }





    // THIS IS FOR CREATING A FILE
    public void createInputVisibility(int visibility) {
        if(creating == 0) {
            ((ImageView) findViewById(R.id.nameInputImage)).setImageResource(R.drawable.foldersmall);
        }
        else if(creating == 1) {
            ((ImageView) findViewById(R.id.nameInputImage)).setImageResource(R.drawable.textsmall);
        }
        else if(creating == 2) {
            ((ImageView) findViewById(R.id.nameInputImage)).setImageResource(R.drawable.chainsmall);
        }
        ((TextView) findViewById(R.id.nameInput)).setText("");
        findViewById(R.id.nameInputBox).setVisibility(visibility);
    }
    public void createChoiceVisibility(int visibility) {
        findViewById(R.id.fabFile).setVisibility(visibility);
        findViewById(R.id.fabFolder).setVisibility(visibility);
        findViewById(R.id.fabChain).setVisibility(visibility);
    }
    public void create() {
        TextView textView = findViewById(R.id.nameInput);
        if(textView.getText().toString().equals("")) {
            Toast.makeText(this, "Input empty", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                if (creating == 0) {
                    File temp = new File(currentDirectory, textView.getText().toString());
                    if (!temp.exists()) {
                        Log.e(LOGTAG, "Directory not exist");
                        if (!temp.mkdir()) {
                            Log.e(LOGTAG, "Directory not created");
                        } else {
                            Log.i(LOGTAG, "Directory is created");
                        }
                    }
                }
                if (creating == 1) {
                    File temp = new File(currentDirectory, textView.getText().toString() + ".txt");
                    if (!temp.exists()) {
                        Log.e(LOGTAG, "File not exist");
                        if (!temp.createNewFile()) {
                            Log.e(LOGTAG, "File not created");
                        } else {
                            Log.i(LOGTAG, "File is created");
                        }
                    }
                }
                if (creating == 2) {
                    File file = new File(notesDirectory, textView.getText().toString());
                    if (file.exists()) {
                        File temp = new File(currentDirectory, file.getName().substring(0, file.getName().length() - 4) + ".chn");
                        if (!temp.exists()) {
                            Log.e(LOGTAG, "File not exist");
                            if (!temp.createNewFile()) {
                                Log.e(LOGTAG, "File not created");
                            } else {
                                Log.i(LOGTAG, "File is created");
                                try {
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(temp));
                                    Log.i(LOGTAG, textView.getText().toString());
                                    outputStreamWriter.write(textView.getText().toString());
                                    outputStreamWriter.close();
                                } catch (IOException e) {
                                    Log.e(MainActivity.LOGTAG, e.toString());
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "File doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }
                fA.refresh();
            } catch (IOException e) {
                Log.e(LOGTAG, e.toString());
            }
        }
    }





    // THIS IS FOR THE SETTINGS POPUP
    public void settingsVisibility(int visibility) {
        findViewById(R.id.folderSettings).setVisibility(visibility);
        setImage(findViewById(R.id.folderImage));
        ((TextView) findViewById(R.id.folderName)).setText(selectedFile.getName());
    }




    // THIS IS FOR RENAMING A FILE
    public void renameVisibility(int visibility) {
        findViewById(R.id.folderRenameBox).setVisibility(visibility);
        setImage(findViewById(R.id.folderRenameImage));
        ((TextView) findViewById(R.id.folderRenameInput)).setText(selectedFile.getName());
    }
    public void rename() {
        String newName = ((TextView) findViewById(R.id.folderRenameInput)).getText().toString();
        if(selectedFile.isDirectory()) {
            selectedFile.renameTo(new File(selectedFile.getParentFile(), newName));
            fA.refresh();
        }
        else {
            if(newName.substring(newName.length() - 4).equals(".txt")) {
                selectedFile.renameTo(new File(selectedFile.getParentFile(), newName));
                fA.refresh();
            }
            else {
                selectedFile.renameTo(new File(selectedFile.getParentFile(), newName + ".txt"));
                fA.refresh();
            }
        }
    }




    // THIS IS FOR DELETING A FILE
    public void deleteVisibility(int view) {
        findViewById(R.id.folderDeleteBox).setVisibility(view);
    }
    public void delete() {
        if(selectedFile.isDirectory()) {
            deleteFolder(selectedFile);
        }
        else {
            selectedFile.delete();
        }
        fA.refresh();
    }
    public void deleteFolder(File folder) {
        File[] folderFiles = folder.listFiles();
        for(int i = 0; i < folderFiles.length; i++) {
            if(folderFiles[i].isDirectory()) {
                deleteFolder(folderFiles[i]);
            }
            else {
                folderFiles[i].delete();
            }
        }
        folder.delete();
    }



    public void setImage(View view) {
        setImage((ImageView) view);
    }
    public void setImage(ImageView image) {
        if(selectedFile.isDirectory()) {
            image.setImageResource(R.drawable.foldersmall);
        }
        else {
            image.setImageResource(R.drawable.textsmall);
        }
    }
}
