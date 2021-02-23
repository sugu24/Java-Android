package ieexp.id180441112.ieexp3_step6;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ResultDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "resultDB.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "resultdb";
    private static final String TABLE_CREATE_SQL =
            "create table " + TABLE_NAME + "(" +
                    "question char(25)," +
                    "result char(5)" +
                    ")";
    private static final String DELETE_TABLE_SQL =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private String resultS;
    private String showTrueDataText, showFalseDataText;

    public ResultDatabase(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TABLE_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DELETE_TABLE_SQL);
        onCreate(db);
    }

    public void insertData(SQLiteDatabase db, String question, String answer, String result){
        try{
            question = question.substring(0, question.length()-1);
        }catch(StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        String insertSQL =
                "insert into " + TABLE_NAME + "(question, result)"+
                        " values (\"" + question  + answer + "\",\"" + result + "\")";

        try{
            db.execSQL(insertSQL);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String showData(SQLiteDatabase db){
        showTrueDataText = "";
        showFalseDataText = "";
        String question = "";
        String result = "";

        Cursor cursor = db.query(TABLE_NAME,  new String[] {"question", "result"}, null,null,null,null,null);
        boolean isEof = cursor.moveToFirst();
        if(!isEof){
            cursor.close();
            return "You do not save question";
        }
        while(isEof){
            question = cursor.getString(0);
            result = cursor.getString(1);
            if(result.equals("True")){
                showTrueDataText += question + "\n";
            }else if(result.equals("False")){
                showFalseDataText += question + "\n";
            }
            isEof = cursor.moveToNext();
        }
        cursor.close();
        return "Answer History\n\n\n" + "  True\n" + showTrueDataText + "\n  False\n" + showFalseDataText;
    }

    public void deleteTable(SQLiteDatabase db){
        db.execSQL(DELETE_TABLE_SQL);
        onCreate(db);
    }
}
