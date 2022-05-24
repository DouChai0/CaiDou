package com.ldq.connect.MainWorker.LittileHook;


public class Hook_For_Prevent_TroopFile_Delete {
    public static void Start() throws Throwable{

    }
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytes2HexStr(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            try {
                byte b = bArr[i];
                int i2 = i * 2;
                char[] cArr2 = DIGITS;
                cArr[i2 + 1] = cArr2[b & 15];
                cArr[i2 + 0] = cArr2[((byte) (b >>> 4)) & 15];
            } catch (Exception e) {

                return "";
            }
        }
        return new String(cArr);
    }

}
