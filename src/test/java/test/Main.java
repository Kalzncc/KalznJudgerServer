package test;

import com.google.gson.Gson;
import com.kalzn.callback.Acceptable;
import com.kalzn.config.JudgerConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.config.ResultConfig;
import com.kalzn.config.TaskConfig;
import com.kalzn.datamanager.DataManager;
import com.kalzn.factory.JudgerConfigFactory;
import com.kalzn.factory.ResultConfigFactory;
import com.kalzn.runbox.Runbox;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

    JudgerServerConfig judgerServerConfig = null;
    JudgerConfigFactory judgerConfigFactory = null;
    JudgerConfig judgerConfig = null;

    @Before
    public void init() throws IOException {
        judgerServerConfig = new JudgerServerConfig();
        judgerServerConfig.setMaxJsonFileSize(10240000);
        judgerServerConfig.setJudgerCorePath("/home/kalzn/project/judger_queue_manager/lib/judger_core/judger");
        judgerServerConfig.setJudgerCoreLogPath("/home/kalzn/project/judger_queue_manager/case/log.log");
        judgerConfigFactory = new JudgerConfigFactory(judgerServerConfig);
        judgerConfig = judgerConfigFactory.createJudgerConfigFromJsonFile("case/config.json");
    }

    @Test
    public void test() {
        System.out.println("Hello World!");
    }

    @Test
    public void testGsonToClass() throws IOException {
        JudgerServerConfig judgerServerConfig = new JudgerServerConfig();
        judgerServerConfig.setMaxJsonFileSize(10240000);
        JudgerConfigFactory judgerConfigFactory = new JudgerConfigFactory(judgerServerConfig);
        JudgerConfig judgerConfig = judgerConfigFactory.createJudgerConfigFromJsonFile("case/config.json");
        System.out.println(judgerConfig);
    }

    @Test
    public void testClassToGson() throws IOException {
        JudgerServerConfig judgerServerConfig = new JudgerServerConfig();
        judgerServerConfig.setMaxJsonFileSize(10240000);
        JudgerConfigFactory judgerConfigFactory = new JudgerConfigFactory(judgerServerConfig);
        JudgerConfig judgerConfig = judgerConfigFactory.createJudgerConfigFromJsonFile("case/config.json");
        judgerConfig.setTaskID("HelloWorld!");
        FileWriter writer = new FileWriter("case/config2.json");
        writer.write(judgerConfig.toJson());
        writer.close();
    }

    @Test
    public void testGsonToResultClass() throws IOException {
        JudgerServerConfig judgerServerConfig = new JudgerServerConfig();
        judgerServerConfig.setMaxJsonFileSize(10240000);
        ResultConfigFactory resultConfigFactory = new ResultConfigFactory(judgerServerConfig);
        ResultConfig resultConfig = resultConfigFactory.createResultConfigFromJsonFile("case/result.json");
        System.out.println(resultConfig);
    }

    @Test
    public void testBuildWorkSpace() throws IOException {
        judgerConfig.setCodeSourcePath("/home/kalzn/project/judger_queue_manager/case/test_sample_0/std.cpp");
        judgerConfig.setCodeFileName("std.cpp");
        judgerConfig.setDataSourcePath("/home/kalzn/project/judger_queue_manager/case/test_sample_0");
        TaskConfig config = new TaskConfig("123", judgerConfig);

        Runbox runbox = new Runbox(config, new Acceptable() {
            @Override
            public void accept(TaskConfig taskConfig) {
                System.out.println("Runbox push result.");
            }
        }, judgerServerConfig, new DataManager());
        runbox.run();
    }

    @Test
    public void testRunning() {
        judgerConfig.setCodeSourcePath("/home/kalzn/project/judger_queue_manager/case/test_sample_0/std.cpp");
        judgerConfig.setCodeFileName("std.cpp");
        judgerConfig.setDataSourcePath("/home/kalzn/project/judger_queue_manager/case/test_sample_0");
        TaskConfig config = new TaskConfig("123", judgerConfig);

        Runbox runbox = new Runbox(config, new Acceptable() {
            @Override
            public void accept(TaskConfig taskConfig) {
                System.out.println("Runbox push result.");
            }
        }, judgerServerConfig, new DataManager());
        runbox.run();
    }

    @Test
    public void testDownloadFile() throws Exception{

        HttpClient leaderHttpClient = new HttpClient();
        leaderHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
        GetMethod getMethod = new GetMethod("http://code.visualstudio.com/sha/download?build=stable&os=linux-deb-x64");
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 2000);
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = leaderHttpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
            }
            InputStream inputStream = getMethod.getResponseBodyAsStream();
            FileOutputStream fileOutputStream = new FileOutputStream(new File("case/123.deb"));
            byte[] buffer = new byte[1024];
            int ch;
            while( (ch = inputStream.read(buffer))!=-1) {
                fileOutputStream.write(buffer, 0, ch);
            }
            fileOutputStream.close();
        } finally {
            getMethod.releaseConnection();
        }
    }

    @Test
    public void unzipTest() throws Exception{
        String inputFile = "case/test.zip";
        String destDirPath = "case/unzip_test";
        File srcFile = new File(inputFile);//获取当前压缩文件
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new Exception("Src file is not found");
        }
        ZipFile zipFile = new ZipFile(srcFile);//创建压缩文件对象
        //开始解压
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                String dirPath = destDirPath + "/" + entry.getName();
                srcFile.mkdirs();
            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                File targetFile = new File(destDirPath + "/" + entry.getName());
                // 保证这个文件的父文件夹必须要存在
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                targetFile.createNewFile();
                // 将压缩文件内容写入到这个文件中
                InputStream is = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(targetFile);
                int len;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                // 关流顺序，先打开的后关闭
                fos.close();
                is.close();
            }
        }
    }
}
