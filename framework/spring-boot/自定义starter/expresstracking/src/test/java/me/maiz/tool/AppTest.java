package me.maiz.tool;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        ExprsTrackConfig config = new ExprsTrackConfig();
        config.setUserKey("1663793");
        config.setUserSecret("0d253737-fc22-4064-87cd-5052fc6af857");
        config.setApiUrl("http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx");
        KuaiDiNiaoQueryAPI kuaiDiNiaoQueryAPI = new KuaiDiNiaoQueryAPI(config);

        try {
            String kuaidi = kuaiDiNiaoQueryAPI.getOrderTracesByJson("YTO", "YT9302135036468");
            System.out.println(kuaidi);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
