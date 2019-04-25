package com.nulp.daliavskyimusic;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.AcraNotification;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;

@AcraNotification(resText = R.string.notification_text,
        resTitle = R.string.notification_title,
        resChannelName = R.string.notification_channel)
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.JSON);
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .setMailTo("mobivlad@outlook.com")
                .setSubject("DaliavskyiMusic report")
                .setReportFileName("report")
                .setEnabled(true);
        ACRA.init(this,builder);
    }
}
