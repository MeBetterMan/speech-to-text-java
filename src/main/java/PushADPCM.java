import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.LinkedList;

public class PushADPCM {

    public static void dataDispose(Object msg) throws URISyntaxException, InterruptedException {
        LinkedList<byte[]> DataBlock_list = new LinkedList<>();
        JSONArray msg_arr = JSON.parseArray(msg.toString());
        final Base64.Decoder decoder = Base64.getDecoder();
//        byte[] data_byte = null;
        for (Object o : msg_arr) {
            String data_base64 = JSON.parseObject(o.toString()).getString("data");
            //先将数据base64解码 然后通过解码器解码
            byte[] data_byte = decodeByFri(decoder.decode(data_base64));
            data_byte = getOneChannel(data_byte);
            //语音必须持续不断发给引擎 缓存后发过去 会不翻译
            Client.sendAudioMsg((data_byte));
        }
    }

    private static byte[] acVolume(byte[] audio) {
        byte[] acAudio = new byte[audio.length];
        for (int i = 0; i < audio.length; i++) {
            acAudio[i] = (byte) (audio[i] << 1);
        }
        return acAudio;
    }

    /**
     * 将语音保存到本地
     *
     * @param audio_pcm
     * @param fileName
     */
    private static void saveToLocal(byte[] audio_pcm, String fileName) {
        File file_wav = new File("C:\\Users\\fang\\Desktop\\audio_transcription\\" + fileName);
        //存在本地确定可播放
        try {
            FileUtils.writeByteArrayToFile(file_wav, audio_pcm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取单声道数据
     *
     * @param twoChannel
     * @return
     */
    public static byte[] getOneChannel(byte[] twoChannel) {
        //只取偶数索引的单声道值 奇数索引的数据为另一个声道
        byte[] oneChannel = new byte[twoChannel.length / 2 + 2];
        for (int i = 0, j = 0; i < twoChannel.length; ) {
            oneChannel[j] = twoChannel[i];
            oneChannel[j + 1] = twoChannel[i + 1];
            i = i + 4;
            j = j + 2;

        }
        return oneChannel;
    }

    public static byte[] decodeByFri(byte[] base64_decoded) {
        FRIAudioDecoder mDecoder = new FRIAudioDecoder();
        byte[] decoded = new byte[(base64_decoded.length - 6) * 4];
        mDecoder.DecodeStereo(base64_decoded, decoded, 1);
        return decoded;
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        /*byte[] res = IOUtils.toByteArray(new FileInputStream("C:\\Users\\fang\\Desktop\\audio_transcription\\three.wav"));
        Client.sendAudioMsg(res);*/


        LinkedList<String> DataBlock_list = new LinkedList<>();
        DataBlock_list.addFirst("a");
        DataBlock_list.addFirst("b");
        for (String s : DataBlock_list) {
            System.out.println(s);
        }
    }
}
