package com.leon.counter_reading.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.leon.counter_reading.tables.CounterReportDao;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.CounterStateDao;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.DynamicTraverse;
import com.leon.counter_reading.tables.DynamicTraverseDao;
import com.leon.counter_reading.tables.ForbiddenDao;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.Guilds;
import com.leon.counter_reading.tables.GuildsDao;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.ImageDao;
import com.leon.counter_reading.tables.KarbariDao;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OffLoadReportDao;
import com.leon.counter_reading.tables.OnOffLoadDao;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.QotrDictionaryDao;
import com.leon.counter_reading.tables.ReadingConfigDefaultDao;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.tables.SavedLocationsDao;
import com.leon.counter_reading.tables.TrackingDao;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.tables.VoiceDao;

@Database(entities = {SavedLocation.class, KarbariDto.class, OnOffLoadDto.class,
        QotrDictionary.class, ReadingConfigDefaultDto.class, TrackingDto.class, Voice.class,
        CounterStateDto.class, Image.class, CounterReportDto.class, OffLoadReport.class,
        ForbiddenDto.class, Guilds.class, DynamicTraverse.class},
        version = 1/*, exportSchema = false*/)
public abstract class MyDatabase extends RoomDatabase {
    public static final Migration MIGRATION_4_5 = new Migration(16, 17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM KarbariDto");
            database.execSQL("DROP TABLE KarbariDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO KarbariDto");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM SavedLocation");
            database.execSQL("DROP TABLE SavedLocation");
            database.execSQL("ALTER TABLE t1_backup RENAME TO SavedLocation");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM OnOffLoadDto");
            database.execSQL("DROP TABLE OnOffLoadDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO OnOffLoadDto");
            database.execSQL("DROP TABLE t1_backup");


            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM QotrDictionary");
            database.execSQL("DROP TABLE QotrDictionary");
            database.execSQL("ALTER TABLE t1_backup RENAME TO QotrDictionary");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM ReadingConfigDefaultDto");
            database.execSQL("DROP TABLE ReadingConfigDefaultDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO ReadingConfigDefaultDto");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM TrackingDto");
            database.execSQL("DROP TABLE TrackingDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO TrackingDto");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM CounterStateDto");
            database.execSQL("DROP TABLE CounterStateDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO CounterStateDto");
            database.execSQL("DROP TABLE t1_backup");
        }
    };
    public static final Migration MIGRATION_3_4 = new Migration(18, 19) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM TrackingDto");
            database.execSQL("DROP TABLE TrackingDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO TrackingDto");
            database.execSQL("DROP TABLE t1_backup");
        }
    };
    public static final Migration MIGRATION_6_7 = new Migration(17, 18) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE "OnOffLoadDtoTemp" (
                    \t"customId"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    \t"id"\tTEXT,
                    \t"billId"\tTEXT,
                    \t"radif"\tINTEGER NOT NULL,
                    \t"eshterak"\tTEXT,
                    \t"qeraatCode"\tTEXT,
                    \t"firstName"\tTEXT,
                    \t"sureName"\tTEXT,
                    \t"address"\tTEXT,
                    \t"pelak"\tTEXT,
                    \t"karbariCode"\tINTEGER NOT NULL,
                    \t"ahadMaskooniOrAsli"\tINTEGER NOT NULL,
                    \t"ahadTejariOrFari"\tINTEGER NOT NULL,
                    \t"ahadSaierOrAbBaha"\tINTEGER NOT NULL,
                    \t"qotrCode"\tINTEGER NOT NULL,
                    \t"sifoonQotrCode"\tINTEGER NOT NULL,
                    \t"postalCode"\tTEXT,
                    \t"preNumber"\tINTEGER NOT NULL,
                    \t"preDate"\tTEXT,
                    \t"preDateMiladi"\tTEXT,
                    \t"preAverage"\tREAL NOT NULL,
                    \t"preCounterStateCode"\tINTEGER NOT NULL,
                    \t"counterSerial"\tTEXT,
                    \t"counterInstallDate"\tTEXT,
                    \t"tavizDate"\tTEXT,
                    \t"tavizNumber"\tTEXT,
                    \t"trackingId"\tTEXT,
                    \t"trackNumber"\tINTEGER,
                    \t"zarfiat"\tINTEGER NOT NULL,
                    \t"mobile"\tTEXT,
                    \t"hazf"\tINTEGER NOT NULL,
                    \t"noeVagozariId"\tINTEGER NOT NULL,
                    \t"counterNumber"\tINTEGER NOT NULL,
                    \t"counterStateId"\tINTEGER NOT NULL,
                    \t"possibleAddress"\tTEXT,
                    \t"possibleCounterSerial"\tTEXT,
                    \t"possibleEshterak"\tTEXT,
                    \t"possibleMobile"\tTEXT,
                    \t"possiblePhoneNumber"\tTEXT,
                    \t"possibleAhadMaskooniOrAsli"\tINTEGER NOT NULL,
                    \t"possibleAhadTejariOrFari"\tINTEGER NOT NULL,
                    \t"possibleAhadSaierOrAbBaha"\tINTEGER NOT NULL,
                    \t"possibleEmpty"\tINTEGER NOT NULL,
                    \t"possibleKarbariCode"\tINTEGER NOT NULL,
                    \t"description"\tTEXT,
                    \t"offLoadStateId"\tINTEGER NOT NULL,
                    \t"zoneId"\tINTEGER NOT NULL,
                    \t"gisAccuracy"\tREAL NOT NULL,
                    \t"x"\tREAL NOT NULL,
                    \t"y"\tREAL NOT NULL,
                    \t"counterNumberShown"\tINTEGER NOT NULL,
                    \t"highLowStateId"\tINTEGER NOT NULL,
                    \t"isBazdid"\tINTEGER NOT NULL,
                    \t"counterStatePosition"\tINTEGER
                    );""");
            database.execSQL("DROP TABLE OnOffLoadDto");
            database.execSQL("ALTER TABLE OnOffLoadDtoTemp RENAME TO OnOffLoadDto");


        }
    };
    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX 'id'");
        }
    };

    public abstract KarbariDao karbariDao();

    public abstract ImageDao imageDao();

    public abstract OnOffLoadDao onOffLoadDao();

    public abstract QotrDictionaryDao qotrDictionaryDao();

    public abstract ReadingConfigDefaultDao readingConfigDefaultDao();

    public abstract SavedLocationsDao savedLocationDao();

    public abstract CounterStateDao counterStateDao();

    public abstract TrackingDao trackingDao();

    public abstract CounterReportDao counterReportDao();

    public abstract OffLoadReportDao offLoadReportDao();

    public abstract ForbiddenDao forbiddenDao();

    public abstract VoiceDao voiceDao();

    public abstract GuildsDao guildDao();

    public abstract DynamicTraverseDao dynamicTraverseDao();
}
