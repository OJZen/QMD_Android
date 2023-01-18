package com.qmd.jzen.qmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.orhanobut.logger.Logger;
import com.qmd.jzen.utils.EncryptAndDecrypt;
import com.qmd.jzen.utils.SystemInfoUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();
        assertEquals("com.qmd.jzen", appContext.getPackageName());
    }

    @Test
    public void encryptTest() {
        String str = "85LUICGytpc=";
        String text = EncryptAndDecrypt.encryptDES("nothing", "88888888");
        assertEquals(str, text);
        assertEquals("nothing", EncryptAndDecrypt.decryptDES(text, "88888888"));
        //String cookieEn = "vQocBZWUVipDbffGIJVA2m7167SnInxYYgfT630vMRrvxqgB3sUgTcjw/-WHGyXxxkAlUGTRBolQXzSOiSoHJTe2e1|dwKhvxt%rf0c4-jgkomSDZrLQ9yHwYw==";

        assertEquals("F000001YxfVL2UdnwB.flac|0025erd01yIoTW",
                EncryptAndDecrypt.decryptText("ODQaYYoM/tNE-/pyQXUDfYDVBcT-6q254GOyW8udgppgH23IeUR43-4Ew==", "1920363953"));
    }

    @Test
    public void outputTest() {
        Logger.e(SystemInfoUtil.getAppVersionName());
        Logger.e(SystemInfoUtil.getAppVersionCode() + "");
    }
/*
    @Test
    public void tagTest() throws InvalidDataException, UnsupportedTagException, IOException, NotSupportedException {

        Mp3File mp3file = new Mp3File("/sdcard/Music/Ice Paper - 心如止水.flac");

        System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
        System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
        System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
        System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
        System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
        System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));

        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            System.out.println("Track: " + id3v1Tag.getTrack());
            System.out.println("Artist: " + id3v1Tag.getArtist());
            System.out.println("Title: " + id3v1Tag.getTitle());
            System.out.println("Album: " + id3v1Tag.getAlbum());
            System.out.println("Year: " + id3v1Tag.getYear());
            System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
            System.out.println("Comment: " + id3v1Tag.getComment());
        }

        ID3v1 id3v1Tag;
        if (mp3file.hasId3v1Tag()) {
            id3v1Tag =  mp3file.getId3v1Tag();
        } else {
            id3v1Tag = new ID3v1Tag();
            mp3file.setId3v1Tag(id3v1Tag);
        }
        id3v1Tag.setTrack("5");
        id3v1Tag.setArtist("An Artist");
        id3v1Tag.setTitle("The Title");
        id3v1Tag.setAlbum("The Album");
        id3v1Tag.setYear("2001");
        id3v1Tag.setGenre(12);
        id3v1Tag.setComment("Some comment");
        mp3file.save("MyMp3File.mp3");


        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            System.out.println("Track: " + id3v2Tag.getTrack());
            System.out.println("Artist: " + id3v2Tag.getArtist());
            System.out.println("Title: " + id3v2Tag.getTitle());
            System.out.println("Album: " + id3v2Tag.getAlbum());
            System.out.println("Year: " + id3v2Tag.getYear());
            System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
            System.out.println("Comment: " + id3v2Tag.getComment());
            System.out.println("Composer: " + id3v2Tag.getComposer());
            System.out.println("Publisher: " + id3v2Tag.getPublisher());
            System.out.println("Original artist: " + id3v2Tag.getOriginalArtist());
            System.out.println("Album artist: " + id3v2Tag.getAlbumArtist());
            System.out.println("Copyright: " + id3v2Tag.getCopyright());
            System.out.println("URL: " + id3v2Tag.getUrl());
            System.out.println("Encoder: " + id3v2Tag.getEncoder());

            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                String mimeType = id3v2Tag.getAlbumImageMimeType();
                System.out.println("Mime type: " + mimeType);
                // Write image to file - can determine appropriate file extension from the mime type
                FileOutputStream stream = new FileOutputStream("/sdcard/Pictures/1.jpg");
                stream.write(imageData);
                stream.flush();
                stream.close();
            }
        }

        ID3v2 id3v2Tag;
        if (mp3file.hasId3v2Tag()) {
            id3v2Tag =  mp3file.getId3v2Tag();
        } else {
            id3v2Tag = new ID3v24Tag();
            mp3file.setId3v2Tag(id3v2Tag);
        }
        id3v2Tag.setTrack("5");
        id3v2Tag.setArtist("An Artist");
        id3v2Tag.setTitle("The Title");
        id3v2Tag.setAlbum("The Album");
        id3v2Tag.setYear("2001");
        id3v2Tag.setGenre(12);
        id3v2Tag.setComment("Some comment");
        id3v2Tag.setComposer("The Composer");
        id3v2Tag.setPublisher("A Publisher");
        id3v2Tag.setOriginalArtist("Another Artist");
        id3v2Tag.setAlbumArtist("An Artist");
        id3v2Tag.setCopyright("Copyright");
        id3v2Tag.setUrl("http://foobar");
        id3v2Tag.setEncoder("The Encoder");
        mp3file.save("MyMp3File.mp3");

    }
    */

    @Test
    public void decodeID3() throws IOException {
        FileInputStream stream = new FileInputStream("/sdcard/Music/Ice Paper - 心如止水.mp3");
        // 64MB
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024 * 64);
        int len = 1024 * 128;
        while (true) {
            byte[] buffer = new byte[len];
            int rlen = stream.read(buffer, 0, buffer.length);
            if (rlen <= 0) {
                break;
            }
            byteBuffer.put(buffer, 0, rlen);
        }
        byteBuffer.flip();
        Id3Decoder decoder = new Id3Decoder();

        Metadata data = decoder.decode(byteBuffer.array(), byteBuffer.limit());
        assert data != null;
        System.out.println(data.length());
    }

}
