package brut.apktool;

import java.net.URLDecoder;

/**
 * Created by billyyoyo on 15-10-16.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        String str = URLDecoder.decode("{\"protocol\":{\"result\":{\"status\":1,\"msg\":\"ok\"},\"cookie\":\"\"},\"data\":{\"serverTime\":1444979407,\"orderId\":\"s1N0000002042\",\"MailCount\":4,\"TaskTraceList\":{\"10101\":\"6\",\"10201\":\"0\",\"10301\":\"0\",\"10401\":\"2075\",\"10601\":\"0\",\"10701\":\"0\",\"10801\":\"1\",\"10901\":\"6\",\"11001\":\"0\",\"11201\":\"2\",\"11301\":\"0\",\"11401\":\"0\",\"11501\":\"0\",\"11601\":\"0\",\"11701\":\"0\",\"11801\":\"0\",\"11901\":\"0\",\"12001\":\"0\",\"12101\":\"0\",\"12201\":\"1\",\"12301\":\"0\",\"10502\":\"3\"},\"TowerRush\":{\"isRush\":false,\"time\":0,\"resetCount\":\"0\"},\"HeroPub\":{\"time\":1445144474},\"tv\":{\"{\\\"action\\\":\\\"system\\\",\\\"content\\\":\\\"\u52a0Q\u7fa4385032750 \u627eBUG\u9001\u798f\u5229\\\",\\\"time\\\":1,\\\"id\\\":2}\":2,\"{\\\"action\\\":\\\"system\\\",\\\"content\\\":\\\"\u672c\u6b21\u6d4b\u8bd5\u4e0d\u4ee3\u8868\u6e38\u620f\u7684\u6700\u7ec8\u54c1\u8d28\uff0c\u6211\u4eec\u5c06\u4f1a\u6839\u636e\u60a8\u7684\u5b9d\u8d35\u610f\u89c1\u4e0d\u65ad\u5b8c\u5584\uff01\\\",\\\"time\\\":1,\\\"id\\\":3}\":3,\"{\\\"action\\\":\\\"system\\\",\\\"content\\\":\\\"\u52a0Q\u7fa4385032750 \u6bcf\u592912\u70b9\u3001\n", "utf-8");
        System.out.println(str);

    }


}
