package gustavoposts.jimenez.privatizednotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import gustavoposts.jimenez.privatizednotes.database.model.Note;


//This class is used to perform CRUD operations on the notes
// database
public class DatabaseHelper extends SQLiteOpenHelper
{

    //keeps track of the current database version
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "notes_db";

    //constructor that takes the applications context as input
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //executed once the application is installed
    //starts the calling process necessary to create the needed
    //tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Note.CREATE_TABLE);
    }

    //removes the table if it exists and creates a new table when
    //upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        onCreate(db);
    }

    //insert row into database, returns the id of that note
    public long insertNote(String note)
    {

        //get a writable database where a new row may be inserted into
        SQLiteDatabase db = this.getWritableDatabase();

        //values will store the row data to be stored in the database
        ContentValues values = new ContentValues();

        //sets the column name, data, id and timestamp
        values.put(Note.COLUMN_NOTE, note);

        //inserts the row and store the id of the inserted
        //row
        long id = db.insert(Note.TABLE_NAME, null, values);

        //closes the database
        db.close();

        //returns the id of the inserted row
        return id;
    }

    //retrieves note by opening readable database
    public Note getNote(long id)
    {
        //gets a database only to be used for reading from database
        SQLiteDatabase db = this.getReadableDatabase();

        //cursor stores the readable data values
        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor.moveToFirst())
        {
        }

        //prepares new note
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        //closes the database connection
        cursor.close();

        return note;
    }

    //returns a list containing all of the stored notes
    public List<Note> getAllNotes()
    {
        //ArrayList for storing all of the notes
        List<Note> notes = new ArrayList<>();

        //selects all notes in timestamp order
        String selectQuery = "Select * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        while(cursor.moveToNext())
        {
            Note note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
            note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
            note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
            notes.add(note);
        }

            db.close();
            return notes;
    }

    //counts the number of stored notes
    public int getNotesCount()
    {
        String countQuery = "SELECT * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateNote(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " =?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " =?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
