package me.maiz.app.dailycost.components;

import com.avos.avoscloud.AVOSCloud;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class StartupConfigBean implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        AVOSCloud.initialize("T58uP1GMRsthGB3vOTazuNHR-gzGzoHsz","gwSD1SlhXOJKd19K73QgEwoY","x7wmPUBUESilMTB6GkpKtv5t");
        AVOSCloud.setDebugLogEnabled(true);

    }
}
