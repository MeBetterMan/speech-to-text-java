
/**
 * Created by Y Tian on 8/21/2015.
 */
public class FRIAudioDecoder {

    private static final String LOG_TAG = "FRIAudioDecoder";

    private static final int[] INDEX_TABLE = {
            -1, -1, -1, -1, 2, 4, 6, 8,
            -1, -1, -1, -1, 2, 4, 6, 8
    };

//    private static final int[] STEP_SIZE_TABLE = {
//            1,	2,	3,	4,	5,	6,	7,	8,	9,	10,
//            11,	12,	13,	14,	15,	16,	17,	18,	19,	20,
//            21,	22,	23,	24,	25,	26,	27,	28,	29,	30,
//            31,	32,	33,	34,	35,	36,	37,	38,	40,	45,
//            49,	54,	59,	65,	72,	79,	87,	95,	105,115,
//            127
//    };

    private static final int[] STEP_SIZE_TABLE = {
            7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
            19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
            50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
            130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
            337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
            876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
            2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
            5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
            15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767

    };

    private static final short[] STEP_SIZE_TABLE_S = {
            7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
            19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
            50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
            130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
            337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
            876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
            2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
            5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
            15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767

    };

    private int sPrevsample_l = 0;
    private int sPrevindex_l = 0;
    private int sPrevsample_r = 0;
    private int sPrevindex_r = 0;

    private short sPrevsample_ls = 0;
    private short sPrevindex_ls = 0;
    private short sPrevsample_rs = 0;
    private short sPrevindex_rs = 0;


    public FRIAudioDecoder() {
    }


//    public void setInitSample(byte[] sample){
//        sPrevsample = sample[sample.length - 2];
//        sPrevindex = sample[sample.length - 1];
//
//            if( sPrevindex < 0){
//                sPrevindex = 0;
//            }else if( sPrevindex > 88 ){
//                sPrevindex = 88;
//        }
//    }

    public int DecodeMono(byte[] input, byte[] output, int cnt) {
//        byte[] output = new byte[input.length * 2];

        int count = 0;
        byte temp1, temp2;
        int step;
        int prevsample_l, prevsample_r;
        int diffq;
        int index_l, index_r;

        if (cnt == 1) {//single channel
//            prevsample_l = (((int)input[input.length - 6]<<8) & 0xFF00) + ((int)input[input.length - 5] & 0x00FF);
//            index_l = input[input.length - 4];
//            int tmp1 = ((int)input[input.length -3] <<8) & 0xFF00 ;
//            int tmp2 =  (int)input[input.length - 2];
//            int tmp3 = tmp1 + (int)input[input.length - 2];
            prevsample_r = (((int) input[input.length - 3] << 8) & 0xFF00) + ((int) input[input.length - 2] & 0x00FF);
            index_r = input[input.length - 1];

//            if( (prevsample_l & 0x8000) != 0) {
//                prevsample_l = prevsample_l + 0xFFFF0000;
//            }

            if ((prevsample_r & 0x8000) != 0) {
                prevsample_r = prevsample_r + 0xFFFF0000;
            }
        } else {
//            prevsample_l = sPrevsample_l;
//            index_l = sPrevindex_l;
            prevsample_r = sPrevsample_r;
            index_r = sPrevindex_r;

        }

        do {
            temp1 = (byte) ((input[count] >> 4) & 0x0F);
            step = STEP_SIZE_TABLE[index_r];

            diffq = step >> 3;

            if ((temp1 & 4) != 0) {
                diffq += step;
            }
            if ((temp1 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp1 & 1) != 0) {
                diffq += (step >> 2);
//                prevsample_r = (byte) (prevsample_r - diffq);
            }

            if ((temp1 & 8) != 0) {
                prevsample_r = (prevsample_r - diffq);
            } else {
                prevsample_r = (prevsample_r + diffq);
            }

            if (prevsample_r > 32767) {
                prevsample_r = 32767;
            } else if (prevsample_r < -32768) {
                prevsample_r = -32768;
            }

            index_r += INDEX_TABLE[temp1];

            if (index_r < 0) {
                index_r = 0;
            }
            if (index_r > 88) {
                index_r = 88;
            }


            //Left channel sample-1
            output[4 * count + 1] = (byte) ((prevsample_r >> 8) & 0x00FF);
            output[4 * count] = (byte) (prevsample_r & 0x00FF);


            //Right channel sampe-1
            temp2 = (byte) (input[count] & 0x0F);
            //right channel
            step = STEP_SIZE_TABLE[index_r];

            diffq = step >> 3;

            if ((temp2 & 4) != 0) {
                diffq += step;
            }
            if ((temp2 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp2 & 1) != 0) {
                diffq += (step >> 2);
            }

            if ((temp2 & 8) != 0) {
                prevsample_r = (prevsample_r - diffq);
            } else {
                prevsample_r = (prevsample_r + diffq);
            }

            if (prevsample_r > 32767) {
                prevsample_r = 32767;
            } else if (prevsample_r < -32768) {
                prevsample_r = -32768;
            }

            index_r += INDEX_TABLE[temp2];

            if (index_r < 0) {
                index_r = 0;
            }
            if (index_r > 88) {
                index_r = 88;
            }

            output[4 * count + 3] = (byte) ((prevsample_r >> 8) & 0x00FF);
            output[4 * count + 2] = (byte) (prevsample_r & 0x00FF);

//            output[count * 2 + 1] = (byte) (prevsample + 128);

            count++;
            if (count == 73) {
                //Log.d(LOG_TAG,"break point");
            }
        } while (count < (input.length - 6));


//        sPrevsample = prevsample;
//        sPrevsample_l = prevsample_l;
//        sPrevindex_l = index_l;
        sPrevsample_r = prevsample_r;
        sPrevindex_r = index_r;

        return (count << 2);
    }

