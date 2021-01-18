import jdk.nashorn.internal.ir.annotations.Ignore;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.sql.*;

public class Main {
    private static Connection conn = null;//定义数据库连接对象
    public static void main(String[] args) throws EncoderException {
        File fileDic = new File("H:\\MyDoucument\\Tencent Files\\MobileFile\\download");
        for (File file : fileDic.listFiles()) {
            if (file.isFile()) {
                File target = getNewFile(file, "H:/MyDoucument/Tencent Files/MobileFile/ximalaya.db");
                if (target!=null){
                    AudioAttributes audio = new AudioAttributes();
                    audio.setCodec("libmp3lame");
                    audio.setBitRate(128000);
                    audio.setChannels(2);
                    audio.setSamplingRate(44100);
                    //Encoding attributes
                    EncodingAttributes attrs = new EncodingAttributes();
                    attrs.setOutputFormat("mp3");
                    attrs.setAudioAttributes(audio);

                    //Encode
                    Encoder encoder = new Encoder();
                    System.out.println("正在转换["+file.getName()+"]->["+target.getName()+"]");
                    encoder.encode(new MultimediaObject(file), target, attrs);
                    file.delete();
                    System.out.println("转换完成["+target.getName()+"]");

                }else {
                    System.out.println(file.getName()+"未找到目标名称");
                }
//                File target=new File(fileDic.getAbsolutePath()+"/"+file.getName().replaceAll(".m4a",".mp3"));
                //Audio Attributes


            }
        }
    }

    private static File getNewFile(File file, String dbPath) {
        try {
            getSqlliteConnect(dbPath);
            String sql="select * from newtrack where downloadedsavefilepath like ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,"%"+file.getName()+"%");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                File parentFile = file.getParentFile();
                String newPath = parentFile.getAbsolutePath() + "/mp3";

                File newFile = new File(newPath);
                newFile.mkdir();
                File result=new File(newPath+"/"+rs.getString("tracktitle")+".mp3");
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private static void getSqlliteConnect(String dbPath) {
        if (conn==null) {
            try {
                String url = "jdbc:sqlite:" + dbPath;  //定义连接数据库的url(url:访问数据库的URL路径),test为数据库名称
                Class.forName("org.sqlite.JDBC");//加载数据库驱动
                conn = DriverManager.getConnection(url);    //获取数据库连接
                System.out.println("数据库连接成功！\n");//数据库连接成功输出提示
            } catch ( Exception e) {
                e.printStackTrace();
                System.out.println("数据库连接失败！");
            }
        }
    }

}
