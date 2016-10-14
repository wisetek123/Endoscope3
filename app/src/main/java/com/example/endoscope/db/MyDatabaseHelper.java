package com.example.endoscope.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String CREATE_DOCTOR = "create table Doctor ("
			+ "Did text primary key , " + "Daccount text not null, "
			+ "Dpassword text not null)";

	public static final String CREATE_PATIENT = "create table Patient ("
			+ "Pno integer primary key autoincrement  , "
			+ "Pid text not null ," + "Pname text not null, "
			+ "Psex text not null, " + "Page text not null, "
			+ "Ptel text, " + "Padr text, " + "PIDnum text ," +"Judge integer not null)";

	public static final String CREATE_PD = "create table PD ("
			+ "Did text not null, " + "Pno text not null,"+"No interger not null)";

	public static final String CREATE_DIAGNOSIS = "create table Diagnosis ("
			+ "Dno integer primary key , " + "Pno text not null, "
			+ "Ddate date not null, " + "Dsymptom text not null, "
			+ "Dresult text not null, " + "image text null)";
	public static final String CREATE_UPLOAD = "CREATE TABLE uploadlog (id integer primary key autoincrement, uploadfilepath varchar(100))";
	// public static final String CREATE_PD = "create table PD ("
	// + "Pno integer primary key autoincrement, "
	// + "Dno integer primary key autoincrement, "
	// + "foreign key (Pno) references Patient (Pno))";

	private Context mContext;

	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DOCTOR);
		db.execSQL(CREATE_PATIENT);
		db.execSQL(CREATE_DIAGNOSIS);
		db.execSQL(CREATE_PD);
		db.execSQL(CREATE_UPLOAD);
		// Toast.makeText(mContext, "Create succeeded !",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("drop table if exists Book");
		// db.execSQL("drop table if exists Category");
		onCreate(db);
	}

}