    public int DecodeStereo(byte[] input, byte[] output, int cnt) {
//        byte[] output = new byte[input.length * 2];

        int count = 0;
        byte temp1, temp2;
        int step;
        int prevsample_l, prevsample_r;
        int diffq;
        int index_l, index_r;

        if (cnt == 1) {//single channel
            prevsample_l = (((int) input[input.length - 6] << 8) & 0xFF00) + ((int) input[input.length - 5] & 0x00FF);
            index_l = input[input.length - 4];
//            int tmp1 = ((int)input[input.length -3] <<8) & 0xFF00 ;
//            int tmp2 =  (int)input[input.length - 2];
//            int tmp3 = tmp1 + (int)input[input.length - 2];
            prevsample_r = (((int) input[input.length - 3] << 8) & 0xFF00) + ((int) input[input.length - 2] & 0x00FF);
            index_r = input[input.length - 1];

            if ((prevsample_l & 0x8000) != 0) {
                prevsample_l = prevsample_l + 0xFFFF0000;
            }

            if ((prevsample_r & 0x8000) != 0) {
                prevsample_r = prevsample_r + 0xFFFF0000;
            }
        } else {
            prevsample_l = sPrevsample_l;
            index_l = sPrevindex_l;
            prevsample_r = sPrevsample_r;
            index_r = sPrevindex_r;

        }

        do {
            temp1 = (byte) ((input[count] >> 4) & 0x0F);
            step = STEP_SIZE_TABLE[index_l];

            diffq = step >> 3;

            if ((temp1 & 4) != 0) {
                diffq += step;
            }
            if ((temp1 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp1 & 1) != 0) {
                diffq += (step >> 2);
//                prevsample_l = (byte) (prevsample_l - diffq);
            }

            if ((temp1 & 8) != 0) {
                prevsample_l = (prevsample_l - diffq);
            } else {
                prevsample_l = (prevsample_l + diffq);
            }

            if (prevsample_l > 32767) {
                prevsample_l = 32767;
            } else if (prevsample_l < -32768) {
                prevsample_l = -32768;
            }

            index_l += INDEX_TABLE[temp1];

            if (index_l < 0) {
                index_l = 0;
            }
            if (index_l > 88) {
                index_l = 88;
            }


            //Left channel sample-1
            output[4 * count + 1] = (byte) ((prevsample_l >> 8) & 0x00FF);
            output[4 * count] = (byte) (prevsample_l & 0x00FF);


            //Right channel sampe-1
            temp2 = (byte) (input[count] & 0x0F);
            //right channel
            step = STEP_SIZE_TABLE[index_r];

            diffq = step >> 3;

            if ((temp2 & 4) != 0) {
                diffq += step;
            }
            if ((temp2 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp2 & 1) != 0) {
                diffq += (step >> 2);
            }

            if ((temp2 & 8) != 0) {
                prevsample_r = (prevsample_r - diffq);
            } else {
                prevsample_r = (prevsample_r + diffq);
            }

            if (prevsample_r > 32767) {
                prevsample_r = 32767;
            } else if (prevsample_r < -32768) {
                prevsample_r = -32768;
            }

            index_r += INDEX_TABLE[temp2];

            if (index_r < 0) {
                index_r = 0;
            }
            if (index_r > 88) {
                index_r = 88;
            }

            output[4 * count + 3] = (byte) ((prevsample_r >> 8) & 0x00FF);
            output[4 * count + 2] = (byte) (prevsample_r & 0x00FF);

            if (output[4 * count + 1] == 0) {
                output[4 * count + 1] = output[4 * count + 3];
            }

            if (output[4 * count] == 0) {
                output[4 * count] = output[4 * count + 2];
            }
//            output[count * 2 + 1] = (byte) (prevsample + 128);

            count++;
            if (count == 73) {
//                //Log.d("aa","break point");
            }

//            //Log.d("FRI_Decoder", "prevsample_l=" + prevsample_l);
//            //Log.d("FRI_Decoder", "prevsample_r=" + prevsample_r);
        } while (count < (input.length - 6));


//        sPrevsample = prevsample;
        sPrevsample_l = prevsample_l;
        sPrevindex_l = index_l;
        sPrevsample_r = prevsample_r;
        sPrevindex_r = index_r;

        return (count << 2);
    }

