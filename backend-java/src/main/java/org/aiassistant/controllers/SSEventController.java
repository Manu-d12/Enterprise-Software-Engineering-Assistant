package org.aiassistant.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/sse-time")
public class SSEventController {

//    @GetMapping("/stream-sse-mvc")
//    public SseEmitter streamSseMvc() {
//        SseEmitter emitter = new SseEmitter();
//        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
//        sseMvcExecutor.execute(() -> {
//            try {
//                for (int i = 0; true; i++) {
//                    SseEmitter.SseEventBuilder event = SseEmitter.event()
//                            .data("SSE MVC - " + System.currentTimeMillis())
//                            .id(String.valueOf(i))
//                            .name("sse event - mvc");
//                    emitter.send(event);
//                    Thread.sleep(1000);
//                }
//            } catch (Exception ex) {
//                emitter.completeWithError(ex);
//            }
//        });
//        return emitter;
//    }

}
