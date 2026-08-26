package com.example.student_management.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotService {

    @Value("${spring.ai.google.genai.api-key}")
    private String APIKey;

    private final ChatClient chatClient;

    @PersistenceContext
    private EntityManager entityManager;

    public ChatBotService(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }

    public String askChatBot(String question){

        //generate a query here by putting in the rules for our schema and other rules
        String prompt =
                """
                You are a MySQL generator who's job is to create SQL queries based on the schema provided and what
                questions a user may ask you.
                
                Here is the schema for the database:
                
                - Table: student (id INT, course VARCHAR, email VARCHAR, name VARCHAR, city VARCHAR, phone_number VARCHAR, address VARCHAR, date_of_birth VARCHAR,
                		departments VARCHAR, gender VARCHAR, parent_name VARCHAR, is_admin BOOLEAN)
                
                - Rules:
                    - You will output raw SQL statements ONLY.
                    - You will ONLY create SELECT queries. You will NOT create statements that alter the table or database in any way.
                    - If you cannot generate a SELECT statement based on the schema provided and the user's question, simply state "I don't know the answer to that question."
                    - The departments column in particular contains a VARCHAR of some combination of ece, it, cse, civil, and mech, all separated by spaces. ece is electrical 
                      and computer engineering, it is Internet Technology, civil is civil and environmental engineering, cse is computer science engineering, and mech is mechanical
                      engineering. 
                		
                The user's question: """ + question;

        //send the prompt to Gemini to create a query
        String sqlStatement = chatClient.prompt()
                .user(prompt) //the user inputted message to the AI
                .call() //send the message to the AI platform
                .content(); //the response from the AI

        if (sqlStatement == null){
            return "I don't know the answer to that question.";
        }

        //make sure the generated sql query is a select statement
        String queryCheck = sqlStatement.substring(0, 6).toUpperCase();
        if (!queryCheck.equals("SELECT")){
            return "I cannot perform your requested action. Sorry!";
        }

        Query query = entityManager.createNativeQuery(sqlStatement);
        List<?> results = query.getResultList();

        String responsePrompt = "The user asked " + question + " and the result was " + results
                + ". Make a response based off these results.";

        return chatClient.prompt()
                .user(responsePrompt)
                .call()
                .content();
    }
}
