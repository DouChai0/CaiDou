package com.ldq.connect.HookInstance;

import java.io.Serializable;

public class HookRecallMsg {
    public static class TYSave implements Serializable
    {
        private static final long serialVersionUID=12345678;
        public int GIFId;
        public byte[] sData;
        public byte[] sShowData;
        public int offSet;
        public String CombineUrlPath;
    }
}
