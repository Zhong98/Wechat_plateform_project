package platform.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha getKaptcha(){

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        Config config = new Config(properties);
        //设置图片边框
        properties.setProperty("kaptcha.border", "yes");
        //设置边框颜色只支持RGB和英文单词颜色
        properties.setProperty("kaptcha.border.color", "75,139,244");
        //设置字体颜色只支持RGB和英文单词颜色
        properties.setProperty("kaptcha.textproducer.font.color", "75,139,244");
        //设置图片宽
        properties.setProperty("kaptcha.image.width","100");
        //设置图片高
        properties.setProperty("kaptcha.image.height","40");
        //设置字体大小
        properties.setProperty("kaptcha.image.font.size","20");
        //设置验证码长度
        properties.setProperty("kaptcha.textproducer.char.length","4");
        //设置取消干扰
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        kaptcha.setConfig(config);
        return kaptcha;
    }
}