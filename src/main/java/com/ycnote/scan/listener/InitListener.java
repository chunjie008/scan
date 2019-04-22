package com.ycnote.scan.listener;

import com.ycnote.scan.utils.Ping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/**
 * @author 游超
 * 初始化监听
 */
@Slf4j
@Component
public class InitListener implements ApplicationListener<ContextRefreshedEvent> {



    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        /**
         * 通过ping网卡获取默认语言
         */
        new Ping("127.0.0.1",1,1000).setLanguage();
        log.info("项目启动成功");

        String url = "http://localhost:8888/index.html";
        try {
            poenBrowser(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法描述:打开浏览器
     *
     * @author leon 2016年10月28日 下午4:37:35
     * @param url 打开的url
     * @throws Exception
     */
    public static void poenBrowser(String url) throws Exception {
        // 获取操作系统的名字
        String osName = System.getProperty("os.name", "");
        if (osName.startsWith("Mac OS")) {
            // 苹果的打开方式
            Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
            openURL.invoke(null, new Object[] { url });
        } else if (osName.startsWith("Windows")) {
            // windows的打开方式。
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else {
            // Unix or Linux的打开方式
            String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
                // 执行代码，在brower有值后跳出，
                // 这里是如果进程创建成功了，==0是表示正常结束。
                if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
                    browser = browsers[count];
                }
            if (browser == null) {
                throw new Exception("Could not find web browser");
            } else {
                // 这个值在上面已经成功的得到了一个进程。
                Runtime.getRuntime().exec(new String[] { browser, url });
            }
        }
    }

}
