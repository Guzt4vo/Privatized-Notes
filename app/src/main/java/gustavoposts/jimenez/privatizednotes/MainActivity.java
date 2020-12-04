package gustavoposts.jimenez.privatizednotes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import gustavoposts.jimenez.privatizednotes.database.DatabaseHelper;
import gustavoposts.jimenez.privatizednotes.database.model.Note;
import gustavoposts.jimenez.privatizednotes.utils.MyDividerItemDecoration;
import gustavoposts.jimenez.privatizednotes.utils.RecyclerTouchListener;
import gustavoposts.jimenez.privatizednotes.view.NotesAdapter;

//After the user is validated by LoginActivity then it will enter here in MainActivity where the majority of
//the app functionality is centered around, all CRUD operations are done through here to the database and back and forth.

public class MainActivity extends AppCompatActivity {
    //pattern used to identify alphanumneric characters
    private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
    //makes data in the database accessible via the UI
    private NotesAdapter adapteri;
    //List of notes stored in the SQLite database
    private List<Note> notesList = new ArrayList<>();
    //coordinates transitions between views
    private CoordinatorLayout coordinatorLayout;
    //provides a conveniet way to view notes
    private RecyclerView recyclerView;
    private TextView noNotesView;
    
    //used to access the functions necessary to complete the crud functions
    //of the SQLite Database
    private DatabaseHelper db;
    
    //upon the user being verified the activity_main layout is initiated
    //where the user will see options to interact with the notes UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);
        
        //retrieves all of the stored so as to begin the session
        notesList.addAll(db.getAllNotes());
        
        //button that provides the user with the option to create a note
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showNoteDialog(false, null, -1);
            }
        });
        
        //recyclerview parameters being set so that the UI and Database may present the
        //stored notes
        adapteri = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapteri);

        toggleEmptyNotes();
        
        //adds functionality to individual notes. When a note is clicked an actions dialog is displayed
        //that gives the user the option of editing or deleting a note
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position)
            {
                showActionsDialog(position);
            }
            //nothing is done on a long click
            @Override
            public void onLongClick(View view, int position)
            {
            }
        }));
    }
    
    //When a note is created so too is a hash that corresponds to the respective note
    private void createNote(String note) throws NoSuchAlgorithmException {
        //A note is inseted into the database and the id of its row is retrieved
        long id = db.insertNote(note);
        //That row is now retrieved which includes the note plus any pertinent data associated with it
        Note n = db.getNote(id);

        if(n != null)
        {
            notesList.add(0, n);

            adapteri.notifyDataSetChanged();

            toggleEmptyNotes();
        }
        
        //This is where the hash of the inputed note is calculated with the hashing algorithm SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String toHash = note;
        md.update(toHash.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String hash = String.format("%064x", new BigInteger(1, digest));

        long iid = db.insertNote(hash);
        Note m = db.getNote(iid);
        if(m != null)
        {
            notesList.add(0, m);
            adapteri.notifyDataSetChanged();
            toggleEmptyNotes();
        }

    }
    //deletes note at given position
     private void deleteNote(int position)
    {
        db.deleteNote(notesList.get(position));

        notesList.remove(position);
        adapteri.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    //note is updated at the position where it was selected
    //when a note is updated so too is its respective hash value entry
    private void updateNote(String note, int position) throws NoSuchAlgorithmException {
        //the note at the clicked position is retrieved
        Note notas = notesList.get(position);
        
        //
        notas.setNote(note);

        db.updateNote(notas);

        notesList.set(position, notas);
        adapteri.notifyItemChanged(position);

        toggleEmptyNotes();

        MessageDigest mdd = MessageDigest.getInstance("SHA-256");
        String toUpdateHash = note;
        mdd.update(toUpdateHash.getBytes(StandardCharsets.UTF_8));
        byte[] digests = mdd.digest();
        String hashes = String.format("%064x", new BigInteger(1, digests));

        Note mm = notesList.get(position-1);
        mm.setNote(hashes);
        db.updateNote(mm);
        notesList.set(position-1, mm);
        adapteri.notifyItemChanged(position-1);
        toggleEmptyNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    //makes a small options menu with an edit and delete button
    private void showActionsDialog(final int position)
    {
        CharSequence[] opciones = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    showNoteDialog(true, notesList.get(position), position);
                }
                else
                {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }
    
    //gives functionality to the buttons inside the options menu
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position)
    {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));
        
        //if the user wants to update a note then the new note is retrieved and the previous entry is updated
        if(shouldUpdate && note != null)
        {
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput.setCancelable(false).setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {

            }
        }).setNegativeButton("cancel",
                (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            //various checks made to check that inserted note is within the legal constraints of not being empty
            //and not being over 1000 characters
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty(inputNote.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "No note entered", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(inputNote.length() > 1000)
                {
                    Toast.makeText(MainActivity.this, "Note is too long!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    alertDialog.dismiss();
                }

                if(shouldUpdate && note != null)
                {
                    try {
                        updateNote(inputNote.getText().toString(), position);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        createNote(inputNote.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void toggleEmptyNotes()
    {
        if(db.getNotesCount()>0)
        {
            noNotesView.setVisibility(View.GONE);
        }
        else
        {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
