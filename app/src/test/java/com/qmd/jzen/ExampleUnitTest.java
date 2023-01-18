package com.qmd.jzen.qmd;

import static org.junit.Assert.assertEquals;

import com.qmd.jzen.utils.EncryptAndDecrypt;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        float a = (3 / 2) * 2.0f;
        assertEquals(4, 2 + 2);
    }

    @Test
    public void encryptTest() {
        String str = "85LUICGytpc=";
        String text = EncryptAndDecrypt.encryptDES("nothing", "88888888");
        assertEquals(str, text);
        assertEquals("nothing", EncryptAndDecrypt.decryptDES(text, "88888888"));
        String cookieEn = "vQocBZWUVipDbffGIJVA2m7167SnInxYYgfT630vMRrvxqgB3sUgTcjw/-WHGyXxxkAlUGTRBolQXzSOiSoHJTe2e1|dwKhvxt%rf0c4-jgkomSDZrLQ9yHwYw==";

        assertEquals("F000001YxfVL2UdnwB.flac|0025erd01yIoTW",
                EncryptAndDecrypt.decryptText("ODQaYYoM/tNE-/pyQXUDfYDVBcT-6q254GOyW8udgppgH23IeUR43-4Ew==", "1920363953"));
    }

    @Test
    public void test() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < 5) {
                list.remove(i);
                i = 0;
                continue;
            }
            System.out.println(list.get(i));
        }
    }

}