package com.example.student_management.controller;

import com.example.student_management.dto.ChatMessage;
import com.example.student_management.dto.ChatResponse;
import com.example.student_management.service.ChatBotService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping("/query")
    public ChatResponse answerQuery(@RequestBody ChatMessage question){

        System.out.println("Our question is " + question.getMessage());

        //send question to service here
        String response = chatBotService.askChatBot(question.getMessage());

        System.out.println("Our response is " + response);

        //send response here
        return new ChatResponse(response);
    }
}