    public int DecodeFakeStereo(byte[] input, byte[] output, int cnt) {
//        byte[] output = new byte[input.length * 2];

        int count = 0;
        byte temp1, temp2;
        int step;
        int prevsample_l, prevsample_r;
        int diffq;
        int index_l, index_r;

        if (cnt == 1) {//single channel
            prevsample_l = (((int) input[input.length - 6] << 8) & 0xFF00) + ((int) input[input.length - 5] & 0x00FF);
            index_l = input[input.length - 4];
//            int tmp1 = ((int)input[input.length -3] <<8) & 0xFF00 ;
//            int tmp2 =  (int)input[input.length - 2];
//            int tmp3 = tmp1 + (int)input[input.length - 2];
            prevsample_r = (((int) input[input.length - 3] << 8) & 0xFF00) + ((int) input[input.length - 2] & 0x00FF);
            index_r = input[input.length - 1];

            if ((prevsample_l & 0x8000) != 0) {
                prevsample_l = prevsample_l + 0xFFFF0000;
            }

            if ((prevsample_r & 0x8000) != 0) {
                prevsample_r = prevsample_r + 0xFFFF0000;
            }
        } else {
            prevsample_l = sPrevsample_l;
            index_l = sPrevindex_l;
            prevsample_r = sPrevsample_r;
            index_r = sPrevindex_r;

        }

        do {
            temp1 = (byte) ((input[count] >> 4) & 0x0F);
            step = STEP_SIZE_TABLE[index_l];

            diffq = step >> 3;

            if ((temp1 & 4) != 0) {
                diffq += step;
            }
            if ((temp1 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp1 & 1) != 0) {
                diffq += (step >> 2);
//                prevsample_l = (byte) (prevsample_l - diffq);
            }

            if ((temp1 & 8) != 0) {
                prevsample_l = (prevsample_l - diffq);
            } else {
                prevsample_l = (prevsample_l + diffq);
            }

            if (prevsample_l > 32767) {
                prevsample_l = 32767;
            } else if (prevsample_l < -32768) {
                prevsample_l = -32768;
            }

            index_l += INDEX_TABLE[temp1];

            if (index_l < 0) {
                index_l = 0;
            }
            if (index_l > 88) {
                index_l = 88;
            }


            //Left channel sample-1
            output[4 * count + 1] = (byte) ((prevsample_l >> 8) & 0x00FF);
            output[4 * count] = (byte) (prevsample_l & 0x00FF);


            //Right channel sampe-1
            temp2 = (byte) (input[count] & 0x0F);
            //right channel
            step = STEP_SIZE_TABLE[index_r];

            diffq = step >> 3;

            if ((temp2 & 4) != 0) {
                diffq += step;
            }
            if ((temp2 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp2 & 1) != 0) {
                diffq += (step >> 2);
            }

            if ((temp2 & 8) != 0) {
                prevsample_r = (prevsample_r - diffq);
            } else {
                prevsample_r = (prevsample_r + diffq);
            }

            if (prevsample_r > 32767) {
                prevsample_r = 32767;
            } else if (prevsample_r < -32768) {
                prevsample_r = -32768;
            }

            index_r += INDEX_TABLE[temp2];

            if (index_r < 0) {
                index_r = 0;
            }
            if (index_r > 88) {
                index_r = 88;
            }

            output[4 * count + 3] = (byte) ((prevsample_r >> 8) & 0x00FF);
            output[4 * count + 2] = (byte) (prevsample_r & 0x00FF);
            output[4 * count + 1] = output[4 * count + 3];
            output[4 * count] = output[4 * count + 2];

//            output[count * 2 + 1] = (byte) (prevsample + 128);

            count++;
            if (count == 73) {
                //Log.d("aa","break point");
            }
        } while (count < (input.length - 6));


//        sPrevsample = prevsample;
        sPrevsample_l = prevsample_l;
        sPrevindex_l = index_l;
        sPrevsample_r = prevsample_r;
        sPrevindex_r = index_r;

        return (count << 2);
    }

