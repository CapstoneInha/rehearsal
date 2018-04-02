package com.rehearsal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.Template;
import com.linecorp.bot.model.response.BotApiResponse;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = Logger.getLogger(Handler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


        System.out.println(input.get("events"));
        ArrayList events = (ArrayList) input.get("events");
        LinkedHashMap message = (LinkedHashMap) events.get(0);


        Map source = (Map) message.get("source");
        String userId = source.get("userId").toString();

        Map messageObject = (Map) message.get("message");
        String text = messageObject.get("text").toString();


//        String userId = message.get("userId").toString();
        LOG.info(userId);
        LOG.info(text);
        LOG.info(source);
        LOG.info(messageObject);


//        message.forEach((index, object) -> {
//            System.out.println(index);
//            System.out.println(object);
//        });
//
//        events.forEach(index -> {
//            System.out.println(index);
//        });

        final LineMessagingClient client = LineMessagingClient
                .builder("")
                .build();

        Action action = new MessageAction("View details", "http://www.naver.com");
        ArrayList<Action> arrayList = new ArrayList();
        arrayList.add(action);
        Template template = new ButtonsTemplate("https://1.bp.blogspot.com/-kzOVjO-0-cQ/VtO7GTH2RTI/AAAAAAAAHME/iSF5gx4OIbY/s1600/raccoon-193647.jpg", "너구리", "뭐래", arrayList);
        TemplateMessage templateMessage = new TemplateMessage("This is a buttons template", template);


        final TextMessage textMessage = new TextMessage("1. 일정안내 \n2. 참가여부입력");
//        final PushMessage pushMessage = new PushMessage(
//                ,
//                templateMessage);o
        final PushMessage pushTMessage = new PushMessage(
                userId,
                textMessage);

//      text.equals("1")
        if (text == "1") {
            TextMessage num1Message = new TextMessage("일시: 2018년 4월 17일 15:00\n장소: 인하대학교");
            BotApiResponse botApi1Response = null;
            try {

                botApi1Response = client.pushMessage(pushTMessage).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {

            BotApiResponse botApiResponse = null;
            BotApiResponse botApiStartResponse = null;
            try {
                //botApiResponse = client.pushMessage(pushMessage).get();
                botApiStartResponse = client.pushMessage(pushTMessage).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


        System.out.println("received: " + input);
        Response responseBody = new Response("Go Serverless v1.x! Your function executed successfully!", input);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }
}
