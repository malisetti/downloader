package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.abbiya.downloader.greendao");
        Entity link = schema.addEntity("Link");
        link.addIdProperty();
        link.addStringProperty("name");
        link.addStringProperty("location");
        link.addStringProperty("description");
        link.addStringProperty("status");
        link.addIntProperty("progress");
        link.addLongProperty("downloadId");

        Entity job = schema.addEntity("Job");
        job.addIdProperty();
        job.addStringProperty("tag");
        job.addStringProperty("status");
        job.addLongProperty("size");
        job.addIntProperty("progress");
        job.addStringProperty("location");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
