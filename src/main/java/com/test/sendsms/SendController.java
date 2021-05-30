package com.test.sendsms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class SendController {

    @Value("${signName}")
    private String signName;

    @Value("${templateCode}")
    private String templateCode;

    @Value("${accessKey}")
    private String accessKey;

    @Value("${accesssSecret}")
    private String accesssSecret;

    /**
     * 发送验证码逻辑
     */
    private Map<String,String> sendSms(String mobile) throws ClientException {

        Map<String,String> resultMap = new HashMap<>();

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey,accesssSecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        request.setTemplateCode(templateCode);

        int rand_num = (int)((Math.random()*9+1)*100000);

        request.setTemplateParam("{\"code\":\""+rand_num+"\"}");//此处最好使用转json工具

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        String code = sendSmsResponse.getCode();
        if(code.equals("OK")){
            resultMap.put("code","1000");
            resultMap.put("msg","发送成功");
        }else{
            resultMap.put("code","-1000");
            resultMap.put("msg","发送失败");
        }

        return resultMap;
    }

    @RequestMapping(value="/sendsms")
    @ResponseBody
    public Map<String,String> getValidCode(String mobile){
        try {
            return this.sendSms(mobile);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("code","-1000");
        resultMap.put("msg","发送失败");
        return resultMap;
    }



}