    public short Decode2(byte[] input, byte[] output, int cnt) {
//        byte[] output = new byte[input.length * 2];

        short count = 0;
        byte temp1, temp2;
        short step;
        short prevsample_l, prevsample_r;
        short diffq;
        short index_l, index_r;

        if (cnt == 1) {//single channel
            prevsample_l = (short) ((((short) input[input.length - 6] << 8) & 0xFF00) + ((short) input[input.length - 5] & 0x00FF));
            index_l = input[input.length - 4];
//            short tmp1 = ((short)input[input.length -3] <<8) & 0xFF00 ;
//            short tmp2 =  (short)input[input.length - 2];
//            short tmp3 = tmp1 + (short)input[input.length - 2];
            prevsample_r = (short) ((((short) input[input.length - 3] << 8) & 0xFF00) + ((short) input[input.length - 2] & 0x00FF));
            index_r = input[input.length - 1];

            if ((prevsample_l & 0x8000) != 0) {
                prevsample_l = (short) (prevsample_l + 0xFFFF0000);
            }

            if ((prevsample_r & 0x8000) != 0) {
                prevsample_r = (short) (prevsample_r + 0xFFFF0000);
            }
        } else {
            prevsample_l = sPrevsample_ls;
            index_l = sPrevindex_ls;
            prevsample_r = sPrevsample_rs;
            index_r = sPrevindex_rs;

        }

        do {
            temp1 = (byte) ((input[count] >> 4) & 0x0F);
            step = STEP_SIZE_TABLE_S[index_l];

            diffq = (short) (step >> 3);

            if ((temp1 & 4) != 0) {
                diffq += step;
            }
            if ((temp1 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp1 & 1) != 0) {
                diffq += (step >> 2);
//                prevsample_l = (byte) (prevsample_l - diffq);
            }

            if ((temp1 & 8) != 0) {
                prevsample_l = (short) (prevsample_l - diffq);
            } else {
                prevsample_l = (short) (prevsample_l + diffq);
            }

            if (prevsample_l > 32767) {
                prevsample_l = 32767;
            } else if (prevsample_l < -32768) {
                prevsample_l = -32768;
            }

            index_l += INDEX_TABLE[temp1];

            if (index_l < 0) {
                index_l = 0;
            }
            if (index_l > 88) {
                index_l = 88;
            }


            //Left channel sample-1
            output[4 * count + 1] = (byte) ((prevsample_l >> 8) & 0x00FF);
            output[4 * count] = (byte) (prevsample_l & 0x00FF);


            //Right channel sampe-1
            temp2 = (byte) (input[count] & 0x0F);
            //right channel
            step = STEP_SIZE_TABLE_S[index_r];

            diffq = (short) (step >> 3);

            if ((temp2 & 4) != 0) {
                diffq += step;
            }
            if ((temp2 & 2) != 0) {
                diffq += (step >> 1);
            }
            if ((temp2 & 1) != 0) {
                diffq += (step >> 2);
            }

            if ((temp2 & 8) != 0) {
                prevsample_r = (short) (prevsample_r - diffq);
            } else {
                prevsample_r = (short) (prevsample_r + diffq);
            }

            if (prevsample_r > 32767) {
                prevsample_r = 32767;
            } else if (prevsample_r < -32768) {
                prevsample_r = -32768;
            }

            index_r += INDEX_TABLE[temp2];

            if (index_r < 0) {
                index_r = 0;
            }
            if (index_r > 88) {
                index_r = 88;
            }

            output[4 * count + 3] = (byte) ((prevsample_r >> 8) & 0x00FF);
            output[4 * count + 2] = (byte) (prevsample_r & 0x00FF);

//            output[count * 2 + 1] = (byte) (prevsample + 128);

            count++;
            if (count == 73) {
                //Log.d("aa","break poshort");
            }
        } while (count < (input.length - 6));


//        sPrevsample = prevsample;
        sPrevsample_ls = prevsample_l;
        sPrevindex_ls = index_l;
        sPrevsample_rs = prevsample_r;
        sPrevindex_rs = index_r;

        return (short) (count << 2);
    }

}
