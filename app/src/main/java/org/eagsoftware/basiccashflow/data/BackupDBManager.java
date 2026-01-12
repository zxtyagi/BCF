package org.eagsoftware.basiccashflow.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupDBManager {
    public static final String EXC_NOT_VALID_FILE = "not_valid_file";
    public static final String  EXC_GENERIC = "generic_error";
    public static final String EXC_CORRUPT_DB = "corrupt_database";
    private static final int APP_ID = 1783456789;
    private final Context context;
    private final MyDB db;
    private final String dbName;
    private final Executor ioExec = Executors.newSingleThreadExecutor();

    public BackupDBManager(Context context, MyDB db, String dbName) {
        this.context = context;
        this.db = db;
        this.dbName = dbName;
    }


    public void asyncExportDB(final Uri destUri, final Callback callback){
        // Esegue PRAGMA wal_checkpoint(FULL) per consolidare WAL, poi copia file
        ioExec.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    // Thread.sleep(10_000); // DEBUG
                    // 1) Esegui checkpoint
                    runWalCheckpoint();
                    // 2) Imposta l'ID app (necessario per la verifica in import)
                    setAppID();
                    // 3) Chiudi DB per sicurezza
                    closeDatabase();
                    // 4) Copia file
                    //      Copia file DB principale
                    File dbFile = context.getDatabasePath(dbName);
                    //      Crea file ZIP
                    ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(destUri, "w");
                    if(pfd == null) throw new Exception("Impossibile aprire destinazione ZIP - pfd is null");
                    //noinspection TryFinallyCanBeTryWithResources
                    try(
                        FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                        ZipOutputStream zos = new ZipOutputStream(fos)
                    ){
                        addFileToZip(zos, dbFile, "database/" + dbFile.getName());
                    } finally {
                        pfd.close();
                    }

                    // 5) Riapre DB
                    reopenDatabase();

                    runOnSuccess(callback);
                } catch (Exception e){
                    runOnError(callback, e);
                }
            }
        });
    }


    public void asyncImportDB(final Uri sourceUri, final Callback callback){
        ioExec.execute(new Runnable() {
            @Override
            public void run() {
                /* DEBUG
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                 */

                File dbFile = context.getDatabasePath(dbName);
                File tmpFile = new File(context.getCacheDir(), "import_tmp.db");
                boolean resDel = true;
                try {
                    // 1) Chiude DB
                    runWalCheckpoint();
                    closeDatabase();
                    // 2) Estrai SOLO il DB in tmpFile
                    ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(sourceUri, "r");
                    if(pfd == null) throw new Exception("Impossibile aprire destinazione ZIP - pfd is null");
                    try (FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
                         ZipInputStream zis = new ZipInputStream(fis)) {
                        ZipEntry entry;
                        while ((entry = zis.getNextEntry()) != null) {
                            if (entry.getName().startsWith("database/")) {
                                try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
                                    copyStream(zis, fos);
                                }
                                zis.closeEntry();
                                break; // trovato il DB, basta
                            }
                            zis.closeEntry();
                        }
                    }
                    // 3) Verifica application_id sul DB temporaneo e lo copia
                    if(!verifyAppID(tmpFile)) throw new IllegalArgumentException(EXC_NOT_VALID_FILE);
                    copyFile(tmpFile, dbFile);

                    // 4) Elimina -wal e -shm per coerenza (Room li ricreerà)
                    File wal = new File(dbFile.getAbsolutePath() + "-wal");
                    File shm = new File(dbFile.getAbsolutePath() + "-shm");
                    if (wal.exists()) resDel = wal.delete();
                    if (shm.exists()) resDel = shm.delete();
                    if (!resDel) throw new RuntimeException(
                            "Impossibile eliminare file wal o shm - delete() failed");

                    // 6) Riapre DB
                    reopenDatabase();
                    runOnSuccess(callback);
                } catch (Exception e) {
                    // Invia il messaggio di errore
                    Exception exc;
                    String msg = e.getMessage();
                    if (msg != null && (msg.equals(EXC_NOT_VALID_FILE) || msg.equals(EXC_CORRUPT_DB))) exc = e;
                    else exc = new Exception(EXC_GENERIC, e);
                    runOnError(callback, exc);
                } finally {
                    // 7) Pulizia della cache
                    if (tmpFile.exists()) //noinspection ResultOfMethodCallIgnored
                        tmpFile.delete();
                    // Elimina la cartella temporanea se c'è
                    File tmpDir = new File(context.getCacheDir(), "import_tmp");
                    if (tmpDir.exists()) //noinspection ResultOfMethodCallIgnored
                        tmpDir.delete();
                    // Elimina anche i file WAL e SHM in cache
                    File tmpWal = new File(tmpFile.getAbsolutePath() + "-wal");
                    File tmpShm = new File(tmpFile.getAbsolutePath() + "-shm");
                    if (tmpWal.exists()) //noinspection ResultOfMethodCallIgnored
                        tmpWal.delete();
                    if (tmpShm.exists())//noinspection ResultOfMethodCallIgnored
                        tmpShm.delete();
                }
            }
        });
    }


    public interface Callback {
        void onComplete(Exception error); // Se error == null  => success
    }



    // -------------------------------------------------------
    // METODI PRIVATI
    // -------------------------------------------------------
    @SuppressWarnings("all")
     private void runWalCheckpoint() throws Exception{
       /* db.getOpenHelper().getWritableDatabase().execSQL("PRAGMA wal_checkpoint(FULL)");*/
         SupportSQLiteDatabase sq = db.getOpenHelper().getWritableDatabase();
         try (Cursor c = sq.query("PRAGMA wal_checkpoint(FULL);", new Object[]{})) {
             // non fare niente
         } catch (Exception e){
             throw new RuntimeException("wal_checkpoint fallito.", e);
         }
     }


     private void closeDatabase() {
        MyDB.closeInstance();
     }


     private void reopenDatabase(){
        MyDB.getInstance(context);
     }


     private void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
        try ( FileInputStream fis = new FileInputStream(file) ) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            copyStream(fis, zos);
            zos.closeEntry();
        }
     }


     private void copyFile(File src, File dest) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(src);
                FileOutputStream fos = new FileOutputStream(dest)
            ) {
            copyStream(fis, fos);
        }
     }


     private void runOnSuccess(Callback cbk) {
        if(cbk != null) cbk.onComplete(null);
     }

     private void runOnError(Callback cbk, Exception e){
        if(cbk != null) cbk.onComplete(e);
     }


     private boolean verifyAppID(File dbFile) {
        try (SQLiteDatabase tmpDB = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null,
                SQLiteDatabase.OPEN_READONLY);
             Cursor crs = tmpDB.rawQuery("PRAGMA application_id;", null)) {
            if(crs.moveToFirst()) {
                int appID = crs.getInt(0);
                return appID == APP_ID;
            }
            return false;
         } catch (SQLiteDatabaseCorruptException corruptExc) {
            throw new IllegalArgumentException(EXC_CORRUPT_DB, corruptExc);
        }
     }

     private void setAppID() {
        SupportSQLiteDatabase sqlDB = db.getOpenHelper().getWritableDatabase();
        sqlDB.execSQL("PRAGMA application_id = " + APP_ID);
     }

    private void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, len);
        outputStream.flush();
    }

}
