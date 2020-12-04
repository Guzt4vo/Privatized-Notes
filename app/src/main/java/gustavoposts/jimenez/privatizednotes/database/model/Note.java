package gustavoposts.jimenez.privatizednotes.database.model;

//Note class defines the columns and table name
//the table has three columns: id, note and timestamp
//id is a unique identifier per row of the table
//note is the stored note of text
//timestamp stores the date and time

public class Note {
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    //id value is automatically incremented each time
    //a note is created
    private int id;
    private String note;
    private String timestamp;

    /* todo
    * public static final String COLUMN_HASH = "hash"
    * private String hash;*/

    //string used to query for the creation of an SQLitedatabase
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

    public Note()
    {
    }

    //constructs the note object
    public Note(int id, String note, String timestamp)
    {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
    }

    //the following functions return the relevant data of the
    //desired note
    public int getId()
    {
        return id;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    //public void setHash(String hash { this.hash = hash; }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
